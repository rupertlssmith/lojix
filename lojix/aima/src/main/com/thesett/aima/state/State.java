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

/**
 * State represents a collection of properties, which are assignments of values to names. A state could represent the
 * state of an object, but it is more general than that, it can also represent the state of a piece of XML data or a
 * database result set, or any other data type that can be viewed as a collection of properties. This abstraction allows
 * algorithms, or user interface views, or other clients of the state to work seamlessly with both object models and
 * other kinds of state without knowing the implementation.
 *
 * <p/>Each individual property value defines the states membership of the set of objects with that property value. The
 * property value is a member of the set of possible values defined by its type. Taken as a whole a state is a member of
 * the cross product of all its property type sets; this is the same thing as the state of an object which is a member
 * of a class. The cross product of all the property type sets is defined in a {@link ComponentType}, which encapsulates
 * the type of a state.
 *
 * <p/>State is different from a class because it does not encapsulate behaviour; a class is typed state plus behaviour.
 * In this sense state breaks the OO model because it exposes state without behaviour but this can often be extremely
 * usefull.
 *
 * <p>See the {@link com.thesett.aima.search.Traversable} interface for an extension of states with operators to move
 * between states; generating new states from old or in-place modifying a state to become a new state. These kinds of
 * states are used to perform searches over a state space.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide read and write access to named properties.
 * <tr><td> Check if properties exist on a bean.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface State
{
    /**
     * Returns a single named property of the bean.
     *
     * @param  property The property of this state to get the value of.
     *
     * @return A single Object value of the bean for the specified property name.
     */
    Object getProperty(String property);

    /**
     * Sets the value of a property of the state by name.
     *
     * @param name  The name of the property to set.
     * @param value The value of the property to set.
     */
    void setProperty(String name, Object value);

    /**
     * Checks if the bean has a named property. Note that if the property value is set to null on the bean, this method
     * will still return true, it tests for the existance of a named property, including null ones.
     *
     * @param  property The property to check if this state contains.
     *
     * @return <tt>true</tt> if this state has the property, <tt>false</tt> if it does not.
     */
    boolean hasProperty(String property);

    /**
     * Gets the Component of this state.
     *
     * @return The Component of this state.
     */
    ComponentType getComponentType();
}
