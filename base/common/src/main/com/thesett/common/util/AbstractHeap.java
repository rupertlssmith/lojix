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
import java.util.NoSuchElementException;

/**
 * A Heap is a collection of data items that are stored as a tree that obeyes the heap property. The heap property is
 * that there exists an ordering amongst the elements, and the elements are stored in a tree in such a way that the
 * child elements are all ordered the same way with respect to their parent node and that the same direction of ordering
 * applies accross the whole tree. Most often the ordering is "greater than" in which case the minimum element is always
 * at the root of the tree and child elements are always greater than their parents. One of the purposes of heaps is to
 * be able to provide fast access to the smallest element amongst a data collection.
 *
 * <p>Viewed in this way a heap is a priorty queue of elements. Heaps are often used to store key and value pairs where
 * the keys are ordered as in a heap. This implementation of heaps does not explicitly split the data elements into keys
 * and values because this is unnecessary and easily achieved by providing an ordering comparator that only looks at
 * what is considered to be the 'key' part of a data element. The end user can easily split up the elements just as
 * could be done for storage in any collection. A heap is not a map in the sense of java.util.Map because it does not
 * provide a mapping from any key onto any value, it can contain multiple entries for the same 'key' and it is only
 * designed to provide quick access to the smallest key and not the entire key set.
 *
 * <p>AbstractHeap provides a skeletal implementation of a heap to be sub-classed with specific implementations.
 *
 * <p>All methods that are allowed to be optional and throw unsupported operation exceptions do so in this base class if
 * they are not implemented. It is up to sub-class imlpementations to provide these operations if desired.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Add one or many elements to the heap.
 * <tr><td>Check if the heap contains an element or many elements.
 * <tr><td>Remove one or many element from the heap.
 * <tr><td>Get the heap size or check if it is empty.
 * <tr><td>Convert the heap into an array.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Add the decrease key and merge operations. Extract interface for heaps with these operations.
 */
public abstract class AbstractHeap<E> implements Queue<E>
{
    /**
     * Holds a reference to the element comparator used to order the heap. This will be null if no comparator is set by
     * the constructor, in which case the natural ordering should be used instead.
     */
    protected final Comparator<? super E> entryComparator;

    /** Used to hold the number of elements in the heap. */
    protected int size;

    /**
     * Creates a heap that uses the given comparator to order its elements.
     *
     * @param comparator The comparator to be used to order the heap elements.
     */
    protected AbstractHeap(Comparator<? super E> comparator)
    {
        // Set the size to zero.
        size = 0;

        // Keep the comparator.
        this.entryComparator = comparator;
    }

    /**
     * Returns an iterator over the elements in this heap.
     *
     * @return An iterator over the elements in this heap.
     */
    public abstract Iterator<E> iterator();

    /**
     * Inserts the specified element into this heap.
     *
     * @param  o The data element to add to the heap.
     *
     * @return True if the element was added, false if it was not.
     */
    public abstract boolean offer(E o);

    /**
     * Retrieves, but does not remove, the (head) minimum element of this heap, returning null if this heap is empty.
     *
     * @return The head element of the heap, or null if the heap is empty.
     */
    public abstract E peek();

    /**
     * Retrieves and removes the (head) minimum element of this heap, or null if this heap is empty.
     *
     * @return The head element of the heap, or null if the heap is empty.
     */
    public abstract E poll();

    /**
     * Adds the specified element to this heap. This implementation returns <tt>true</tt> if <tt>offer</tt> succeeds,
     * else throws an IllegalStateException.
     *
     * @param  o the element
     *
     * @return <tt>true</tt> (as per the general contract of <tt>Collection.add</tt>).
     *
     * @throws NullPointerException  if the specified element is <tt>null</tt>
     * @throws IllegalStateException if element cannot be added
     */
    public boolean add(E o)
    {
        if (offer(o))
        {
            return true;
        }
        else
        {
            throw new IllegalStateException("Heap full");
        }
    }

