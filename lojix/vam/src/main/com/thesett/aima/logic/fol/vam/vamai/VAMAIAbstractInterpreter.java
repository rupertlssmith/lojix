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

import com.thesett.aima.logic.fol.LinkageException;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.bytecode.BaseByteArrayCodeMachine;
import com.thesett.aima.logic.fol.bytecode.CallPoint;
import com.thesett.aima.logic.fol.vam.VAMInstructionCodes;
import com.thesett.aima.logic.fol.vam.vamai.instructions.VAMAIInstruction;
import com.thesett.common.util.ByteBufferUtils;
import com.thesett.common.util.doublemaps.SymbolTable;

/**
 * VAMAIAbstractInterpreter implements an abstract machine of the VAM AI instruction set, that performs an iterated
 * abstract interpretation of a logic program in order to derive properties of its execution that may be used to
 * generate more efficient exeuctable code for the program.
 *
 * <p/>The machine has two code pointers, one for the current goal being unified with, and one for the current head
 * being unified against.
 *
 * <p/>The machine has a single data area, which is used for two stacks. The call frame stack holds call points and the
 * abstract type domains for local variables. The variable trail stack is used to build up the full set of derived
 * information for variables. The two stacks grow from opposite ends of the data area.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAMAIAbstractInterpreter extends BaseByteArrayCodeMachine<VAMAIInstruction, VAMAICompiledClause>
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(VAMAIAbstractInterpreter.class.getName()); */

    /** Defines the name of the symbol table field for holding call points. */
    public static final String CALL_POINTS_SYMBOL_FIELD = "vamAiCallPoints";

    /** Defines the environment stack size to use for the virtual machine. */
    private static final int ENVIRONMENT_STACK_SIZE = 10000;

    /** Defines the variable binding trail size to use for the virtual machine. */
    private static final int TRAIL_SIZE = 10000;

    /** Defines the highest address in the data area of the virtual machine. */
    private static final int TOP = ENVIRONMENT_STACK_SIZE + TRAIL_SIZE;

    /** Holds the enire data segment of the machine. All registers, heaps and stacks are held in here. */
    private int[] data;

    /** Holds the current goal instruction pointer into the code. */
    private int gip;

    /** Holds the current head instruction pointer into the code. */
    private int hip;

    /** Holds a clause pointer for the next alternative clause in the code. */
    private int cip;

    /** Holds the frame pointer for the current goal. */
    protected int gfp;

    /** Holds the frame pointer for the current head. */
    protected int hfp;

    /** Holds the environment stack pointer. */
    protected int esp;

    /** Holds the trail stack pointer. */
    protected int tp;

    /**
     * Creates an empty VAM AI abstract interpreting machine.
     *
     * @param symbolTable The symbol table for the machine.
     * @param interner    The symbol interner for the machine.
     */
    public VAMAIAbstractInterpreter(SymbolTable<Integer, String, Object> symbolTable,
        VariableAndFunctorInterner interner)
    {
        super(symbolTable, interner, new VAMAIInstructionEncoder());

        reset();
    }

    /** {@inheritDoc} */
    public String getCallPointSymbolField()
    {
        return CALL_POINTS_SYMBOL_FIELD;
    }

    /** {@inheritDoc} */
    public void reset()
    {
        super.reset();

        // Create fresh heaps and stacks.
        data = new int[TOP];
    }

    /**
     * Adds a clause compiled to VAM AI instruction code to the abstract interpreter. Calculates the projection of the
     * logic program onto its abstract domain as a fixpoint.
     *
     * @param  clause The clause to add.
     *
     * @throws LinkageException If the clause cannot be added because its call points cannot be resolved within this
     *                          machine.
     */
    public void analyze(VAMAICompiledClause clause) throws LinkageException
    {
        // Write the byte code for the compiled clause into the machine.
        emmitCode(clause);

        // Start the analysis machine at the entry point of the newly added code.
        CallPoint callPoint = resolveCallPoint(clause.getName());

        // If the clause is a query, it will begin with a goal instruction. Locate the target of that
        // instruction and set it up as the current head to unify with, and take that instruction itself
        // as the current goal to unify against.

        // If the clause is a program, it will begin with a head, possibly followed by a body. Simulate calling the
        // head with unbound variables for every argument, to create a starting point to derive information about
        // more specialized invocations from.

        // Either way, start by winding forward to find the first 'goal' instruction.
        boolean firstGoalFound = locateFirstGoal(callPoint.entryPoint);

        if (firstGoalFound)
        {
            execute();
        }
    }

    /**
     * Scans forward from an entry point to the first 'goal' instruction. If a 'nogoal' is encountered before any 'goal'
     * is encountered, then the procedure must contain no goals.
     *
     * @param  entryPoint The initial entry point of the procedure to locate the first goal of.
     *
     * @return <tt>true</tt> if a goal is found. If this is the case, when this method returns, the {@link #gip} will
     *         point to the goal instructions, and the {@link #hip} will point to the target that it is calling.
     */
    private boolean locateFirstGoal(int entryPoint)
    {
        /*log.fine("private boolean locateFirstGoal(int entryPoint = " + entryPoint + "): called");*/

        gip = entryPoint;

        // Used to indicate that a goal instruction was found, before a nogoal was encountered. Start by assuming
        // that one will not be found.
        boolean goalFound = false;

        // Scan for a goal instruction, and set the 'gip' to point to it, and the 'hip' to point to its target.
        // If a 'nogoal' is encountered first, the scan will stop with 'goalFound' still set to false.
        while (true)
        {
            short instruction = ByteBufferUtils.getShortFromBytes(code, gip);
            VAMAIInstruction.VAMAIInstructionSet mnemonic = VAMAIInstruction.VAMAIInstructionSet.fromCode(instruction);

            /*log.fine(mnemonic.toString());*/

            if (VAMInstructionCodes.GOAL == instruction)
            {
                goalFound = true;
                hip = ByteBufferUtils.getIntFromBytes(code, gip + 2);

                /*log.fine("Found 'goal', hip = " + hip);*/

                break;
            }
            else if (VAMInstructionCodes.NOGOAL == instruction)
            {
                break;
            }

            gip += mnemonic.length();
        }

        return goalFound;
    }

    /** Calculates the projection of the logic program onto its abstract domain as a fixpoint. */
    private void execute()
    {
        /*log.fine("gip = " + gip);*/
        /*log.fine("hip = " + hip);*/

        // Used to indicate when the fix point has been reached and no further information about the abstract domain
        // can be derived.
        boolean fixPointReached = false;

        while (!fixPointReached)
        {
            short gInstruction = ByteBufferUtils.getShortFromBytes(code, gip);
            VAMAIInstruction.VAMAIInstructionSet gMnemonic =
                VAMAIInstruction.VAMAIInstructionSet.fromCode(gInstruction);

            short hInstruction = ByteBufferUtils.getShortFromBytes(code, hip);
            VAMAIInstruction.VAMAIInstructionSet hMnemonic =
                VAMAIInstruction.VAMAIInstructionSet.fromCode(hInstruction);

            /*log.fine("g-" + gMnemonic + "+h-" + hMnemonic);*/

            switch (gInstruction)
            {
            case VAMInstructionCodes.NOGOAL:

                fixPointReached = true;
                break;

            case VAMInstructionCodes.GOAL:
            case VAMInstructionCodes.ATOM:
            case VAMInstructionCodes.INT:
            case VAMInstructionCodes.STRUCT:
            case VAMInstructionCodes.CALL:
            case VAMInstructionCodes.VOID:
            case VAMInstructionCodes.FIRST_TEMP:
            case VAMInstructionCodes.FIRST_VAR:
            case VAMInstructionCodes.NEXT_TEMP:
            case VAMInstructionCodes.NEXT_VAR:

                gip += gMnemonic.length();

                break;

            default:
                fixPointReached = true;
                break;
            }
        }
    }

    /** Pushes a new variable entry onto the trail. */
    private void pushTrail()
    {
    }

    /** Pops a variable entry off of the trail. */
    private void popTrail()
    {
    }

    /** Clears the entire variable trail back to zero. */
    private void clearTrail()
    {
    }

    /**
     * Pushes a new stack frame and choice point onto the call stack.
     *
     * @param numLocals The number of local variables to allocate in the frame.
     */
    private void pushFrame(int numLocals)
    {
    }

    /** Pops a stack frame and choice point off of th call stack. */
    private void popFrame()
    {
    }

    /** Clears the entire call stack back to zero. */
    private void clearFrames()
    {
    }

    /** Unifies the abstract domains of two variables. */
    private void unify()
    {
    }

    /** Calculates the least upper bound of the domains of two variables. */
    private void lub()
    {
    }
}
