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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.OrdinalAttribute;
import com.thesett.aima.state.Type;
import com.thesett.common.error.NotImplementedException;

/**
 * EnumAttributes are used to encapsulate Java 1.5 enums as implementations of the {@link OrdinalAttribute} interface.
 * This means that there must be methods to state how many different values an enum can take on and to list all those
 * possible values as {@link OrdinalAttribute}s. The actual enums are stored as integer ordinals. A map of class level
 * enum information is maintained to provide information about how many and what values a given class of enums can take
 * on.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><th> Provide factory to convert an enum into an OrdinalAttribute.
 * <tr><th> Provide count of how many values an enum can have.
 * <tr><th> Provide listing of all enum values.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EnumAttribute implements OrdinalAttribute
{
    /** Used to hold all the different enum classes. */
    private static Map<Class, EnumClassImpl> attributeClasses = new HashMap<Class, EnumClassImpl>();

    /** Holds the enum ordinal. */
    private int ordinal;

    /** Holds a reference to the class of enum attribute that this one belongs to. */
    EnumClassImpl attributeClass;

    /**
     * Creates a new EnumAttribute object.
     *
     * @param value          The value of the enum attribute.
     * @param attributeClass The factory implementation for the class type of enum for this attribute.
     */
    private EnumAttribute(Enum value, EnumClassImpl attributeClass)
    {
        // Keep the ordinal represenation and the pointer to the attribute class information.
        this.ordinal = value.ordinal();
        this.attributeClass = attributeClass;
    }

    /**
     * Generates a factory for building enum attributes of the specified enum class.
     *
     * @param  cls The class type of enum to get an enum attribute factory for.
     *
     * @return The enum attribute factory for the enum class type specified.
     */
    public static EnumAttributeFactory getFactoryForClass(Class cls)
    {
        // Check that the requested class is actually an enum.
        if (!cls.isEnum())
        {
            throw new IllegalArgumentException("Can only create enum attribute factories for classes that are enums.");
        }

        return EnumClassImpl.getInstance(cls);
    }

    /**
     * Returns the attribute type of this attribute.
     *
     * @return The attribute type of this attribute.
     */
    public Type<EnumAttribute> getType()
    {
        return attributeClass;
    }

    /**
     * The ordinal value of the enum value of this attribute.
     *
     * @return The ordinal value of the enum value of this attribute.
     */
    public int ordinal()
    {
        return ordinal;
    }

    /**
     * The number of possible values that an instance of this enum attribute can take on.
     *
     * @return the number of possible values that an instance of this enum attribute can take on.
     */
    /*public int getNumPossibleValues()
    {
        // Fetch the number of possible values from the attribute class.
        return attributeClass.numValues;
    }*/

    /**
     * Gets the underlying value of this attribute as an Enum.
     *
     * @return An instance of the underlying enum.
     */
    public Enum getEnumValue()
    {
        return attributeClass.values[ordinal];
    }

    /**
     * Generates hashCodes unique to the value of the enum value.
     *
     * @return A hash code for the enum value of this attribute.
     */
    public int hashCode()
    {
        // Return a hash made from the enum ordinal.
        return Integer.valueOf(ordinal).hashCode();
    }

    /**
     * Tests if two enum attributes are equal. They are equal if they are of the same enum class and have the same enum
     * value.
     *
     * @param  o The object to compare to.
     *
     * @return True if the comparator is an enum attribute of the same class of enum as this one and has the same value,
     *         false otherwise.
     */
    public boolean equals(Object o)
    {
        EnumAttribute compareTo = (EnumAttribute) o;

        return compareTo.attributeClass.enumClass.equals(attributeClass.enumClass) && (compareTo.ordinal == ordinal);
    }

    /**
     * Defines the type interface for enum attributes.
     */
    public static interface EnumType extends Type<EnumAttribute>
    {
    }

    /**
     * Used to provide a factory for creating enum attributes of a given class.
     */
    public static interface EnumAttributeFactory
    {
        /**
         * Creates an enum attribute of the enum class that this is a factory for.
         *
         * @param  value The enum value that the attribute should have.
         *
         * @return An EnumAttribute with the specified enum value.
         */
        public EnumAttribute createEnumAttribute(Enum value);

        /** Drops an attribute class. The attribute class is explicitly deleted. */
        public void dropAttributeClass();
    }

    /**
     * Defines the class of enum attributes. Class = Type + Factory.
     */
    public static interface EnumClass extends EnumType, EnumAttributeFactory
    {
    }

    /**
     * Provides an implementation of the enum attribute factory.
     */
    private static class EnumClassImpl extends BaseType<EnumAttribute> implements EnumClass
    {
        /** The number of possible values this attribute can take on. */
        int numValues = 0;

        /** Used to hold the class of the enum class that this is an attribute factory for. */
        Class enumClass;

        /** Used to hold a mapping from the ordinals to the underlying enums that they correspond to. */
        Enum[] values;

        /**
         * Builds a new factory for a given enum class.
         *
         * @param enumClass The class type of the enumeration.
         */
        private EnumClassImpl(Class enumClass)
        {
            // Keep a reference to the underlying enum class.
            this.enumClass = enumClass;

            // Count the number of possible values that this enum can have.
            numValues = enumClass.getEnumConstants().length;

            // Create a populated array of all those values.
            values = (Enum[]) enumClass.getEnumConstants();
        }

        /**
         * Gets an instance of this enum attribute factory implementation. This may be an existing object if one already
         * exists for this enum class or a new one will be created if not.
         *
         * @param  enumClass The class type of the enum to get a factory for.
         *
         * @return An enum factory implementation for the enum class.
         */
        public static EnumClassImpl getInstance(Class enumClass)
        {
            // Try to get the attribute class from the map of those that have already been created.
            EnumClassImpl attributeClass = attributeClasses.get(enumClass);

            // Check if this is a new class and create it if so.
            if (attributeClass == null)
            {
                attributeClass = new EnumClassImpl(enumClass);
                attributeClasses.put(enumClass, attributeClass);
            }

            return attributeClass;
        }

        /**
         * Gets a new default instance of the type. The types value will be set to its default uninitialized value.
         *
         * @return A new default instance of the type. Always <tt>false</tt>.
         */
        public EnumAttribute getDefaultInstance()
        {
            throw new NotImplementedException();
        }

        /**
         * Creates an enum attribute of the class that this is a factory for.
         *
         * @param  value The enum value to create a new enum attribute for.
         *
         * @return A new enum attribute with the specified value.
         */
        public EnumAttribute createEnumAttribute(Enum value)
        {
            return new EnumAttribute(value, this);
        }

        /** Drops an attribute class. The attribute class is explicitly deleted. */
        public void dropAttributeClass()
        {
            attributeClasses.remove(enumClass);
        }

        /**
         * Returns the name of this enum attribute class. This will be the name of the underlying enum Java Class.
         *
         * @return The name of this enum attribute class. This will be the name of the underlying enum Java Class.
         */
        public String getName()
        {
            return enumClass.getName();
        }

        /**
         * Gets the underlying Java class that implements the type.
         *
         * @return The underlying Java class that implements the type.
         */
        public Class<EnumAttribute> getBaseClass()
        {
            return EnumAttribute.class;
        }

        /** {@inheritDoc} */
        public String getBaseClassName()
        {
            return getBaseClass().getName();
        }

        /**
         * The number of possible values that an instance of this enum attribute can take on.
         *
         * @return the number of possible values that an instance of this enum attribute can take on.
         */
        public int getNumPossibleValues()
        {
            // Fetch the number of possible values from the attribute class.
            return numValues;
        }

        /**
         * Gets the set of all possible values that this enum attribute can have. This will be a set of enum attributes.
         *
         * @return A set of enum attributes for all possible values of this enum attribute.
         */
        public Set<EnumAttribute> getAllPossibleValuesSet()
        {
            // Turn the array of possible enum values into a set.
            Enum[] enumValues = (Enum[]) enumClass.getEnumConstants();

            Set<EnumAttribute> valueSet = new HashSet<EnumAttribute>();

            for (Enum enumValue : enumValues)
            {
                valueSet.add(createEnumAttribute(enumValue));
            }

            return valueSet;
        }

        /**
         * Generates an iterator over the set of all possible enum attribute values of this attribute.
         *
         * @return An iterator over the set of all possible enum attribute values of this attribute.
         */
        public Iterator<EnumAttribute> getAllPossibleValuesIterator()
        {
            return getAllPossibleValuesSet().iterator();
        }
    }

    /**
     * Gets the set of all possible values that this enum attribute can have. This will be a set of enum attributes.
     *
     * @return A set of enum attributes for all possible values of this enum attribute.
     */
    /*public Set<EnumAttribute> getAllPossibleValuesSet()
    {
        // Turn the array of possible enum values into a set.
        Enum[] enumValues = (Enum[]) attributeClass.enumClass.getEnumConstants();

        Set<EnumAttribute> valueSet = new HashSet<EnumAttribute>();

        for (int i = 0; i < enumValues.length; i++)
        {
            valueSet.add(attributeClass.createEnumAttribute(enumValues[i]));
        }

        return valueSet;
    }*/

    /**
     * Generates an iterator over the set of all possible enum attribute values of this attribute.
     *
     * @return An iterator over the set of all possible enum attribute values of this attribute.
     */
    /*public Iterator<EnumAttribute> getAllPossibleValuesIterator()
    {
        return getAllPossibleValuesSet().iterator();
    }*/
}
