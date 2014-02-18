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

import com.thesett.common.util.Function;

/**
 * A SequentialFunction maps keys onto a contiguous sequence of integers from zero; as the function is applied to new,
 * not seen before, keys, the functions output range is incrementally increased by one; when keys are re-evaluated their
 * originally assigned sequence numbers are returned. Effectively, this turns keys of arbitrary type into a compact
 * sequence of keys which can be used to index an array without wasting any space.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Map keys onto sequential integers.
 * <tr><td> Remove a key from the function.
 * <tr><td> Check if a key already exists in the functions input range.
 * <tr><td> Reset the function.
 * </table></pre>
 *
 * @param  <K> The type of the keys.
 *
 * @author Rupert Smith
 */
public interface SequentialFunction<K> extends Function<K, Integer>
{
    /**
     * Checks if the specified key is already mapped in the functions input range.
     *
     * @param  key The key to check for.
     *
     * @return <tt>true</tt> if the key is already in the function, <tt>false</tt> if not.
     */
    boolean containsKey(K key);

    /**
     * Removes a key from the function, leaving behind a 'hole' in the functions output range. If the removed key is
     * later re-evaluated by the function, it will be assigned a new sequence number at the end of the functions range.
     *
     * @param  objectKey The key to remove from the function.
     *
     * @return The removed keys value, or <tt>null</tt> if it had no assigned value.
     */
    Integer remove(Object objectKey);

    /** Resets the function to its initial state with no mapped keys. */
    void clear();
}
