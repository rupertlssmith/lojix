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
 * Defines the basic int type.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Define the basic int type.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class IntLiteral extends IntegerType
{
    /** Holds the value of the number. */
    private final int value;

    /**
     * Creates a new number with the specified value.
     *
     * @param value The value of the number.
     */
    public IntLiteral(int value)
    {
        this.value = value;
    }

    /**
     * Determines whether a number is of basic type int.
     *
     * @return <tt>true</tt> if a number is an int, <tt>false</tt> otherwise.
     */
    public boolean isInt()
    {
        return true;
    }

    /**
     * Gets the value of the number converted to an int.
     *
     * @return The value of the number as int.
     */
    public int intValue()
    {
        return value;
    }

    /**
     * Gets the value of the number converted to a long.
     *
     * @return The value of the number as long.
     */
    public long longValue()
    {
        return (long) value;
    }

    /**
     * Gets the value of the number converted to a float.
     *
     * @return The value of the number as float.
     */
    public float floatValue()
    {
        return (float) value;
    }

    /**
     * Gets the value of the number converted to a double.
     *
     * @return The value of the number as double.
     */
    public double doubleValue()
    {
        return (double) value;
    }

    /**
     * Determines if this number is equal to another.
     *
     * @param  comparator The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is a number equal in value to this one, <tt>false</tt> otherwise.
     */
    public boolean equals(Object comparator)
    {
        if (this == comparator)
        {
            return true;
        }

        if ((comparator == null) || !(comparator instanceof NumericType))
        {
            return false;
        }

        NumericType comparatorNumber = (NumericType) comparator;

        return value == comparatorNumber.intValue();
    }

    /**
     * Computes a hash code based on the value of this number.
     *
     * @return A hash code based on the value of this number.
     */
    public int hashCode()
    {
        return value;
    }

    /**
     * Pretty prints the value of this number, mostly for debugging purposes.
     *
     * @return The value of this number as a string.
     */
    public String toString()
    {
        return "IntLiteral: [ value = " + value + "]";
    }

    /** {@inheritDoc} */
    public String toString(VariableAndFunctorInterner interner, boolean printVarName, boolean printBindings)
    {
        return Integer.toString(value);
    }
}
