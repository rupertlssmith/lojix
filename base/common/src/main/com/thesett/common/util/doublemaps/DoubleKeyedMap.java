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
package com.thesett.common.util.doublemaps;

import com.thesett.common.util.Countable;

/**
 * DoubleKeyedMap is analogous to java.util.Map using a pair of keys to store and look-up values. It can be use for a
 * variety of purposes:
 *
 * <pre><ul>
 * <li>The keys may for a coordinate system for storing data on a 2 dimensional grid.</li>
 * <li>The first key may hash to records in a table, and the second key may hash to the names of fields within the
 * records. This allows for records that can grow or shrink the number of fields that they hold at any one time, by the
 * possibility of dynmically adding or removing fields. This technique could be used to conserve memory in a long
 * computation that creates many temporary values it must hold in fields that are only required for some and not all of
 * the computations life-cycle. A compiler may find such a data structure usefull.</li>
 * </ul></pre>
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Clear the whole map
 * <tr><td> Check if the map is empty
 * <tr><td> Report the number of object stored in the map
 * <tr><td> Insert an object into the map
 * <tr><td> Retrieve an associated object from the map
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface DoubleKeyedMap<K, L, E> extends Countable
{
    /**
     * Removes all mappings from this map (optional operation).
     *
     * @throws UnsupportedOperationException If Clear is not supported by this map.
     */
    void clear();

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified coordinate.
     *
     * @param  primaryKey   The primaryKey coordinate.
     * @param  secondaryKey The secondaryKey coordinate.
     *
     * @return <tt>true</tt> if this map contains a mapping for the specified coordinate.
     */
    boolean containsKey(K primaryKey, L secondaryKey);

    /**
     * Associates the specified value with the specified coordinate in this map (optional operation). If the map
     * previously contained a mapping for this coordinate, the old value is replaced.
     *
     * @param  primaryKey   The primaryKey coordinate.
     * @param  secondaryKey The secondaryKey coordinate.
     * @param  value        Value to be associated with the specified coordinate.
     *
     * @return Previous value associated with specified coordinate, or <tt>null</tt> if there was no mapping for key. A
     *         <tt>null</tt> return can also indicate that the map previously associated <tt>null</tt> with the
     *         specified coordinate, if the implementation supports <tt>null</tt> values.
     *
     * @throws UnsupportedOperationException If the <tt>put</tt> operation is not supported by this map.
     * @throws ClassCastException            If the class of the specified value prevents it from being stored in this
     *                                       map.
     * @throws IllegalArgumentException      If some aspect of this value prevents it from being stored in this map.
     * @throws NullPointerException          This map does not permit <tt>null</tt> values, and the specified value is
     *                                       <tt>null</tt>.
     */
    E put(K primaryKey, L secondaryKey, E value);

    /**
     * Returns the value to which this map maps the specified coordinate. Returns <tt>null</tt> if the map contains no
     * mapping for this coordinate. A return value of <tt>null</tt> does not <i>necessarily</i> indicate that the map
     * contains no mapping for the coordinate; it's also possible that the map explicitly maps the coordinate to <tt>
     * null</tt>. The <tt>containsCoordinate</tt> operation may be used to distinguish these two cases.
     *
     * @param  primaryKey   The primaryKey coordinate.
     * @param  secondaryKey The secondaryKey coordinate.
     *
     * @return The value to which this map maps the specified coordinate, or <tt>null</tt> if the map contains no
     *         mapping for this coordinate.
     */
    E get(K primaryKey, L secondaryKey);

    /**
     * Removes the mapping for this coordinate from this map if present (optional operation).
     *
     * @param  primaryKey   The primaryKey coordinate.
     * @param  secondaryKey The secondaryKey coordinate.
     *
     * @return Previous value associated with specified coordinate, or <tt>null</tt> if there was no mapping for
     *         coordinate. A <tt>null</tt> return can also indicate that the map previously associated <tt>null</tt>
     *         with the specified coordinate, if the implementation supports <tt>null</tt> values.
     *
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is not supported by this map.
     */
    E remove(K primaryKey, L secondaryKey);
}
