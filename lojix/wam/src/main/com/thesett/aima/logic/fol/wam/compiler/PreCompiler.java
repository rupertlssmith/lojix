/*
 * Copyright The Sett Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thesett.aima.logic.fol.wam.compiler;

import com.thesett.aima.logic.fol.BasePositionalVisitor;
import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.LogicCompiler;
import com.thesett.aima.logic.fol.LogicCompilerObserver;
import com.thesett.aima.logic.fol.PositionalTermVisitor;
import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.bytecode.BaseMachine;
import com.thesett.aima.logic.fol.compiler.PositionalTermTraverser;
import com.thesett.aima.logic.fol.compiler.TermWalker;
import com.thesett.aima.logic.fol.wam.TermWalkers;
import com.thesett.aima.logic.fol.wam.builtins.BuiltInFunctor;
import com.thesett.aima.logic.fol.wam.builtins.BuiltInTransform;
import com.thesett.common.parsing.SourceCodeException;
import com.thesett.common.util.doublemaps.SymbolTable;

/**
 * PreCompiler transforms clauses for compilation, substituting built-ins for any built-ins in the source expressions to
 * compile.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Perform the built-ins transformation.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PreCompiler extends BaseMachine implements LogicCompiler<Clause, Clause, Clause>
{
    /** Used for debugging. */
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(PreCompiler.class.getName());

    /** Holds the compiler output observer. */
    private LogicCompilerObserver<Clause, Clause> observer;

    /** Holds the default built in, for standard compilation and interners and symbol tables. */
    private final DefaultBuiltIn defaultBuiltIn;

    /** Holds the built in transformation. */
    private BuiltInTransform builtInTransform;

    /**
     * Creates a new PreCompiler.
     *
     * @param symbolTable    The symbol table.
     * @param interner       The machine to translate functor and variable names.
     * @param defaultBuiltIn The default built in, for standard compilation and interners and symbol tables.
     */
    public PreCompiler(SymbolTable<Integer, String, Object> symbolTable, VariableAndFunctorInterner interner,
        DefaultBuiltIn defaultBuiltIn)
    {
        super(symbolTable, interner);

        this.defaultBuiltIn = defaultBuiltIn;
        builtInTransform = new BuiltInTransform(defaultBuiltIn);
    }

    /** {@inheritDoc} */
    public void compile(Sentence<Clause> sentence) throws SourceCodeException
    {
        Clause clause = sentence.getT();

        substituteBuiltIns(clause);

        if (observer != null)
        {
            if (clause.isQuery())
            {
                observer.onQueryCompilation(sentence);
            }
            else
            {
                observer.onCompilation(sentence);
            }
        }
    }

    /** {@inheritDoc} */
    public void setCompilerObserver(LogicCompilerObserver<Clause, Clause> observer)
    {
        this.observer = observer;
    }

    /** {@inheritDoc} */
    public void endScope() throws SourceCodeException
    {
    }

    /**
     * Substitutes built-ins within a clause, with their built-in definitions.
     *
     * @param clause The clause to transform.
     */
    private void substituteBuiltIns(Clause clause)
    {
        TermWalker walk = TermWalkers.positionalWalker(new BuiltInTransformVisitor(interner, symbolTable, null));
        walk.walk(clause);
    }

    /**
     * BuiltInTransformVisitor should be used with a depth first positional walk over a term to compile. On leaving each
     * term, that is in a post-fix order, if the term is a functor, the built-in transformation function is applied to
     * it. If the built-in applies a transformation to a functor, it is substituted within its parent for the built-in.
     *
     * <p/>Conjunctions and disjunctions are treated specially by this transform. The conjunction and disjunction
     * operators may appear within any structure, but are only to be compiled as such if they are 'top-level'. They are
     * considered top-level when they appear at the top-level within a clause, or directly beneath a parent conjunction
     * or disjunction that is considered to be top-level. Effectively they are flattened into the top-level of the
     * clause in which they appear, but the original structure is preserved rather than actually flattened at this time,
     * as it can change meaning depending on how the term is bracketed. This traversal simply marks all conjunctions and
     * disjunctions that are part of the clause top-level, with the top-level flag.
     */
    private class BuiltInTransformVisitor extends BasePositionalVisitor implements PositionalTermVisitor
    {
        /**
         * Creates the visitor with the supplied interner, symbol table and traverser.
         *
         * @param interner    The name interner.
         * @param symbolTable The compiler symbol table.
         * @param traverser   The positional context traverser.
         */
        private BuiltInTransformVisitor(VariableAndFunctorInterner interner,
            SymbolTable<Integer, String, Object> symbolTable, PositionalTermTraverser traverser)
        {
            super(interner, symbolTable, traverser);
        }

        /** {@inheritDoc} */
        public void setPositionalTraverser(PositionalTermTraverser traverser)
        {
            this.traverser = traverser;
        }

        /**
         * Applies the built-in transform during a post-fix visit of a term.
         *
         * @param functor The functor to visit.
         */
        protected void leaveFunctor(Functor functor)
        {
            int pos = traverser.getPosition();

            if (!traverser.isInHead() && (pos >= 0))
            {
                Functor transformed = builtInTransform.apply(functor);

                if (functor != transformed)
                {
                    log.fine("Transformed: " + functor + " to " + transformed.getClass());

                    BuiltInFunctor builtInFunctor = (BuiltInFunctor) transformed;

                    Term parentTerm = traverser.getParentContext().getTerm();

                    if (parentTerm instanceof Clause)
                    {
                        Clause parentClause = (Clause) parentTerm;

                        parentClause.getBody()[pos] = builtInFunctor;

                    }
                    else if (parentTerm instanceof Functor)
                    {
                        Functor parentFunctor = (Functor) parentTerm;

                        parentFunctor.getArguments()[pos] = builtInFunctor;
                    }

                    if (isTopLevel())
                    {
                        builtInFunctor.setTopLevel(true);
                    }
                }
            }
        }

        /**
         * Functors are considered top-level when they appear at the top-level within a clause, or directly beneath a
         * parent conjunction or disjunction that is considered to be top-level.
         */
        private boolean isTopLevel()
        {
            if (traverser.isTopLevel())
            {
                return true;
            }
            else
            {
                Term parentTerm = traverser.getParentContext().getTerm();

                if (parentTerm instanceof BuiltInFunctor)
                {
                    BuiltInFunctor parentBuiltIn = (BuiltInFunctor) parentTerm;

                    return parentBuiltIn.isTopLevel();
                }
            }

            return false;
        }
    }
}
