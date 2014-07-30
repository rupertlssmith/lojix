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

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.thesett.common.error.NotImplementedException;
import com.thesett.common.util.Queue;

/**
 * An key queued map is a data structure in which the elements may be referenced by a key in the same way as a
 * {@link java.util.Map} as well as by an ordering of the keys provided by an arbitrary queue implementation. Different
 * queue implementations may be provided in order to create different orderings and behaviours of the map.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class KeyQueuedMap<K, V> implements Map<K, V>
{
    /** The HashMap that holds the key to index mapping. */
    private Map<K, V> dataMap;

    /** The queue that holds the keys in order. */
    private Queue<K> keys;

    /** Creates a new empty hash array. */
    public KeyQueuedMap()
    {
        this.dataMap = new HashMap<K, V>();
        this.keys = createQueue();
    }

    /**
     * Creates a new empty hash array with the specified starting size.
     *
     * @param size The initial size of the hash array to create.
     */
    public KeyQueuedMap(int size)
    {
        this.dataMap = new HashMap<K, V>(size);
        this.keys = createQueue();
    }

    /**
     * This abstract method should be overriden to return an empty queue of keys. Different implementations of queued
     * key maps can control the ordering and behaviour of the data structure by providing different queue
     * implementations with different properties.
     *
     * @return An empty queue of keys.
     */
    public abstract Queue<K> createQueue();

    /** Clears the whole data structure. */
    public void clear()
    {
        dataMap.clear();
        keys.clear();
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
        return dataMap.containsKey(key);
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
        return dataMap.containsValue(value);
    }

    /**
     * Returns a set view of the mappings contained in this map.
     *
     * @return A set view of the mappings contained in this map.
     */
    public Set<Map.Entry<K, V>> entrySet()
    {
        return new AbstractSet<Map.Entry<K, V>>()
            {
                /**
                 * Gets an iterator over the set.
                 *
                 * @return An iterator over the set.
                 */
                public Iterator<Map.Entry<K, V>> iterator()
                {
                    return new Iterator<Map.Entry<K, V>>()
                        {
                            Iterator<K> keyIterator = dataMap.keySet().iterator();

                            public boolean hasNext()
                            {
                                return keyIterator.hasNext();
                            }

                            public Map.Entry<K, V> next()
                            {
                                final K key = keyIterator.next();
                                final V data = get(key);

                                return new Map.Entry<K, V>()
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
                    return dataMap.containsValue(o);
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
                    Object removed = KeyQueuedMap.this.remove(o);

                    return removed != null;
                }

                /**
                 * Gets the size of the set.
                 *
                 * @return The size of the set.
                 */
                public int size()
                {
                    return KeyQueuedMap.this.size();
                }

                /** Removes everything from the set. */
                public void clear()
                {
                    KeyQueuedMap.this.clear();
                }
            };
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
        if (this == o)
        {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        KeyQueuedMap that = (KeyQueuedMap) o;

        return !((dataMap != null) ? (!dataMap.equals(that.dataMap)) : (that.dataMap != null)) &&
            !((keys != null) ? (!keys.equals(that.keys)) : (that.keys != null));

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
        return dataMap.get(key);
    }

    /**
     * Returns a hash code for this map.
     *
     * @return A hash code for this map.
     */
    public int hashCode()
    {
        return dataMap.hashCode();
    }

    /**
     * Returns true if this map contains no key-value mappings.
     *
     * @return True if this map contains no key-value mappings.
     */
    public boolean isEmpty()
    {
        return dataMap.isEmpty();
    }

    /**
     * Returns a set view of the keys contained in this map.
     *
     * @return A set view of the keys contained in this map.
     */
    public Set<K> keySet()
    {
        return dataMap.keySet();
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

        // Insert the data into the map.
        dataMap.put(key, value);

        // If the key is fresh, enqueue it.
        if (removedObject != null)
        {
            keys.offer(key);
        }

        // Return the replaced value if there was one
        return removedObject;
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
        return dataMap.remove(key);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return The size of this map.
     */
    public int size()
    {
        return dataMap.size();
    }

    /**
     * Returns a collection view of the value contained in this map.
     *
     * @return The collection of values in this map.
     */
    public Collection<V> values()
    {
        return dataMap.values();
    }

    /**
     * Prints out the contents of the map, usefull for debugging.
     *
     * @return A string of the index and key/value pairs in this map.
     */
    public String toString()
    {
        String result = "KeyQueuedMap: [";

        // Cycle through all the indexes
        for (Iterator<Map.Entry<K, V>> i = dataMap.entrySet().iterator(); i.hasNext();)
        {
            // This will be used to hold the key found to match the current index
            Map.Entry<K, V> entry = i.next();
            Object nextKey = entry.getKey();
            Object nextData = entry.getValue();

            // Write out (key -> value) into the output string
            result += (nextKey + "-> " + nextData + (i.hasNext() ? ", " : ""));
        }

        result += "]";

        return result;
    }
}
