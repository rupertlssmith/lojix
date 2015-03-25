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

import java.util.Collection;

import com.thesett.common.util.TypeConverter;

/**
 * A Memento provides indirect access to the fields of an object, allowing them all to be accessed by name. This enables
 * the state of an object to be decoupled from the object itself. Through this mechanism any object can have a snapshot
 * of its state externalized or its state restored from such a snapshot.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Create a memento from an object.
 * <tr><td>Write to an objects fields from a memento.
 * <tr><td>Read field values.
 * <tr><td>Modifiy field values.
 * <tr><td>Get list of all fields.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Memento
{
    /** Captures an objects properties in this memento. */
    void capture();

    /** Captures an objects non-null properties in this memento. */
    void captureNonNull();

    /**
     * Restores the properties currently in this memento to the specified object.
     *
     * @param  ob The object to which the values from this memento should be restored.
     *
     * @throws NoSuchFieldException If a setter method could not be found for a property.
     */
    void restore(Object ob) throws NoSuchFieldException;

    /**
     * Gets the value of the named property of the specified class.
     *
     * @param  cls      The class in which the property to get is declared.
     * @param  property The name of the property.
     *
     * @return The object value of the property.
     *
     * @throws NoSuchFieldException If the named field does not exist on the class.
     */
    Object get(Class cls, String property) throws NoSuchFieldException;

    /**
     * Sets the value of the named property as a multi type object.
     *
     * @param cls      The class in which the property is declared.
     * @param property The name of the property to set.
     * @param value    The multi type object to set that value from.
     */
    void put(Class cls, String property, TypeConverter.MultiTypeData value);

    /**
     * Places the specified value into the memento based on the property's declaring class and name.
     *
     * @param cls      The class in which the property is declared.
     * @param property The name of the property.
     * @param value    The value to store into this memento.
     */
    void put(Class cls, String property, Object value);

    /**
     * Generates a list of all the fields of the object that this memento maps for a given class.
     *
     * @param  cls The class to get all field names for.
     *
     * @return A collection of the field names or null if the specified class is not part of the objects class hierarchy
     *         chain.
     */
    Collection getAllFieldNames(Class cls);
}
