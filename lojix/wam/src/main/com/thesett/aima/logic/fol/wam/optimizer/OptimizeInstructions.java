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
package com.thesett.aima.logic.fol.wam.optimizer;

import java.util.LinkedList;

import com.thesett.aima.logic.fol.wam.WAMCompiler;
import com.thesett.aima.logic.fol.wam.WAMInstruction;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.GetConstant;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.GetList;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.GetStruc;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.GetVar;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.PutConstant;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.PutList;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.PutStruc;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.SetConstant;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.SetVar;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.SetVoid;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.UnifyConstant;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.UnifyVar;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.UnifyVoid;
import com.thesett.common.util.doublemaps.SymbolKey;
import com.thesett.common.util.doublemaps.SymbolTable;

/**
 * Performs an optimization pass for specialized constant instructions.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Optimize constant instructions in the head of a clause.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class OptimizeInstructions implements StateMachine<WAMInstruction, WAMInstruction>
{
    /** Used for debugging. */
    private static final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(OptimizeInstructions.class.getName());

    /** Defines the possible states that this state machine can be in. */
    private enum State
    {
        /** No Match. */
        NM,

        /** UnifyVar instruction seen. */
        UV,

        /** PutStruc instruction seen. */
        PS,

        /** UnifyVar to UnifyVoid elimination. */
        UVE,

        /** SetVar to SetVoid elimination. */
        SVE;
    }

    /** Holds the matcher that is driving this state machine. */
    private Matcher<WAMInstruction, WAMInstruction> matcher;

    /** Holds the current state machine state. */
    private State state = State.NM;

    /** Holds the last instruction seen. */
    private WAMInstruction last;

    /** Holds a buffer of pending instructions to output. */
    private LinkedList<WAMInstruction> buffer = new LinkedList<WAMInstruction>();

    /** The symbol table. */
    protected final SymbolTable<Integer, String, Object> symbolTable;

    /** Counts the number of void variables seen in a row. */
    private int voidCount = 0;

    /**
     * Builds an instruction optimizer.
     *
     * @param symbolTable The symbol table to get instruction analysis from.
     */
    public OptimizeInstructions(SymbolTable<Integer, String, Object> symbolTable)
    {
        this.symbolTable = symbolTable;
    }

    /** {@inheritDoc} */
    public void apply(WAMInstruction next)
    {
        shift(next);

        // Anonymous or singleton variable optimizations.
        if ((UnifyVar == next.getMnemonic()) && isSingletonNonArgVariable(next))
        {
            if (state != State.UVE)
            {
                state = State.UVE;
                voidCount = 0;
            }

            discard((voidCount == 0) ? 1 : 2);

            WAMInstruction unifyVoid = new WAMInstruction(UnifyVoid, WAMInstruction.REG_ADDR, (byte) ++voidCount);
            shift(unifyVoid);

            log.fine(next + " -> " + unifyVoid);
        }
        else if ((SetVar == next.getMnemonic()) && isSingletonNonArgVariable(next))
        {
            if (state != State.SVE)
            {
                state = State.SVE;
                voidCount = 0;
            }

            discard((voidCount == 0) ? 1 : 2);

            WAMInstruction setVoid = new WAMInstruction(SetVoid, WAMInstruction.REG_ADDR, (byte) ++voidCount);
            shift(setVoid);

            log.fine(next + " -> " + setVoid);
        }
        else if ((GetVar == next.getMnemonic()) && (next.getMode1() == WAMInstruction.REG_ADDR) &&
                (next.getReg1() == next.getReg2()))
        {
            discard(1);

            log.fine(next + " -> eliminated");
        }

        // Constant optimizations.
        else if (UnifyVar == next.getMnemonic())
        {
            state = State.UV;
            last = next;
        }
        else if ((GetStruc == next.getMnemonic()) && (state == State.UV) && (last.getReg1() == next.getReg1()) &&
                (next.getFn().getArity() == 0))
        {
            discard(2);

            WAMInstruction unifyConst = new WAMInstruction(UnifyConstant, next.getFn());
            shift(unifyConst);
            flush();
            state = State.NM;

            log.fine(last + ", " + next + " -> " + unifyConst);
        }
        else if ((GetStruc == next.getMnemonic()) && (next.getFn().getArity() == 0))
        {
            discard(1);

            WAMInstruction getConst = new WAMInstruction(GetConstant, next.getMode1(), next.getReg1(), next.getFn());
            shift(getConst);
            flush();
            state = State.NM;

            log.fine(next + " -> " + getConst);
        }
        else if ((state == State.NM) && (PutStruc == next.getMnemonic()) && (next.getFn().getArity() == 0))
        {
            state = State.PS;
            last = next;
        }
        else if ((state == State.PS) && (SetVar == next.getMnemonic()) && (last.getReg1() == next.getReg1()))
        {
            discard(2);

            WAMInstruction setConst = new WAMInstruction(SetConstant, next.getFn());
            shift(setConst);
            flush();
            state = State.NM;

            log.fine(last + ", " + next + " -> " + setConst);
        }
        else if (state == State.PS)
        {
            discard(2);

            WAMInstruction putConst = new WAMInstruction(PutConstant, last.getMode1(), last.getReg1(), last.getFn());
            shift(putConst);

            log.fine(last + " -> " + putConst);

            if ((PutStruc == next.getMnemonic()) && (next.getFn().getArity() == 0))
            {
                last = next;
                shift(last);
            }
            else
            {
                state = State.NM;
                apply(next);
            }
        }

        // List optimizations.
        else if ((GetStruc == next.getMnemonic()) &&
                ("cons".equals(next.getFn().getName()) && (next.getFn().getArity() == 2)))
        {
            discard(1);

            WAMInstruction getList = new WAMInstruction(GetList, next.getMode1(), next.getReg1());
            shift(getList);

            log.fine(next + " -> " + getList);
        }
        else if ((PutStruc == next.getMnemonic()) &&
                ("cons".equals(next.getFn().getName()) && (next.getFn().getArity() == 2)))
        {
            discard(1);

            WAMInstruction putList = new WAMInstruction(PutList, next.getMode1(), next.getReg1());
            shift(putList);

            log.fine(next + " -> " + putList);
        }

        // Default.
        else
        {
            state = State.NM;
            flush();
        }
    }

    /** {@inheritDoc} */
    public void end()
    {
        flush();
    }

    /** {@inheritDoc} */
    public void setMatcher(Matcher<WAMInstruction, WAMInstruction> matcher)
    {
        this.matcher = matcher;
    }

    /**
     * Checks if the term argument to an instruction was a singleton, non-argument position variable.
     *
     * @param  instruction The instruction to test.
     *
     * @return <tt>true</tt> iff the term argument to the instruction was a singleton, non-argument position variable.
     */
    private boolean isSingletonNonArgVariable(WAMInstruction instruction)
    {
        SymbolKey symbolKey = instruction.getSymbolKeyReg1();

        if (symbolKey != null)
        {
            Integer count = (Integer) symbolTable.get(symbolKey, WAMCompiler.SYMKEY_VAR_OCCURRENCE_COUNT);
            Boolean nonArgPositionOnly = (Boolean) symbolTable.get(symbolKey, WAMCompiler.SYMKEY_VAR_NON_ARG);

            if ((count != null) && count.equals(1) && (nonArgPositionOnly != null) && nonArgPositionOnly.equals(true))
            {
                return true;
            }
        }

        return false;
    }

    private boolean isNonArgConstant(WAMInstruction instruction)
    {
        SymbolKey symbolKey = instruction.getSymbolKeyReg1();

        if (symbolKey != null)
        {
            Integer count = (Integer) symbolTable.get(symbolKey, WAMCompiler.SYMKEY_VAR_OCCURRENCE_COUNT);
            Boolean nonArgPositionOnly = (Boolean) symbolTable.get(symbolKey, WAMCompiler.SYMKEY_VAR_NON_ARG);

            if ((count != null) && count.equals(1) && (nonArgPositionOnly != null) && nonArgPositionOnly.equals(true))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Discards the specified number of most recent instructions from the output buffer.
     *
     * @param n The number of instructions to discard.
     */
    private void discard(int n)
    {
        for (int i = 0; i < n; i++)
        {
            buffer.pollLast();
        }
    }

    /**
     * Adds an instruction to the output buffer.
     *
     * @param instruction The instruction to add.
     */
    private void shift(WAMInstruction instruction)
    {
        buffer.offer(instruction);
    }

    /** Flushes the output buffer. */
    private void flush()
    {
        matcher.sinkAll(buffer);
    }
}
