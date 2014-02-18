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
package com.thesett.common.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Compares collections element by element using a specified comparator over the elements, in order to determine the
 * lexicographic ordering of the collections. The collections iterator determines the order in which its elements are
 * scanned.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Lexicographically order two collections.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class LexicographicalCollectionComparator<T> implements Comparator<Collection<T>>
{
    /** Holds the element comparator for comparing elements of collections. */
    Comparator<T> comparator;

    /**
     * Creates a lexicographic ordering comparator over collections base on an underlying comparator for the elements of
     * the collections.
     *
     * @param comparator The element comparator.
     */
    public LexicographicalCollectionComparator(Comparator<T> comparator)
    {
        // Keep the element comparator.
        this.comparator = comparator;
    }

    /**
     * Compares two collections using lexicographic ordering based on a comparator of their elements.
     *
     * @param  c1 The first collection to compare.
     * @param  c2 The second collection to compare.
     *
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
     *         than the second.
     */
    public int compare(Collection<T> c1, Collection<T> c2)
    {
        // Simultaneously iterator over both collections until one runs out.
        Iterator<T> i1 = c1.iterator();
        Iterator<T> i2 = c2.iterator();

        while (i1.hasNext() && i2.hasNext())
        {
            T t1 = i1.next();
            T t2 = i2.next();

            // Compare t1 and t2.
            int comp = comparator.compare(t1, t2);

            // Check if t1 < t2 in which case c1 < c2.
            if (comp < 0)
            {
                return -1;
            }

            // Check if t2 < t1 in which case c2 < c1.
            else if (comp > 0)
            {
                return 1;
            }

            // Otherwise t1 = t2 in which case further elements must be examined in order to determine the ordering.
        }

        // If this point is reached then one of the collections ran out of elements before the ordering was determined.

        // Check if c1 ran out and c2 still has elements, in which case c1 < c2.
        if (!i1.hasNext() && i2.hasNext())
        {
            return -1;
        }

        // Check if c2 ran out and c1 still has elements, in which case c2 < c1.
        if (i1.hasNext() && !i2.hasNext())
        {
            return 1;
        }

        // Otherwise both ran out in which case c1 = c2.
        return 0;
    }

    /**
     * Checks if another comparator is the same as this one.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt> only if the specified object is also a comparator and it imposes the same ordering as this
     *         comparator.
     */
    public boolean equals(Object o)
    {
        return (o instanceof LexicographicalCollectionComparator) &&
            ((LexicographicalCollectionComparator) o).comparator.equals(comparator);
    }
}
