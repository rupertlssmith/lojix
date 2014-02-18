/*
 * Copyright The Sett Ltd, 2005 to 2009.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides helper methods for operating on classes and methods using reflection. Reflection methods tend to return a
 * lot of checked exception so writing code to use them can be tedious and harder to read, especially when such errors
 * are not expected to occur. This class always works with {@link ReflectionUtilsException}, which is a runtime
 * exception, to wrap the checked exceptions raised by the standard Java reflection methods. Code using it does not
 * normally expect these errors to occur, usually does not have a recovery mechanism for them when they do, but is
 * cleaner, quicker to write and easier to read in the majority of cases.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Look up Classes by name.
 * <tr><td>Instantiate Classes by no-arg constructor.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ReflectionUtils
{
    /* private static final Logger log = Logger.getLogger(ReflectionUtils.class.getName()); */
    /**
     * Checks if the named class exists and is loadable.
     *
     * @param  className The class to check for.
     *
     * @return <tt>true</tt> if the named class exists and is loadable.
     */
    public static boolean classExistsAndIsLoadable(String className)
    {
        try
        {
            Class.forName(className);

            return true;
        }
        catch (ClassNotFoundException e)
        {
            // Exception noted and ignored.
            e = null;

            return false;
        }
    }

    /**
     * Checks if the named class exists and is loadable and is a sub-type of the specified class.
     *
     * @param  parent    The class to check that the named class is a sub-type of.
     * @param  className The class to check for.
     *
     * @return <tt>true</tt> if the named class exists and is loadable and is a sub-type of the specified class.
     */
    public static boolean isSubTypeOf(Class parent, String className)
    {
        try
        {
            Class cls = Class.forName(className);

            return parent.isAssignableFrom(cls);
        }
        catch (ClassNotFoundException e)
        {
            // Exception noted and ignored.
            e = null;

            return false;
        }
    }

    /**
     * Checks that the named child class is the same type or a sub-type of the named parent class.
     *
     * @param  parent The parent class name.
     * @param  child  The child class name.
     *
     * @return <tt>true</tt> if the named child class is the same type or a sub-type of the named parent class, <tt>
     *         false</tt> otherwise.
     */
    public static boolean isSubTypeOf(String parent, String child)
    {
        try
        {
            return isSubTypeOf(Class.forName(parent), Class.forName(child));
        }
        catch (ClassNotFoundException e)
        {
            // Exception noted so can be ignored.
            e = null;

            return false;
        }
    }

    /**
     * Checks that the child class is the same type or a sub-type of the parent class.
     *
     * @param  parentClass The parent class.
     * @param  childClass  The child class.
     *
     * @return <tt>true</tt> if the child class is the same type or a sub-type of the parent class, <tt>false</tt>
     *         otherwise.
     */
    public static boolean isSubTypeOf(Class parentClass, Class childClass)
    {
        try
        {
            // Check that the child class can be cast as a sub-type of the parent.
            childClass.asSubclass(parentClass);

            return true;
        }
        catch (ClassCastException e)
        {
            // Exception noted so can be ignored.
            e = null;

            return false;
        }
    }

    /**
     * Gets the Class object for a named class.
     *
     * @param  className The class to get the Class object for.
     *
     * @return The Class object for the named class.
     */
    public static Class<?> forName(String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new ReflectionUtilsException("ClassNotFoundException whilst finding class: " + className + ".", e);
        }
    }

    /**
     * Creates an instance of a Class, instantiated through its no-args constructor.
     *
     * @param  cls The Class to instantiate.
     * @param  <T> The Class type.
     *
     * @return An instance of the class.
     */
    public static <T> T newInstance(Class<T> cls)
    {
        try
        {
            return cls.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new ReflectionUtilsException("InstantiationException whilst instantiating class.", e);
        }
        catch (IllegalAccessException e)
        {
            throw new ReflectionUtilsException("IllegalAccessException whilst instantiating class.", e);
        }
    }

    /**
     * Calls a constuctor with the specified arguments.
     *
     * @param  constructor The constructor.
     * @param  args        The arguments.
     * @param  <T>         The Class type.
     *
     * @return An instance of the class that the constructor is for.
     */
    public static <T> T newInstance(Constructor<T> constructor, Object[] args)
    {
        try
        {
            return constructor.newInstance(args);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls a named method on an object with a specified set of parameters, any Java access modifier are overridden.
     *
     * @param  o            The object to call.
     * @param  method       The method name to call.
     * @param  params       The parameters to pass.
     * @param  paramClasses The argument types.
     *
     * @return The return value from the method call.
     */
    public static Object callMethodOverridingIllegalAccess(Object o, String method, Object[] params,
        Class[] paramClasses)
    {
        // Get the objects class.
        Class cls = o.getClass();

        // Get the classes of the parameters.
        /*Class[] paramClasses = new Class[params.length];

        for (int i = 0; i < params.length; i++)
        {
            paramClasses[i] = params[i].getClass();
        }*/

        try
        {
            // Try to find the matching method on the class.

            Method m = cls.getDeclaredMethod(method, paramClasses);

            // Make it accessible.
            m.setAccessible(true);

            // Invoke it with the parameters.
            return m.invoke(o, params);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls a named method on an object with a specified set of parameters.
     *
     * @param  o      The object to call.
     * @param  method The method name to call.
     * @param  params The parameters to pass.
     *
     * @return The return value from the method call.
     */
    public static Object callMethod(Object o, String method, Object[] params)
    {
        // Get the objects class.
        Class cls = o.getClass();

        // Get the classes of the parameters.
        Class[] paramClasses = new Class[params.length];

        for (int i = 0; i < params.length; i++)
        {
            paramClasses[i] = params[i].getClass();
        }

        try
        {
            // Try to find the matching method on the class.
            Method m = cls.getMethod(method, paramClasses);

            // Invoke it with the parameters.
            return m.invoke(o, params);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls a named static method on a class with a specified set of parameters.
     *
     * @param  method The method name to call.
     * @param  params The parameters to pass.
     *
     * @return The return value from the method call.
     */
    public static Object callStaticMethod(Method method, Object[] params)
    {
        try
        {
            return method.invoke(null, params);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the constructor of a class that takes the specified set of arguments if any matches. If no matching
     * constructor is found then a runtime exception is raised.
     *
     * @param  cls  The class to get a constructor from.
     * @param  args The arguments to match.
     * @param  <T>  The class type.
     *
     * @return The constructor.
     */
    public static <T> Constructor<T> getConstructor(Class<T> cls, Class[] args)
    {
        try
        {
            return cls.getConstructor(args);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds the argument types of all setter methods on a bean for a given property name. For a method to be a setter
     * method it must have a void return type, be public and accept only a single argument. Its name must be 'set'
     * followed by the property name.
     *
     * @param  obClass      The class to find all setter methods matching a given property for.
     * @param  propertyName The property name to find all matching setter methods for.
     *
     * @return An array of the types (as classes) of all the possible types that the setter method can be called with.
     */
    public static Set<Class> findMatchingSetters(Class obClass, String propertyName)
    {
        /*log.fine("private Set<Class> findMatchingSetters(Object ob, String propertyName): called");*/
        Set<Class> types = new HashSet<Class>();

        // Convert the first letter of the property name to upper case to match against the upper case version of
        // it that will be in the setter method name. For example the property test will have a setter method called
        // setTest.
        String upperPropertyName = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);

        // Scan through all the objects methods.
        Method[] methods = obClass.getMethods();

        for (Method nextMethod : methods)
        {
            // Get the next method.
            /*log.fine("nextMethod = " + nextMethod.getName());*/
            // Check if a method has the correct name, accessibility and the correct number of arguments to be a setter
            // method for the property.
            String methodName = nextMethod.getName();

            if (methodName.equals("set" + upperPropertyName) && Modifier.isPublic(nextMethod.getModifiers()) &&
                    (nextMethod.getParameterTypes().length == 1))
            {
                /*log.fine(methodName + " is a valid setter method for the property " + propertyName +
                        " with argument of type " + nextMethod.getParameterTypes()[0]);*/

                // Add its argument type to the array of setter types.
                types.add(nextMethod.getParameterTypes()[0]);
            }
        }

        return types;
    }
}
