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
package com.thesett.aima.logic.fol.vam.vamai.instructions;

import com.thesett.common.util.visitor.Visitor;

/**
 * Int is a VAMAI instruction.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Encode a VAM AI instruction.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class NextVar extends VAMAIInstruction
{
    /** Holds the interned name of the variable. */
    private int arg;

    /** Holds the bit set encoded type of the variable. */
    private byte type;

    /** Holds the reference chain length of the variable. */
    private short refChainLength;

    /** Holds a flag to indicate if the variable is aliased. */
    private boolean aliased;

    /** Holds a flag to indicate if the variable can be aliased. */
    private boolean aliasable;

    /**
     * Creates a VAM AI instruction with the specified variable argument.
     *
     * @param var            The variable argument.
     * @param type           The type of the variable.
     * @param refChainLength The length of the variables reference chain.
     * @param aliased        <tt>true</tt> if the variable is aliased.
     * @param aliasable      <tt>true</tt> if the variable can be aliased.
     */
    public NextVar(int var, byte type, short refChainLength, boolean aliased, boolean aliasable)
    {
        super(VAMAIInstructionSet.NextVar);

        this.arg = var;
        this.type = type;
        this.refChainLength = refChainLength;
        this.aliased = aliased;
        this.aliasable = aliasable;
    }

    /** {@inheritDoc} */
    public void accept(Visitor<VAMAIInstruction> visitor)
    {
        if (visitor instanceof NextVarVisitor)
        {
            ((NextVarVisitor) visitor).visit(this);
        }
        else
        {
            super.accept(visitor);
        }
    }

    /**
     * Provides the interned name of the variable.
     *
     * @return The interned name of the variable.
     */
    public int getArg()
    {
        return arg;
    }

    /**
     * Provides the bit-set encoded type domain of the variable.
     *
     * @return The bit-set encoded type domain of the variable.
     */
    public byte getType()
    {
        return type;
    }

    /**
     * Provides the reference chain length of the variable.
     *
     * @return The reference chain length of the variable.
     */
    public short getRefChainLength()
    {
        return refChainLength;
    }

    /**
     * Indicates if the variable is aliased.
     *
     * @return <tt>true</tt> if the variable is aliased.
     */
    public boolean isAliased()
    {
        return aliased;
    }

    /**
     * Indicates if the variable can be aliased.
     *
     * @return <tt>true</tt> if the variable can be aliased.
     */
    public boolean isAliasable()
    {
        return aliasable;
    }
}
