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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import com.thesett.aima.state.BaseType;
import com.thesett.aima.state.InfiniteValuesException;
import com.thesett.aima.state.Type;

/**
 * UnknownType is a {@link Type} used in situations where a type cannot be inferred at runtime, for example, the value
 * null does not have a known type in some situations.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Act as placeholder for unknown types.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class UnknownType extends BaseType implements Type, Serializable
{
    /**
     * Should return a name that uniquely identifies the type.
     *
     * @return The name of the attribute type.
     */
    public String getName()
    {
        return "UnkownType";
    }

    /**
     * Gets a new default instance of the type. The types value will be set to its default uninitialized value.
     *
     * @return A new default instance of the type, always <tt>null</tt>.
     */
    public Object getDefaultInstance()
    {
        return null;
    }

    /**
     * Returns the underlying Java class that this is the type for.
     *
     * @return The underlying Java class that this is the type for.
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

    /**
     * Should return all the different values that an instance of this type can take on.
     *
     * @return A set of values defining the possible value set for this attribute if this is finite.
     *
     * @throws InfiniteValuesException If the set of values cannot be listed because it is infinite.
     */
    public Set getAllPossibleValuesSet() throws InfiniteValuesException
    {
        throw new InfiniteValuesException("UnkownType has infinite values.", null);
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
        throw new InfiniteValuesException("UnkownType has infinite values.", null);
    }
}
