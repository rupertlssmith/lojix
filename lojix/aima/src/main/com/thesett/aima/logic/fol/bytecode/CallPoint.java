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

/**
 * CallPoint holds the interned name, entry address and size of a block of callable code.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Record the size and entry point of the byte code for a procedure.
 * <tr><td> Record the procedures interned name.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class CallPoint
{
    /** The procedures entry point. */
    public int entryPoint;

    /** The procedures length. */
    public int length;

    /** The procedures interned name. */
    public int name;

    /**
     * Creates a call table entry for the code with the specified entry address and length.
     *
     * @param entryPoint The entry address of the code.
     * @param length     The length of the procedure at the address.
     * @param name       The interned name of the block of code.
     */
    public CallPoint(int entryPoint, int length, int name)
    {
        this.entryPoint = entryPoint;
        this.length = length;
        this.name = name;
    }
}
