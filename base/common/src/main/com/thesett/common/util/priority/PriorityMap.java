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
package com.thesett.common.util.priority;

import java.util.Map;

import com.thesett.common.util.Function;

/**
 * PriorityMap is closely related to java.util.SortedMap, in that it has can impose an ordering over its keys. SortedMap
 * allows arbitrary ordering over unbounded key values, whereas a priority ordering must be over a mapping from the keys
 * to a finite integer range. This allows implementations to use more efficient ordering mechanisms, such as radix
 * sorts, or simple bucketing, than are possible over general orderings.
 *
 * <p/>Note that priority maps cannot accept null keys. This is to avoid having to throw an exception when polling for
 * the lowest key, and none is available. Instead <tt>null</tt> is returned, and this could not be distinguished from
 * the null key, if they were allowed.
 *
 * <p/>The priority function over keys should be constant. That is to say that if two keys are equal, they should always
 * map to the same priority. It is possible through to implement equality on keys, and priority on keys so that this is
 * not the case, so a choice has to be made in how to implement this data structure to deal with this possibility. One
 * possibility is to allow multiple 'equal' keys to be stored in the map, but with different priorities. Then when
 * querying for a key, to return the smallest matching key found. The simpler solution of now allowing this has been
 * chosen instead.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide the priority function used to order the map.
 * <tr><td>Poll for the head key in the map.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface PriorityMap<K, V> extends Map<K, V>
{
    /**
     * Returns the priority function associated with this prioty map, or <tt>null</tt> if it does not impose a priority
     * ordering.
     *
     * @return The priority function associated with this priority map, or <tt>null</tt> if it does not import a
     *         priority ordering.
     */
    public Function<K, Integer> priority();

    /**
     * Returns the first (lowest) key currently in this priority map.
     *
     * @return The first (lowest) key currently in this priority map, or <tt>null</tt> if there is no first key.
     */
    public K pollKey();

    /**
     * Returns the value of the first (lowest) key currently in this priority map.
     *
     * @return The value of the first (lowest) key currently in this priority map.
     *
     * @throws java.util.NoSuchElementException If there are no keys in the map. This is thrown to distinguish from the
     *                                          case where the first key has a null value.
     */
    public V pollValue();

    /*
     * Gets the map entry for the first (lowest) key currently in this priority map.
     *
     * @return The map entry for the first (lowest) key currently in this priority map, or <tt>null</tt> if there is no
     *         first key.
     */
    // public Entry<K, V> pollEntry();
}
