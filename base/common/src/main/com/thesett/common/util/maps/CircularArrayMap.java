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

import java.util.Iterator;

import com.thesett.common.util.Countable;
import com.thesett.common.util.Sizeable;

/**
 * CircularArrayMap is a map, that is keyed by integers. It places some approximate expectations of the life-cycle of
 * keys used in the map, in order to provide a fast and reasonably compact data structure. The expectations are:
 *
 * <ol>
 * <li>The integer keys will be generated sequentially and inserted into the map roughly in the order that they are
 * generated. This means that new keys are added as a compact block of sequential keys.</li>
 * <li>The integer keys will be removed from the map roughly in the same order that they were inserted. This means that
 * as keys are removed from the map, the removals will not create blocks of keys that are very sparse.</li>
 * </ol>
 *
 * <p/>These expectations on the keys may be met by a process that works over a set of data in an approximately forward
 * only style. For example a process that takes data records and processes them though some sort of pipe-line, where
 * temporary values must be calculated and stored against the records at each stage of the pipe-line, but that can be
 * discarded later in the pipe-line, might be able to use this data structure efficiently.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Look up a value by a key.
 * <tr><td>Store a value against a key.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class CircularArrayMap<V> implements Dictionary<Integer, V>, Countable, Iterable<V>, Sizeable
{
    /** Default initial size of the array. */
    public static final int DEFAULT_INITIAL_SIZE = 16;

    /** Holds the low mark designating the highest table entry no longer used. */
    private int lowMark;

    /** Holds the start offset of the first entry in the array. */
    private int start;

    /** Holds the end offset of the last entry in the array. */
    private int end;

    /** Holds the current length of the allocated array. */
    private int length;

    /** Holds the mapped data. */
    private Object[] data;

    /** Holds a count of the number of elements in the map. */
    private int count;

    /**
     * Holds an offset, used to adjust the indexing when the element zero of the array does not align with index zero.
     */
    private int offset;

    /** Creates an array with the default initial size. */
    public CircularArrayMap()
    {
        this(DEFAULT_INITIAL_SIZE);
    }

    /**
     * Creates an array with the specified initial size.
     *
     * @param initialSize The initial size of the array.
     */
    public CircularArrayMap(int initialSize)
    {
        data = new Object[initialSize];
        length = data.length;
        lowMark = -1;
        start = 0;
        end = 0;
        count = 0;
        offset = 0;
    }

    /** {@inheritDoc} */
    public int size()
    {
        return count;
    }

    /** {@inheritDoc} */
    public boolean isEmpty()
    {
        return count == 0;
    }

    /**
     * {@inheritDoc}
     *
     * @throws ClassCastException If the key is not an Integer.
     */
    public boolean containsKey(Object objectKey)
    {
        int key = (Integer) objectKey;

        return keyInRange(key) && (data[offset(key)] != null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ClassCastException If the key is not an Integer.
     */
    public V get(Object objectKey)
    {
        int key = (Integer) objectKey;

        if (keyInRange(key))
        {
            return (V) data[offset(key)];
        }

        return null;
    }

    /** {@inheritDoc} */
    public V put(Integer key, V value)
    {
        if (keyTooLarge(key))
        {
            expand(key);
        }

        int offset = offset(key);
        V oldValue = (V) data[offset];
        data[offset] = value;

        // If the key is beyond the current end of the array, then move the end up.
        if (key >= end)
        {
            end = key + 1;
        }

        // Increment the count only if a new value was inserted.
        if (oldValue == null)
        {
            count++;
        }

        return oldValue;
    }

    /**
     * Clears entries up to and including the specified key, from the map. This is a simple garbage collection operation
     * to clear consumed data from the circular buffer.
     *
     * @param key The key to clear up to.
     */
    public void clearUpTo(int key)
    {
        if (((start <= key) && (key < (end - 1))))
        {
            // Loop from the start of the data, up to the key to clear up to, clearing all data encountered in-between.
            int newStart;

            for (newStart = start; (newStart <= end) && (newStart <= key); newStart++)
            {
                int offset = offset(newStart);

                if (data[offset] != null)
                {
                    data[offset] = null;
                    count--;
                }
            }

            // Continue on after the clear up to point, until the first non-null entry or end of array is encountered,
            // and make that the new start.
            for (; newStart <= end; newStart++)
            {
                if (data[offset(newStart)] != null)
                {
                    break;
                }
            }

            start = newStart;
        }
        else
        {
            // The key does not lie between the start and end markers, so clear the entire map up to the end
            int newStart;

            for (newStart = start; (newStart <= end); newStart++)
            {
                int offset = offset(newStart);

                if (data[offset] != null)
                {
                    data[offset] = null;
                    count--;
                }
            }

            start = newStart;

            offset = -start;
        }
    }

    /**
     * Sets the low mark to the specified value, provided it is higher than the current low mark.
     *
     * @param key The new minimum low mark.
     */
    public void setLowMark(int key)
    {
        lowMark = (lowMark < key) ? key : lowMark;
    }

    /** Clears entries up to and including the current low mark. */
    public void clearUpToLowMark()
    {
        if (lowMark >= 0)
        {
            clearUpTo(lowMark);
        }
    }

    /** {@inheritDoc} */
    public V remove(Object objectKey)
    {
        int key = (Integer) objectKey;

        if (keyInRange(key))
        {
            // Check if the key is the first element in the list, and walk forward to find the next non-empty element
            // in order to advance the start to.
            if (key == start)
            {
                int newStart = start + 1;

                while ((data[offset(newStart)] == null) && (newStart <= end))
                {
                    newStart++;
                }

                start = newStart;
            }

            int offset = offset(key);
            V result = (V) data[offset];
            data[offset] = null;

            // Decrement the count only if a value was removed.
            if (result != null)
            {
                count--;
            }

            return result;
        }

        return null;
    }

    /** {@inheritDoc} */
    public void clear()
    {
        data = new Object[data.length];
        length = data.length;
        lowMark = -1;
        start = 0;
        end = 0;
        count = 0;
        offset = 0;
    }

    /** {@inheritDoc} */
    public Iterator<V> iterator()
    {
        return new Iterator<V>()
            {
                /** Holds the current offset into the data. */
                int current = start;

                /** {@inheritDoc} */
                public boolean hasNext()
                {
                    return current < end;
                }

                /** {@inheritDoc} */
                public V next()
                {
                    return (V) data[current++];
                }

                /** {@inheritDoc} */
                public void remove()
                {
                    throw new UnsupportedOperationException("'remove' not supported on this iterator.");
                }
            };
    }

    /** {@inheritDoc} */
    public long sizeof()
    {
        return length;
    }

    /** {@inheritDoc} */
    public String toString()
    {
        return "CircularArrayMap: [ start = " + start + ", end = " + end + ", length = " + length + ", count = " +
            count + ", lowMark = " + lowMark + " ]";
    }

    /**
     * Expands the size of the storage to whichever is the larger of 1.5 times the old size, or an array large enough to
     * hold the proposed key that caused the expansion, copying the old data into a new array.
     *
     * @param key The key that was too large and caused expansion.
     */
    private void expand(int key)
    {
        // Set the new size to whichever is the larger of 1.5 times the old size, or an array large enough to hold
        // the proposed key that caused the expansion.
        int newFactorSize = ((length * 3) / 2) + 1;
        int newSpaceSize = spaceRequired(key);

        int newSize = (newSpaceSize > newFactorSize) ? newSpaceSize : newFactorSize;

        Object[] oldData = data;
        data = new Object[newSize];

        // The valid data in the old array runs from offset(start) to offset(end) when offset(start) < offset(end), and
        // from offset(start) to length - 1 and 0 to offset(end) when offset(start) >= offset(end).
        int offsetStart = offset(start);
        int offsetEnd = offset(end);

        if (offsetStart < offsetEnd)
        {
            System.arraycopy(oldData, offsetStart, data, 0, end - start);
        }
        else
        {
            System.arraycopy(oldData, offsetStart, data, 0, length - offsetStart);
            System.arraycopy(oldData, 0, data, length - offsetStart, offsetEnd);
        }

        offset = -start;
        length = newSize;
    }

    /**
     * Computes the offset within the storage array for a given key.
     *
     * @param  key The key to compute an offset for.
     *
     * @return The offset of the key in the storage array.
     */
    private int offset(int key)
    {
        return (key + offset) % length;
    }

    /**
     * Checks if the key can possibly refer to a value in the map, because it lies between the start and end markers.
     *
     * @param  key The key to check.
     *
     * @return <tt>true</tt> if the key can possibly refer to a value in the map.
     */
    private boolean keyInRange(int key)
    {
        return ((start <= key) && (key < end));
    }

    /**
     * Checks if the key is large enough to be beyond the end of the array.
     *
     * @param  key The key to check.
     *
     * @return <tt>true</tt> if the key is large enough to be beyond the end of the array.
     */
    private boolean keyTooLarge(int key)
    {
        return (key - start) > (length - 1);
    }

    /**
     * Calculates the size of array required to store the specified key.
     *
     * @param  key The key to check.
     *
     * @return The size of array required to store the specified key.
     */
    private int spaceRequired(int key)
    {
        return key - start + 1;
    }
}
