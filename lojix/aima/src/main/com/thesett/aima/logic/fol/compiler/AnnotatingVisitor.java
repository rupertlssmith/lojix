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
package com.thesett.aima.logic.fol.compiler;

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.FunctorVisitor;
import com.thesett.aima.logic.fol.LiteralType;
import com.thesett.aima.logic.fol.LiteralTypeVisitor;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.VariableVisitor;
import com.thesett.common.util.doublemaps.SymbolKey;
import com.thesett.common.util.doublemaps.SymbolTable;

/**
 * AnnotatingVisitor annotates terms within a clause in order to provide more detailed information that can be used to
 * generate more optimal code. Going to perform symbolic evaluation of the program in order to determine as much as can
 * possibly be known about it at compile time. This visitor will probably be split up into more phases and renamed and
 * so on, this is just an experiment to get things started at the moment. Use a postfix search with the backtracking
 * algorithm with this visitor, to ensure that leaves are visited before nodes. This reverses the usual left-right
 * ordering so the clause head is examined after the body, but the body is examined backwards. Want to actually traverse
 * the body forwards, so that terms are evaluated in the order that they would be executed.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Scan for terms that are always fully ground.
 * <tr><td> Scan variables usages to determine which ones are temporary (occurr in the head only).
 *     <td> {@link PositionalTermTraverserImpl}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class AnnotatingVisitor implements FunctorVisitor, VariableVisitor, LiteralTypeVisitor
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(AnnotatingVisitor.class.getName()); */

    /** Defines the symbol field for the abstract domain over all terms. */
    public static final String TERM_DOMAIN = "termDomain";

    /** Defines the symbol field for the abstract domain over all variables. */
    public static final String VARIABLE_DOMAIN = "varDomain";

    /** The basic traverser used to provide positional context to visited terms. */
    private PositionalTermTraverser traverser;

    /** The name interner. */
    private VariableAndFunctorInterner interner;

    /** Holds the symbol table to record the annotations in. */
    private SymbolTable<Integer, String, Object> symbolTable;

    /**
     * Creates an annotating visitor using the specified basic traverser to provide positional context information about
     * symbols.
     *
     * @param interner    The symbol name interner.
     * @param symbolTable The symbol table to store annotations in.
     * @param traverser   The positional context traverser.
     */
    public AnnotatingVisitor(VariableAndFunctorInterner interner, SymbolTable<Integer, String, Object> symbolTable,
        PositionalTermTraverser traverser)
    {
        this.traverser = traverser;
        this.interner = interner;
        this.symbolTable = symbolTable;
    }

    /** {@inheritDoc} */
    public void visit(Functor functor)
    {
        // functor is ground if all of its arguments are ground.
        boolean ground = true;

        if (functor.getArguments() != null)
        {
            for (Term argument : functor.getArguments())
            {
                SymbolKey symbolKey = argument.getSymbolKey();
                TermDomain annotation = (TermDomain) symbolTable.get(symbolKey, TERM_DOMAIN);

                if ((annotation == null) || !annotation.ground)
                {
                    ground = false;

                    break;
                }
            }
        }

        /*log.fine((ground ? "ground " : "non-ground ") + functor.toString(interner, true, false));*/
        symbolTable.put(functor.getSymbolKey(), TERM_DOMAIN, new TermDomain(ground));
    }

    /** {@inheritDoc} */
    public void visit(Variable variable)
    {
        SymbolKey symbolKey = variable.getSymbolKey();

        // Check if the variable has already been annotated.
        TermDomain annotation = (TermDomain) symbolTable.get(symbolKey, TERM_DOMAIN);

        if (annotation == null)
        {
            // variable is ground if it appears in a call to a predicate that always grounds that argument.
            /*log.fine("non-ground " + variable.toString(interner, true, false));*/
            symbolTable.put(symbolKey, TERM_DOMAIN, new TermDomain(false));
        }
        else
        {
            /*log.fine("already seen " + variable.toString(interner, true, false));*/
        }

        // Check if the variable domain has already been annotated for a previous occurrence of the variable.
        VarDomain varDomain = (VarDomain) symbolTable.get(symbolKey, VARIABLE_DOMAIN);

        if (varDomain == null)
        {
            varDomain = new VarDomain(traverser.isInHead());
            symbolTable.put(symbolKey, VARIABLE_DOMAIN, varDomain);
        }
        else
        {
            varDomain.isTemporary = traverser.isInHead() && varDomain.isTemporary();
        }

        /*log.fine(variable.toString(interner, true, false) +
            (varDomain.isTemporary() ? " may be temporary." : " is not temporary."));*/
    }

    /** {@inheritDoc} */
    public void visit(LiteralType literal)
    {
        // literal is ground.
        /*log.fine("ground " + literal.toString(interner, true, false));*/
        symbolTable.put(literal.getSymbolKey(), TERM_DOMAIN, new TermDomain(true));
    }

    /** {@inheritDoc} */
    public void visit(Term term)
    {
    }

    /**
     * Abstract domain over variables.
     */
    public static class VarDomain
    {
        /**
         * Flag used to indicate that a variable occurs in the head or as the argument to determinate predicates only.
         */
        boolean isTemporary;

        /**
         * Creates a variable domain element.
         *
         * @param temporary <tt>true</tt> if the variable can be temporary.
         */
        private VarDomain(boolean temporary)
        {
            isTemporary = temporary;
        }

        /**
         * Reports whether a variable is temporary.
         *
         * @return <tt>true</tt> if the variable is temporary.
         */
        public boolean isTemporary()
        {
            return isTemporary;
        }
    }

    /**
     * Implements the abstract domain to be computed over terms.
     */
    public static class TermDomain
    {
        /** Flag used to indicate that a term is fully ground, that is, cannot contain any free variables. */
        boolean ground;

        /**
         * Creates a domain element.
         *
         * @param ground <tt>true</tt> to indicate that a term is fully ground.
         */
        private TermDomain(boolean ground)
        {
            this.ground = ground;
        }
    }
}
