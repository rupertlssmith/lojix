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
package com.thesett.aima.state.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PropertyIntrospectorBase provides methods for instrospecting and calling the setter and getter methods of beans.
 *
 * <p/>The getter methods must take no arguments and the setter methods must take exactly one argument. Calling getter
 * methods to extract values is straight forward because there can only ever be one getter method for each property
 * name. Calling setter methods can be more complex because there may be multiple setter methods for each property name,
 * each of which may be called with arguments of different types. This implementation provides a way of matching up the
 * type of the actual arguments given with a setter method that accepts arguments of that type.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Get properties as attributes by name.
 * <tr><td> Set properties from attribute by name.
 * <tr><td> Check if properties exist on a bean.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class PropertyIntrospectorBase
{
    /* private static final Logger log = Logger.getLogger(PropertyIntrospectorBase.class.getName()); */

    /** Holds a cache of getter methods by property name. */
    private Map<String, Method> getters = new HashMap<String, Method>();

    /** Holds a cache of setter methods by property name. */
    private Map<String, Method[]> setters = new HashMap<String, Method[]>();

    /** A flag used to indicate that initialization has been performed. */
    private boolean initialized;

    /**
     * Checks if the bean has a named property. Note that if the property value is set to null on the bean, this method
     * will still return true, it tests for the existance of a named property, including null ones.
     *
     * @param  property The property to check if this state contains.
     *
     * @return <tt>true</tt> if this state has the property, <tt>false</tt> if it does not.
     */
    public boolean hasProperty(String property)
    {
        // Check if a getter method exists for the property.
        Method getterMethod = getters.get(property);

        return getterMethod != null;
    }

    /**
     * Sets the value of a property of the bean by name.
     *
     * @param callee   The object to call the setter method on.
     * @param property The name of the property to set.
     * @param value    The value of the property to set.
     */
    protected void setProperty(Object callee, String property, Object value)
    {
        // Initialize this meta bean if it has not already been initialized.
        if (!initialized)
        {
            initialize(callee);
        }

        // Check that at least one setter method exists for the property.
        Method[] setterMethods = setters.get(property);

        if ((setterMethods == null) || (setterMethods.length == 0))
        {
            throw new IllegalArgumentException("No setter method for the property " + property + " exists.");
        }

        // Choose which setter method to call based on the type of the value argument. If the value argument is null
        // then call the first available one.
        Method setterMethod = null;
        Class valueType = (value == null) ? null : value.getClass();

        // Check if the value is null and use the first available setter if so, as type cannot be extracted.
        if (value == null)
        {
            setterMethod = setterMethods[0];
        }

        // Loop through the available setter methods for one that matches the arguments type.
        else
        {
            for (Method method : setterMethods)
            {
                Class argType = method.getParameterTypes()[0];

                if (argType.isAssignableFrom(valueType))
                {
                    setterMethod = method;

                    break;
                }

                // Check if the arg type is primitive but the value type is a wrapper type that matches it.
                else if (argType.isPrimitive() && !valueType.isPrimitive() &&
                        isAssignableFromPrimitive(valueType, argType))
                {
                    setterMethod = method;

                    break;
                }

                // Check if the arg type is a wrapper but the value type is a primitive type that matches it.
                else if (valueType.isPrimitive() && !argType.isPrimitive() &&
                        isAssignableFromPrimitive(argType, valueType))
                {
                    setterMethod = method;

                    break;
                }
            }

            // Check if this point has been reached but no matching setter method could be found, in which case raise
            // an exception.
            if (setterMethod == null)
            {
                Class calleeType = (callee == null) ? null : callee.getClass();

                throw new IllegalArgumentException("No setter method for property " + property + ", of type, " +
                    calleeType + " will accept the type of value specified, " + valueType + ".");
            }
        }

        // Call the setter method with the value.
        try
        {
            Object[] args = new Object[] { value };
            setterMethod.invoke(callee, args);
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalArgumentException("The setter method for the property " + property +
                " threw an invocation target exception.", e);
        }

        // This should never happen as the initiliazed method should already have checked this.
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException("The setter method for the property " + property + " cannot be accessed.", e);
        }
    }

    /**
     * Returns a single named property of the bean.
     *
     * @param  callee   The object to call the setter method on.
     * @param  property The property of this state to get the value of.
     *
     * @return A single Object value of the bean for the specified property name.
     */
    protected Object getProperty(Object callee, String property)
    {
        // Initialize this meta bean if it has not already been initialized.
        if (!initialized)
        {
            initialize(callee);
        }

        // Check if a getter method exists for the property being fetched.
        Method getterMethod = getters.get(property);

        if (getterMethod == null)
        {
            throw new IllegalArgumentException("No getter method for the property " + property + " exists.");
        }

        // Fetch the value by calling the getter method.
        Object result;

        try
        {
            result = getterMethod.invoke(callee);
        }

        // This should never happen as the initiliazation method should already have checked this.
        catch (InvocationTargetException e)
        {
            throw new IllegalStateException("The getter method for the property " + property +
                " threw an invocation target exception.", e);
        }

        // This should never happen as the initiliazation method should already have checked this.
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException("The getter method for the property " + property + " cannot be accessed.", e);
        }

        return result;
    }

    /**
     * Checks if a wrapper type is assignable from a primtive type.
     *
     * @param  wrapperType   The wrapper type.
     * @param  primitiveType The primitive type.
     *
     * @return <tt>true</tt> if the wrapper type can be assinged from the primitive.
     */
    private boolean isAssignableFromPrimitive(Class wrapperType, Class primitiveType)
    {
        boolean result = false;

        if (primitiveType.equals(boolean.class) && wrapperType.equals(Boolean.class))
        {
            result = true;
        }
        else if (primitiveType.equals(byte.class) && wrapperType.equals(Byte.class))
        {
            result = true;
        }
        else if (primitiveType.equals(char.class) && wrapperType.equals(Character.class))
        {
            result = true;
        }
        else if (primitiveType.equals(short.class) && wrapperType.equals(Short.class))
        {
            result = true;
        }
        else if (primitiveType.equals(int.class) && wrapperType.equals(Integer.class))
        {
            result = true;
        }
        else if (primitiveType.equals(long.class) && wrapperType.equals(Long.class))
        {
            result = true;
        }
        else if (primitiveType.equals(float.class) && wrapperType.equals(Float.class))
        {
            result = true;
        }
        else if (primitiveType.equals(double.class) && wrapperType.equals(Double.class))
        {
            result = true;
        }
        else
        {
            result = false;
        }

        return result;
    }

    /**
     * Initialized this property introspector on a specified object, building the caches of getter and setter methods.
     *
     * @param callee The object to call the getter and setter methods on.
     */
    private void initialize(Object callee)
    {
        // This is used to build up all the setter methods in.
        Map<String, List<Method>> settersTemp = new HashMap<String, List<Method>>();

        // Get all the property getters and setters on this class.
        Method[] methods = callee.getClass().getMethods();

        for (Method nextMethod : methods)
        {
            String methodName = nextMethod.getName();

            // Check if it is a getter method.
            if (methodName.startsWith("get") && (methodName.length() >= 4) &&
                    Character.isUpperCase(methodName.charAt(3)) && Modifier.isPublic(nextMethod.getModifiers()) &&
                    (nextMethod.getParameterTypes().length == 0))
            {
                String propertyName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);

                getters.put(propertyName, nextMethod);
            }

            // Check if it is a setter method.
            else if (methodName.startsWith("set") && Modifier.isPublic(nextMethod.getModifiers()) &&
                    (nextMethod.getParameterTypes().length == 1))
            {
                /*log.fine("Found setter method.");*/

                String propertyName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                /*log.fine("propertyName = " + propertyName);*/

                // Check the setter to see if any for this name already exist, and start a new list if not.
                List<Method> setterMethodsForName = settersTemp.get(propertyName);

                if (setterMethodsForName == null)
                {
                    setterMethodsForName = new ArrayList<Method>();
                    settersTemp.put(propertyName, setterMethodsForName);
                }

                // Add the setter method to the list of setter methods for the named property.
                setterMethodsForName.add(nextMethod);
            }
        }

        // Convert all the lists of setter methods into arrays.
        for (Map.Entry<String, List<Method>> entries : settersTemp.entrySet())
        {
            String nextPropertyName = entries.getKey();
            List<Method> nextMethodList = entries.getValue();

            Method[] methodArray = nextMethodList.toArray(new Method[nextMethodList.size()]);
            setters.put(nextPropertyName, methodArray);
        }

        // Initialization completed, set the initialized flag.
        initialized = true;
    }
}
