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
 * ReferencableAttribute represents a type that can be mapped onto an explicit reference in the form of a long id that
 * uniquely identifies the value within its type. Such attributes may also be mutable by setting their id to that of
 * another attribute value of the same type in which case they may change and take on that attributes values.
 *
 * <p/>The references are always exposed as long ids. Often the id will correspond to the position of the attribute in
 * the iterator returned by {@link com.thesett.aima.state.Type#getAllPossibleValuesIterator} as this forms a natural
 * ordering of the attribute values onto integers.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Map attribute values onto integer ids.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface ReferencableAttribute extends Attribute
{
    /**
     * Returns the integer id of the attribute.
     *
     * @return The integer id of the attribute.
     */
    long getId();

    /**
     * Sets the integer id of the attribute.
     *
     * @param id The new id value.hi
     */
    void setId(long id);
}
