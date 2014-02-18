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

import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.bytecode.InstructionListing;
import com.thesett.aima.logic.fol.vam.vam2p.instructions.VAM2PInstruction;
import com.thesett.common.util.SizeableList;
import com.thesett.common.util.doublemaps.SymbolKey;

/**
 * VAM2PCompiledFunctor provides access to the byte code for a clause compiled into the VAM2P instruction set.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide VAM2P byte code for a clause.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAM2PCompiledClause implements Sentence<VAM2PCompiledClause>, InstructionListing<VAM2PInstruction>
{
    /** Holds the interned name of the clause. */
    private int name;

    /** Holds the symbol key of the clause that this is a compiled version of. */
    private SymbolKey symbolKey;

    /** Holds the compiled instructions for the clause. */
    private SizeableList<VAM2PInstruction> instructions;

    /**
     * Creates a compiled clause for the clause with the specified symbol key, consisting of the specified instruction
     * sequence.
     *
     * @param name         The interned name of the clause.
     * @param symbolKey    The symbol key of the clause that this is a compiled version of.
     * @param instructions The compiled instructions for the clause.
     */
    public VAM2PCompiledClause(int name, SymbolKey symbolKey, SizeableList<VAM2PInstruction> instructions)
    {
        this.name = name;
        this.symbolKey = symbolKey;
        this.instructions = instructions;
    }

    /** {@inheritDoc} */
    public VAM2PCompiledClause getT()
    {
        return this;
    }

    /** {@inheritDoc} */
    public SizeableList<VAM2PInstruction> getInstructions()
    {
        return instructions;
    }

    /** {@inheritDoc} */
    public int getName()
    {
        return name;
    }
}