    /**
     * Adds all of the elements in the specified collection to this heap. Attempts to addAll of a queue to itself result
     * in <tt>IllegalArgumentException</tt>. Further, the behavior of this operation is undefined if the specified
     * collection is modified while the operation is in progress.
     *
     * <p>This implementation iterates over the specified collection, and adds each element returned by the iterator to
     * this collection, in turn. A runtime exception encountered while trying to add an element (including, in
     * particular, a <tt>null</tt> element) may result in only some of the elements having been successfully added when
     * the associated exception is thrown.
     *
     * @param  collection collection whose elements are to be added to this collection.
     *
     * @return <tt>true</tt> if this collection changed as a result of the call.
     *
     * @throws NullPointerException     if the specified collection or any of its elements are null.
     * @throws IllegalArgumentException if collection is this queue.
     *
     * @see    #add(Object)
     */
    public boolean addAll(Collection<? extends E> collection)
    {
        if (collection == null)
        {
            throw new IllegalArgumentException("The 'collection' parameter may not be null.");
        }

        if (collection == this)
        {
            throw new IllegalArgumentException();
        }

        boolean modified = false;

        for (E aC : collection)
        {
            if (add(aC))
            {
                modified = true;
            }
        }

        return modified;
    }

    /**
     * Removes all of the elements from this heap. The heap will be empty after this call returns.
     *
     * <p>This implementation repeatedly invokes {@link #poll poll} until it returns <tt>null</tt>.
     */
    public void clear()
    {
        while (poll() != null)
        {
            ;
        }
    }

