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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.thesett.aima.attribute.time.TimeOnly;
import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.InfiniteValuesException;
import com.thesett.aima.state.RandomInstanceFactory;
import com.thesett.aima.state.Type;
import com.thesett.aima.state.TypeVisitor;

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
public class TimeRangeType extends BaseType<TimeOnly> implements Type<TimeOnly>, RandomInstanceFactory<TimeOnly>,
    Serializable
{
    /** Holds all of the named time range types that have been defined. */
    private static final Map<String, TimeRangeType> INT_RANGE_TYPES = new HashMap<String, TimeRangeType>();

    /** Holds the minimum value an instance of this type can take. */
    private TimeOnly minValue;

    /** Holds the maximum value an instance of this type can take. */
    private TimeOnly maxValue;

    /** Used to hold the name of this time range type. */
    private String typeName;

    /**
     * Creates an instance of an integer range type with the specified name and min and max values.
     *
     * @param name The name of the type.
     * @param min  The minimum value an instance of the type can take.
     * @param max  The maximum value an instance of the type can take.
     */
    private TimeRangeType(String name, TimeOnly min, TimeOnly max)
    {
        typeName = name;
        minValue = min;
        maxValue = max;
    }

    /**
     * Gets the named integer range type, if one exists.
     *
     * @param  name The name of the time range type to get.
     *
     * @return The named time range type, or <tt>null</tt> if none matching the name exists.
     */
    public static TimeRangeType getInstance(String name)
    {
        synchronized (INT_RANGE_TYPES)
        {
            return INT_RANGE_TYPES.get(name);
        }
    }

    /**
     * Creates a new time range type with the specified name, if it does not already exist.
     *
     * @param  name The name of the time range type to create.
     * @param  min  The minimum value that an instance of the type can take.
     * @param  max  The maximum value that an instance of teh type can take.
     *
     * @return A new time range type.
     *
     * @throws IllegalArgumentException If min is not less than or equal to max, or the named type already exists.
     */
    public static TimeRangeType createInstance(String name, TimeOnly min, TimeOnly max)
    {
        // Ensure that min is less than or equal to max.
        if ((min != null) && (max != null) && (min.compareTo(max) > 0))
        {
            throw new IllegalArgumentException("'min' must be less than or equal to 'max'.");
        }

        synchronized (INT_RANGE_TYPES)
        {
            // Add the newly created type to the map of all types.
            TimeRangeType newType = new TimeRangeType(name, min, max);

            // Ensure that the named type does not already exist, unless it has an identical definition already, in which
            // case the old definition can be re-used and the new one discarded.
            TimeRangeType oldType = INT_RANGE_TYPES.get(name);

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
     * <p/>The default value of an time range type is 'min'.
     */
    public TimeOnly getDefaultInstance()
    {
        return minValue;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>A random value of this type lies between 'min' and 'max'.
     */
    public TimeOnly createRandomInstance()
    {
        int start = minValue.getMilliseconds();
        int end = maxValue.getMilliseconds();

        return new TimeOnly((long) (start + random.nextInt(end - start)));
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return typeName;
    }

    /** {@inheritDoc} */
    public Class<TimeOnly> getBaseClass()
    {
        return TimeOnly.class;
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
     * Checks an integer value against this type, to determine if it is a valid instance of the type.
     *
     * @param  value The value to check.
     *
     * @return <tt>true</tt> if the value lies in the types range, <tt>false</tt> if it does not.
     */
    public boolean isInstance(TimeOnly value)
    {
        return (value.compareTo(minValue) >= 0) && (value.compareTo(maxValue) <= 0);
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Beware that as this operation creates a fully expanded set of the integer range in memory, large ranges may
     * use a lot of memory. Consider using the {@link #getAllPossibleValuesIterator()} method instead, as it lazily
     * creates the values on demand.
     */
    public Set<TimeOnly> getAllPossibleValuesSet() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("TimeRangeType has too many values to enumerate.", null);
    }

    /** {@inheritDoc} */
    public Iterator<TimeOnly> getAllPossibleValuesIterator() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("TimeRangeType has too many values to enumerate.", null);
    }

    /** {@inheritDoc} */
    public void acceptVisitor(TypeVisitor visitor)
    {
        if (visitor instanceof TimeRangeTypeVisitor)
        {
            ((TimeRangeTypeVisitor) visitor).visit(this);
        }
        else
        {
            super.acceptVisitor(visitor);
        }
    }

    /**
     * Checks two time range types for equality, based on their name and time range.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is an time range type with the same name and range as this one.
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof TimeRangeType))
        {
            return false;
        }

        TimeRangeType that = (TimeRangeType) o;

        return (maxValue == that.maxValue) && (minValue == that.minValue) &&
            !((typeName != null) ? (!typeName.equals(that.typeName)) : (that.typeName != null));
    }

    /**
     * Provides a hashcode based on the name, and time range of this type.
     *
     * @return A hashcode based on the name, and time range of this type.
     */
    public int hashCode()
    {
        int result;
        result = (minValue != null) ? minValue.hashCode() : 0;
        result = (31 * result) + ((maxValue != null) ? maxValue.hashCode() : 0);
        result = (31 * result) + ((typeName != null) ? typeName.hashCode() : 0);

        return result;
    }
}
