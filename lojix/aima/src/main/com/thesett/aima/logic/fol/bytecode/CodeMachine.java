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

import com.thesett.aima.logic.fol.LinkageException;

/**
 * CodeMachine is a base type for deriving abstract machines with a binary code area. It provides the ability to keep
 * track of the size of the code area, where the next available point to add code the machine is, and to encode byte
 * code into the machine using an appropriate encoder.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Accept byte code into the machine.
 * </table></pre>
 *
 * @param  <C> The opaque type of compiled entities that this code machine accepts.
 *
 * @author Rupert Smith
 */
public interface CodeMachine<C>
{
    /**
     * Emmits byte code in binary format into the machine for a compiled entity.
     *
     * @param  compiled The compiled entity to emmit byte code into the machine for.
     *
     * @throws com.thesett.aima.logic.fol.LinkageException If the entity to write to the machine cannot be added to it,
     *                                                     because it depends on the existance of other callable
     *                                                     entities which are not in the machine. Implementations may
     *                                                     elect to raise this as an error now, at the time the entity
     *                                                     is added to the machine, or later during execution, depending
     *                                                     on what works for the language being encoded.
     */
    public void emmitCode(C compiled) throws LinkageException;
}
