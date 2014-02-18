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
package com.thesett.aima.state.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.InfiniteValuesException;
import com.thesett.aima.state.RandomInstanceNotSupportedException;
import com.thesett.aima.state.Type;

/**
 * JavaType is a {@link Type} for basic Java types.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Encapsulate basic Java types.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class JavaType<T> extends BaseType<T> implements Type<T>, Serializable
{
    /** The default value of a boolean. */
    public static final boolean DEFAULT_BOOLEAN = false;

    /** The default value of a char. */
    public static final char DEFAULT_CHARACTER = '\u0000';

    /** The default value of a byte. */
    public static final byte DEFAULT_BYTE = 0;

    /** The default value of a short. */
    public static final short DEFAULT_SHORT = 0;

    /** The default value of an int. */
    public static final int DEFAULT_INTEGER = 0;

    /** The default value of a long. */
    public static final long DEFAULT_LONG = 0;

    /** The default value of a float. */
    public static final float DEFAULT_FLOAT = 0.0f;

    /** The default value of a double. */
    public static final double DEFAULT_DOUBLE = 0.0d;

    /** Ready made boolean type. */
    public static final Type<Boolean> BOOLEAN_TYPE = new JavaType<Boolean>(Boolean.class);

    /** Ready made character type. */
    public static final Type<Character> CHARACTER_TYPE = new JavaType<Character>(Character.class);

    /** Ready made byte type. */
    public static final Type<Byte> BYTE_TYPE = new JavaType<Byte>(Byte.class);

    /** Ready made short type. */
    public static final Type<Short> SHORT_TYPE = new JavaType<Short>(Short.class);

    /** Ready made integer type. */
    public static final Type<Integer> INTEGER_TYPE = new JavaType<Integer>(Integer.class);

    /** Ready made long type. */
    public static final Type<Long> LONG_TYPE = new JavaType<Long>(Long.class);

    /** Ready made float type. */
    public static final Type<Float> FLOAT_TYPE = new JavaType<Float>(Float.class);

    /** Ready made double type. */
    public static final Type<Double> DOUBLE_TYPE = new JavaType<Double>(Double.class);

    /** Ready made string type. */
    public static final Type<String> STRING_TYPE = new JavaType<String>(String.class);

    /** Ready made date type. */
    public static final Type<Date> DATE_TYPE = new JavaType<Date>(Date.class);

    /** Used to generate random instances. */
    public static final Random RANDOM = new Random();

    /** Enumeration of the basic Java types. */
    public static enum BasicTypes
    {
        /** Boolean type. */
        BOOLEAN,

        /** Character type. */
        CHARACTER,

        /** Byte type. */
        BYTE,

        /** Short type. */
        SHORT,

        /** Integer type. */
        INTEGER,

        /** Long type. */
        LONG,

        /** Float type. */
        FLOAT,

        /** Double type. */
        DOUBLE,

        /** Object type. */
        OTHER
    }

    /** Holds the underlying class of the type. */
    private Class<T> underlyingClass;

    /** Holds the basic type that this type represents. */
    private BasicTypes type;

    /**
     * Creates a type for a java class.
     *
     * @param c The class to create a type wrapper for.
     */
    public JavaType(Class c)
    {
        underlyingClass = c;

        // Work out the basic java type based on the underlying class.
        setBasicType(underlyingClass);
    }

    /**
     * Creates a type for a java object.
     *
     * @param o The object to create a type wrapper for.
     */
    public JavaType(T o)
    {
        underlyingClass = (Class<T>) o.getClass();

        // Work out the basic java type based on the underlying class.
        setBasicType(underlyingClass);
    }

    /** {@inheritDoc} */
    public T getDefaultInstance()
    {
        Object result;

        // For basic Java types return the standard default value. For others, try the default constructor.
        switch (type)
        {
        case BOOLEAN:
            result = DEFAULT_BOOLEAN;
            break;

        case CHARACTER:
            result = DEFAULT_CHARACTER;
            break;

        case BYTE:
            result = DEFAULT_BYTE;
            break;

        case SHORT:
            result = DEFAULT_SHORT;
            break;

        case INTEGER:
            result = DEFAULT_INTEGER;
            break;

        case LONG:
            result = DEFAULT_LONG;
            break;

        case FLOAT:
            result = DEFAULT_FLOAT;
            break;

        case DOUBLE:
            result = DEFAULT_DOUBLE;
            break;

        case OTHER:
        default:

            try
            {
                result = underlyingClass.newInstance();
            }
            catch (InstantiationException e)
            {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }

            break;
        }

        return (T) result;
    }

    /** {@inheritDoc} */
    public T getRandomInstance() throws RandomInstanceNotSupportedException
    {
        Object result = null;

        // For basic Java types return the standard default value. For others, try the default constructor.
        switch (type)
        {
        case BOOLEAN:
            result = RANDOM.nextBoolean();
            break;

        case CHARACTER:
            result = (char) RANDOM.nextInt();
            break;

        case BYTE:
            result = (byte) RANDOM.nextInt();
            break;

        case SHORT:
            result = (short) RANDOM.nextInt();
            break;

        case INTEGER:
            result = RANDOM.nextInt();
            break;

        case LONG:
            result = RANDOM.nextLong();
            break;

        case FLOAT:
            result = RANDOM.nextFloat();
            break;

        case DOUBLE:
            result = RANDOM.nextDouble();
            break;

        case OTHER:
        default:

            if (String.class.equals(underlyingClass))
            {
                byte[] bytes = new byte[6];
                RANDOM.nextBytes(bytes);

                result = new String(bytes);
            }

            break;
        }

        if (result != null)
        {
            return (T) result;
        }
        else
        {
            throw new RandomInstanceNotSupportedException("Type does not support random instance creation.", null);
        }
    }

    /**
     * Should return a name that uniquely identifies the type.
     *
     * @return The name of the attribute type.
     */
    public String getName()
    {
        return underlyingClass.getName();
    }

    /**
     * Returns the underlying Java class that this is the type for.
     *
     * @return The underlying Java class that this is the type for.
     */
    public Class<T> getBaseClass()
    {
        return underlyingClass;
    }

    /** {@inheritDoc} */
    public String getBaseClassName()
    {
        return getBaseClass().getName();
    }

    /**
     * Should determine how many different values an instance of the implementations type can take on.
     *
     * @return The number of possible values that an instance of this attribute can take on. If the value is -1 then
     *         this is to be interpreted as infinity.
     */
    public int getNumPossibleValues()
    {
        return -1;
    }

    /**
     * Should return all the different values that an instance of this type can take on.
     *
     * @return A set of values defining the possible value set for this attribute if this is finite.
     *
     * @throws InfiniteValuesException If the set of values cannot be listed because it is infinite.
     */
    public Set<T> getAllPossibleValuesSet() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("JavaType has infinite values.", null);
    }

    /**
     * Should return all the different values that an instance of this type can take on as an iterator over these
     * values. The set of values may be infinte if the iterator can lazily generate them as needed. If the number is
     * expected to be large it may be better to use this method to list the values than the
     * {@link #getAllPossibleValuesSet} if a lazy iterator is used because this will avoid generating a large collection
     * to hold all the possible values.
     *
     * @return An iterator over the set of attributes defining the possible value set for this attribute if this is
     *         finite or can be generated as required.
     *
     * @throws InfiniteValuesException If the set of values cannot be listed because it is infinite.
     */
    public Iterator<T> getAllPossibleValuesIterator() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("JavaType has infinite values.", null);
    }

    /**
     * Sets the basic type of this type depending on the class.
     *
     * @param c The underlying class.
     */
    private void setBasicType(Class c)
    {
        if (Boolean.class.equals(c))
        {
            type = BasicTypes.BOOLEAN;
        }
        else if (Character.class.equals(c))
        {
            type = BasicTypes.CHARACTER;
        }
        else if (Byte.class.equals(c))
        {
            type = BasicTypes.BYTE;
        }
        else if (Short.class.equals(c))
        {
            type = BasicTypes.SHORT;
        }
        else if (Integer.class.equals(c))
        {
            type = BasicTypes.INTEGER;
        }
        else if (Long.class.equals(c))
        {
            type = BasicTypes.LONG;
        }
        else if (Float.class.equals(c))
        {
            type = BasicTypes.FLOAT;
        }
        else if (Double.class.equals(c))
        {
            type = BasicTypes.DOUBLE;
        }
        else
        {
            type = BasicTypes.OTHER;
        }
    }
}
