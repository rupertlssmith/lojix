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
package com.thesett.aima.logic.fol;

/**
 * IntegerType is the base type for all integer numeric literals.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Indicate that a term is a real number.
 * <tr><td> Indicate that a term is not an integer.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class RealType extends NumericType
{
    /**
     * Determines whether a number is a real.
     *
     * @return <tt>true</tt> if a number is a real, <tt>false</tt> otherwise.
     */
    public boolean isReal()
    {
        return true;
    }

    /**
     * Determines whether a number is of basic type int.
     *
     * @return <tt>true</tt> if a number is an int, <tt>false</tt> otherwise.
     */
    public boolean isInt()
    {
        return false;
    }

    /**
     * Determines whether a number is of basic type long.
     *
     * @return <tt>true</tt> if a number is a long, <tt>false</tt> otherwise.
     */
    public boolean isLong()
    {
        return false;
    }
}
