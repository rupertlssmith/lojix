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
package com.thesett.common.util.doublemaps;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMapXY defines a data structure associating data with a pair of x and y coordinates in 2 dimensions. To allow
 * fairly massive data structures (or very sparse) the x and y coordinates are specified as long integers (64 bits).
 *
 * <p/>HashMapXY is a DoubleKeyedMap data structure based on a hash table with bucketing. 2D arrays of coordinates are
 * stored within a HashMap keyed by coordinates modulo the size of the 2D array. This creates a compromise between the
 * constant time access of the arrays and the ability of the hash table to store disconnected regions of data in sparser
 * maps without wasting storage space and with near constant time access. The size of the buckets may be configured when
 * the data structure is created to select the best compromise for a particular usage. Smaller buckets means less wasted
 * space but performance closer to that of using simple hash table representation.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Clear the whole map
 * <tr><td>Check if the map is empty
 * <tr><td>Report the number of object stored in the map
 * <tr><td>Insert an object into the map
 * <tr><td>Retrieve an associated object from the map
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class HashMapXY<E> implements DoubleKeyedMap<Long, Long, E>
{
    /** The underlying map of data regions. */
    Map<Coordinates, Bucket<E>> regions;

    /** A counter used to record the number of items in the data structure. */
    int size;

    /**
     * The bucket size to use. Note that this is the size of one edge of a 2D bucket so the number of items in the
     * bucket will be the square of this value.
     */
    int bucketSize;

    /**
     * Builds a new HashMapXY.
     *
     * @param bucketSize The size of the buckets to use. Note that this is the size of one edge of a 2D bucket so the
     *                   number of items in the bucket will be the square of this value.
     */
    public HashMapXY(int bucketSize)
    {
        // Initialize the data structure
        regions = new HashMap<Coordinates, Bucket<E>>();
        size = 0;
        this.bucketSize = bucketSize;
    }

    /** Removes all mappings from this map. */
    public void clear()
    {
        // Re-initialize the data structure
        regions = new HashMap<Coordinates, Bucket<E>>();
        size = 0;
    }

    /**
     * Returns true if this map contains no coordinate-value mappings.
     *
     * @return true if this map contains no coordinate-value mappings.
     */
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * Returns the number of coordinate-value mappings in this map. If the map contains more than <tt>
     * Integer.MAX_VALUE</tt> elements, returns <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of coordinate-value mappings in this map.
     */
    public int size()
    {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified coordinates.
     *
     * @param  x The X coordinate.
     * @param  y The Y coordinate.
     *
     * @return True if this map contains a mapping for the specified coordinate.
     */
    public boolean containsKey(Long x, Long y)
    {
        // Extract the region containing the coordinates from the hash table
        Bucket region = (Bucket) regions.get(new Coordinates(div(x, bucketSize), div(y, bucketSize)));

        // Check if the whole region is empty
        if (region == null)
        {
            return false;
        }

        // Check if the coordinates within the region contain data
        return (region.array[mod(x, bucketSize)][mod(y, bucketSize)] != null);
    }

    /**
     * Associates the specified value with the specified coordinate in this map (optional operation). If the map
     * previously contained a mapping for this coordinate, the old value is replaced.
     *
     * @param  x     The x coordinate.
     * @param  y     The y coordinate.
     * @param  value Value to be associated with the specified coordinate.
     *
     * @return Previous value associated with specified coordinate, or <tt>null</tt> if there was no mapping for
     *         coordinate.
     *
     * @throws NullPointerException If the specified value is <tt>null</tt>.
     */
    public E put(Long x, Long y, E value)
    {
        // Check that the value is not null as this data structure does not allow nulls
        if (value == null)
        {
            throw new IllegalArgumentException("Null values not allowed in HashMapXY data structure.");
        }

        // Extract the region containing the coordinates from the hash table
        Bucket<E> region = (Bucket<E>) regions.get(new Coordinates(div(x, bucketSize), div(y, bucketSize)));

        // Check if the region does not exist yet and create it if so
        if (region == null)
        {
            region = new Bucket<E>(bucketSize);
            regions.put(new Coordinates(div(x, bucketSize), div(y, bucketSize)), region);
        }

        // Take a reference to the old value if there is one
        E old = region.array[mod(x, bucketSize)][mod(y, bucketSize)];

        // Insert the new value into the bucket
        region.array[mod(x, bucketSize)][mod(y, bucketSize)] = value;

        // Increment the bucket and whole data structure item counts to reflect the true size
        size++;
        region.itemCount++;

        // Return the replaced value
        return old;
    }

    /**
     * Returns the value to which this map maps the specified coordinate. Returns <tt>null</tt> if the map contains no
     * mapping for this coordinate. A return value of <tt>null</tt> indicates that the map contains no mapping for the
     * coordinate.
     *
     * @param  x The x coordinate.
     * @param  y The y coordinate.
     *
     * @return The value to which this map maps the specified coordinate, or <tt>null</tt> if the map contains no
     *         mapping for this coordinate.
     */
    public E get(Long x, Long y)
    {
        // Extract the region containing the coordinates from the hash table
        Bucket<E> region = regions.get(new Coordinates(div(x, bucketSize), div(y, bucketSize)));

        // Check if the whole region is empty
        if (region == null)
        {
            return null;
        }

        // Get the coordinates within the region
        return (region.array[mod(x, bucketSize)][mod(y, bucketSize)]);
    }

    /**
     * Removes the mapping for this coordinate from this map if present (optional operation).
     *
     * @param  x The x coordinate.
     * @param  y The y coordinate.
     *
     * @return Previous value associated with specified coordinate, or <tt>null</tt> if there was no mapping for
     *         coordinate.
     */
    public E remove(Long x, Long y)
    {
        // Extract the region containing the coordinates from the hash table
        Bucket<E> region = (Bucket<E>) regions.get(new Coordinates(div(x, bucketSize), div(y, bucketSize)));

        // Check if the whole region is empty
        if (region == null)
        {
            // Return null as nothing to remove
            return null;
        }

        // Get the coordinates within the region
        E removed = (region.array[mod(x, bucketSize)][mod(y, bucketSize)]);

        // Clear the coordinate within the region
        region.array[mod(x, bucketSize)][mod(y, bucketSize)] = null;

        // Decrement the bucket and whole data strucutre item counts to reflect the true size
        size--;
        region.itemCount--;

        // Return the removed data item
        return removed;
    }

    /**
     * Calculates the modulo of a coordinate with the bucket size. Correctly calculates this module for negative
     * coordinates such the the first negative bucket is -1 with element 0 corresponding to -100 running to 99
     * corresponding to -1.
     *
     * @param  c          The coordinate value.
     * @param  bucketSize The bucket size.
     *
     * @return The coordinate offset into the bucket modulo the bucket size.
     */
    private int mod(long c, int bucketSize)
    {
        return (int) ((c < 0) ? ((bucketSize + (c % bucketSize)) % bucketSize) : (c % bucketSize));
    }

    /**
     * Calculates the bucket size divided into the coordinate to generate the bucket number. Correctly calculates this
     * for negative coordinates such that bucket -1 runs from -100 to -1, -2 runs from -200 to -101 and so on.
     *
     * @param  c          The coordinate value.
     * @param  bucketSize The bucket size.
     *
     * @return The bucket number to find the coordinate in.
     */
    private long div(long c, int bucketSize)
    {
        return ((c < 0) ? ((c - (bucketSize - 1)) / bucketSize) : (c / bucketSize));
    }

    /**
     * Bucket defines a data structure used to hold the contents of a bucket. This is simply a 2D array but a data
     * structure is used to hold additional properties of the array such as the number of items held in it. This is
     * useful when deciding whether or not the array is empty.
     *
     * <pre><p/><table id="crc"><caption>CRC Card</caption>
    * <tr><th>Responsibilities<th>Collaborations
     *
     * <tr>
     * <td>Hold a two dimensional array of data.
     * </table></pre>
     */
    private static class Bucket<E>
    {
        /** The data array. */
        public E[][] array;

        /** The number of data items held in the array. */
        public int itemCount;

        /**
         * Creates a new Bucket of the specified size.
         *
         * @param size The side-size of bucket to create.
         */
        public Bucket(int size)
        {
            // Initialize the bucket
            array = (E[][]) new Object[size][size];
            itemCount = 0;
        }
    }

    /**
     * Coordinates defines a simple data structure that holds a pair of long x and y coordinates. This is used as the
     * key in the hash table to store data regions against. This class also defines hash and equals methods for the
     * coordinates for use within the hash table hashing mechanism.
     *
     * <pre><p/><table id="crc"><caption>CRC Card</caption>
     * <tr><th>Responsibilities<th>Collaborations
     * <tr><td>Hold a pair of integer X and Y coordinates.
     * </table></pre>
     */
    private static class Coordinates
    {
        /** The X coordinate. */
        public long x;

        /** The Y coordinate. */
        public long y;

        /**
         * Creates a new Coordinates object.
         *
         * @param x The X coord.
         * @param y The Y coord.
         */
        public Coordinates(long x, long y)
        {
            this.x = x;
            this.y = y;
        }

        /**
         * Checks if this coordinate equals another.
         *
         * @param  o The object to compare to.
         *
         * @return True if the object is a coordinate equal to this one.
         */
        public boolean equals(Object o)
        {
            // Check that the comparitor is a Coordinate
            if (!(o instanceof Coordinates))
            {
                return false;
            }

            Coordinates compare = (Coordinates) o;

            // Check if the coordinates are identical
            return (compare.x == x) && (compare.y == y);
        }

        /**
         * Computes a hash code from the X and Y coordinates.
         *
         * @return A hash code computed from the X and Y coordinates.
         */
        public int hashCode()
        {
            return (int) ((x ^ (x >> 32)) + (y ^ (y >> 32)));
        }
    }
}
