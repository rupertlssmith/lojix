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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.thesett.common.error.NotImplementedException;
import com.thesett.common.util.TypeConverter;

/**
 * A DirectMemento provides indirect access to the fields of an object, allowing them all to be accessed by name. This
 * allows other objects to access all of an object fields even if they have protect, private or package modifiers. This
 * enables the state of an object to be decoupled from the object itself. Through this mechanism any object can have a
 * snapshot of its state externalized or its state restored from such a snapshot.
 *
 * <p/>To give examples of some of the possible uses of this: in a persistence framework object snapshots can be
 * externalized into persistent storage and restored from there (like entity beans), in a GUI program the state of the
 * data or document model can pushed onto a stack from where it can be restored if the user requests an 'undo'
 * operation.
 *
 * <p/>This DirectMemento class provides the methods to access another objects internal state as well as providing the
 * storage into which to place the snapshot of the state. This class is also serializable, allowing the state to be
 * externalized.
 *
 * <p/>A class is just one step in an inheritance chain; an object is actually composed from many classes that layer on
 * top of each other. The only objects that are built from just one class are objects built from the Object class
 * itself. Every other object has at least one top level class, possibly many middle layers and always the Object class
 * at the root of its inheritence chain. Classes higher up the chain may replace the fields of classes further down the
 * chain with fields that have the same name. This means that a field name may not uniquely identify a field of an
 * object because it is possible for an object to have more than one field with the same name. For this reason, the
 * fields of an object ar always stored against the class in the hierarchy to which the field belongs. When objects are
 * captured the entire inheritence chain is scanned and all fields right back to the root Object are captured.
 *
 * <p/>The {@link #get}, {@link Memento#put(Class, String, TypeConverter.MultiTypeData)},
 * {@link #put(Class, String, Object)}, and {@link #getAllFieldNames} methods are only valid after an object has been
 * captured. Capture an object by using the appropriate constructor or by calling the {@link #capture} method directly
 * before using these methods.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Create a memento from an object.
 * <td> {@link Class}, {@link Field}
 * <tr><td>Write to an objects fields from a memento. <td>< {@link Class}, {@link Field}
 * <tr><td>Read field values.
 * <tr><td>Modifiy field values.
 * <tr><td>Get list of all fields.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Make this a base class into which different serialization methods can be plugged. For example, serialize to
 *         binary data, to a SQL update or create, to an XML document etc. This would provide greater flexibility over
 *         how state can be marshalled into external forms.
 * @todo   Consider the threading issue of this. Should obtain a lock on an objects monitor and read and write all its
 *         fields atomically.
 */
