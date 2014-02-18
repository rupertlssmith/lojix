/*
 * Copyright The Sett Ltd, 2005 to 2009.
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
package com.thesett.common.util.maps;

import java.util.List;
import java.util.Map;

/**
 * An indexed map is a data structure in which the elements may be referenced by a key in the same way as a
 * {@link java.util.Map} as well as by an integer index in the same way as a list or array. IndexedMap is an extension
 * of the {@link java.util.Map} interface. The order into which elements are entered into the data must be preserved.
 *
 * <p>The iterator, key set, value and entry set method of the {@link java.util.Map} interface must also be implemented
 * in a way that preserves the index ordering on the map.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr><td>Get elements by index
 * <tr><td>Insert elements by index
 * <tr><td>Remove elements by index
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface IndexedMap<K, V> extends Map<K, V>
{
    /**
     * Returns the element at the specified position in this data structure.
     *
     * @param  index index of element to return
     *
     * @return the element at the specified position in this list.
     *
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    public V get(int index) throws IndexOutOfBoundsException;

    /**
     * Returns the index to which this map maps the specified key.
     *
     * @param  key The key to get the index from this map for.
     *
     * @return The index of the specified key, or -1 if the key is not in this map.
     */
    public int getIndexOf(Object key);

    /**
     * Replaces the element at the specified position in this data structure with the specified element. This is an
     * optional operation.
     *
     * @param  index index of element to replace
     * @param  value element to be stored at the specified position
     *
     * @return the element previously at the specified position
     *
     * @throws UnsupportedOperationException if the method is not supported by this indexed map.
     * @throws IndexOutOfBoundsException     if the index is out of range (index < 0 || index >= size())
     */
    public V set(int index, V value) throws IndexOutOfBoundsException, UnsupportedOperationException;

    /**
     * Removes the element at the specified position in this data structure. This is an optioanl operation. Shifts any
     * subsequent elements to the left and adjusts the indexes of the keys by subtracting one from their indices.
     * Returns the element that was removed from the list.
     *
     * @param  index the index of the element to be removed.
     *
     * @return the element previously at the specified position.
     *
     * @throws UnsupportedOperationException if the method is not supported by this indexed map.
     * @throws IndexOutOfBoundsException     if the index is out of range (index < 0 || index >= size())
     */
    public V remove(int index) throws IndexOutOfBoundsException, UnsupportedOperationException;

    /**
     * Returns a list view of the values conatins in this map.
     *
     * @return A list view of the values conatins in this map.
     */
    public List<V> valuesAsList();
}
