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

import java.nio.ByteBuffer;

import com.thesett.aima.logic.fol.bytecode.CallPointResolver;
import com.thesett.aima.logic.fol.bytecode.InstructionEncoder;
import com.thesett.aima.logic.fol.vam.VAMInstructionCodes;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.Call;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.CallVisitor;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.Const;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.ConstVisitor;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.FirstVar;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.FirstVarVisitor;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.LastCall;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.LastCallVisitor;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.NextVar;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.NextVarVisitor;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.Struct;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.StructVisitor;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.VAM2PInstruction;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.VoidVisitor;

/**
 * VAM2PInstructionEncoder encodes VAM 2p instructions into their binary, byte-coded form.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Encode VAM 2p instructions in binary.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAM2PInstructionEncoder implements InstructionEncoder<VAM2PInstruction>, CallVisitor, ConstVisitor,
    FirstVarVisitor, LastCallVisitor, NextVarVisitor, StructVisitor, VoidVisitor
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
    public void visit(Call instruction)
    {
        buffer.putShort(VAMInstructionCodes.CALL);
    }

    /** {@inheritDoc} */
    public void visit(Const instruction)
    {
        buffer.putShort(VAMInstructionCodes.CONST);
    }

    /** {@inheritDoc} */
    public void visit(FirstVar instruction)
    {
        buffer.putShort(VAMInstructionCodes.FIRST_VAR);
    }

    /** {@inheritDoc} */
    public void visit(LastCall instruction)
    {
        buffer.putShort(VAMInstructionCodes.LASTCALL);
    }

    /** {@inheritDoc} */
    public void visit(NextVar instruction)
    {
        buffer.putShort(VAMInstructionCodes.NEXT_VAR);
    }

    /** {@inheritDoc} */
    public void visit(Struct instruction)
    {
        buffer.putShort(VAMInstructionCodes.STRUCT);
    }

    /** {@inheritDoc} */
    public void visit(com.thesett.aima.logic.fol.vam.vam2p.instructions.Void instruction)
    {
        buffer.putShort(VAMInstructionCodes.VOID);
    }

    /** {@inheritDoc} */
    public void visit(VAM2PInstruction instruction)
    {
    }
}
