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
import com.thesett.common.error.NotImplementedException;

/**
 * WrappedBeanState provides a {@link State} implementation that takes an existing Java bean and acts as a facade to
 * expose its properties using the State interface. This class looks up all the methods of the wrapped class and takes
 * note of all getter and setter methods.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Get properties as attributes by name.
 * <tr><td> Set properties from attribute by name.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class WrappedBeanState extends PropertyIntrospectorBase implements State
{
    /** Holds the wrapped bean. */
    Object bean;

    /**
     * Builds a wrapped bean state from the specified bean.
     *
     * @param bean The bean to wrap as a state.
     */
    public WrappedBeanState(Object bean)
    {
        this.bean = bean;
    }

    /**
     * Returns a single named property of the bean.
     *
     * @param  property The property of this state to get the value of.
     *
     * @return A single Object value of the bean for the specified property name.
     */
    public Object getProperty(String property)
    {
        return getProperty(bean, property);
    }

    /**
     * Sets the value of a property of the state by name.
     *
     * @param name  The name of the property to set.
     * @param value The value of the property to set.
     */
    public void setProperty(String name, Object value)
    {
        setProperty(bean, name, value);
    }

    /**
     * Gets the Component of this state.
     *
     * @return The Component of this state.
     */
    public ComponentType getComponentType()
    {
        throw new NotImplementedException();
    }
}
