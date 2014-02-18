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
package com.thesett.aima.logic.fol.vam.vamai;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.LogicCompiler;
import com.thesett.aima.logic.fol.LogicCompilerObserver;
import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.bytecode.BaseMachine;
import com.thesett.aima.logic.fol.compiler.PositionalTermTraverser;
import com.thesett.aima.logic.fol.compiler.PositionalTermTraverserImpl;
import com.thesett.aima.logic.fol.compiler.TermWalker;
import com.thesett.aima.logic.fol.vam.vamai.instructions.VAMAIInstruction;
import com.thesett.aima.search.util.backtracking.DepthFirstBacktrackingSearch;
import com.thesett.common.parsing.SourceCodeException;
import com.thesett.common.util.SizeableLinkedList;
import com.thesett.common.util.SizeableList;
import com.thesett.common.util.doublemaps.SymbolTable;

/**
 * VAMAICompiler compiles clauses down to VAMAI byte code.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Compile clauses to VAMAI byte code.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAMAICompiler extends BaseMachine
    implements LogicCompiler<Clause, VAMAICompiledClause, VAMAICompiledClause>
{
    /** Holds the symbol table field which holds the compiled vam AI code. */
    public static final String VAM_2P_CODE_SYMBOL_FIELD = "vam_ai_code";

    /** Holds the compiler output observer. */
    private LogicCompilerObserver<VAMAICompiledClause, VAMAICompiledClause> observer;

    /**
     * Creates a VAMAI compiler that uses the specified symbol table.
     *
     * @param symbolTable The symbol table for the compiler.
     * @param interner    The interner for the compiler.
     */
    public VAMAICompiler(SymbolTable<Integer, String, Object> symbolTable, VariableAndFunctorInterner interner)
    {
        super(symbolTable, interner);
    }

    /** {@inheritDoc} */
    public void compile(Sentence<Clause> sentence) throws SourceCodeException
    {
        // Extract the clause to compile from the parsed sentence.
        Clause clause = sentence.getT();

        // Use a depth first walk over the syntax tree to generate the VAMAI instructions for it.
        SizeableList<VAMAIInstruction> results = new SizeableLinkedList<VAMAIInstruction>();

        PositionalTermTraverser traverser = new PositionalTermTraverserImpl();
        VAMAIInstructionGeneratingVisitor instructionGenerator =
            new VAMAIInstructionGeneratingVisitor(interner, symbolTable, traverser, results);
        traverser.setContextChangeVisitor(instructionGenerator);

        TermWalker walker =
            new TermWalker(new DepthFirstBacktrackingSearch<Term, Term>(), traverser, instructionGenerator);

        walker.walk(clause);

        // Create a compiled clause to hold the output of the compilation in and store the compiled instructions
        // in the symbol table for the clause.
        VAMAICompiledClause compiledClause =
            new VAMAICompiledClause(clause.getHead().getName(), clause.getSymbolKey(), results);
        symbolTable.put(clause.getSymbolKey(), VAM_2P_CODE_SYMBOL_FIELD, compiledClause);

        if (clause.isQuery())
        {
            observer.onQueryCompilation(compiledClause);
        }
        else
        {
            observer.onCompilation(compiledClause);
        }
    }

    /** {@inheritDoc} */
    public void setCompilerObserver(LogicCompilerObserver<VAMAICompiledClause, VAMAICompiledClause> observer)
    {
        this.observer = observer;
    }

    /** {@inheritDoc} */
    public void endScope()
    {
    }
}
