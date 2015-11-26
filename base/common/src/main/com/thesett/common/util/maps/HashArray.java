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

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thesett.common.error.NotImplementedException;

/**
 * A HashArray is an implementation of the {@link IndexedMap} interface that preserves the ordering of its elements in
 * the same order in which they are inserted into the map. It differs from a {@link java.util.TreeMap} which orders its
 * elements according to a comparison function.
 *
 * <p/>The data structure is build on top of an array into which the elements are inserted in arrival order and a
 * HashMap which is used to quickly convert keys into into indexes into the array. In this way it can provide a very
 * efficient keyed array with roughly constant access time provided the hashing is good.
 *
 * <p/>Note that a HashArray does not implement the {@link java.util.SortedMap} interface as this is only to be
 * implemented by data structures that guarantee an ordering based on the ordering of the keys. This data structure is
 * not ordered by its keys but by the order in which elements are inserted.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Manage keys and values as a map
 * <tr><td>Get elements by index
 * <tr><td>Insert elements by index
 * <tr><td>Remove elements by index
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   A LinkedHashSet was used to track the insertion order of the keys. This was probably done as a quick hack to
 *         get this working. Would be better to store the keys in the data array itself. Better to drop the data array
 *         altogether and use a key list instead. LinkedList or ArrayList implementation for the key ordering list to be
 *         chosen using a constructor.
 * @todo   Add a constructor that allows the backing map to be passed as an argument so that map types other than hash
 *         maps can be supported. Rename this class to MapArray once this has been done. When combined with a TreeMap
 *         this data structure can provide both insertion ordering and value based ordering simultaneously.
 */
public class HashArray<K, V> implements IndexedMap<K, V>, Serializable
{
    /** The HashMap that holds the key to index mapping. */
    private final Map<K, Integer> keyToIndex;

    /** A linked list hash set that is used to keep track of the insertion order of the keys. */
    private final Set<K> keySet;

    /** The array that holds the data. */
    private final List<V> data;

    /** The array that holds the keys in insertion order. */
    private List<K> keys;

    /** Creates a new empty hash array. */
    public HashArray()
    {
        this.keyToIndex = new HashMap<K, Integer>();
        this.keySet = new LinkedHashSet<K>();
        this.data = new ArrayList<V>();
    }

    /**
     * Creates a new empty hash array with the specified starting size.
     *
     * @param size The initial size of the hash array to create.
     */
    public HashArray(int size)
    {
        this.keyToIndex = new HashMap<K, Integer>(size);
        this.keySet = new LinkedHashSet<K>(size);
        this.data = new ArrayList<V>(size);
    }

    /**
     * Builds a hash array from a map of keys to indexes and a collection of data elements that are stored in a
     * collection at the desired indexes referenced by the map.
     *
     * @param keyToIndex The key to index map.
     * @param data       The data collection at the correct indexes.
     */
    public HashArray(Map keyToIndex, Collection data)
    {
        this.keyToIndex = new HashMap<K, Integer>(keyToIndex);
        this.keySet = new LinkedHashSet<K>(keyToIndex.keySet());
        this.data = new ArrayList<V>(data);
    }

