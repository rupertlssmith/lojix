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

import com.thesett.common.util.SizeableList;

/**
 * InstructionListing is generally implemented by compiled entities that are capable of providing a listing of their
 * instructions of objects.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide a listing of instructions over some instruction set.
 * </table></pre>
 *
 * @param  <I> The type of instructions that the listing is over.
 *
 * @author Rupert Smith
 */
public interface InstructionListing<I>
{
    /**
     * Provides the interned name of the procedure that this is an instruction listing for.
     *
     * @return The interned name of the procedure that this is an instruction listing for.
     */
    public int getName();

    /**
     * Provides the instruction listing for the compiled entity.
     *
     * @return The instruction listing for the compiled entity.
     */
    public SizeableList<I> getInstructions();
}