    /**
     * Returns true if this heap contains the specified element. More formally, returns <tt>true</tt> if and only if
     * this collection contains at least one element <tt>e</tt> such that <tt>(o==null ? e==null : o.equals(e))</tt>.
     *
     * <p>This implementation iterates over the elements in the collection, checking each element in turn for equality
     * with the specified element.
     *
     * @param  o object to be checked for containment in this collection.
     *
     * @return <tt>true</tt> if this collection contains the specified element.
     */
    public boolean contains(Object o)
    {
        // Get an iterator over the whole heap.
        Iterator<E> e = iterator();

        // Check if the element to be scanned for is null.
        if (o == null)
        {
            while (e.hasNext())
            {
                if (e.next() == null)
                {
                    return true;
                }
            }
        }

        // Its not a null element so check for it by the equality method.
        else
        {
            while (e.hasNext())
            {
                if (o.equals(e.next()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if this heap contains all of the elements in the specified collection.
     *
     * <p>This implementation iterates over the specified collection, checking each element returned by the iterator in
     * turn to see if it's contained in this collection. If all elements are so contained <tt>true</tt> is returned,
     * otherwise <tt>false</tt>.
     *
     * @param  c collection to be checked for containment in this collection.
     *
     * @return <tt>true</tt> if this collection contains all of the elements in the specified collection.
     *
     * @throws NullPointerException if the specified collection is null.
     *
     * @see    #contains(Object)
     */
    public boolean containsAll(Collection<?> c)
    {
        for (Object aC : c)
        {
            if (!contains(aC))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if this heap contains no elements.
     *
     * @return <tt>true</tt> if this heap contains no elements.
     */
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * Removes a single instance of the specified element from this heap, if it is present. (optional operation). More
     * formally, removes an element <tt>e</tt> such that <tt>(o==null ? e==null : o.equals(e))</tt>, if the collection
     * contains one or more such elements. Returns <tt>true</tt> if the collection contained the specified element (or
     * equivalently, if the collection changed as a result of the call).
     *
     * <p>This implementation iterates over the collection looking for the specified element. If it finds the element,
     * it removes the element from the collection using the iterator's remove method.
     *
     * <p>Note that this implementation throws an <tt>UnsupportedOperationException</tt> if the iterator returned by
     * this collection's iterator method does not implement the <tt>remove</tt> method and this collection contains the
     * specified object.
     *
     * @param  o element to be removed from this collection, if present.
     *
     * @return <tt>true</tt> if the collection contained the specified element.
     *
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is not supported by this collection.
     */
    public boolean remove(Object o)
    {
        Iterator<E> e = iterator();

        if (o == null)
        {
            while (e.hasNext())
            {
                if (e.next() == null)
                {
                    e.remove();

                    return true;
                }
            }
        }
        else
        {
            while (e.hasNext())
            {
                if (o.equals(e.next()))
                {
                    e.remove();

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Removes all this heap's elements that are also contained in the specified collection.
     *
     * <p>This implementation iterates over this collection, checking each element returned by the iterator in turn to
     * see if it's contained in the specified collection. If it's so contained, it's removed from this collection with
     * the iterator's <tt>remove</tt> method.
     *
     * <p>Note that this implementation will throw an <tt>UnsupportedOperationException</tt> if the iterator returned by
     * the <tt>iterator</tt> method does not implement the <tt>remove</tt> method and this collection contains one or
     * more elements in common with the specified collection.
     *
     * @param  c elements to be removed from this collection.
     *
     * @return <tt>true</tt> if this collection changed as a result of the call.
     *
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> method is not supported by this collection.
     * @throws NullPointerException          if the specified collection is null.
     *
     * @see    #remove(Object)
     * @see    #contains(Object)
     */
    public boolean removeAll(Collection<?> c)
    {
        boolean modified = false;
        Iterator<?> e = iterator();

        while (e.hasNext())
        {
            if (c.contains(e.next()))
            {
                e.remove();
                modified = true;
            }
        }

        return modified;
    }

    /**
     * Retains only the elements in this heap that are contained in the specified collection. In other words, removes
     * from this collection all of its elements that are not contained in the specified collection.
     *
     * <p>This implementation iterates over this collection, checking each element returned by the iterator in turn to
     * see if it's contained in the specified collection. If it's not so contained, it's removed from this collection
     * with the iterator's <tt>remove</tt> method.
     *
     * <p>Note that this implementation will throw an <tt>UnsupportedOperationException</tt> if the iterator returned by
     * the <tt>iterator</tt> method does not implement the <tt>remove</tt> method and this collection contains one or
     * more elements not present in the specified collection.
     *
     * @param  c elements to be retained in this collection.
     *
     * @return <tt>true</tt> if this collection changed as a result of the call.
     *
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> method is not supported by this Collection.
     * @throws NullPointerException          if the specified collection is null.
     *
     * @see    #remove(Object)
     * @see    #contains(Object)
     */
    public boolean retainAll(Collection<?> c)
    {
        boolean modified = false;
        Iterator<E> e = iterator();

        while (e.hasNext())
        {
            if (!c.contains(e.next()))
            {
                e.remove();
                modified = true;
            }
        }

        return modified;
    }

    /**
     * Returns the number of elements in this heap.
     *
     * @return The number of elements in this heap.
     */
    public int size()
    {
        return size;
    }

    /**
     * Returns an array containing all of the elements in this heap. If the collection makes any guarantees as to what
     * order its elements are returned by its iterator, this method must return the elements in the same order. The
     * returned array will be "safe" in that no references to it are maintained by the collection. (In other words, this
     * method must allocate a new array even if the collection is backed by an Array). The caller is thus free to modify
     * the returned array.
     *
     * <p>This implementation allocates the array to be returned, and iterates over the elements in the collection,
     * storing each object reference in the next consecutive element of the array, starting with element 0.
     *
     * @return an array containing all of the elements in this collection.
     */
    public Object[] toArray()
    {
        Object[] result = new Object[size()];

        Iterator<E> e = iterator();

        for (int i = 0; e.hasNext(); i++)
        {
            result[i] = e.next();
        }

        return result;
    }

    /**
     * Returns an array containing all of the elements in this heap; the runtime type of the returned array is that of
     * the specified array. If the collection fits in the specified array, it is returned therein. Otherwise, a new
     * array is allocated with the runtime type of the specified array and the size of this collection.
     *
     * <p>If the collection fits in the specified array with room to spare (i.e., the array has more elements than the
     * collection), the element in the array immediately following the end of the collection is set to <tt>null</tt>.
     * This is useful in determining the length of the collection <i>only</i> if the caller knows that the collection
     * does not contain any <tt>null</tt> elements.)
     *
     * <p>If this collection makes any guarantees as to what order its elements are returned by its iterator, this
     * method must return the elements in the same order.
     *
     * <p>This implementation checks if the array is large enough to contain the collection; if not, it allocates a new
     * array of the correct size and type (using reflection). Then, it iterates over the collection, storing each object
     * reference in the next consecutive element of the array, starting with element 0. If the array is larger than the
     * collection, a <tt>null</tt> is stored in the first location after the end of the collection.
     *
     * @param  a   The array into which the elements of the collection are to be stored, if it is big enough; otherwise,
     *             a new array of the same runtime type is allocated for this purpose.
     * @param  <T> The type of the array.
     *
     * @return an array containing the elements of the collection.
     *
     * @throws NullPointerException if the specified array is <tt>null</tt>.
     * @throws ArrayStoreException  if the runtime type of the specified array is not a supertype of the runtime type of
     *                              every element in this collection.
     */
    public <T> T[] toArray(T[] a)
    {
        int size = size();

        if (a.length < size)
        {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }

        Iterator<E> it = iterator();
        Object[] result = a;

        for (int i = 0; i < size; i++)
        {
            result[i] = it.next();
        }

        if (a.length > size)
        {
            a[size] = null;
        }

        return a;
    }

    /**
     * Returns the comparator used to order this heap.
     *
     * @return The comparator used to order this heap, or null if this heap is sorted according to its elements natural
     *         ordering (using Comparable).
     */
    public Comparator<? super E> comparator()
    {
        return entryComparator;
    }

    /**
     * Retrieves, but does not remove, the (head) minimum element of this heap. This implementation returns the result
     * of <tt>peek</tt> unless the queue is empty.
     *
     * @return the head of this queue.
     *
     * @throws NoSuchElementException if this queue is empty.
     */
    public E element()
    {
        E x = peek();

        if (x != null)
        {
            return x;
        }
        else
        {
            throw new NoSuchElementException();
        }
    }

    /**
     * Retrieves and removes the (head) minimum element of this heap. This implementation returns the result of <tt>
     * poll</tt> unless the queue is empty.
     *
     * @return the head of this queue.
     *
     * @throws NoSuchElementException if this queue is empty.
     */
    public E remove()
    {
        E x = poll();

        if (x != null)
        {
            return x;
        }
        else
        {
            throw new NoSuchElementException();
        }
    }

    /**
     * Returns a string representation of this heap. The string representation consists of a list of the collection's
     * elements in the order they are returned by its iterator, enclosed in square brackets (<tt>"[]"</tt>). Adjacent
     * elements are separated by the characters <tt>", "</tt> (comma and space). Elements are converted to strings as by
     * <tt>String.valueOf(Object)</tt>.
     *
     * <p>This implementation creates an empty string buffer, appends a left square bracket, and iterates over the
     * collection appending the string representation of each element in turn. After appending each element except the
     * last, the string <tt>", "</tt> is appended. Finally a right bracket is appended. A string is obtained from the
     * string buffer, and returned.
     *
     * @return a string representation of this collection.
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();

        buf.append("[");

        Iterator<E> i = iterator();
        boolean hasNext = i.hasNext();

        while (hasNext)
        {
            E o = i.next();

            buf.append((o == this) ? "(this Collection)" : String.valueOf(o));
            hasNext = i.hasNext();

            if (hasNext)
            {
                buf.append(", ");
            }
        }

        buf.append("]");

        return buf.toString();
    }
}
