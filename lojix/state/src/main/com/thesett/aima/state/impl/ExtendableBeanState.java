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

import com.thesett.aima.state.ComponentType;
import com.thesett.aima.state.State;

/**
 * ExtendableBeanState provides a {@link State} implementation that should be extended by classes that are to have their
 * properties exposed by the state interface. This class looks up all the methods of the class, which automatically
 * includes the methods of any extending child classes, and takes note of all getter and setter methods. In this way, it
 * can see the properties of any extending class and expose them as meta bean properties.
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
public class ExtendableBeanState extends PropertyIntrospectorBase implements State
{
    /** Holds the state type mapping. */
    private final DynaComponent component = new DynaComponent(this.getClass().getName());

    /**
     * Returns a single named property of the bean.
     *
     * @param  property The property of this state to get the value of.
     *
     * @return A single Object value of the bean for the specified property name.
     */
    public Object getProperty(String property)
    {
        return getProperty(this, property);
    }

    /**
     * Sets the value of a property of the state by name.
     *
     * @param name  The name of the property to set.
     * @param value The value of the property to set.
     */
    public void setProperty(String name, Object value)
    {
        setProperty(this, name, value);
    }

    /**
     * Gets the Component that models the type of this state.
     *
     * @return The Component that models the type of this state.
     */
    public ComponentType getComponentType()
    {
        return component;
    }
}
