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
 * Implements a float range type. This allows an interval of floats to be defined as a named type.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Maintain a set of named float range types.
 * <tr><td> Provide a default value that lies in the range.
 * <tr><td> Check a float value against the range type.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class FloatRangeType extends BaseType<Float> implements Type<Float>, RandomInstanceFactory<Float>, Serializable
{
    /** Holds all of the named float range types that have been defined. */
    private static final Map<String, FloatRangeType> FLOAT_RANGE_TYPES = new HashMap<String, FloatRangeType>();

    /** Holds the minimum value an instance of this type can take. */
    private final float minValue;

    /** Holds the maximum value an instance of this type can take. */
    private final float maxValue;

    /** Used to hold the name of this float range type. */
    private final String typeName;

    /**
     * Creates an instance of a float range type with the specified name and min and max values.
     *
     * @param name The name of the type.
     * @param min  The minimum value an instance of the type can take.
     * @param max  The maximum value an instance of the type can take.
     */
    private FloatRangeType(String name, float min, float max)
    {
        typeName = name;
        minValue = min;
        maxValue = max;
    }

    /**
     * Gets the named float range type, if one exists.
     *
     * @param  name The name of the float range type to get.
     *
     * @return The named float range type, or <tt>null</tt> if none matching the name exists.
     */
    public static FloatRangeType getInstance(String name)
    {
        synchronized (FLOAT_RANGE_TYPES)
        {
            return FLOAT_RANGE_TYPES.get(name);
        }
    }

    /**
     * Creates a new float range type with the specified name, if it does not already exist.
     *
     * @param  name The name of the float range type to create.
     * @param  min  The minimum value that an instance of the type can take.
     * @param  max  The maximum value that an instance of teh type can take.
     *
     * @return A new float range type.
     *
     * @throws IllegalArgumentException If min is not less than or equal to max, or the named type already exists.
     */
    public static FloatRangeType createInstance(String name, float min, float max)
    {
        // Ensure that min is less than or equal to max.
        if (min > max)
        {
            throw new IllegalArgumentException("'min' must be less than or equal to 'max'.");
        }

        synchronized (FLOAT_RANGE_TYPES)
        {
            // Add the newly created type to the map of all types.
            FloatRangeType newType = new FloatRangeType(name, min, max);

            // Ensure that the named type does not already exist, unless it has an identical definition already, in which
            // case the old definition can be re-used and the new one discarded.
            FloatRangeType oldType = FLOAT_RANGE_TYPES.get(name);

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
                FLOAT_RANGE_TYPES.put(name, newType);

                return newType;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p/>The default value of a float range type is zero when zero lies between 'min' and 'max', otherwise it is
     * 'min'.
     */
    public Float getDefaultInstance()
    {
        return ((0 >= minValue) && (0 <= maxValue)) ? 0 : minValue;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>A random value of this type lies between 'min' and 'max'.
     */
    public Float createRandomInstance()
    {
        return random.nextFloat() + (minValue * (maxValue - minValue));
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return typeName;
    }

    /** {@inheritDoc} */
    public Class<Float> getBaseClass()
    {
        return Float.class;
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
     * Checks a float value against this type, to determine if it is a valid instance of the type.
     *
     * @param  value The value to check.
     *
     * @return <tt>true</tt> if the value lies in the types range, <tt>false</tt> if it does not.
     */
    public boolean isInstance(float value)
    {
        return (value >= minValue) && (value <= maxValue);
    }

    /** {@inheritDoc} */
    public Set<Float> getAllPossibleValuesSet() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("FloatRangeType has too many values to enumerate.", null);
    }

    /** {@inheritDoc} */
    public Iterator<Float> getAllPossibleValuesIterator() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("FloatRangeType has too many values to enumerate.", null);
    }

    /**
     * Checks two float range types for equality, based on their name and float range.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is an float range type with the same name and range as this one.
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof FloatRangeType))
        {
            return false;
        }

        FloatRangeType that = (FloatRangeType) o;

        return (Float.compare(that.maxValue, maxValue) == 0) && (Float.compare(that.minValue, minValue) == 0) &&
            !((typeName != null) ? (!typeName.equals(that.typeName)) : (that.typeName != null));
    }

    /**
     * Provides a hashcode based on the name, and float range of this type.
     *
     * @return A hashcode based on the name, and float range of this type.
     */
    public int hashCode()
    {
        int result;
        result = ((minValue != +0.0f) ? Float.floatToIntBits(minValue) : 0);
        result = (31 * result) + ((maxValue != +0.0f) ? Float.floatToIntBits(maxValue) : 0);
        result = (31 * result) + ((typeName != null) ? typeName.hashCode() : 0);

        return result;
    }
}
