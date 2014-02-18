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

import java.nio.ByteBuffer;

import com.thesett.aima.logic.fol.bytecode.CallPointResolver;
import com.thesett.aima.logic.fol.bytecode.InstructionEncoder;
import com.thesett.aima.logic.fol.vam.VAMInstructionCodes;
import com.thesett.aima.logic.fol.vam.vamai.instructions.Atom;
import com.thesett.aima.logic.fol.vam.vamai.instructions.AtomVisitor;
import com.thesett.aima.logic.fol.vam.vamai.instructions.Call;
import com.thesett.aima.logic.fol.vam.vamai.instructions.CallVisitor;
import com.thesett.aima.logic.fol.vam.vamai.instructions.FirstTemp;
import com.thesett.aima.logic.fol.vam.vamai.instructions.FirstTempVisitor;
import com.thesett.aima.logic.fol.vam.vamai.instructions.FirstVar;
import com.thesett.aima.logic.fol.vam.vamai.instructions.FirstVarVisitor;
import com.thesett.aima.logic.fol.vam.vamai.instructions.Goal;
import com.thesett.aima.logic.fol.vam.vamai.instructions.GoalVisitor;
import com.thesett.aima.logic.fol.vam.vamai.instructions.Int;
import com.thesett.aima.logic.fol.vam.vamai.instructions.IntVisitor;
import com.thesett.aima.logic.fol.vam.vamai.instructions.NextTemp;
import com.thesett.aima.logic.fol.vam.vamai.instructions.NextTempVisitor;
import com.thesett.aima.logic.fol.vam.vamai.instructions.NextVar;
import com.thesett.aima.logic.fol.vam.vamai.instructions.NextVarVisitor;
import com.thesett.aima.logic.fol.vam.vamai.instructions.NoGoal;
import com.thesett.aima.logic.fol.vam.vamai.instructions.NoGoalVisitor;
import com.thesett.aima.logic.fol.vam.vamai.instructions.Struct;
import com.thesett.aima.logic.fol.vam.vamai.instructions.StructVisitor;
import com.thesett.aima.logic.fol.vam.vamai.instructions.VAMAIInstruction;
import com.thesett.aima.logic.fol.vam.vamai.instructions.VoidVisitor;

/**
 * VAM2PInstructionEncoder encodes VAM AI instructions into their binary, byte-coded form.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Encode VAM AI instructions in binary.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAMAIInstructionEncoder implements InstructionEncoder<VAMAIInstruction>, AtomVisitor, CallVisitor,
    FirstTempVisitor, FirstVarVisitor, GoalVisitor, IntVisitor, NextTempVisitor, NextVarVisitor, NoGoalVisitor,
    StructVisitor, VoidVisitor
{
    /** Holds the byte buffer to write encoded instructions to. */
    private ByteBuffer buffer;

    /** Holds the call point resolver for 'call' instructions. */
    private CallPointResolver callResolver;

    /** {@inheritDoc} */
    public void setCallPointResolver(CallPointResolver resolver)
    {
        this.callResolver = resolver;
    }

    /** {@inheritDoc} */
    public void setCodeBuffer(ByteBuffer buffer)
    {
        this.buffer = buffer;
    }

    /** {@inheritDoc} */
    public void visit(Atom instruction)
    {
        buffer.putShort(VAMInstructionCodes.ATOM);
        buffer.putInt(instruction.getArg());
    }

    /** {@inheritDoc} */
    public void visit(Call instruction)
    {
        buffer.putShort(VAMInstructionCodes.CALL);
    }

    /** {@inheritDoc} */
    public void visit(FirstTemp instruction)
    {
        buffer.putShort(VAMInstructionCodes.FIRST_TEMP);
        buffer.putInt(instruction.getArg());
    }

    /** {@inheritDoc} */
    public void visit(FirstVar instruction)
    {
        buffer.putShort(VAMInstructionCodes.FIRST_VAR);
        buffer.putInt(instruction.getArg());
        buffer.put(instruction.getType());
        buffer.putShort(instruction.getRefChainLength());
        buffer.put((byte) (instruction.isAliased() ? 1 : 0));
        buffer.put((byte) (instruction.isAliasable() ? 1 : 0));
    }

    /** {@inheritDoc} */
    public void visit(Goal instruction)
    {
        buffer.putShort(VAMInstructionCodes.GOAL);
        buffer.putInt(instruction.getArg());
        buffer.putInt(instruction.getContinuation());
    }

    /** {@inheritDoc} */
    public void visit(Int instruction)
    {
        buffer.putShort(VAMInstructionCodes.INT);
        buffer.putInt(instruction.getArg());
    }

    /** {@inheritDoc} */
    public void visit(NextTemp instruction)
    {
        buffer.putShort(VAMInstructionCodes.NEXT_TEMP);
        buffer.putInt(instruction.getArg());
    }

    /** {@inheritDoc} */
    public void visit(NextVar instruction)
    {
        buffer.putShort(VAMInstructionCodes.NEXT_VAR);
        buffer.putInt(instruction.getArg());
        buffer.put(instruction.getType());
        buffer.putShort(instruction.getRefChainLength());
        buffer.put((byte) (instruction.isAliased() ? 1 : 0));
        buffer.put((byte) (instruction.isAliasable() ? 1 : 0));
    }

    /** {@inheritDoc} */
    public void visit(NoGoal instruction)
    {
        buffer.putShort(VAMInstructionCodes.NOGOAL);
    }

    /** {@inheritDoc} */
    public void visit(Struct instruction)
    {
        buffer.putShort(VAMInstructionCodes.STRUCT);
        buffer.putInt(instruction.getArg());
    }

    /** {@inheritDoc} */
    public void visit(com.thesett.aima.logic.fol.vam.vamai.instructions.Void instruction)
    {
        buffer.putShort(VAMInstructionCodes.VOID);
    }

    /** {@inheritDoc} */
    public void visit(VAMAIInstruction instruction)
    {
    }
}
