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
package com.thesett.common.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Filterators is a utilty class providing convenience methods to assist with working with {@link Filterator}s.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Apply a filterator to a collection, accumulating the results in another collection.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Filterators
{
    /**
     * Extracts all elements from an iterator, usually created from a filterator, and adds them into the target
     * collection, returning that collection as the result.
     *
     * @param  <T>              The type of the iterator and collection to add to.
     * @param  iterator         The iterator to extract from.
     * @param  targetCollection The collection to add to.
     *
     * @return The collection added to.
     */
    public static <T> Collection<T> collectIterator(Iterator<T> iterator, Collection<T> targetCollection)
    {
        while (iterator.hasNext())
        {
            targetCollection.add(iterator.next());
        }

        return targetCollection;
    }
}