    /** Clears the whole data structure. */
    public void clear()
    {
        keyToIndex.clear();
        keySet.clear();
        data.clear();
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param  key The key to check in the map for.
     *
     * @return True if this map contains the key, false otherwise.
     */
    public boolean containsKey(Object key)
    {
        return keyToIndex.containsKey(key);
    }

    /**
     * Returns true if this map maps one or more keys to the specified value.
     *
     * @param  value The value to check if this array contains.
     *
     * @return True if this map maps one or more keys to the specified value.
     */
    public boolean containsValue(Object value)
    {
        return data.contains(value);
    }

    /**
     * Returns a set view of the mappings contained in this map.
     *
     * @return A set view of the mappings contained in this map.
     */
    public Set<Map.Entry<K, V>> entrySet()
    {
        Set entrySet =
            new AbstractSet<Map.Entry<K, V>>()
            {
                /**
                 * Gets an iterator over the set.
                 *
                 * @return An iterator over the set.
                 */
                public Iterator<Map.Entry<K, V>> iterator()
                {
                    return new Iterator<Entry<K, V>>()
                        {
                            Iterator<K> keyIterator = keyToIndex.keySet().iterator();

                            public boolean hasNext()
                            {
                                return keyIterator.hasNext();
                            }

                            public Entry<K, V> next()
                            {
                                final K key = keyIterator.next();
                                final V data = get(key);

                                return new Entry<K, V>()
                                    {
                                        public K getKey()
                                        {
                                            return key;
                                        }

                                        public V getValue()
                                        {
                                            return data;
                                        }

                                        public V setValue(V v)
                                        {
                                            throw new NotImplementedException();
                                        }
                                    };
                            }

                            public void remove()
                            {
                                throw new NotImplementedException();
                            }
                        };
                }

                /**
                 * Checks if the set contains the specified object.
                 *
                 * @param o The object to check if the set contains.
                 */
                public boolean contains(Object o)
                {
                    return data.contains(o);
                }

                /**
                 * Removes the specified object from the set.
                 *
                 * @param  o The object to remove.
                 *
                 * @return True if an object was removed and false if not.
                 */
                public boolean remove(Object o)
                {
                    Object removed = HashArray.this.remove(o);

                    return removed != null;
                }

                /**
                 * Gets the size of the set.
                 *
                 * @return The size of the set.
                 */
                public int size()
                {
                    return HashArray.this.size();
                }

                /** Removes everything from the set. */
                public void clear()
                {
                    HashArray.this.clear();
                }
            };

        return entrySet;
    }

    /**
     * Compares the specified object with this map for equality.
     *
     * @param  o The object to compare to.
     *
     * @return True if this hash array exactly equals the test one.
     */
    public boolean equals(Object o)
    {
        // Check that the object has the same type as this
        if (!(o instanceof HashArray))
        {
            return false;
        }

        // Check that the object matches the index map and its data
        return keyToIndex.equals(((HashArray) o).keyToIndex) && data.equals(((HashArray) o).data);
    }

    /**
     * Returns the value to which this map maps the specified key.
     *
     * @param  key The key to get the value from this map for.
     *
     * @return The object stored under the specified key, or null if the key is not in this map.
     */
    public V get(Object key)
    {
        // Get the index from the map
        Integer index = keyToIndex.get(key);

        // Check that the key is in the map
        if (index == null)
        {
            return null;
        }

        // Get the data from the array
        return data.get(index.intValue());
    }

    /**
     * Returns the index to which this map maps the specified key.
     *
     * @param  key The key to get the index from this map for.
     *
     * @return The index of the specified key, or -1 if the key is not in this map.
     */
    public int getIndexOf(Object key)
    {
        // Get the index from the map
        Integer index = keyToIndex.get(key);

        // Check that the key is in the map and return -1 if it is not.
        if (index == null)
        {
            return -1;
        }

        return index;
    }

    /**
     * Returns the value at the specified index.
     *
     * @param  index The index to get the value from this array for.
     *
     * @return The object stored at the specified index.
     *
     * @throws IndexOutOfBoundsException If the index is out of the range covered by this data structure.
     */
    public V get(int index) throws IndexOutOfBoundsException
    {
        return data.get(index);
    }

    /**
     * Returns a hash code for this map.
     *
     * @return A hash code for this map.
     */
    public int hashCode()
    {
        return keyToIndex.hashCode();
    }

    /**
     * Returns true if this map contains no key-value mappings.
     *
     * @return True if this map contains no key-value mappings.
     */
    public boolean isEmpty()
    {
        return keyToIndex.isEmpty();
    }

    /**
     * Returns a set view of the keys contained in this map.
     *
     * @return A set view of the keys contained in this map.
     */
    public Set keySet()
    {
        // return keyToIndex.keySet();
        return keySet;
    }

    /**
     * Associates the specified value with the specified key in this map.
     *
     * @param  key   The key to store against in this map.
     * @param  value The value to store against the key.
     *
     * @return The value that was previously stored against the key, or null if there was none.
     */
    public V put(K key, V value)
    {
        // Remove any existing matching key from the data
        V removedObject = remove(key);

        // Insert the data into the array
        data.add(value);

        // Insert a key into the map that points to the end of the array
        keyToIndex.put(key, data.size() - 1);

        // Create an entry in the key set to track the insertion order of the keys (automatically goes at the end of
        // a linked hash set)
        keySet.add(key);

        // Return the replaced value if there was one
        return removedObject;
    }

    /**
     * Inserts the element at the specified index. This only works if this index already exists.
     *
     * @param  index The index to insert at.
     * @param  value The value to store at that index.
     *
     * @return The value that was previously stored at that index.
     *
     * @throws IndexOutOfBoundsException If the index is out of the range covered by this data structure.
     */
    public V set(int index, V value) throws IndexOutOfBoundsException
    {
        // Check if the index does not already exist
        if (index >= data.size())
        {
            throw new IndexOutOfBoundsException();
        }

        return data.set(index, value);
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     *
     * @param t The map to copy into this one.
     */
    public void putAll(Map<? extends K, ? extends V> t)
    {
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet())
        {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param  key The key to remove from this map.
     *
     * @return The value that stored at that key, or null if there was none.
     */
    public V remove(Object key)
    {
        // Check if the key is in the map
        Integer index = keyToIndex.get(key);

        if (index == null)
        {
            return null;
        }

        // Leave the data in the array but remove its key
        keyToIndex.remove(key);
        keySet.remove(key);

        // Remove the data from the array
        V removedValue = data.remove(index.intValue());

        // Go through the whole key to index map reducing by one the value of any indexes greater that the removed index
        for (K nextKey : keyToIndex.keySet())
        {
            Integer nextIndex = keyToIndex.get(nextKey);

            if (nextIndex > index)
            {
                keyToIndex.put(nextKey, nextIndex - 1);
            }
        }

        // Return the removed object
        return removedValue;
    }

    /**
     * Removes the specified index from the data structure. This only works if the index already exists.
     *
     * @param  index The index to remove.
     *
     * @return The value that stored at that index.
     *
     * @throws IndexOutOfBoundsException If the index is out of the range covered by this data structure.
     */
    public V remove(int index) throws IndexOutOfBoundsException
    {
        // Check that the index is not too large
        if (index >= data.size())
        {
            throw new IndexOutOfBoundsException();
        }

        // Get the key for the index by scanning through the key to index mapping
        for (K nextKey : keyToIndex.keySet())
        {
            int nextIndex = keyToIndex.get(nextKey);

            // Found the key for the index, now remove it
            if (index == nextIndex)
            {
                return remove(nextKey);
            }
        }

        // No matching index was found
        throw new IndexOutOfBoundsException();
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return The size of this map.
     */
    public int size()
    {
        return keyToIndex.size();
    }

    /**
     * Returns a collection view of the value contained in this map.
     *
     * @return The collection of values in this map.
     */
    public Collection values()
    {
        return data;
    }

    /**
     * Returns a list view of the values conatins in this map.
     *
     * @return A list view of the values conatins in this map.
     */
    public List valuesAsList()
    {
        return data;
    }

    /**
     * Prints out the contents of the map, usefull for debugging.
     *
     * @return A string of the index and key/value pairs in this map.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();

        result.append("[");

        // Cycle through all the indexes
        for (int i = 0; i < data.size(); i++)
        {
            // This will be used to hold the key found to match the current index
            Object nextKey = null;

            // Find the matching key for next index by scanning through the key to index mapping
            for (K k : keyToIndex.keySet())
            {
                nextKey = k;

                int nextIndex = keyToIndex.get(nextKey);

                // Check if the the key for the index has been found
                if (i == nextIndex)
                {
                    // Stop at the key that matches the current index
                    break;
                }
            }

            // Find the data for the next index
            Object nextData = data.get(i);

            // Write out (index, key, value) into the output string
            result.append("(" + i + ", " + nextKey + ", " + nextData + ")");

            // Add a comma and a space to the output string if more results will follow
            if (i < (data.size() - 1))
            {
                result.append(", ");
            }
        }

        result.append("]");

        return result.toString();
    }
}
