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
package com.thesett.common.reflect;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.thesett.common.error.NotImplementedException;
import com.thesett.common.util.ReflectionUtils;
import com.thesett.common.util.TypeConverter;

/**
 * BeanMemento provides indirect access to the properties of a bean, allowing them to be accessed by name. It is related
 * to the {@link DirectMemento} class. The difference between this class and that one is that this one only provides
 * access to the bean properties of an object, that is those that have 'getter' and 'setter' methods. The
 * {@link DirectMemento} class bypasses the getter and setter methods and directly accesses the fields themselves. For
 * this reason {@link DirectMemento} is a stronger memento class for taking a snapshot of an objects internal state.
 * This BeanMemento class is usefull for accessing beans but cannot be relied upon to take a full snapshot of an objects
 * state. This is still a usefull memento class however, because for beans it is often only the properties that
 * constitute the significant state of the bean.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Create a memento from an object.
 * <tr><td>Write to an objects fields from a memento. <td> {@link TypeConverter}
 * <tr><td>Read field values.
 * <tr><td>Modifiy field values.
 * <tr><td>Get list of all fields.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Need to document solution to dealing with read only/write only properties of a bean. Also what to do when
 *         setters/getters are not accessibly? Bean methods should be public so I think to treat innaccesability as
 *         meaning that the property is unavailable to read or write and treat it the same as dealing with read
 *         only/write only properties.
 */