public class DirectMemento implements Memento, Serializable
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(DirectMemento.class.getName()); */

    /** The bitmask of attribute modifiers indicating that a field should not be saved into the memento. */
    public static final int NOSAVE = (Modifier.FINAL | Modifier.STATIC | Modifier.TRANSIENT);

    /** Holds the fields of the object that this memento has captured. */
    private Map values = new HashMap();

    /** The object that this is a memento for. */
    Object ob;

    /**
     * Builds a memento on the specified object. The object is not captured by this constructor, a seperate call to the
     * {@link #capture()} method must be made to do that.
     *
     * @param ob The object to create a memento on.
     */
    public DirectMemento(Object ob)
    {
        this.ob = ob;
    }

    /**
     * Helper method to determine whether or not a given field should be saved into this memento. A field should not be
     * saved if it is final, static, or transient.
     *
     * @param  f The field to be tested.
     *
     * @return True if the field should be saved.
     */
    public static boolean shouldBeSaved(Field f)
    {
        // Get the fields modifiers
        int mod = f.getModifiers();

        // Check that the field has no modifiers that mean it should not be saved.
        return (mod & DirectMemento.NOSAVE) == 0;
    }

    /** Captures an objects state in this memento. */
    public void capture()
    {
        // Get the class of the object to build a memento for.
        Class cls = ob.getClass();

        // Iterate through the classes whole inheritence chain.
        while (!cls.equals(Object.class))
        {
            // Get the classes fields.
            Field[] attrs = cls.getDeclaredFields();

            // Build a new map to put the fields in for the current class.
            HashMap map = new HashMap();

            // Cache the field values by the class name.
            values.put(cls, map);

            // Loop over all the fields in the current class.
            for (Field attr : attrs)
            {
                // Make the field accessible (it may be protected or private).
                attr.setAccessible(true);

                // Check that the field should be captured.
                if (shouldBeSaved(attr))
                {
                    // Use a try block as access to the field may fail, although this should not happen because
                    // even private, protected and package fields have been made accessible.
                    try
                    {
                        // Cache the field by its name.
                        map.put(attr.getName(), attr.get(ob));
                    }
                    catch (IllegalAccessException e)
                    {
                        // The field could not be accessed but all fields have been made accessible so this should
                        // not happen.
                        throw new RuntimeException("Field '" + attr.getName() +
                            "' could not be accessed but the 'setAccessible(true)' method was invoked on it.", e);
                    }
                }
            }

            // Get the superclass for the next step of the iteration over the whole inheritence chain.
            cls = cls.getSuperclass();
        }
    }

    /** {@inheritDoc} */
    public void captureNonNull()
    {
        throw new RuntimeException("Not implemented.");
    }

    /**
     * Restores the values currently in this memento to the specified object.
     *
     * @param  ob The object to which the values from this memento should be restored.
     *
     * @throws NoSuchFieldException If the object in question does not have a field for one of the memento values.
     */
    public void restore(Object ob) throws NoSuchFieldException
    {
        /*log.fine("public void map(Object ob): called");*/
        /*log.fine("class is " + ob.getClass());*/

        // Iterate over the whole inheritence chain.
        for (Object key : values.keySet())
        {
            // Get the next class from the cache.
            Class cls = (Class) key;

            // Get the cache of field values for the class.
            HashMap vals = (HashMap) values.get(cls);

            // Loop over all fields in the class.
            for (Object o : vals.keySet())
            {
                // Get the next field name.
                String attr = (String) o;

                // Get the next field value.
                Object val = vals.get(attr);

                // Get a reference to the field in the object.
                Field f = cls.getDeclaredField(attr);

                // Make the field accessible (it may be protected, package or private).
                f.setAccessible(true);

                // Use a try block as writing to the field may fail.
                try
                {
                    // Write to the field.
                    f.set(ob, val);
                }
                catch (IllegalAccessException e)
                {
                    // The field could not be written to but all fields have been made accessible so this should
                    // not happen.
                    throw new RuntimeException("Field '" + f.getName() +
                        "' could not be accessed but the 'setAccessible(true)' method was invoked on it.", e);
                }
            }
        }
    }

    /**
     * Gets the value of the named field of the specified class.
     *
     * @param  cls  The class in which the field to get is declared.
     * @param  attr The name of the field.
     *
     * @return The object value of the attribute.
     */
    public Object get(Class cls, String attr)
    {
        HashMap map;

        // See if the class exists in the cache.
        if (!values.containsKey(cls))
        {
            // Class not in cache so return null.
            return null;
        }

        // Get the cache of field values for the class.
        map = (HashMap) values.get(cls);

        // Extract the specified field from the cache.
        return map.get(attr);
    }

    /**
     * Sets the value of the named property as a multi type object.
     *
     * @param cls      The class in which the property is declared.
     * @param property The name of the property to set.
     * @param value    The multi type object to set that value from.
     */
    public void put(Class cls, String property, TypeConverter.MultiTypeData value)
    {
        throw new NotImplementedException();
    }

    /**
     * Places the specified value into the memento based on the field's declaring class and name.
     *
     * @param cls  The class in which the field is declared.
     * @param attr The name of the attribute.
     * @param val  The value to store into this memento.
     */
    public void put(Class cls, String attr, Object val)
    {
        /*log.fine("public void put(Class cls, String attr, Object val): called");*/
        /*log.fine("class name is " + cls.getName());*/
        /*log.fine("attribute is " + attr);*/
        /*log.fine("value to set is " + val);*/

        HashMap map;

        // Check that the cache for the class exists in the cache.
        if (values.containsKey(cls))
        {
            // Get the cache of field for the class.
            map = (HashMap) values.get(cls);
        }
        else
        {
            // The class does not already exist in the cache to create a new cache for its fields.
            map = new HashMap();

            // Cache the new field cache against the class.
            values.put(cls, map);
        }

        // Store the attribute in the field cache for the class.
        map.put(attr, val);
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
        /*log.fine("public Collection getAllFieldNames(Class cls): called");*/

        // See if the class exists in the cache
        if (!values.containsKey(cls))
        {
            // Class not in cache so return null
            return null;
        }

        // Get the cache of fields for the class
        HashMap map = (HashMap) values.get(cls);

        // Return all the keys from cache of fields
        return map.keySet();
    }
}
