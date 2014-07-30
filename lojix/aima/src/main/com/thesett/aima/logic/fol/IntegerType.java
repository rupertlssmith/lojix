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
 * IntegerType is the base type for all integer numeric literals.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Indicate that a term is an integer.
 * <tr><td> Indicate that a term is not a real number.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class IntegerType extends NumericType
{
    /**
     * Determines whether a number is an integer.
     *
     * @return <tt>true</tt> if a number is an integer, <tt>false</tt> otherwise.
     */
    public boolean isInteger()
    {
        return true;
    }

    /** {@inheritDoc} */
    public void accept(TermVisitor visitor)
    {
        if (visitor instanceof IntegerTypeVisitor)
        {
            ((IntegerTypeVisitor) visitor).visit(this);
        }
        else
        {
            super.accept(visitor);
        }
    }
}
