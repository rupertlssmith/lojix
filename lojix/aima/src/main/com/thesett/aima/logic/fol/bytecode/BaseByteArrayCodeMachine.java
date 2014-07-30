/*
 * Copyright The Sett Ltd, 2005 to 2014.
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
package com.thesett.aima.logic.fol.bytecode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.common.util.doublemaps.SymbolTable;
import com.thesett.common.util.visitor.Acceptor;

/**
 * BaseByteArrayCodeMachine is a basis for implements byte code interpreters that hold their byte code in a byte arry on
 * the Java heap.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide a byte buffer and current insertion point for new code.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BaseByteArrayCodeMachine<I extends Acceptor<I>, C extends InstructionListing<I>>
    extends BaseCodeMachine<I, C>
{
    /** Defines the code area size to use for the abstract machine. */
    public static final int CODE_SIZE = 10000;

    /** Holds the byte code. */
    protected byte[] code;

    /** Holds the current load offset within the code area. */
    private int loadPoint;

    /**
     * Creates the base machine.
     *
     * @param symbolTable        The symbol table for the machine.
     * @param interner           The symbol interner for the machine.
     * @param instructionEncoder The binary instruction encoder for the machine.
     */
    public BaseByteArrayCodeMachine(SymbolTable<Integer, String, Object> symbolTable,
        VariableAndFunctorInterner interner, InstructionEncoder<I> instructionEncoder)
    {
        super(symbolTable, interner, instructionEncoder);

        reset();
    }

    /** {@inheritDoc} */
    public ByteBuffer getCodeBuffer(CallPoint callPoint)
    {
        ByteBuffer buffer = ByteBuffer.wrap(code, callPoint.entryPoint, callPoint.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        return buffer;
    }

    /** {@inheritDoc} */
    public int getCodeInsertionPoint()
    {
        return loadPoint;
    }

    /** {@inheritDoc} */
    public void advanceCodeInsertionPoint(int length)
    {
        loadPoint += length;
    }

    /** Resets the machine, clearing out its code area. */
    public void reset()
    {
        code = new byte[CODE_SIZE];
        loadPoint = 0;
    }
}
