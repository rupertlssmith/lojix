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

import java.util.HashMap;
import java.util.Map;

import com.thesett.aima.state.ComponentType;
import com.thesett.aima.state.State;

/**
 * MapBackedState provides an implementation of the {@link State} interface that is backed by a hash map.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Get properties as attributes by name.
 * <tr><td> Set properties from attribute by name.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class MapBackedState implements State
{
    /** Holds the property values. */
    private Map<String, Object> properties = new HashMap<String, Object>();

    /** Holds the state type mapping. */
    private DynaComponent componentType = new DynaComponent("MapBackedState");

    /** {@inheritDoc} */
    public Object getProperty(String property)
    {
        return properties.get(property);
    }

    /** {@inheritDoc} */
    public void setProperty(String name, Object value)
    {
        // Set the type of the new property in the state type mapping.
        componentType.addPropertyType(name, TypeHelper.getTypeFromObject(value));

        // Add the new property to the property map.
        properties.put(name, value);
    }

    /** {@inheritDoc} */
    public boolean hasProperty(String property)
    {
        return properties.containsKey(property);
    }

    /**
     * Gets the Component of this state.
     *
     * @return The Component of this state.
     */
    public ComponentType getComponentType()
    {
        return componentType;
    }
}
