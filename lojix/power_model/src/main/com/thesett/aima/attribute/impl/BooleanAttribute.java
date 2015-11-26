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
package com.thesett.aima.attribute.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.OrdinalAttribute;
import com.thesett.aima.state.Type;

/**
 * BooleanAttribute encapsulates a boolean in the {@link OrdinalAttribute} interface. This allows algorithms to work
 * with values of this type in the same way as other attributes by supplying some meta-information about the type.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><th> Provide count of how many values a boolean can have.
 * <tr><th> Provide listing of all boolean values.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BooleanAttribute implements OrdinalAttribute
{
    /** Holds a reference to the type class for boolean attributes. */
    private static final BooleanClassImpl attributeClass = new BooleanClassImpl();

    /** Used to hold the underlying boolean value of this attribute. */
    private boolean value;

    /**
     * Creates a new BooleanAttribute object.
     *
     * @param value The boolean value that the attribute will have.
     */
    public BooleanAttribute(boolean value)
    {
        this.value = value;
    }

    /**
     * Returns the attribute type of this attribute.
     *
     * @return The attribute type of this attribute.
     */
    public Type<BooleanAttribute> getType()
    {
        return attributeClass;
    }

    /**
     * Gets the underlying boolean value of the attribute.
     *
     * @return The boolean value of the attribute.
     */
    public boolean booleanValue()
    {
        return value;
    }

    /**
     * An ordinal representing the boolean value of this attribute. 1 is true and 0 is false.
     *
     * @return 1 if this attribute has the value true, and 0 if it has the value false.
     */
    public int ordinal()
    {
        return value ? 1 : 0;
    }

    /**
     * Compares this attribute to another for equality. To be equal the other attribute must be of the same class and
     * have the same value.
     *
     * @param  o The object to compare to.
     *
     * @return True if the comparator is a BooleanAttribute with the same value as this one, and false otherwise.
     */
    public boolean equals(Object o)
    {
        return ((BooleanAttribute) o).value == value;
    }

    /**
     * A hascode for the valueof this attribute. The has 1231 is used for true and 1237 for false, as in the
     * java.lang.Boolean class.
     *
     * @return A hascode of the value of this attribute.
     */
    public int hashCode()
    {
        return value ? 1231 : 1237;
    }

    /**
     * Renders the value of this attribute as a readable string, "true" for true and "false" for false.
     *
     * @return "true" if the value of this attribute is true and "false" if it is false.
     */
    public String toString()
    {
        return value ? "true" : "false";
    }

    /**
     * Defines the type interface for boolean attributes.
     */
    public static interface BooleanType extends Type<BooleanAttribute>
    {
    }

    /**
     * Defines a factory interface for creating boolean attributes. Currently not used.
     */
    public static interface BooleanAttributeFactory
    {
    }

    /**
     * Defines the class of boolean attributes. Class = Type + Factory.
     */
    public static interface BooleanClass extends BooleanType, BooleanAttributeFactory
    {
    }

    /**
     * Implements the boolean attribute type class. Used to hold the set of all possible values, the name and the
     * enumerator methods.
     */
    private static class BooleanClassImpl extends BaseType<BooleanAttribute> implements BooleanClass
    {
        /** Defines the name of the boolean attribute type. This will always be "BOOLEAN". */
        private static final String BOOLEAN_ATTR_NAME = "BOOLEAN";

        /** Used to hold the set of all possible values of this attribute type. */
        protected static Set<BooleanAttribute> allPossibleValues = null;

        /**
         * Gets a new default instance of the type. The types value will be set to its default uninitialized value.
         *
         * @return A new default instance of the type. Always <tt>false</tt>.
         */
        public BooleanAttribute getDefaultInstance()
        {
            return new BooleanAttribute(false);
        }

        /**
         * Returns the name of the boolean attribute type. This will always be "BOOLEAN".
         *
         * @return The name of the boolean attribute type. This will always be "BOOLEAN".
         */
        public String getName()
        {
            return BOOLEAN_ATTR_NAME;
        }

        /**
         * Gets the underlying Java class that implements the type.
         *
         * @return The underlying Java class that implements the type.
         */
        public Class<BooleanAttribute> getBaseClass()
        {
            return BooleanAttribute.class;
        }

        /** {@inheritDoc} */
        public String getBaseClassName()
        {
            return getBaseClass().getName();
        }

        /**
         * The number of possible value that a boolean can have. Always 2.
         *
         * @return The number 2.
         */
        public int getNumPossibleValues()
        {
            return 2;
        }

        /**
         * The set of all possible values of this attribute. This is the set {true, false}.
         *
         * @return The set {true, false}.
         */
        public Set<BooleanAttribute> getAllPossibleValuesSet()
        {
            // Create the all possible values set if it does not already exist.
            if (allPossibleValues == null)
            {
                allPossibleValues = new HashSet<BooleanAttribute>();

                allPossibleValues.add(new BooleanAttribute(true));
                allPossibleValues.add(new BooleanAttribute(false));
            }

            return new HashSet<BooleanAttribute>(allPossibleValues);
        }

        /**
         * An iterator over the possible value set for this attribute. This is an iterator over the set {true, false}.
         *
         * @return An iterator over the set {true, false}.
         */
        public Iterator<BooleanAttribute> getAllPossibleValuesIterator()
        {
            return getAllPossibleValuesSet().iterator();
        }
    }
}
