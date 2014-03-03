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

import com.thesett.aima.logic.fol.wam.WAMInstruction;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.GetConstant;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.GetStruc;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.PutConstant;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.PutStruc;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.SetConstant;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.SetVar;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.UnifyConstant;
import static com.thesett.aima.logic.fol.wam.WAMInstruction.WAMInstructionSet.UnifyVar;

/**
 * Performs an optimization pass for constants on instructions in the head of a clause.
 *
 * <p/>The following instruction sequences can be optimized:
 *
 * <pre>
 * unify_var Xi
 * get_struc a/0, Xi
 * -> unify_const a/0
 * </pre>
 *
 * <pre>
 * get_struc a/0, Xi
 * -> get_const a/0
 * </pre>
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Optimize constant instructions in the head of a clause.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class OptimizeConstants implements StateMachine<WAMInstruction, WAMInstruction>
{
    private enum State
    {
        NM, UV, PS;
    }

    private Matcher<WAMInstruction, WAMInstruction> matcher;

    private State state = State.NM;

    private WAMInstruction last;

    private LinkedList<WAMInstruction> shifts = new LinkedList<WAMInstruction>();

    /** {@inheritDoc} */
    public void apply(WAMInstruction next)
    {
        shift(next);

        if (UnifyVar == next.getMnemonic())
        {
            state = State.UV;
            last = next;
        }
        else if ((GetStruc == next.getMnemonic()) && (state == State.UV) && (last.getReg1() == next.getReg1()) &&
                (next.getFn().getArity() == 0))
        {
            discard(2);
            shift(new WAMInstruction(UnifyConstant, next.getFn()));
            flush();
            state = State.NM;
        }
        else if ((GetStruc == next.getMnemonic()) && (next.getFn().getArity() == 0))
        {
            discard(1);
            shift(new WAMInstruction(GetConstant, next.getMode1(), next.getReg1(), next.getFn()));
            flush();
            state = State.NM;
        }
        else if ((state == State.NM) && (PutStruc == next.getMnemonic()) && (next.getFn().getArity() == 0))
        {
            state = State.PS;
            last = next;
        }
        else if ((state == State.PS) && (SetVar == next.getMnemonic()) && (last.getReg1() == next.getReg1()))
        {
            discard(2);
            shift(new WAMInstruction(SetConstant, next.getFn()));
            flush();
            state = State.NM;
        }
        else if (state == State.PS)
        {
            discard(2);
            shift(new WAMInstruction(PutConstant, last.getMode1(), last.getReg1(), last.getFn()));

            if ((PutStruc == next.getMnemonic()) && (next.getFn().getArity() == 0))
            {
                last = next;
                shift(last);
            }
            else
            {
                shift(next);
                flush();
                state = State.NM;
            }
        }
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

    public void setMatcher(Matcher<WAMInstruction, WAMInstruction> matcher)
    {
        this.matcher = matcher;
    }

    private void discard(int n)
    {
        for (int i = 0; i < n; i++)
        {
            shifts.pollLast();
        }
    }

    private void discard()
    {
        shifts.clear();
    }

    private void shift(WAMInstruction next)
    {
        shifts.offer(next);
    }

    private void flush()
    {
        matcher.buffer(shifts);
    }
}
