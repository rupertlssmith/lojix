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
 * A CallPointResolver is capable of resolving a call point (callable address) from the interned named of a procedure or
 * block of code. It is typically used by a code generator, to resolve 'call' instructions which are defined over the
 * high level interned names of callable procedures, onto the actuall call addresses when inserting code into a binary
 * machine.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Resolve an interned name onto a call point.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface CallPointResolver
{
    /**
     * Resolves a call point for the interned name of a callable procedure that may exist in the code that the machine
     * already holds.
     *
     * @param  name The interned name of the procedure to call.
     *
     * @return The call point of the callable procedure, or <tt>null</tt> if none exists for the specified name.
     */
    CallPoint resolveCallPoint(int name);
}
