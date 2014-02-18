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
package com.thesett.aima.logic.fol.bytecode;

import java.nio.ByteBuffer;

import com.thesett.common.util.visitor.Visitor;

/**
 * InstructionEncoder encapsulates a visitor over an instruction set, that can be directed to output the instructions in
 * binary format into a specified byte buffer.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Accept a call point resolver to resolve addresses for 'call' instructions.
 * <tr><td> Write binary code into a byte buffer over an instruction set.
 * </table></pre>
 *
 * @param  <I> The instruction type that this encoder works over.
 *
 * @author Rupert Smith
 */
public interface InstructionEncoder<I> extends Visitor<I>
{
    /**
     * Establishes a call point resolver on the encoder, that it can use to resolve the targets of 'call' instructions
     * onto machine addresses.
     *
     * @param resolver The call point resolver.
     */
    public void setCallPointResolver(CallPointResolver resolver);

    /**
     * Provides a buffer to the encoder, to which it should write its encoded binary instructions.
     *
     * @param buffer The bufer to output to.
     */
    public void setCodeBuffer(ByteBuffer buffer);
}
