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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.ComponentType;
import com.thesett.aima.state.InfiniteValuesException;
import com.thesett.aima.state.State;
import com.thesett.aima.state.Type;
import com.thesett.common.error.NotImplementedException;
import com.thesett.common.util.ReflectionUtils;

/**
 * DynaComponent provides an implementation of the {@link ComponentType} interface. It consists of a mapping from
 * property names to their {@link Type}s and provides methods to modify this property map.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> List all properties in the state.
 * <tr><td> Get the type of a property of a state.
 * <tr><td> Add and remove property type mapping to the state.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DynaComponent extends BaseType implements ComponentType
{
    /** Holds the map from property names to their types. */
    private final Map<String, Type> typeMap = new HashMap<String, Type>();

    /** Holds the name of this dyna class. */
    private final String name;

    /** Holds the meta-model instance associated with this component. */
    private State metaModel;

    /**
     * Creates a named dyna class.
     *
     * @param name The name of this dyna class.
     */
    public DynaComponent(String name)
    {
        this.name = name;
    }

    /**
     * Gets a new default instance of the type. The types value will be set to its default uninitialized value.
     *
     * @return A new default instance of the type.
     */
    public Object getDefaultInstance()
    {
        throw new NotImplementedException();
    }

    /**
     * Gets the type of the named property.
     *
     * @param  name The property to get the type of.
     *
     * @return The type of the named property, or null if no such property exists on the underlying state.
     */
    public Type getPropertyType(String name)
    {
        return typeMap.get(name);
    }

    /** {@inheritDoc} */
    public Map<String, String> getPropertyPresentAsAliases()
    {
        return null;
    }

    /** {@inheritDoc} */
    public String getPropertyPresentAsAlias(String name)
    {
        return null;
    }

    /** {@inheritDoc} */
    public Set<String> getNaturalKeyFieldNames()
    {
        return new HashSet<String>();
    }

    /** {@inheritDoc} */
    public void setPropertyType(String name, Type type)
    {
        typeMap.put(name, type);
    }

    /** {@inheritDoc} */
    public Map<String, Type> getAllPropertyTypes()
    {
        return typeMap;
    }

    /** {@inheritDoc} */
    public Map<String, List<String>> getPropertiesByUniqueGrouping()
    {
        return null;
    }

    /**
     * Gets the names of all the properties defined in a state.
     *
     * @return The names of all the properties defined in a state.
     */
    public Set<String> getAllPropertyNames()
    {
        return typeMap.keySet();
    }

    /** {@inheritDoc} */
    public Set<ComponentType> getImmediateAncestors()
    {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    public void setImmediateAncestors(Set<ComponentType> immediateAncestors)
    {
        throw new NotImplementedException();
    }

    /**
     * Adds a property type mapping.
     *
     * @param name The name of the property to add a type mapping for.
     * @param type The type of the property.
     */
    public void addPropertyType(String name, Type type)
    {
        typeMap.put(name, type);
    }

    /**
     * Removes a property type mapping.
     *
     * @param name The name of the property to remove the mapping for.
     */
    public void removePropertyType(String name)
    {
        typeMap.remove(name);
    }

    /**
     * Should return a name that uniquely identifies the type.
     *
     * @return The name of the attribute type.
     */
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    public State getInstance()
    {
        return (State) ReflectionUtils.newInstance(getBaseClass());
    }

    /**
     * Returns the underlying Java class that this is the type for, if there is one.
     *
     * @return The underlying Java class that this is the type for, if there is one.
     */
    public Class getBaseClass()
    {
        return null;
    }

    /** {@inheritDoc} */
    public String getBaseClassName()
    {
        return "";
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

    /** {@inheritDoc} */
    public State getMetaModel()
    {
        return metaModel;
    }

    /**
     * Associated a meta-model instance with this component.
     *
     * @param metaModel A meta-model instance to associate with this component.
     */
    public void setMetaModel(State metaModel)
    {
        this.metaModel = metaModel;
    }

    /**
     * Should return all the different values that an instance of this type can take on.
     *
     * @return A set of values defining the possible value set for this attribute if this is finite.
     *
     * @throws InfiniteValuesException If the set of values cannot be listed because it is infinite.
     */
    public Set getAllPossibleValuesSet() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("DynaComponent has too many values to enumerate.", null);
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
    public Iterator getAllPossibleValuesIterator() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("DynaComponent has too many values to enumerate.", null);
    }
}
