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
 * NumericLiteral extends the basic syntax of first order logic into arithmetic. Although this seems like a natural and
 * usefull extension, it significantly alters the shape of the mathematical space occupied by the language by
 * introducing the possibilities of infinity and induction. These change the language from a decidable one (in which all
 * true statement can be proven) to an undecidable one.
 *
 * <p/>It is worth noing that the numerical types based on primitive Java or machine register types, are all of finite
 * size, therefore infinity is not really introduced by these types. A big integer type would certainly introduce it.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class NumericType extends LiteralType implements Term
{
    /**
     * Gets the value of the number converted to an int.
     *
     * @return The value of the number as int.
     */
    public abstract int intValue();

    /**
     * Gets the value of the number converted to a long.
     *
     * @return The value of the number as long.
     */
    public abstract long longValue();

    /**
     * Gets the value of the number converted to a float.
     *
     * @return The value of the number as float.
     */
    public abstract float floatValue();

    /**
     * Gets the value of the number converted to a double.
     *
     * @return The value of the number as double.
     */
    public abstract double doubleValue();

    /**
     * Determines whether a number is an integer.
     *
     * @return <tt>true</tt> if a number is an integer, <tt>false</tt> otherwise.
     */
    public boolean isInteger()
    {
        return false;
    }

    /**
     * Determines whether a number is a real.
     *
     * @return <tt>true</tt> if a number is a real, <tt>false</tt> otherwise.
     */
    public boolean isReal()
    {
        return false;
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

    /**
     * Determines whether a number is of basic type float.
     *
     * @return <tt>true</tt> if a number is a float, <tt>false</tt> otherwise.
     */
    public boolean isFloat()
    {
        return false;
    }

    /**
     * Determines whether a number is of basic type double.
     *
     * @return <tt>true</tt> if a number is a double, <tt>false</tt> otherwise.
     */
    public boolean isDouble()
    {
        return false;
    }

    /**
     * Reports whether or not this term is a number.
     *
     * @return Always <tt>true</tt>.
     */
    public boolean isNumber()
    {
        return true;
    }

    /**
     * Gets the actual value of a term, which is either the term itself, or in the case of variables, the value that is
     * currently assigned to the variable.
     *
     * @return The term itself.
     */
    public Term getValue()
    {
        return this;
    }

    /**
     * Frees all assigned variables in the term, leaving them unnassigned. Numbers are not variables, so this method
     * does nothing.
     */
    public void free()
    {
    }

    /** {@inheritDoc} */
    public void accept(TermVisitor visitor)
    {
        if (visitor instanceof NumericTypeVisitor)
        {
            ((NumericTypeVisitor) visitor).visit(this);
        }
        else
        {
            super.accept(visitor);
        }
    }
}
