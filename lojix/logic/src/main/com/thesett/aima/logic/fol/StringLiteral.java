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
 * StringLiteral extends the basic syntax of first order logic with string constants.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Represent a string constant as a term.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class StringLiteral extends LiteralType implements Term
{
    /** Holds the value of this string literal. */
    private final String value;

    /**
     * Creates a new string literal with the specified value.
     *
     * @param value The value of the number.
     */
    public StringLiteral(String value)
    {
        this.value = value;
    }

    /**
     * Gets the actual value of a term, which is either the term itself, or in the case of variables, the value that is
     * currently assigned to the variable.
     *
     * @return The term itself, or the assigned value for variables.
     */
    public Term getValue()
    {
        return this;
    }

    /**
     * Frees all assigned variables in the term, leaving them unnassigned. Strings are not variables, so this method
     * does nothing.
     */
    public void free()
    {
    }

    /**
     * Two string literals are considered equal, if their string values are equal.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is a string literal of equal value.
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        StringLiteral that = (StringLiteral) o;

        return !((value != null) ? (!value.equals(that.value)) : (that.value != null));

    }

    /**
     * Provides a hash code based on the string value, compatable with the equality method.
     *
     * @return A hash code based on the string value.
     */
    public int hashCode()
    {
        return ((value != null) ? value.hashCode() : 0);
    }

    /**
     * Returns the value of this literal as a string.
     *
     * @return The value of this literal as a string.
     */
    public String stringValue()
    {
        return value;
    }

    /**
     * Pretty prints the value of this string, mostly for debugging purposes.
     *
     * @return The value of this string literal as a string.
     */
    public String toString()
    {
        return "StringLiteral: [ value = " + value + "]";
    }

    /** {@inheritDoc} */
    public String toString(VariableAndFunctorInterner interner, boolean printVarName, boolean printBindings)
    {
        return value;
    }
}
