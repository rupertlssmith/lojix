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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * TimeoutMap is an implementation of {@link java.util.Map} that times out its entries after a specified period using a
 * mark and sweep caching algorithm. As the map implements the {@link java.util.Map} interface it can be used as any
 * other Map can in order to implement a cache. It has the advantage that the cache elements automatically time out and
 * are periodically removed from the cache without any code having to be written to do this explicitly.
 *
 * <p/>This class protects its underlying cache implementation from conflicting concurrent accesses in a multi-threaded
 * environment by synchronizing on the underlying cache. This is necessary to ensure that the cache sweep thread does
 * not conflict with any cache accesses. It also has the benefit that the cache can be used confidently in a
 * multi-threaded environment.
 *
 * <p/>All timings used in this class are represented as long values in milliseconds.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TimeoutMap implements Map
{
    /** Used for logging purposes. */
    /* private static final Logger log = Logger.getLogger(TimeoutMap.class.getName()); */

    /** The time between cache sweeps. */
    private long sweepTime;

    /** The maximum age of an untouched entry in the cache before it is removed. */
    private long sweepExpiryTime;

    /** The cache sweeping thread. Reference kept here so that it can be woken up when needed. */
    private Thread cacheSweepThread;

    /** A flag used to indicate to the sweep thread that it should stop running. */
    private boolean sweepThreadKillFlag;

    /** A cache of elements. */
    private Map cache = new HashMap();

    /** A heap of cache entries marked by the cache sweep operation. */
    private Map marked = new HashMap();

    /**
     * Creates a timeout map with the specified sweeping interval and maximum time out.
     *
     * @param sweepTime  The time in milliseconds between cache sweeps.
     * @param expiryTime The maximum age of cache elements before they expire.
     */
    public TimeoutMap(long sweepTime, long expiryTime)
    {
        // Keep the specified sweep and expiry time.
        this.sweepTime = sweepTime;
        this.sweepExpiryTime = expiryTime;

        // Clear the sweep thread kill flag.
        sweepThreadKillFlag = false;

        // Start the sweep thread running with low priority.
        cacheSweepThread =
            new Thread()
            {
                public void run()
                {
                    sweep();
                }
            };

        cacheSweepThread.setPriority(Thread.MIN_PRIORITY);
        cacheSweepThread.start();
    }

    /** Stops the sweep algorithm from running. */
    public void kill()
    {
        // Set the sweep thread kill flag.
        sweepThreadKillFlag = true;

        // Wake up the sweep thread so that it reads its kill flag.
        cacheSweepThread.interrupt();
    }

    /** Restarts the sweep alogirithm. Useful after a kill has stopped it. */
    public void restart()
    {
        // Clear the sweep thread kill flag.
        sweepThreadKillFlag = false;

        // Start the sweep thread running with low priority.
        cacheSweepThread =
            new Thread()
            {
                public void run()
                {
                    sweep();
                }
            };

        cacheSweepThread.setPriority(Thread.MIN_PRIORITY);
        cacheSweepThread.start();
    }

    /**
     * Returns the number of key-value mappings in this map. If the map contains more than <tt>Integer.MAX_VALUE</tt>
     * elements, returns <tt>Integer.MAX_VALUE</tt>.
     *
     * @return The number of key-value mappings in this map.
     */
    public int size()
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            return cache.size();
        }
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    public boolean isEmpty()
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            return cache.isEmpty();
        }
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified key.
     *
     * @param  key Key whose presence in this map is to be tested.
     *
     * @return <tt>true</tt> if this map contains a mapping for the specified key.
     *
     * @throws ClassCastException   If the key is of an inappropriate type for this map.
     * @throws NullPointerException If the key is <tt>null</tt> and this map does not permit <tt>null</tt> keys.
     */
    public boolean containsKey(Object key)
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            return cache.containsKey(key);
        }
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the specified value. More formally, returns <tt>
     * true</tt> if and only if this map contains at least one mapping to a value <tt>v</tt> such that <tt>(value==null
     * ? v==null : value.equals(v))</tt>. This operation will probably require time linear in the map size for most
     * implementations of the <tt>Map</tt> interface.
     *
     * @param  value Value to test the presence of in this map.
     *
     * @return <tt>true</tt> if this map maps one or more keys to the specified value.
     */
    public boolean containsValue(Object value)
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            // Look for a matching ElementMonitor in the cache. The comparison operation of ElementMonitor will ignore
            // the zero time stamp in the comparison ElementMonitor created here.
            return cache.containsValue(new ElementMonitor(value, 0L));
        }
    }

    /**
     * Returns the value to which this map maps the specified key. Returns <tt>null</tt> if the map contains no mapping
     * for this key. A return value of <tt>null</tt> does not <i>necessarily</i> indicate that the map contains no
     * mapping for the key; it's also possible that the map explicitly maps the key to <tt>null</tt>. The <tt>
     * containsKey</tt> operation may be used to distinguish these two cases.
     *
     * <p>More formally, if this map contains a mapping from a key <tt>k</tt> to a value <tt>v</tt> such that <tt>
     * (key==null ? k==null : key.equals(k))</tt>, then this method returns <tt>v</tt>; otherwise it returns <tt>
     * null</tt>. (There can be at most one such mapping.)
     *
     * @param  key Key whose associated value is to be returned.
     *
     * @return The value to which this map maps the specified key, or <tt>null</tt> if the map contains no mapping for
     *         this key.
     *
     * @throws ClassCastException   If the key is of an inappropriate type for this map (optional).
     * @throws NullPointerException If the key is <tt>null</tt> and this map does not permit <tt>null</tt> keys
     *                              (optional).
     *
     * @see    #containsKey(Object)
     */
    public Object get(Object key)
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            // Try to extract the matching element as an ElementMonitor,
            ElementMonitor monitor = (ElementMonitor) cache.get(key);

            // If the element is not null then return its value, else null.
            // Also upgrade the timestamp of the matching element to the present to show that it has been recently
            // accessed.
            if (monitor != null)
            {
                // Upgrade the timestamp.
                long t = System.currentTimeMillis();
                monitor.lastTouched = t;

                // Return the element.
                return monitor.element;
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Associates the specified value with the specified key in this map (optional operation). If the map previously
     * contained a mapping for this key, the old value is replaced by the specified value. (A map <tt>m</tt> is said to
     * contain a mapping for a key <tt>k</tt> if and only if {@link #containsKey(Object) m.containsKey(k)} would return
     * <tt>true</tt>.))
     *
     * @param  key   Key with which the specified value is to be associated.
     * @param  value Value to be associated with the specified key.
     *
     * @return Previous value associated with specified key, or <tt>null</tt> if there was no mapping for key. A <tt>
     *         null</tt> return can also indicate that the map previously associated <tt>null</tt> with the specified
     *         key, if the implementation supports <tt>null</tt> values.
     *
     * @throws UnsupportedOperationException If the <tt>put</tt> operation is not supported by this map.
     * @throws ClassCastException            If the class of the specified key or value prevents it from being stored in
     *                                       this map.
     * @throws IllegalArgumentException      If some aspect of this key or value prevents it from being stored in this
     *                                       map.
     * @throws NullPointerException          If this map does not permit <tt>null</tt> keys or values, and the specified
     *                                       key or value is <tt>null</tt>.
     */
    public Object put(Object key, Object value)
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            // Create a new ElementMonitor in the cache for the new element.
            // Timestamp the new element with the present time.
            long t = System.currentTimeMillis();

            // Extract the element value out of the replaced element monitor if any.
            ElementMonitor replaced = (ElementMonitor) cache.put(key, new ElementMonitor(value, t));

            if (replaced != null)
            {
                return replaced.element;
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * Removes the mapping for this key from this map if it is present (optional operation). More formally, if this map
     * contains a mapping from key <tt>k</tt> to value <tt>v</tt> such that <code>(key==null ? k==null :
     * key.equals(k))</code>, that mapping is removed. (The map can contain at most one such mapping.)
     *
     * <p>Returns the value to which the map previously associated the key, or <tt>null</tt> if the map contained no
     * mapping for this key. (A <tt>null</tt> return can also indicate that the map previously associated <tt>null</tt>
     * with the specified key if the implementation supports <tt>null</tt> values.) The map will not contain a mapping
     * for the specified key once the call returns.
     *
     * @param  key key whose mapping is to be removed from the map.
     *
     * @return previous value associated with specified key, or <tt>null</tt> if there was no mapping for key.
     *
     * @throws ClassCastException            if the key is of an inappropriate type for this map (optional).
     * @throws NullPointerException          if the key is <tt>null</tt> and this map does not permit <tt>null</tt> keys
     *                                       (optional).
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is not supported by this map.
     */
    public Object remove(Object key)
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            // Remove the element from the marked heap (if it exists in the heap) and from the cache.
            marked.remove(key);

            return cache.remove(key);
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map (optional operation). The effect of this call is
     * equivalent to that of calling {@link #put(Object,Object) put(k, v)} on this map once for each mapping from key
     * <tt>k</tt> to value <tt>v</tt> in the specified map. The behavior of this operation is unspecified if the
     * specified map is modified while the operation is in progress.
     *
     * @param  t Mappings to be stored in this map.
     *
     * @throws UnsupportedOperationException if the <tt>putAll</tt> method is not supported by this map.
     * @throws ClassCastException            if the class of a key or value in the specified map prevents it from being
     *                                       stored in this map.
     * @throws IllegalArgumentException      some aspect of a key or value in the specified map prevents it from being
     *                                       stored in this map.
     * @throws NullPointerException          if the specified map is <tt>null</tt>, or if this map does not permit <tt>
     *                                       null</tt> keys or values, and the specified map contains <tt>null</tt> keys
     *                                       or values.
     */
    public void putAll(Map t)
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            // Iterate over all elements in the map to add, placing each of them into an ElementMonitor before
            // putting them into the cache.
            for (Object nextKey : t.keySet())
            {
                Object nextValue = t.get(nextKey);

                // Delegate to the put method to wrap the new item in an ElementMonitor.
                cache.put(nextKey, nextValue);
            }
        }
    }

    /**
     * Removes all mappings from this map (optional operation).
     *
     * @throws UnsupportedOperationException clear is not supported by this map.
     */
    public void clear()
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            // Clear the cache and the marked heap.
            marked.clear();
            cache.clear();
        }
    }

    /**
     * Returns a set view of the keys contained in this map. The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa. If the map is modified while an iteration over the set is in progress
     * (except through the iterator's own <tt>remove</tt> operation), the results of the iteration are undefined. The
     * set supports element removal, which removes the corresponding mapping from the map, via the <tt>
     * Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt> <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the add or <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this map.
     */
    public Set keySet()
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            return cache.keySet();
        }
    }

    /**
     * Returns a collection view of the values contained in this map. The collection is backed by the map, so changes to
     * the map are reflected in the collection, and vice-versa. If the map is modified while an iteration over the
     * collection is in progress (except through the iterator's own <tt>remove</tt> operation), the results of the
     * iteration are undefined. The collection supports element removal, which removes the corresponding mapping from
     * the map, via the <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations. It does not support the add or <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this map.
     */
    public Collection values()
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            return cache.values();
        }
    }

    /**
     * Returns a set view of the mappings contained in this map. Each element in the returned set is a {@link Map.Entry}
     * . The set is backed by the map, so changes to the map are reflected in the set, and vice-versa. If the map is
     * modified while an iteration over the set is in progress (except through the iterator's own <tt>remove</tt>
     * operation, or through the <tt>setValue</tt> operation on a map entry returned by the iterator) the results of the
     * iteration are undefined. The set supports element removal, which removes the corresponding mapping from the map,
     * via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt>
     * operations. It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map.
     */
    public Set entrySet()
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            return cache.entrySet();
        }
    }

    /**
     * Compares the specified object with this map for equality. Returns <tt>true</tt> if the given object is also a map
     * and the two Maps represent the same mappings. More formally, two maps <tt>t1</tt> and <tt>t2</tt> represent the
     * same mappings if <tt>t1.entrySet().equals(t2.entrySet())</tt>. This ensures that the <tt>equals</tt> method works
     * properly across different implementations of the <tt>Map</tt> interface.
     *
     * @param  o object to be compared for equality with this map.
     *
     * @return <tt>true</tt> if the specified object is equal to this map.
     */
    public boolean equals(Object o)
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            // Check that the comparator is also a TimeoutMap.
            if (o instanceof TimeoutMap)
            {
                TimeoutMap tm = (TimeoutMap) o;

                // Check the underlying caches are identical.
                return tm.cache.equals(cache);
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Returns the hash code value for this map. The hash code of a map is defined to be the sum of the hashCodes of
     * each entry in the map's entrySet view. This ensures that <tt>t1.equals(t2)</tt> implies that <tt>
     * t1.hashCode()==t2.hashCode()</tt> for any two maps <tt>t1</tt> and <tt>t2</tt>, as required by the general
     * contract of Object.hashCode.
     *
     * @return the hash code value for this map.
     *
     * @see    Map.Entry#hashCode()
     * @see    Object#hashCode()
     * @see    Object#equals(Object)
     * @see    #equals(Object)
     */
    public int hashCode()
    {
        // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
        synchronized (cache)
        {
            return cache.hashCode();
        }
    }

    /**
     * Garbage collects the cache, sweeping out any elements that have timed out. This method should really only be
     * invoked in a seperate thread as it does not return (at least not until the {@link #sweepThreadKillFlag} is set).
     *
     * @todo Should really check that the sweep time has actually expired when the thread wakes up.
     */
    private void sweep()
    {
        /*log.fine("private void sweep(): called");*/

        // Loop until the thread is terminated.
        while (true)
        {
            // Take a marked copy of the cache to examine for timed out elements.
            // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
            synchronized (cache)
            {
                /*log.fine("\tMarking " + cache.size() + " objects.");*/

                // Take a copy of everything in the cache into the marked heap.
                marked.putAll(cache);
            }

            // Use synchronized block to own this objects monitor so that it can be waited on.
            // This is needed so that the kill method, and other methods, can wake this thread up.
            synchronized (this)
            {
                // Use a try block as the thread may be woken up during the pause time between sweeps.
                try
                {
                    // Halt the thread between sweeps, configured by the sweepTime property.
                    wait(sweepTime);
                }
                catch (InterruptedException e)
                {
                    // Ignore this, interuption conditions will be tested later.
                }
            }

            // TODO: Should really check that sweepTime has expired.

            // Check the sweep thread kill flag to see if the sweep algorithm has been stopped.
            if (sweepThreadKillFlag)
            {
                return;
            }

            // Create a counter to count the number of elements removed from the cache.
            int i = 0;

            // Create a map to copy the marked heap into. This is done because the following code must iterate
            // over the marked heap whilst modifying it. A copy is used to generate all the keys to iterate over so
            // that the iterator is not disturbed by its underlying data structure being simultaneously modified.
            Map copy = new HashMap();

            // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
            synchronized (cache)
            {
                // Put everything in the marked cache into the copy.
                copy.putAll(marked);
            }

            // Loop over the copy of the marked heap looking for timed out elements.
            for (Object nextKey : copy.keySet())
            {
                // Get the key of the next element from the copy of the marked heap.
                // Extract the current element from the copy of the marked heap as an ElementMonitor object.
                ElementMonitor nextMonitor = (ElementMonitor) copy.get(nextKey);

                // Get the current time in milliseconds.
                long t = System.currentTimeMillis();

                // Check if the current element has not been accessed for a while, configured by the
                // sweepExpiryTime property.
                if ((t - nextMonitor.lastTouched) > sweepExpiryTime)
                {
                    // Synchronize on the cache to ensure its integrity in a multi-threaded environment.
                    synchronized (cache)
                    {
                        // Remove the out of date element from the marked heap and from the cache.
                        marked.remove(nextKey);
                        cache.remove(nextKey);

                        /*log.fine("Element removed from the cache " + nextKey);*/

                        // Increment the count of invalidated elements.
                        i++;

                    }
                }
            }

            /*log.fine(i + " objects removed.");*/
        }
    }

    /**
     * ElementMonitor is a container object for cache elements that pairs the cache element with a timestamp. This
     * timestamp can be upgraded to the present time every time the element is accesses and is used by the sweep
     * algorithm to determine how old elements are.
     */
    private static class ElementMonitor
    {
        /** The cache element. */
        public Object element;

        /** The time it was last touched. */
        public long lastTouched;

        /**
         * Builds an element monitor.
         *
         * @param element   The cache element to store in the monitor.
         * @param timestamp The creation time stamp for the element.
         */
        public ElementMonitor(Object element, long timestamp)
        {
            this.element = element;
            this.lastTouched = timestamp;
        }

        /**
         * Comparison operator. Compares only the element and not the time stamp. This preserves the normal equality
         * between cache elements irrespective of their timestamp.
         *
         * @param  o The object to compare this element monitor to.
         *
         * @return True if the element is equal to the comparators element, false otherwise.
         */
        public boolean equals(Object o)
        {
            // Check that the comparitor is an element monitor and cast it to one and extract its cache element.
            return (o instanceof ElementMonitor) && element.equals(((ElementMonitor) o).element);
        }
    }
}
