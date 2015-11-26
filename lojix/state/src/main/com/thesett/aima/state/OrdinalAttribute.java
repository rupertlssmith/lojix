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
package com.thesett.aima.state;

/**
 * OrdinalAttribute represents a type that can be mapped onto the integers. Examples of ordinal attributes are booleans,
 * enumerations, interned strings and so on. OrdinalAttribute is a useful marker interface for algorithms that work with
 * types that can take on only a small number of possible values, or that want to work with type that can generate their
 * value sets as lazy sequences.
 *
 * <p/>Not all ordinal attributes can take on a finite number of different values, but where they can and where this
 * number is less than Integer.MAX_VALUE the {@link #ordinal} method will supply an enumeration of the attribute value
 * within its type. This method will throw {@link com.thesett.aima.state.InfiniteValuesException} when this is not
 * possible.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Where number of values of a type is finite supply ordinals for the values.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface OrdinalAttribute extends Attribute
{
    /**
     * Should return an integer index for the current value of this attribute from 0 to num possible values where the
     * number of possible values is finite.
     *
     * @return An integer index for the current value of this attribute from 0 to num possible values where the number
     *         of possible values is finite.
     *
     * @throws com.thesett.aima.state.InfiniteValuesException If the set of values cannot be indexed because it is
     *                                                        infinite or cannot be ordered.
     */
    int ordinal() throws InfiniteValuesException;
}
