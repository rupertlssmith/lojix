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
package com.thesett.aima.attribute.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.InfiniteValuesException;
import com.thesett.aima.state.RandomInstanceFactory;
import com.thesett.aima.state.Type;

/**
 * Implements an integer range type. This allows a sequential range of ints to be defined as a named type.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Maintain a set of named int range types.
 * <tr><td> Provide all integers within the types range.
 * <tr><td> Provide a default value that lies in the range.
 * <tr><td> Check an integer value against the range type.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class IntRangeType extends BaseType<Integer> implements Type<Integer>, RandomInstanceFactory<Integer>,
    Serializable
{
    /** Holds all of the named int range types that have been defined. */
    private static final Map<String, IntRangeType> INT_RANGE_TYPES = new HashMap<String, IntRangeType>();

    /** Holds the minimum value an instance of this type can take. */
    private int minValue;

    /** Holds the maximum value an instance of this type can take. */
    private int maxValue;

    /** Used to hold the name of this int range type. */
    private String typeName;

    /**
     * Creates an instance of an integer range type with the specified name and min and max values.
     *
     * @param name The name of the type.
     * @param min  The minimum value an instance of the type can take.
     * @param max  The maximum value an instance of the type can take.
     */
    private IntRangeType(String name, int min, int max)
    {
        typeName = name;
        minValue = min;
        maxValue = max;
    }

    /**
     * Gets the named integer range type, if one exists.
     *
     * @param  name The name of the int range type to get.
     *
     * @return The named int range type, or <tt>null</tt> if none matching the name exists.
     */
    public static IntRangeType getInstance(String name)
    {
        synchronized (INT_RANGE_TYPES)
        {
            return INT_RANGE_TYPES.get(name);
        }
    }

    /**
     * Creates a new int range type with the specified name, if it does not already exist.
     *
     * @param  name The name of the int range type to create.
     * @param  min  The minimum value that an instance of the type can take.
     * @param  max  The maximum value that an instance of teh type can take.
     *
     * @return A new int range type.
     *
     * @throws IllegalArgumentException If min is not less than or equal to max, or the named type already exists.
     */
    public static IntRangeType createInstance(String name, int min, int max)
    {
        // Ensure that min is less than or equal to max.
        if (min > max)
        {
            throw new IllegalArgumentException("'min' must be less than or equal to 'max'.");
        }

        synchronized (INT_RANGE_TYPES)
        {
            // Add the newly created type to the map of all types.
            IntRangeType newType = new IntRangeType(name, min, max);

            // Ensure that the named type does not already exist, unless it has an identical definition already, in which
            // case the old definition can be re-used and the new one discarded.
            IntRangeType oldType = INT_RANGE_TYPES.get(name);

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
                INT_RANGE_TYPES.put(name, newType);

                return newType;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p/>The default value of an integer range type is zero when zero lies between 'min' and 'max', otherwise it is
     * 'min'.
     */
    public Integer getDefaultInstance()
    {
        return ((0 >= minValue) && (0 <= maxValue)) ? 0 : minValue;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>A random value of this type lies between 'min' and 'max'.
     */
    public Integer createRandomInstance()
    {
        return minValue + random.nextInt(maxValue - minValue);
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return typeName;
    }

    /** {@inheritDoc} */
    public Class<Integer> getBaseClass()
    {
        return Integer.class;
    }

    /** {@inheritDoc} */
    public String getBaseClassName()
    {
        return getBaseClass().getName();
    }

    /** {@inheritDoc} */
    public int getNumPossibleValues()
    {
        return maxValue - minValue + 1;
    }

    /**
     * Checks an integer value against this type, to determine if it is a valid instance of the type.
     *
     * @param  value The value to check.
     *
     * @return <tt>true</tt> if the value lies in the types range, <tt>false</tt> if it does not.
     */
    public boolean isInstance(int value)
    {
        return (value >= minValue) && (value <= maxValue);
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Beware that as this operation creates a fully expanded set of the integer range in memory, large ranges may
     * use a lot of memory. Consider using the {@link #getAllPossibleValuesIterator()} method instead, as it lazily
     * creates the values on demand.
     */
    public Set<Integer> getAllPossibleValuesSet() throws InfiniteValuesException
    {
        Set<Integer> results = new HashSet<Integer>();

        for (int i = minValue; i <= maxValue; i++)
        {
            results.add(i);
        }

        return results;
    }

    /** {@inheritDoc} */
    public Iterator<Integer> getAllPossibleValuesIterator() throws InfiniteValuesException
    {
        return new Iterator<Integer>()
            {
                int current = minValue;

                /** {@inheritDoc} */
                public boolean hasNext()
                {
                    return current <= maxValue;
                }

                /** {@inheritDoc} */
                public Integer next()
                {
                    if (hasNext())
                    {
                        int result = current;
                        current++;

                        return result;
                    }
                    else
                    {
                        throw new NoSuchElementException("No more elements less than or equal to max.");
                    }
                }

                /** {@inheritDoc} */
                public void remove()
                {
                    throw new UnsupportedOperationException("This iterator does not support 'remove'.");
                }
            };
    }

    /**
     * Checks two int range types for equality, based on their name and int range.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is an int range type with the same name and range as this one.
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof IntRangeType))
        {
            return false;
        }

        IntRangeType that = (IntRangeType) o;

        return (maxValue == that.maxValue) && (minValue == that.minValue) &&
            !((typeName != null) ? (!typeName.equals(that.typeName)) : (that.typeName != null));
    }

    /**
     * Provides a hashcode based on the name, and int range of this type.
     *
     * @return A hashcode based on the name, and int range of this type.
     */
    public int hashCode()
    {
        int result;
        result = minValue;
        result = (31 * result) + maxValue;
        result = (31 * result) + ((typeName != null) ? typeName.hashCode() : 0);

        return result;
    }
}
