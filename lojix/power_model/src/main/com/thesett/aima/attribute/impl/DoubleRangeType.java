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

import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.InfiniteValuesException;
import com.thesett.aima.state.RandomInstanceFactory;
import com.thesett.aima.state.Type;

/**
 * Implements a double range type. This allows an interval of doubles to be defined as a named type.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Maintain a set of named double range types.
 * <tr><td> Provide a default value that lies in the range.
 * <tr><td> Check a double value against the range type.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DoubleRangeType extends BaseType<Double> implements Type<Double>, RandomInstanceFactory<Double>,
    Serializable
{
    /** Holds all of the named double range types that have been defined. */
    private static final Map<String, DoubleRangeType> DOUBLE_RANGE_TYPES = new HashMap<String, DoubleRangeType>();

    /** Holds the minimum value an instance of this type can take. */
    private final double minValue;

    /** Holds the maximum value an instance of this type can take. */
    private final double maxValue;

    /** Used to hold the name of this double range type. */
    private final String typeName;

    /**
     * Creates an instance of a double range type with the specified name and min and max values.
     *
     * @param name The name of the type.
     * @param min  The minimum value an instance of the type can take.
     * @param max  The maximum value an instance of the type can take.
     */
    private DoubleRangeType(String name, double min, double max)
    {
        typeName = name;
        minValue = min;
        maxValue = max;
    }

    /**
     * Gets the named double range type, if one exists.
     *
     * @param  name The name of the double range type to get.
     *
     * @return The named double range type, or <tt>null</tt> if none matching the name exists.
     */
    public static DoubleRangeType getInstance(String name)
    {
        synchronized (DOUBLE_RANGE_TYPES)
        {
            return DOUBLE_RANGE_TYPES.get(name);
        }
    }

    /**
     * Creates a new double range type with the specified name, if it does not already exist.
     *
     * @param  name The name of the double range type to create.
     * @param  min  The minimum value that an instance of the type can take.
     * @param  max  The maximum value that an instance of teh type can take.
     *
     * @return A new double range type.
     *
     * @throws IllegalArgumentException If min is not less than or equal to max, or the named type already exists.
     */
    public static DoubleRangeType createInstance(String name, double min, double max)
    {
        // Ensure that min is less than or equal to max.
        if (min > max)
        {
            throw new IllegalArgumentException("'min' must be less than or equal to 'max'.");
        }

        synchronized (DOUBLE_RANGE_TYPES)
        {
            // Add the newly created type to the map of all types.
            DoubleRangeType newType = new DoubleRangeType(name, min, max);

            // Ensure that the named type does not already exist, unless it has an identical definition already, in which
            // case the old definition can be re-used and the new one discarded.
            DoubleRangeType oldType = DOUBLE_RANGE_TYPES.get(name);

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
                DOUBLE_RANGE_TYPES.put(name, newType);

                return newType;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p/>The default value of a double range type is zero when zero lies between 'min' and 'max', otherwise it is
     * 'min'.
     */
    public Double getDefaultInstance()
    {
        return ((0 >= minValue) && (0 <= maxValue)) ? 0 : minValue;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>A random value of this type lies between 'min' and 'max'.
     */
    public Double createRandomInstance()
    {
        return random.nextDouble() + (minValue * (maxValue - minValue));
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return typeName;
    }

    /** {@inheritDoc} */
    public Class<Double> getBaseClass()
    {
        return Double.class;
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

    /**
     * Checks a double value against this type, to determine if it is a valid instance of the type.
     *
     * @param  value The value to check.
     *
     * @return <tt>true</tt> if the value lies in the types range, <tt>false</tt> if it does not.
     */
    public boolean isInstance(double value)
    {
        return (value >= minValue) && (value <= maxValue);
    }

    /** {@inheritDoc} */
    public Set<Double> getAllPossibleValuesSet() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("DoubleRangeType has too many values to enumerate.", null);
    }

    /** {@inheritDoc} */
    public Iterator<Double> getAllPossibleValuesIterator() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("DoubleRangeType has too many values to enumerate.", null);
    }

    /**
     * Checks two float range types for equality, based on their name and float range.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is an double range type with the same name and range as this one.
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof DoubleRangeType))
        {
            return false;
        }

        DoubleRangeType that = (DoubleRangeType) o;

        return (Double.compare(that.maxValue, maxValue) == 0) && (Double.compare(that.minValue, minValue) == 0) &&
            !((typeName != null) ? (!typeName.equals(that.typeName)) : (that.typeName != null));
    }

    /**
     * Provides a hashcode based on the name, and double range of this type.
     *
     * @return A hashcode based on the name, and double range of this type.
     */
    public int hashCode()
    {
        long result;

        result = ((minValue != +0.0d) ? Double.doubleToLongBits(minValue) : 0);
        result = (31 * result) + ((maxValue != +0.0f) ? Double.doubleToLongBits(maxValue) : 0);
        result = (31 * result) + ((typeName != null) ? typeName.hashCode() : 0);

        return (int) (result ^ (result >>> 32));
    }
}
