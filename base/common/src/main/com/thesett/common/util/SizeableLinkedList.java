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
import java.util.LinkedList;

/**
 * SizeableLinkedList is a list of sizeables that tracks the size of its elements to generate a total size for the list.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Keep track of the size of a list of sizeables.
 * <td> {@link Sizeable}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SizeableLinkedList<T extends Sizeable> extends LinkedList<T> implements SizeableList<T>
{
    /** Holds the byte size of the listing. */
    protected long sizeOf;

    /**
     * Calculates the size of this object in bytes.
     *
     * @return The size of this object in bytes.
     */
    public long sizeof()
    {
        return sizeOf;
    }

    /**
     * Appends the specified element to the end of this list (optional operation).
     *
     * <p/>Lists that support this operation may place limitations on what elements may be added to this list. In
     * particular, some lists will refuse to add null elements, and others will impose restrictions on the type of
     * elements that may be added. List classes should clearly specify in their documentation any restrictions on what
     * elements may be added.
     *
     * @param  o element to be appended to this list.
     *
     * @return <tt>true</tt> (as per the general contract of the <tt>Collection.add</tt> method).
     *
     * @throws UnsupportedOperationException if the <tt>add</tt> method is not supported by this list.
     * @throws ClassCastException            if the class of the specified element prevents it from being added to this
     *                                       list.
     * @throws NullPointerException          if the specified element is null and this list does not support null
     *                                       elements.
     * @throws IllegalArgumentException      if some aspect of this element prevents it from being added to this list.
     */
    public boolean add(T o)
    {
        boolean result = super.add(o);

        sizeOf += (result) ? o.sizeof() : 0;

        return result;
    }

    /**
     * Removes the first occurrence in this list of the specified element (optional operation). If this list does not
     * contain the element, it is unchanged. More formally, removes the element with the lowest index i such that <tt>
     * (o==null ? get(i)==null : o.equals(get(i)))</tt> (if such an element exists).
     *
     * @param  o element to be removed from this list, if present.
     *
     * @return <tt>true</tt> if this list contained the specified element.
     *
     * @throws ClassCastException            if the type of the specified element is incompatible with this list
     *                                       (optional).
     * @throws NullPointerException          if the specified element is null and this list does not support null
     *                                       elements (optional).
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is not supported by this list.
     */
    public boolean remove(Object o)
    {
        boolean result = super.remove(o);

        sizeOf -= ((o instanceof Sizeable) && result) ? ((Sizeable) o).sizeof() : 0;

        return result;
    }

    /**
     * Removes all of the elements from this list (optional operation). This list will be empty after this call returns
     * (unless it throws an exception).
     *
     * @throws UnsupportedOperationException if the <tt>clear</tt> method is not supported by this list.
     */
    public void clear()
    {
        super.clear();
        sizeOf = 0;
    }

    /**
     * Inserts the specified element at the specified position in this list (optional operation). Shifts the element
     * currently at that position (if any) and any subsequent elements to the right (adds one to their indices).
     *
     * @param  index   index at which the specified element is to be inserted.
     * @param  element element to be inserted.
     *
     * @throws UnsupportedOperationException if the <tt>add</tt> method is not supported by this list.
     * @throws ClassCastException            if the class of the specified element prevents it from being added to this
     *                                       list.
     * @throws NullPointerException          if the specified element is null and this list does not support null
     *                                       elements.
     * @throws IllegalArgumentException      if some aspect of the specified element prevents it from being added to
     *                                       this list.
     * @throws IndexOutOfBoundsException     if the index is out of range (index &lt; 0 || index &gt; size()).
     */
    public void add(int index, T element)
    {
        super.add(index, element);
        sizeOf += element.sizeof();
    }

    /**
     * Removes the element at the specified position in this list (optional operation). Shifts any subsequent elements
     * to the left (subtracts one from their indices). Returns the element that was removed from the list.
     *
     * @param  index the index of the element to removed.
     *
     * @return the element previously at the specified position.
     *
     * @throws UnsupportedOperationException if the <tt>remove</tt> method is not supported by this list.
     * @throws IndexOutOfBoundsException     if the index is out of range (index &lt; 0 || index &gt;= size()).
     */
    public T remove(int index)
    {
        T result = super.remove(index);

        sizeOf += (result != null) ? result.sizeof() : 0;

        return result;
    }

    /**
     * Appends all of the elements in the specified collection to the end of this list, in the order that they are
     * returned by the specified collection's iterator (optional operation). The behavior of this operation is
     * unspecified if the specified collection is modified while the operation is in progress. (Note that this will
     * occur if the specified collection is this list, and it's nonempty.)
     *
     * @param  c collection whose elements are to be added to this list.
     *
     * @return <tt>true</tt> if this list changed as a result of the call.
     *
     * @throws UnsupportedOperationException if the <tt>addAll</tt> method is not supported by this list.
     * @throws ClassCastException            if the class of an element in the specified collection prevents it from
     *                                       being added to this list.
     * @throws NullPointerException          if the specified collection contains one or more null elements and this
     *                                       list does not support null elements, or if the specified collection is <tt>
     *                                       null</tt>.
     * @throws IllegalArgumentException      if some aspect of an element in the specified collection prevents it from
     *                                       being added to this list.
     *
     * @see    #add(Object)
     */
    public boolean addAll(Collection<? extends T> c)
    {
        if (c instanceof Sizeable)
        {
            sizeOf += ((Sizeable) c).sizeof();
        }

        return super.addAll(c);
    }
}
