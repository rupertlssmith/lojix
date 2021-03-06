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
package com.thesett.aima.state;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ComponentType is a {@link Type} which is a set of fields each of which has a name and a type. The type space formed
 * by a component is the cross product of the sets defined by each of the types that the components properties have. A
 * component groups many types together in the same way that a class has many data fields. It is similar to a 'struc' in
 * C in that it groups named fields together. It is different to a class, because it does not define the types and
 * implementations of methods.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide the types and names of fields that make up a component.
 * <tr><td> Create a transient instance of the component.
 * <tr><td> Provide the immediate set of parent {@link ComponentType}s for the component.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface ComponentType extends Type, Serializable
{
    /**
     * Provides a map of the types of all properties of this dimension.
     *
     * @return A map of the types of all properties of this dimension.
     */
    Map<String, Type> getAllPropertyTypes();

    /**
     * Provides a list of the properties of the component, grouped into sub-lists, one for each 'unique' grouping in the
     * component type.
     *
     * <p/>The first group is special, and its name is empty ("") and it holds all properties that are not in a 'unique'
     * grouping.
     *
     * <p/>The subsequent groups represent the 'unique' groupings of the fields of the component. They are given names
     * that correspond to the type name of the component with an index appended to distinguish the group names.
     *
     * @return A list of the properties of the component, grouped into sub-lists, one for each 'unique' grouping in the
     *         component type.
     */
    Map<String, List<String>> getPropertiesByUniqueGrouping();

    /**
     * Calls <tt>new</tt> on the components implementation, using its no-arg constructor. None of the states properties
     * will have been instantiated and need to be set using its set methods. The returned object will be a proper
     * instance of the implementation class given by the {@link Type#getBaseClass()} method and can be cast to its type
     * if desired.
     *
     * <p/>This method will always produce a transient, in-memory only instance of the component. If the component is to
     * be persisted then additional steps need to be taken to create its persistent form.
     *
     * @return A transient component instance.
     */
    State getInstance();

    /**
     * Obtains the meta-model instance associated with this component.
     *
     * @return The meta-model instance associated with this component.
     */
    State getMetaModel();

    /**
     * Gets the type of the named property.
     *
     * @param  name The property to get the type of.
     *
     * @return The type of the named property, or null if no such property exists on the component.
     */
    Type getPropertyType(String name);

    /**
     * Provides a map of the alias names to present a properties externally as.
     *
     * @return A map of the alias names to present a properties externally as.
     */
    Map<String, String> getPropertyPresentAsAliases();

    /**
     * Obtains the alias name to present a property externally as, if one is set.
     *
     * @param  name The property to get the present as alias of.
     *
     * @return The alias name to present a property externally as, or <tt>null</tt> if none is set.
     */
    String getPropertyPresentAsAlias(String name);


    /**
     * Provides a map of the field names to whether or not the field must be not null.
     *
     * @return A map of the field names to whether or not the field must be not null. <tt>true</tt> means the property
     * is optional, <tt>false</tt> means the property is mandatory.
     */
    Map<String, Boolean> getOptionalProperties();

    /**
     * Alters the type of the named property. Usually property types are immutable, but this is sometimes conventient
     * when building components where there are forward or cirular reference between components. In a two stage process
     * a temporary reference to another as yet unresolved component can be created, then resolved onto the component
     * type once it is more fully known.
     *
     * @param name The name of the field to alter the type of.
     * @param type The new type of the field.
     */
    void setPropertyType(String name, Type type);

    /**
     * Gets the names of all the properties defined in this component.
     *
     * @return The names of all the properties defined in this component.
     */
    Set<String> getAllPropertyNames();

    /**
     * Provides the set of fields by name, that form the natural key of the component. This set may be empty if the
     * component has no natural key.
     *
     * @return The set of natural key fields by name.
     */
    Set<String> getNaturalKeyFieldNames();

    /**
     * Provides the immediate set of {@link ComponentType}s that this component type is a sub-type of. This component
     * type may transitively be a sub-type of further views, but only its immediate ancestors will be returned by this
     * method.
     *
     * @return The immediate set of {@link ComponentType}s that this component type is a sub-type of.
     */
    Set<ComponentType> getImmediateAncestors();

    /**
     * Sets the ancestor types of this type.
     *
     * @param immediateAncestors The immediate parent types of this one.
     */
    void setImmediateAncestors(Set<ComponentType> immediateAncestors);
}
