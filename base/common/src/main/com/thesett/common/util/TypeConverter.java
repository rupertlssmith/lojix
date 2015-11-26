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
package com.thesett.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * TypeConverter provides methods to convert data between the different java primitive types. Some types can always be
 * converted to, for example anything can be converted to a String by calling its toString() method, provided that the
 * implementation of that method does not throw a runtime exception, or for primitive types by using the built in
 * convertion or the methods provided by the primitive wrapper classes. Other data can often be converted to other types
 * but only dependant on its value. For example, the integer 10 can be converted to the byte 10, but the integer 1000
 * cannot be converted to a byte because it is out of range.
 *
 * <p/>TypeConverter provides methods to examine the primitive data types, plus Object and String, to see what other
 * types they can be converted to. The data will be converted to all the possible types that it can be converted to and
 * all of these will be returned packaged in a {@link MultiTypeData} object. This object will have a set of type flags
 * indicating which of its fields contain valid data as well as a native type flag that indicates what the type of the
 * original piece of data was.
 *
 * <p/>Objects will be converted to other types by using their toString() methods; they will be convertable to the same
 * set of types that the String resulting from that mthod can be converted to. So if an objects toString() method return
 * the string "1" it will also be converted to a char, byte, short, int, long, float and double.
 *
 * <p/>TypeConverter also handles the convertion back from multi type data objects into data objects with only one type,
 * provided such a convertion is possible. Object types are never converted to except in the case an object was
 * converted from an object and convertion back to an object of the same class is requested. Even though a class may
 * have a constructor that could be called with a primitive data type, for example ArrayList has a constructor taking a
 * single int argument specifying the initial size of list to create, the type converter would not attempt to call that
 * constructor for a multi type data object that can take a legal int value.
 *
 * <p/>Convertion to simply typed objects always returns wrapper class objects. For example the following class would
 * both return objects of class Integer (provided such a convertion is possible) even though one requests an object of
 * class int.class, because the primitive type int cannot be returned as an Object:
 *
 * <pre>
 * Object result = TypeConverter.convert(data, int.class);
 * Object result = TypeConverter.convert(data, Integer.class);
 * </pre>
 *
 * <p/>In some cases it is desirable to know what the best convertion from a multi type data object to a set of possible
 * classes is. For example if a bean has multiple setter methods for a given property that can take different argument
 * types, which is the best type convertion to use to be able to call one of the setter methods? The method
 * {@link #bestMatchingConversion} decides this. It tries to match the native type of the data to one of the classes and
 * if this is not possible it tries to find a convertion to one of the types, Object, boolean, byte, char, short, int,
 * long, float, double, String, amongst the set of classes in that order.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Convert simple Java types into multi typed data objects.
 * <tr><td>Convert mutli typed data objects back into simply typed Java objects.
 * <tr><td>Find the best convertion from a multi typed object to a set of possible classes.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TypeConverter
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(TypeConverter.class.getName()); */

    /** Flag used to indicate that a multi typed data object can have the boolean type. */
    public static final int BOOLEAN = 1;

    /** Flag used to indicate that a multi typed data object can have the byte type. */
    public static final int BYTE = 2;

    /** Flag used to indicate that a multi typed data object can have the char type. */
    public static final int CHAR = 4;

    /** Flag used to indicate that a multi typed data object can have the short type. */
    public static final int SHORT = 8;

    /** Flag used to indicate that a multi typed data object can have the int type. */
    public static final int INT = 16;

    /** Flag used to indicate that a multi typed data object can have the long type. */
    public static final int LONG = 32;

    /** Flag used to indicate that a multi typed data object can have the float type. */
    public static final int FLOAT = 64;

    /** Flag used to indicate that a multi typed data object can have the double type. */
    public static final int DOUBLE = 128;

    /** Flag used to indicate that a multi typed data object can have the String type. */
    public static final int STRING = 256;

    /** Flag used to indicate that a multi typed data object can have the Object type. */
    public static final int OBJECT = 512;

    /**
     * Converts a boolean into a multi type data object.
     *
     * @param  b The boolean to convert.
     *
     * @return A multi type data object holding all the types that the boolean can be converted into.
     */
    public static MultiTypeData getMultiTypeData(boolean b)
    {
        // Convert the value to a String and return the set of types that that String can be converted to.
        MultiTypeData result = getMultiTypeData(Boolean.toString(b));
        result.nativeType = BOOLEAN;

        return result;
    }

    /**
     * Converts a byte into a multi type data object.
     *
     * @param  b The byte to convert.
     *
     * @return A multi type data object holding all the types that the byte can be converted into.
     */
    public static MultiTypeData getMultiTypeData(byte b)
    {
        // Convert the value to a String and return the set of types that that String can be converted to.
        MultiTypeData result = getMultiTypeData(Byte.toString(b));
        result.nativeType = BYTE;

        return result;
    }

    /**
     * Converts a char into a multi type data object.
     *
     * @param  c The char to convert.
     *
     * @return A multi type data object holding all the types that the char can be converted into.
     */
    public static MultiTypeData getMultiTypeData(char c)
    {
        // Convert the value to a String and return the set of types that that String can be converted to.
        MultiTypeData result = getMultiTypeData(Character.toString(c));
        result.nativeType = CHAR;

        return result;
    }

    /**
     * Converts a short into a multi type data object.
     *
     * @param  s The short to convert.
     *
     * @return A multi type data object holding all the types that the short can be converted into.
     */
    public static MultiTypeData getMultiTypeData(short s)
    {
        // Convert the value to a String and return the set of types that that String can be converted to.
        MultiTypeData result = getMultiTypeData(Short.toString(s));
        result.nativeType = SHORT;

        return result;
    }

    /**
     * Converts a int into a multi type data object.
     *
     * @param  i The int to convert.
     *
     * @return A multi type data object holding all the types that the int can be converted into.
     */
    public static MultiTypeData getMultiTypeData(int i)
    {
        // Convert the value to a String and return the set of types that that String can be converted to.
        MultiTypeData result = getMultiTypeData(Integer.toString(i));
        result.nativeType = INT;

        return result;
    }

    /**
     * Converts a long into a multi type data object.
     *
     * @param  l The long to convert.
     *
     * @return A multi type data object holding all the types that the long can be converted into.
     */
    public static MultiTypeData getMultiTypeData(long l)
    {
        // Convert the value to a String and return the set of types that that String can be converted to.
        MultiTypeData result = getMultiTypeData(Long.toString(l));
        result.nativeType = LONG;

        return result;
    }

    /**
     * Converts a float into a multi type data object.
     *
     * @param  f The float to convert.
     *
     * @return A multi type data object holding all the types that the float can be converted into.
     */
    public static MultiTypeData getMultiTypeData(float f)
    {
        // Convert the value to a String and return the set of types that that String can be converted to.
        MultiTypeData result = getMultiTypeData(Float.toString(f));
        result.nativeType = FLOAT;

        return result;
    }

    /**
     * Converts a double into a multi type data object.
     *
     * @param  d The double to convert.
     *
     * @return A multi type data object holding all the types that the double can be converted into.
     */
    public static MultiTypeData getMultiTypeData(double d)
    {
        // Convert the value to a String and return the set of types that that String can be converted to.
        MultiTypeData result = getMultiTypeData(Double.toString(d));
        result.nativeType = DOUBLE;

        return result;
    }

    /**
     * Converts a String into a multi type data object.
     *
     * <p/>This method will return the native type as String. Methods that use this one to convert other types via
     * String should override this with the correct native type.
     *
     * @param  s The String to convert.
     *
     * @return A multi type data object holding all the types that the String can be converted into.
     */
    public static MultiTypeData getMultiTypeData(String s)
    {
        MultiTypeData result = new MultiTypeData();

        // Start by assuming that the String can only be converted to a String.
        result.typeFlags = STRING;
        result.stringValue = s;

        // Assume that the native type is String. It is up to methods that call this one to override this if this is
        // not the case.
        result.nativeType = STRING;

        // Check if the string can be converted to a boolean.
        if ("true".equals(s))
        {
            result.booleanValue = true;
            result.typeFlags |= BOOLEAN;
        }
        else if ("false".equals(s))
        {
            result.booleanValue = false;
            result.typeFlags |= BOOLEAN;
        }

        // Check if the string can be converted to an int.
        try
        {
            result.intValue = Integer.parseInt(s);
            result.typeFlags |= INT;
        }
        catch (NumberFormatException e)
        {
            // Exception noted so can be ignored.
            e = null;

            result.typeFlags &= (Integer.MAX_VALUE - INT);
        }

        // Check if the string can be converted to a byte.
        try
        {
            result.byteValue = Byte.parseByte(s);
            result.typeFlags |= BYTE;
        }
        catch (NumberFormatException e)
        {
            // Exception noted so can be ignored.
            e = null;

            result.typeFlags = (Integer.MAX_VALUE - BYTE);
        }

        // Check if the string can be converted to a char.
        if (s.length() == 1)
        {
            result.charValue = s.charAt(0);
            result.typeFlags |= CHAR;
        }

        // Check if the string can be converted to a short.
        try
        {
            result.shortValue = Short.parseShort(s);
            result.typeFlags |= SHORT;
        }
        catch (NumberFormatException e)
        {
            // Exception noted so can be ignored.
            e = null;

            result.typeFlags = (Integer.MAX_VALUE - SHORT);
        }

        // Check if the string can be converted to a long.
        try
        {
            result.longValue = Long.parseLong(s);
            result.typeFlags |= LONG;
        }
        catch (NumberFormatException e)
        {
            // Exception noted so can be ignored.
            e = null;

            result.typeFlags = (Integer.MAX_VALUE - LONG);
        }

        // Check if the string can be converted to a float.
        try
        {
            result.floatValue = Float.parseFloat(s);
            result.typeFlags |= FLOAT;
        }
        catch (NumberFormatException e)
        {
            // Exception noted so can be ignored.
            e = null;

            result.typeFlags = (Integer.MAX_VALUE - FLOAT);
        }

        // Check if the string can be converted to a double.
        try
        {
            result.doubleValue = Double.parseDouble(s);
            result.typeFlags |= DOUBLE;
        }
        catch (NumberFormatException e)
        {
            // Exception noted so can be ignored.
            e = null;

            result.typeFlags = (Integer.MAX_VALUE - DOUBLE);
        }

        // Assume the string can never be converted to an object.
        return result;
    }

    /**
     * Converts a Object into a multi type data object.
     *
     * @param  o The Object to convert.
     *
     * @return A multi type data object holding all the types that the Object can be converted into.
     */
    public static MultiTypeData getMultiTypeData(Object o)
    {
        // Convert the value to a String and return the set of types that that String can be converted to.
        MultiTypeData result = getMultiTypeData(o.toString());
        result.nativeType = OBJECT;

        return result;
    }

    /**
     * Given a multi type data object and a class representing a type, this method attemps to return an object of that
     * class created from the multi type data. The exception to this rule is if the specified data type is a primtive
     * type, such as int.clas, then the returned object will be of the equivalent wrapper class type, Integer.class in
     * this case. This is because a primitive cannot be returned under an Object return type.
     *
     * @param     d The multi type data object to convert.
     * @param     c The class to convert to.
     *
     * @return    An object of the specified class which is the result of converting the multi type data object to that
     *            class.
     *
     * @exception ClassCastException Is thrown if the multi type data object cannot be converted to the specified class.
     */
    public static Object convert(MultiTypeData d, Class c)
    {
        // Check if it is an boolean convertion.
        if (((d.typeFlags & BOOLEAN) != 0) && (Boolean.TYPE.equals(c) || Boolean.class.equals(c)))
        {
            return d.booleanValue;
        }

        // Check if it is an int convertion.
        else if (((d.typeFlags & INT) != 0) && (Integer.TYPE.equals(c) || Integer.class.equals(c)))
        {
            return d.intValue;
        }

        // Check if it is an char convertion.
        else if (((d.typeFlags & CHAR) != 0) && (Character.TYPE.equals(c) || Character.class.equals(c)))
        {
            return d.charValue;
        }

        // Check if it is an byte convertion.
        else if (((d.typeFlags & BYTE) != 0) && (Byte.TYPE.equals(c) || Byte.class.equals(c)))
        {
            return d.byteValue;
        }

        // Check if it is an short convertion.
        else if (((d.typeFlags & SHORT) != 0) && (Short.TYPE.equals(c) || Short.class.equals(c)))
        {
            return d.shortValue;
        }

        // Check if it is an long convertion.
        else if (((d.typeFlags & LONG) != 0) && (Long.TYPE.equals(c) || Long.class.equals(c)))
        {
            return d.longValue;
        }

        // Check if it is an float convertion.
        else if (((d.typeFlags & FLOAT) != 0) && (Float.TYPE.equals(c) || Float.class.equals(c)))
        {
            return d.floatValue;
        }

        // Check if it is an double convertion.
        else if (((d.typeFlags & DOUBLE) != 0) && (Double.TYPE.equals(c) || Double.class.equals(c)))
        {
            return d.doubleValue;
        }

        // Check if it is a string convertion.
        else if (((d.typeFlags & STRING) != 0) && String.class.equals(c))
        {
            return d.stringValue;
        }

        // Check if it is an object convertion and th object types match.
        else if (((d.typeFlags & OBJECT) != 0) && d.objectValue.getClass().equals(c))
        {
            return d.objectValue;
        }

        // Throw a class cast exception if the multi data type cannot be converted to the specified class.
        else
        {
            throw new ClassCastException("The multi data type, " + d + ", cannot be converted to the class, " + c +
                ".");
        }
    }

    /**
     * For a set of types this method selects the best type to convert a given multi type data object into. This method
     * can be usefull when deciding which of several setter methods on a bean is the best one to convert the multi type
     * data obejct into before calling the beans setter method.
     *
     * <p/>This method tries first to match the native type of the multi type data and then tries the other types in
     * this order: Object, boolean, char, byte, short, int, long, float, double, String. The Object type is only matched
     * if the multi type data object has a native Object type and the class of that object exactly matches one of the
     * possible classes to convert to.
     *
     * <p/>If the set of classes to convert to contains a primitive class type, such as int.class, and this is matched
     * the primitive class will be returned, int.class in this case. If the set of classes to convert to contains a
     * wrapper class, such as Integer.class, and this is matched then this will be returned. The primitive classes are
     * always matched first so if both the primitive and wrapper versions are in the set to be matched then the
     * primitive version will be returned.
     *
     * @param  d     The multi type data object to test for best convertion.
     * @param  types The possible set of classes to convert to.
     *
     * @return The best matching class or null if none can be matched.
     */
    public static Class bestMatchingConversion(MultiTypeData d, Collection<Class> types)
    {
        /*log.fine("public static Class bestMatchingConvertion(MultiTypeData d, Set<Class> types): called");*/
        /*log.fine("d = " + d);*/
        /*log.fine("types = " + types);*/

        // Try to match the native type first before trying the convertions.
        switch (d.nativeType)
        {
        case OBJECT:
        {
            // Check if the matching Object class can be found in the set of possible classes.
            if (types.contains(d.objectValue.getClass()))
            {
                return d.objectValue.getClass();
            }

            break;
        }

        case BOOLEAN:
        {
            // Check if boolean is in the set of possible classes to match.
            if (types.contains(boolean.class))
            {
                return boolean.class;
            }
            else if (types.contains(Boolean.class))
            {
                return Boolean.class;
            }

            break;
        }

        case CHAR:
        {
            // Check if char is in the set of possible classes to match.
            if (types.contains(char.class))
            {
                return char.class;
            }
            else if (types.contains(Character.class))
            {
                return Character.class;
            }

            break;
        }

        case BYTE:
        {
            // Check if byte is in the set of possible classes to match.
            if (types.contains(byte.class))
            {
                return byte.class;
            }
            else if (types.contains(Byte.class))
            {
                return Byte.class;
            }

            break;
        }

        case SHORT:
        {
            // Check if short is in the set of possible classes to match.
            if (types.contains(short.class))
            {
                return short.class;
            }
            else if (types.contains(Short.class))
            {
                return Short.class;
            }

            break;
        }

        case INT:
        {
            // Check if int is in the set of possible classes to match.
            if (types.contains(int.class))
            {
                return int.class;
            }
            else if (types.contains(Integer.class))
            {
                return Integer.class;
            }

            break;
        }

        case LONG:
        {
            // Check if long is in the set of possible classes to match.
            if (types.contains(long.class))
            {
                return long.class;
            }
            else if (types.contains(Long.class))
            {
                return Long.class;
            }

            break;
        }

        case FLOAT:
        {
            // Check if float is in the set of possible classes to match.
            if (types.contains(float.class))
            {
                return float.class;
            }
            else if (types.contains(Float.class))
            {
                return Float.class;
            }

            break;
        }

        case DOUBLE:
        {
            // Check if double is in the set of possible classes to match.
            if (types.contains(double.class))
            {
                return double.class;
            }
            else if (types.contains(Double.class))
            {
                return Double.class;
            }

            break;
        }

        case STRING:
        {
            // Check if String is in the set of possible classes to match.
            if (types.contains(String.class))
            {
                return String.class;
            }

            break;
        }

        default:
        {
            throw new IllegalStateException("Unknown MultiTypeData type.");
        }
        }

        // Check if the multi type can be converted to a boolean and boolean is in the set of possible convertions.
        if (((d.typeFlags & BOOLEAN) != 0) && types.contains(boolean.class))
        {
            return boolean.class;
        }
        else if (((d.typeFlags & BOOLEAN) != 0) && types.contains(Boolean.class))
        {
            return Boolean.class;
        }

        // Check if the multi type can be converted to a byte and byte is in the set of possible convertions.
        else if (((d.typeFlags & BYTE) != 0) && types.contains(byte.class))
        {
            return byte.class;
        }
        else if (((d.typeFlags & BYTE) != 0) && types.contains(Byte.class))
        {
            return Byte.class;
        }

        // Check if the multi type can be converted to a char and char is in the set of possible convertions.
        else if (((d.typeFlags & CHAR) != 0) && types.contains(char.class))
        {
            return char.class;
        }
        else if (((d.typeFlags & CHAR) != 0) && types.contains(Character.class))
        {
            return Character.class;
        }

        // Check if the multi type can be converted to a short and short is in the set of possible convertions.
        else if (((d.typeFlags & SHORT) != 0) && types.contains(short.class))
        {
            return short.class;
        }
        else if (((d.typeFlags & SHORT) != 0) && types.contains(Short.class))
        {
            return Short.class;
        }

        // Check if the multi type can be converted to a int and int is in the set of possible convertions.
        else if (((d.typeFlags & INT) != 0) && types.contains(int.class))
        {
            return int.class;
        }
        else if (((d.typeFlags & INT) != 0) && types.contains(Integer.class))
        {
            return Integer.class;
        }

        // Check if the multi type can be converted to a long and long is in the set of possible convertions.
        else if (((d.typeFlags & LONG) != 0) && types.contains(long.class))
        {
            return long.class;
        }
        else if (((d.typeFlags & LONG) != 0) && types.contains(Long.class))
        {
            return Long.class;
        }

        // Check if the multi type can be converted to a float and float is in the set of possible convertions.
        else if (((d.typeFlags & FLOAT) != 0) && types.contains(float.class))
        {
            return float.class;
        }
        else if (((d.typeFlags & FLOAT) != 0) && types.contains(Float.class))
        {
            return Float.class;
        }

        // Check if the multi type can be converted to a double and double is in the set of possible convertions.
        else if (((d.typeFlags & DOUBLE) != 0) && types.contains(double.class))
        {
            return double.class;
        }
        else if (((d.typeFlags & DOUBLE) != 0) && types.contains(Double.class))
        {
            return Double.class;
        }

        // Check if the multi type can be converted to a String and String is in the set of possible convertions.
        else if (((d.typeFlags & STRING) != 0) && types.contains(String.class))
        {
            return String.class;
        }

        // No matching type convertion found so return null.
        else
        {
            return null;
        }
    }

    /**
     * MultiTypeData encapsulates the primitive Java data types plus Object and String. It is used to hold data where it
     * is possible that the data could be represented using different types. For example 1 can be a byte, a character, a
     * short, an int, a long or a string, 6000000000 could only be a long or a string. All the possible types that a
     * piece of data can legally have are populated with that data and the set of type flags are used to indicate which
     * data types are populated.
     */
    public static class MultiTypeData
    {
        /** Holds the boolean value of this multi type object if it has one. */
        public boolean booleanValue;

        /** Holds the byte value of this multi type object if it has one. */
        public byte byteValue;

        /** Holds the char value of this multi type object if it has one. */
        public char charValue;

        /** Holds the short value of this multi type object if it has one. */
        public short shortValue;

        /** Holds the int value of this multi type object if it has one. */
        public int intValue;

        /** Holds the long value of this multi type object if it has one. */
        public long longValue;

        /** Holds the float value of this multi type object if it has one. */
        public float floatValue;

        /** Holds the double value of this multi type object if it has one. */
        public double doubleValue;

        /** Holds the String value of this multi type object if it has one. */
        public String stringValue;

        /** Holds the Object value of this multi type object if it has one. */
        public Object objectValue;

        /** Holds the set of type flags indicating which types this multi type object can take. */
        public int typeFlags;

        /** Holds the type flag indicating the type of the original data that this object was built from. */
        public int nativeType;

        /**
         * Prints the contents of this mutli type data to a string. This can be very usefull for debugging purposes.
         *
         * @return A string representation of this multi type data object.
         */
        public String toString()
        {
            List<String> resultList = new ArrayList<String>();

            if ((typeFlags & BOOLEAN) != 0)
            {
                resultList.add(booleanValue + " : boolean");
            }

            if ((typeFlags & CHAR) != 0)
            {
                resultList.add(charValue + " : char");
            }

            if ((typeFlags & BYTE) != 0)
            {
                resultList.add(byteValue + " : byte");
            }

            if ((typeFlags & SHORT) != 0)
            {
                resultList.add(shortValue + " : short");
            }

            if ((typeFlags & INT) != 0)
            {
                resultList.add(intValue + " : int");
            }

            if ((typeFlags & LONG) != 0)
            {
                resultList.add(longValue + " : long");
            }

            if ((typeFlags & FLOAT) != 0)
            {
                resultList.add(floatValue + " : float");
            }

            if ((typeFlags & DOUBLE) != 0)
            {
                resultList.add(doubleValue + " : double");
            }

            if ((typeFlags & STRING) != 0)
            {
                resultList.add(stringValue + " : String");
            }

            if ((typeFlags & OBJECT) != 0)
            {
                resultList.add(objectValue.toString() + " : Object");
            }

            String result = "{ ";

            for (Iterator<String> i = resultList.iterator(); i.hasNext();)
            {
                result += i.next() + (i.hasNext() ? ", " : "");
            }

            result += " }";

            return result;
        }
    }
}