public class BeanMemento implements Memento, Serializable
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(BeanMemento.class.getName()); */

    /** Holds the properties of the object that this memento has captured. */
    private final Map<String, Object> values = new HashMap<String, Object>();

    /** The object that this is a memento for. */
    Object ob;

    /**
     * Builds a bean memento on the specified object. The object is not captured by this constructor, a seperate call to
     * the {@link #capture()} method must be made to do that.
     *
     * @param ob The object to create a memento on.
     */
    public BeanMemento(Object ob)
    {
        this.ob = ob;
    }

    /**
     * Restores the properties currently in this memento to the specified object.
     *
     * @param  ob     The object to which the values from this memento should be restored.
     * @param  values The map of values to restore.
     *
     * @throws NoSuchFieldException If a setter method could not be found for a property.
     */
    public static void restoreValues(Object ob, Map<String, Object> values) throws NoSuchFieldException
    {
        /*log.fine("public void restore(Object ob): called");*/
        /*log.fine("Object to restore to has the type: " + ob.getClass());*/

        // Get the class of th object to restore to.
        Class obClass = ob.getClass();

        // Loop over all the stored properties.
        for (String propName : values.keySet())
        {
            // Get the cached property from this mementos store.
            Object nextValue = values.get(propName);
            /*log.fine("Next property to restore is: " + propName);*/
            /*log.fine("Next value to restore is: " + nextValue);*/

            // Used to hold the value to set.
            Object paramValue;

            // Used to hold the type of the value to set.
            Class paramType;

            // Check if the value store is a null.
            if (nextValue == null)
            {
                paramValue = null;
                paramType = null;
            }

            // Check if the value to store is a multi type data object.
            else if (nextValue instanceof TypeConverter.MultiTypeData)
            {
                /*log.fine("The value to restore is a multi typed data object.");*/

                TypeConverter.MultiTypeData multiValue = (TypeConverter.MultiTypeData) nextValue;

                // Get the types (classes) of all the possible 'setter' methods for the property.
                Set<Class> setterTypes = ReflectionUtils.findMatchingSetters(ob.getClass(), propName);
                /*log.fine("setterTypes = " + setterTypes);*/

                // Use the type converter to get the best matching type with the multi data.
                paramType = TypeConverter.bestMatchingConversion(multiValue, setterTypes);

                // Convert the multi data to an object of the appropriate type.
                paramValue = TypeConverter.convert(multiValue, paramType);
            }

            // The value to store is not a multi type.
            else
            {
                /*log.fine("The value to restore is a simply typed data object.");*/

                // Get the type and value of the plain type to set.
                paramValue = nextValue;
                paramType = nextValue.getClass();
            }

            /*log.fine("paramValue = " + paramValue);*/
            /*log.fine("paramType = " + paramType);*/

            // Call the setter method with the new property value, checking first that the property has a matching
            // 'setter' method.
            Method setterMethod;

            try
            {
                // Convert the first letter of the property name to upper case to match against the upper case version
                // of it that will be in the setter method name. For example the property test will have a setter method
                // called setTest.
                String upperPropertyName = Character.toUpperCase(propName.charAt(0)) + propName.substring(1);

                // Try to find an appropriate setter method on the object to call.
                setterMethod = obClass.getMethod("set" + upperPropertyName, paramType);

                // Call the setter method with the new property value.
                Object[] params = new Object[] { paramValue };
                setterMethod.invoke(ob, params);
            }
            catch (NoSuchMethodException e)
            {
                // Do nothing as properties may have getters but no setter for read only properties.
                /*log.log(java.util.logging.Level.FINE, "A setter method could not be found for " + propName + ".", e);*/

                /*
                // The object does not have a matching setter method for the type.
                NoSuchFieldException nsfe = new NoSuchFieldException("The object does not have a matching setter " +
                                                                     "method 'set" + propName + "'.");
                nsfe.initCause(e);
                throw nsfe;
                */
            }
            catch (IllegalAccessException e)
            {
                /*log.log(java.util.logging.Level.FINE, "IllegalAccessException during call to setter method.", e);*/
            }
            catch (InvocationTargetException e)
            {
                /*log.log(java.util.logging.Level.FINE, "InvocationTargetException during call to setter method.", e);*/
            }
        }
    }

    /** Captures an objects properties in this memento. */
    public void capture()
    {
        capture(false);

    }

    /** {@inheritDoc} */
    public void captureNonNull()
    {
        capture(true);
    }

    /**
     * Restores the properties currently in this memento to the specified object.
     *
     * @param  ob The object to which the values from this memento should be restored.
     *
     * @throws NoSuchFieldException If a setter method could not be found for a property.
     */
    public void restore(Object ob) throws NoSuchFieldException
    {
        restoreValues(ob, values);
    }

    /**
     * Gets the value of the named property of the specified class.
     *
     * @param  cls      The class in which the property to get is declared. This is ignored as only properties of the
     *                  top-level bean class are used by this memento. Properties higher up the inheritence chain can be
     *                  overriden.
     * @param  property The name of the property.
     *
     * @return The object value of the property.
     *
     * @throws NoSuchFieldException If the named field does not exist on the class.
     */
    public Object get(Class cls, String property) throws NoSuchFieldException
    {
        // Check that the field exists.
        if (!values.containsKey(property))
        {
            throw new NoSuchFieldException("The property, " + property + ", does not exist on the underlying class.");
        }

        // Try to find a matching property cached in this memento.
        return values.get(property);
    }

    /**
     * Sets the value of the named property as a multi type object.
     *
     * @param cls      The class in which the property is declared. This is ignored as only properties of the top-level
     *                 bean class are used by this memento. Properties higher up the inheritance chain can be overriden.
     * @param property The name of the property to set.
     * @param value    The multi type object to set that value from.
     */
    public void put(Class cls, String property, TypeConverter.MultiTypeData value)
    {
        /*log.fine("public void put(String property, TypeConverter.MultiTypeData value): called");*/
        /*log.fine("property  = " + property);*/
        /*log.fine("value = " + value);*/

        // Store the multi typed data under the specified property name.
        values.put(property, value);
    }

    /**
     * Places the specified value into the memento based on the property's declaring class and name.
     *
     * @param cls      The class in which the property is declared. This is ignored as only properties of the top-level
     *                 bean class are used by this memento. Properties higher up the inheritance chain can be overriden.
     * @param property The name of the property.
     * @param value    The value to store into this memento.
     */
    public void put(Class cls, String property, Object value)
    {
        // Store the new data under the specified property name.
        values.put(property, value);
    }

    /**
     * Generates a list of all the fields of the object that this memento maps for a given class.
     *
     * @param  cls The class to get all field names for.
     *
     * @return A collection of the field names or null if the specified class is not part of the objects class hierarchy
     *         chain.
     */
    public Collection getAllFieldNames(Class cls)
    {
        throw new NotImplementedException();
    }

    /**
     * Captures the fields of the associated object.
     *
     * @param ignoreNull <tt>true</tt> iff null fields should be ignored, <tt>false</tt> if null fields should be
     *                   captured as nulls.
     */
    private void capture(boolean ignoreNull)
    {
        // Get the class of the object to build a memento for.
        Class cls = ob.getClass();

        // Iterate through all the public methods of the class including all super-interfaces and super-classes.
        Method[] methods = cls.getMethods();

        for (Method nextMethod : methods)
        {
            // Get the next method.
            /*log.fine("nextMethod = " + nextMethod.getName());*/

            // Check if the method is a 'getter' method, is public and takes no arguments.
            String methodName = nextMethod.getName();

            if (methodName.startsWith("get") && (methodName.length() >= 4) &&
                    Character.isUpperCase(methodName.charAt(3)) && Modifier.isPublic(nextMethod.getModifiers()) &&
                    (nextMethod.getParameterTypes().length == 0))
            {
                String propName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                /*log.fine(methodName + " is a valid getter method for the property " + propName + ".");*/

                try
                {
                    // Call the 'getter' method to extract the properties value.
                    Object[] params = new Object[] {};
                    Object value = nextMethod.invoke(ob, params);
                    /*log.fine("The result of calling the getter method is: " + value);*/

                    // Store the property value for the object.
                    if (!ignoreNull || (value != null))
                    {
                        values.put(propName, value);
                    }
                }
                catch (IllegalAccessException e)
                {
                    /*log.log(java.util.logging.Level.FINE, "IllegalAccessException during call to getter method.", e);*/
                    throw new IllegalStateException(e);
                }
                catch (InvocationTargetException e)
                {
                    /*log.log(java.util.logging.Level.FINE, "InvocationTargetException during call to getter method.", e);*/
                    throw new IllegalStateException(e);
                }
            }
            // Should also check if the method is a 'setter' method, is public and takes exactly one argument.
        }
    }
}
