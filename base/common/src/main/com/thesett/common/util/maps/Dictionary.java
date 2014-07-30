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
package com.thesett.common.util.maps;

import com.thesett.common.util.Countable;

/**
 * A Dictionary is a cut-down version of the {@link java.util.Map} interface. A dictionary provides an association from
 * keys to data values. Not all mappings require the fuller interface of a Map, often a Dictionary will suffice, and it
 * permits some simpler implementations.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Look up a value by a key.
 * <tr><td>Store a value against a key.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Dictionary<K, V> extends Countable
{
    /**
     * Checks if this dictionary contains a mapping for the specified key.
     *
     * @param  key The key to check for a mapping for.
     *
     * @return <tt>true</tt> if this dictionary contains a mapping for the specified key.
     *
     * @throws ClassCastException   If the key is of an inappropriate type for this dictionary.
     * @throws NullPointerException If the specified key is null and this dictionary does not permit null keys.
     */
    boolean containsKey(Object key);

    /**
     * Looks up the specified key in the dictionary, and returns the data value that is associated with it.
     *
     * @param  key The key to look up.
     *
     * @return The data value associated with the key, or <tt>null</tt> if the key does not exist in the dictionary, or
     *         possibly if it has been deliberately set to null.
     *
     * @throws ClassCastException   If the key is of an inappropriate type for this dictionary.
     * @throws NullPointerException If the specified key is null and this dictionary does not permit null keys.
     */
    V get(Object key);

    /**
     * Associates a key and a value as a pair in the dictonary.
     *
     * @param  key   The key to associate.
     * @param  value The data value to associate with the key.
     *
     * @return If the key was previously associated with another value then that previous value is returned, otherwise
     *         <tt>null</tt> is returned.
     *
     * @throws ClassCastException       If the class of the specified key or value prevents it from being stored in this
     *                                  dictionary.
     * @throws NullPointerException     If the specified key or value is null and this dictionary does not permit null
     *                                  keys or values.
     * @throws IllegalArgumentException If some property of the specified key or value prevents it from being stored in
     *                                  this dictionary.
     */
    V put(K key, V value);

    /**
     * Removes the value associated with the specified key from the dictionary, if there is one.
     *
     * @param  objectKey The key to remove.
     *
     * @return The value that was associated with the key, or <tt>null</tt> if there was no dictionaryping.
     *
     * @throws UnsupportedOperationException If the <tt>remove</tt> operation is not supported by this dictionary.
     * @throws ClassCastException            If the key is of an inappropriate type for this dictionary.
     * @throws NullPointerException          If the specified key is null and this dictionary does not permit null keys.
     */
    V remove(Object objectKey);

    /**
     * Removes all of the mappings from this dictionary.
     *
     * @throws UnsupportedOperationException If the <tt>clear</tt> operation is not supported by this dictionary.
     */
    void clear();
}
