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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.InfiniteValuesException;
import com.thesett.aima.state.RandomInstanceFactory;
import com.thesett.aima.state.Type;

/**
 * StringPatternType is a restricted type over strings, that allows a sub-class of string so to be specified that match
 * a regular expression or have a maximum length.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Maintain a set of named string pattern types.
 * <tr><td> Check a string value against the type.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class StringPatternType extends BaseType<String> implements Type<String>, RandomInstanceFactory<String>,
    Serializable
{
    /** Holds all of the named string pattern types that have been defined. */
    private static final Map<String, StringPatternType> STRING_PATTERN_TYPES = new HashMap<String, StringPatternType>();

    /** Caches the pattern in compiled form. */
    private Pattern compiledPattern;

    /**
     * Holds the regular expression pattern that instances of this type must match, or <tt>null</tt> if none is defined.
     */
    private String pattern;

    /** Holds the maximum lenth instance of this type may have, or zero if no maximum length is defined. */
    private int maxLength;

    /** Holds the name of this type. */
    private String typeName;

    /**
     * Creates a patterned string type with the specified maximum length and matching regular expression.
     *
     * @param name      The name of the string pattern type to create.
     * @param maxLength The maximum length of instances of the type, may be zero or less for no maximum.
     * @param pattern   A regular expression that instances must match, or <tt>null</tt> for no pattern.
     */
    private StringPatternType(String name, int maxLength, String pattern)
    {
        this.typeName = name;
        this.maxLength = maxLength;
        this.pattern = pattern;

        // Compile the regular expression.
        if (pattern != null)
        {
            this.compiledPattern = Pattern.compile(pattern);
        }
    }

    /**
     * Gets the named string pattern type, if one exists.
     *
     * @param  name The name of the string pattern type to get.
     *
     * @return The named string pattern type, or <tt>null</tt> if none matching the name exists.
     */
    public static StringPatternType getInstance(String name)
    {
        synchronized (STRING_PATTERN_TYPES)
        {
            return STRING_PATTERN_TYPES.get(name);
        }
    }

    /**
     * Creates a new string pattern type with the specified name, if it does not already exist.
     *
     * @param  name      The name of the string pattern type to create.
     * @param  maxLength The maximum length of instances of the type, may be zero or less for no maximum.
     * @param  pattern   A regular expression that instances must match, or <tt>null</tt> for no pattern.
     *
     * @return A new string pattern type.
     *
     * @throws IllegalArgumentException If min is not less than or equal to max, or the named type already exists.
     */
    public static StringPatternType createInstance(String name, int maxLength, String pattern)
    {
        synchronized (STRING_PATTERN_TYPES)
        {
            StringPatternType newType = new StringPatternType(name, maxLength, pattern);

            // Ensure that the named type does not already exist.
            StringPatternType oldType = STRING_PATTERN_TYPES.get(name);

            if ((oldType != null) && !oldType.equals(newType))
            {
                throw new IllegalArgumentException("The type '" + name + "' already exists and cannot be redefined.");
            }
            else if ((oldType != null) && oldType.equals(newType))
            {
                return oldType;
            }
            else
            {
                // Add the newly created type to the map of all types.
                STRING_PATTERN_TYPES.put(name, newType);

                return newType;
            }
        }
    }

    /** {@inheritDoc} */
    public String getDefaultInstance()
    {
        return "";
    }

    /** {@inheritDoc} */
    public String createRandomInstance()
    {
        byte[] bytes = new byte[6];
        random.nextBytes(bytes);

        return new String(bytes);
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return typeName;
    }

    /** {@inheritDoc} */
    public Class<String> getBaseClass()
    {
        return String.class;
    }

    /** {@inheritDoc} */
    public String getBaseClassName()
    {
        return getBaseClass().getName();
    }

    /** {@inheritDoc} */
    public int getNumPossibleValues()
    {
        return -1;
    }

    /** {@inheritDoc} */
    public Set<String> getAllPossibleValuesSet() throws InfiniteValuesException
    {
        throw new InfiniteValuesException(
            "The number of possible values of a string pattern type may be too many to enumerate.", null);
    }

    /** {@inheritDoc} */
    public Iterator<String> getAllPossibleValuesIterator() throws InfiniteValuesException
    {
        throw new InfiniteValuesException(
            "The number of possible values of a string pattern type may be too many to enumerate.", null);
    }

    /**
     * Checks a string value against this type to see if it is a valid instance of the type.
     *
     * @param  value The value to type check.
     *
     * @return <tt>true</tt> if the string is a valid instance of this type.
     */
    public boolean isInstance(String value)
    {
        // Check the value is under the maximum if one is set.
        // Check the value matches the pattern if one is set.
        return ((maxLength <= 0) || (value.length() <= maxLength)) && compiledPattern.matcher(value).matches();
    }

    /**
     * Checks two string pattern types for equality, based on their name, length and pattern.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is a string pattern type with the same name, length and pattern.
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof StringPatternType))
        {
            return false;
        }

        StringPatternType that = (StringPatternType) o;

        return (maxLength == that.maxLength) &&
            !((pattern != null) ? (!pattern.equals(that.pattern)) : (that.pattern != null)) &&
            !((typeName != null) ? (!typeName.equals(that.typeName)) : (that.typeName != null));
    }

    /**
     * Provides a hashcode based on the name, length and pattern of this type.
     *
     * @return A hashcode based on the name, length and pattern of this type.
     */
    public int hashCode()
    {
        int result;
        result = ((pattern != null) ? pattern.hashCode() : 0);
        result = (31 * result) + maxLength;
        result = (31 * result) + ((typeName != null) ? typeName.hashCode() : 0);

        return result;
    }
}
