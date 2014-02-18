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
package com.thesett.aima.logic.fol.vam;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.LogicCompiler;
import com.thesett.aima.logic.fol.LogicCompilerObserver;
import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.bytecode.BaseMachine;
import com.thesett.aima.logic.fol.compiler.AnnotatingVisitor;
import com.thesett.aima.logic.fol.compiler.PositionalTermTraverser;
import com.thesett.aima.logic.fol.compiler.PositionalTermTraverserImpl;
import com.thesett.aima.logic.fol.compiler.SymbolKeyTraverser;
import com.thesett.aima.logic.fol.compiler.TermWalker;
import com.thesett.aima.logic.fol.vam.vam2p.VAM2PCompiledClause;
import com.thesett.aima.logic.fol.vam.vam2p.VAM2PCompiler;
import com.thesett.aima.logic.fol.vam.vamai.VAMAIAbstractInterpreter;
import com.thesett.aima.logic.fol.vam.vamai.VAMAICompiledClause;
import com.thesett.aima.logic.fol.vam.vamai.VAMAICompiler;
import com.thesett.aima.search.util.backtracking.DepthFirstBacktrackingSearch;
import com.thesett.common.parsing.SourceCodeException;
import com.thesett.common.util.doublemaps.SymbolTable;

/**
 * VAMCompiler sequences together the stages of a multi-stage compiler for the VAM machine.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAMCompiler extends BaseMachine implements LogicCompiler<Clause, VAM2PCompiledClause, VAM2PCompiledClause>,
    LogicCompilerObserver<VAMAICompiledClause, VAMAICompiledClause>
{
    /** Holds the 2P compiler. */
    LogicCompiler<Clause, VAM2PCompiledClause, VAM2PCompiledClause> compiler2p;

    /** Holds the 1P compiler. */
    LogicCompiler<Clause, VAMAICompiledClause, VAMAICompiledClause> compilerAi;

    /** Holds the abstract analysis machine to perform the optimizing program analysis. */
    VAMAIAbstractInterpreter analysisMachine;

    /**
     * Creates a compiler from a 2p and 1p compiler, over the specified symbol table and interner.
     *
     * @param symbolTable The symbol table for the compiler.
     * @param interner    The symbol interner for the compiler.
     */
    VAMCompiler(SymbolTable<Integer, String, Object> symbolTable, VariableAndFunctorInterner interner)
    {
        super(symbolTable, interner);

        compiler2p = new VAM2PCompiler(symbolTable, interner);
        compilerAi = new VAMAICompiler(symbolTable, interner);

        analysisMachine = new VAMAIAbstractInterpreter(symbolTable, interner);
    }

    /** {@inheritDoc} */
    public void compile(Sentence<Clause> sentence) throws SourceCodeException
    {
        // Extract the clause to compile from the parsed sentence.
        Clause clause = sentence.getT();

        // Add the clause and all of its elements to the symbol table.
        SymbolKeyTraverser keyWalker = new SymbolKeyTraverser(interner, symbolTable, null);
        TermWalker walker = new TermWalker(new DepthFirstBacktrackingSearch<Term, Term>(), keyWalker, keyWalker);
        walker.walk(clause);

        // Annotate the clause with a symbolic analysis walking over the term in post-fix order.
        PositionalTermTraverser annotationTraverser = new PositionalTermTraverserImpl(true, false, false);
        walker =
            new TermWalker(new DepthFirstBacktrackingSearch<Term, Term>(), annotationTraverser,
                new AnnotatingVisitor(interner, symbolTable, annotationTraverser));
        walker.walk(clause);

        // Compile the clause into VAM AI code and analyze it using the abstract analysis machine.
        compilerAi.compile(sentence);

        // Compile the clause.
        compiler2p.compile(sentence);
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Performs abstract analysis on the prepared clause.
     */
    public void onCompilation(Sentence<VAMAICompiledClause> sentence) throws SourceCodeException
    {
        analysisMachine.analyze(sentence.getT());
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Performs abstract analysis on the prepared clause.
     */
    public void onQueryCompilation(Sentence<VAMAICompiledClause> sentence) throws SourceCodeException
    {
        analysisMachine.analyze(sentence.getT());
    }

    /** {@inheritDoc} */
    public void setCompilerObserver(LogicCompilerObserver<VAM2PCompiledClause, VAM2PCompiledClause> observer)
    {
        compiler2p.setCompilerObserver(observer);
    }

    /** {@inheritDoc} */
    public void endScope() throws SourceCodeException
    {
        compiler2p.endScope();
    }
}
