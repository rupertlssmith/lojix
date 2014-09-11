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
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.InfiniteValuesException;
import com.thesett.aima.state.RandomInstanceFactory;
import com.thesett.aima.state.Type;
import com.thesett.aima.state.TypeVisitor;
import com.thesett.aima.state.restriction.DecimalMaxRestriction;
import com.thesett.aima.state.restriction.DecimalMinRestriction;
import com.thesett.aima.state.restriction.TypeRestriction;
import com.thesett.common.error.NotImplementedException;

/**
 * Implements a decimal type, with precision and scale.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Maintain a set of named decimal types.
 * <tr><td> Provide a default value that is an instance of the type.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BigDecimalTypeImpl extends BaseType<BigDecimal> implements Type<BigDecimal>,
    RandomInstanceFactory<BigDecimal>, Serializable, BigDecimalType
{
    /** Holds all of the named decimal types that have been defined. */
    private static final Map<String, BigDecimalTypeImpl> DECIMAL_TYPES = new HashMap<String, BigDecimalTypeImpl>();

    /** Used to hold the name of this decimal type. */
    private final String typeName;

    /** Holds the precision of this type. */
    private final int precision;

    /** Holds the scale of this type. */
    private final int scale;

    /** The minimum value in BigDecimal string notation, or <tt>null</tt> if none is specified. */
    private final String min;

    /** The maximum value in BigDecimal string notation, or <tt>null</tt> if none is specified. */
    private final String max;

    /**
     * Creates a named decimal type with the specified precision and scale.
     *
     * @param typeName  The name of the decimal type.
     * @param precision The precision of the decimal type.
     * @param scale     The scale of the decimal type.
     * @param min       The minimum value in BigDecimal string notation, or <tt>null</tt> if none is specified.
     * @param max       The maximum value in BigDecimal string notation, or <tt>null</tt> if none is specified.
     */
    public BigDecimalTypeImpl(String typeName, int precision, int scale, String min, String max)
    {
        this.typeName = typeName;
        this.precision = precision;
        this.scale = scale;
        this.min = min;
        this.max = max;

        restrictions = new LinkedList<TypeRestriction>();

        if (min != null)
        {
            restrictions.add(new DecimalMinRestriction(min));
        }

        if (max != null)
        {
            restrictions.add(new DecimalMaxRestriction(max));
        }
    }

    /**
     * Creates a new decimal type with the specified name, if it does not already exist.
     *
     * @param  name      The name of the decimal type to create.
     * @param  precision The precision of the decimal type.
     * @param  scale     The scale of the decimal type.
     *
     * @return A new decimal type.
     *
     * @throws IllegalArgumentException If the named type already exists.
     */
    public static BigDecimalTypeImpl createInstance(String name, int precision, int scale, String min, String max)
    {
        synchronized (DECIMAL_TYPES)
        {
            // Add the newly created type to the map of all types.
            BigDecimalTypeImpl newType = new BigDecimalTypeImpl(name, precision, scale, min, max);

            // Ensure that the named type does not already exist, unless it has an identical definition already, in which
            // case the old definition can be re-used and the new one discarded.
            BigDecimalTypeImpl oldType = DECIMAL_TYPES.get(name);

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
                DECIMAL_TYPES.put(name, newType);

                return newType;
            }
        }
    }

    /** {@inheritDoc} */
    public BigDecimal getDefaultInstance()
    {
        return BigDecimal.ZERO;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return typeName;
    }

    /** {@inheritDoc} */
    public Class<BigDecimal> getBaseClass()
    {
        return BigDecimal.class;
    }

    /** {@inheritDoc} */
    public String getBaseClassName()
    {
        return BigDecimal.class.getName();
    }

    /** {@inheritDoc} */
    public int getNumPossibleValues()
    {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    public Set<BigDecimal> getAllPossibleValuesSet() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("DecimalType has too many values to enumerate.", null);
    }

    /** {@inheritDoc} */
    public Iterator<BigDecimal> getAllPossibleValuesIterator() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("DecimalType has too many values to enumerate.", null);
    }

    /** {@inheritDoc} */
    public int getPrecision()
    {
        return precision;
    }

    /** {@inheritDoc} */
    public int getScale()
    {
        return scale;
    }

    /** {@inheritDoc} */
    public void acceptVisitor(TypeVisitor visitor)
    {
        if (visitor instanceof BigDecimalTypeVisitor)
        {
            ((BigDecimalTypeVisitor) visitor).visit(this);
        }
        else
        {
            super.acceptVisitor(visitor);
        }
    }

    /**
     * Checks two decimal types for equality, based on their name, scale and precision.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is an decimal type with the same name, scale and precision.
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

        BigDecimalTypeImpl that = (BigDecimalTypeImpl) o;

        if (precision != that.precision)
        {
            return false;
        }

        if (scale != that.scale)
        {
            return false;
        }

        if ((typeName != null) ? (!typeName.equals(that.typeName)) : (that.typeName != null))
        {
            return false;
        }

        return true;
    }

    /**
     * Provides a hashcode based on the name, scale and precision of this type.
     *
     * @return A hashcode based on the name, scale and precision of this type.
     */
    public int hashCode()
    {
        int result;
        result = ((typeName != null) ? typeName.hashCode() : 0);
        result = (31 * result) + precision;
        result = (31 * result) + scale;

        return result;
    }
}
