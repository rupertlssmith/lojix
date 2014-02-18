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
package com.thesett.aima.logic.fol.vam.vam2p;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.thesett.aima.logic.fol.LinkageException;
import com.thesett.aima.logic.fol.Resolver;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.bytecode.BaseByteArrayCodeMachine;
import com.thesett.aima.logic.fol.vam.VAMInstructionCodes;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.VAM2PInstruction;
import com.thesett.common.util.doublemaps.SymbolTable;

/**
 * VAM2PResolver implements a {@link Resolver} over clauses compiled to VAM2P byte code.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Execute compiled VAM2P programs and queries.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAM2PResolver extends BaseByteArrayCodeMachine<VAM2PInstruction, VAM2PCompiledClause>
    implements Resolver<VAM2PCompiledClause, VAM2PCompiledClause>
{
    /** Defines the name of the symbol table field for holding call points. */
    public static final String CALL_POINTS_SYMBOL_FIELD = "vam2pCallPoints";

    /** Defines the environment stack size to use for the virtual machine. */
    private static final int ENVIRONMENT_STACK_SIZE = 10000;

    /** Defines the copy stack size to use for the virtual machine. */
    public static final int COPY_STACK_SIZE = 10000;

    /** Defines the variable binding trail size to use for the virtual machine. */
    private static final int TRAIL_SIZE = 10000;

    /** Defines the highest address in the data area of the virtual machine. */
    private static final int TOP = ENVIRONMENT_STACK_SIZE + COPY_STACK_SIZE + TRAIL_SIZE;

    /** Holds the enire data segment of the machine. All registers, heaps and stacks are held in here. */
    private int[] data;

    /** Holds the current goal instruction pointer into the code. */
    private int gip;

    /** Holds the current head instruction pointer into the code. */
    private int hip;

    /** Holds the frame pointer for the current goal. */
    protected int gfp;

    /** Holds the frame pointer for the current head. */
    protected int hfp;

    /** Holds the environment stack pointer. */
    protected int esp;

    /** Holds the copy stack pointer. */
    protected int csp;

    /** Holds the trail stack pointer. */
    protected int tsp;

    /** Holds the last choice pointer. */
    protected int lcp;

    /** Holds the current query. */
    VAM2PCompiledClause query;

    /**
     * Initialized the machine.
     *
     * @param symbolTable The symbol table for the machine.
     * @param interner    The symbol interner for the machine.
     */
    public VAM2PResolver(SymbolTable<Integer, String, Object> symbolTable, VariableAndFunctorInterner interner)
    {
        super(symbolTable, interner, new VAM2PInstructionEncoder());

        reset();
    }

    /** {@inheritDoc} */
    public String getCallPointSymbolField()
    {
        return CALL_POINTS_SYMBOL_FIELD;
    }

    /** {@inheritDoc} */
    public void addToDomain(VAM2PCompiledClause term) throws LinkageException
    {
        // Write the byte code for the compiled term into the machine.
        emmitCode(term);
    }

    /** {@inheritDoc} */
    public void setQuery(VAM2PCompiledClause query) throws LinkageException
    {
        this.query = query;
    }

    /** {@inheritDoc} */
    public Set<Variable> resolve()
    {
        execute();

        return new HashSet<Variable>();
    }

    /** {@inheritDoc} */
    public void reset()
    {
        super.reset();

        this.query = null;

        // Create fresh heaps and stacks.
        data = new int[TOP];
    }

    /** {@inheritDoc} */
    public Iterator<Set<Variable>> iterator()
    {
        return new LinkedList<Set<Variable>>().iterator();
    }

    /** Executes the current query against the domain. */
    private void execute()
    {
        /*List<VAM2PInstruction> ginstructions = query.instructions;
        VAM2PInstruction gip = ginstructions.get(0);*/

        int headInstr = code[hip];
        int goalInstr = code[gip];

        boolean failed = false;

        while (!failed)
        {
            switch (headInstr + goalInstr)
            {
            case VAMInstructionCodes.GOAL + VAMInstructionCodes.CALL:

                continue;

            case VAMInstructionCodes.CUT + VAMInstructionCodes.LASTCALL:
            case VAMInstructionCodes.CUT + VAMInstructionCodes.CALL:

                continue;

            case VAMInstructionCodes.GOAL + VAMInstructionCodes.LASTCALL:

                continue;

            case VAMInstructionCodes.NOGOAL + VAMInstructionCodes.LASTCALL:

            case VAMInstructionCodes.NOGOAL + VAMInstructionCodes.CALL:

                for (;;)
                {
                    switch (gip++)
                    {
                    case VAMInstructionCodes.CUT:

                        continue;

                    case VAMInstructionCodes.NOGOAL:

                        continue;

                    case VAMInstructionCodes.GOAL:
                    default:
                    }

                    break;
                }

            case VAMInstructionCodes.NIL + VAMInstructionCodes.NIL:
            case VAMInstructionCodes.LIST + VAMInstructionCodes.LIST:

                continue;

            case VAMInstructionCodes.CONST + VAMInstructionCodes.CONST:
            case VAMInstructionCodes.STRUCT + VAMInstructionCodes.STRUCT:

                if (gip++ == hip++)
                {
                    continue;
                }

            case VAMInstructionCodes.VOID + VAMInstructionCodes.VOID:
            case VAMInstructionCodes.FIRST_VAR + VAMInstructionCodes.FIRST_VAR:
            case VAMInstructionCodes.FIRST_VAR + VAMInstructionCodes.NEXT_VAR:
            case VAMInstructionCodes.CONST + VAMInstructionCodes.FIRST_VAR:
            case VAMInstructionCodes.CONST + VAMInstructionCodes.NEXT_VAR:
            case VAMInstructionCodes.NEXT_VAR + VAMInstructionCodes.NEXT_VAR:
            case VAMInstructionCodes.VOID + VAMInstructionCodes.STRUCT:
            case VAMInstructionCodes.STRUCT + VAMInstructionCodes.FIRST_VAR:

            default:

            }
        }
    }
}
