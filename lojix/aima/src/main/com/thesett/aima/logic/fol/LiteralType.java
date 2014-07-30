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
package com.thesett.aima.logic.fol;

/**
 * LiteralType is a base class for defining literals. Literals are not functors, atoms or variables or compound. They
 * are constant ground types.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Type as non-functor, non-atom, non-variable and non-compound.
 * <tr><td> Type as constant and ground.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class LiteralType extends BaseTerm
{
    /**
     * Reports whether or not this term is a constant (a number of a functor of arity zero).
     *
     * @return Always <tt>true</tt>.
     */
    public boolean isConstant()
    {
        return true;
    }

    /**
     * Reports whether or not this term is a ground term.
     *
     * @return Always <tt>true</tt>.
     */
    public boolean isGround()
    {
        return true;
    }

    /** {@inheritDoc} */
    public void accept(TermVisitor visitor)
    {
        if (visitor instanceof LiteralTypeVisitor)
        {
            ((LiteralTypeVisitor) visitor).visit(this);
        }
        else
        {
            super.accept(visitor);
        }
    }
}
