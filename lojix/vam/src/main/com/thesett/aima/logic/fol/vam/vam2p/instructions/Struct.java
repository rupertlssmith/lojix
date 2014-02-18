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
package com.thesett.aima.logic.fol.vam.vam2p.instructions;

import com.thesett.common.util.visitor.Visitor;

/**
 * Struct is a VAM2P instruction.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Encode a VAM2P instruction.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Struct extends VAM2PInstruction
{
    /**
     * Creates a VAM2P instruction with the specified argument.
     *
     * @param name The name of the functor argument.
     */
    public Struct(int name)
    {
        super(VAM2PInstructionSet.Struct);
    }

    /** {@inheritDoc} */
    public void accept(Visitor<VAM2PInstruction> visitor)
    {
        if (visitor instanceof StructVisitor)
        {
            ((StructVisitor) visitor).visit(this);
        }
        else
        {
            super.accept(visitor);
        }
    }
}
