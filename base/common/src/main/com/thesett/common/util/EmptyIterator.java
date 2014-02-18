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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * EmptyIterator provides an empty iterator implements, suitable for situation where an iterator is expected as a return
 * argument, but no items to iterate over are going to be provided. This may be better than returning <tt>null</tt> from
 * such a method, as it may be less liekly to cause a NullPointerException.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Iterate over nothing.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EmptyIterator<E> implements Iterator<E>
{
    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other words, returns <tt>true</tt> if <tt>next</tt>
     * would return an element rather than throwing an exception.)
     *
     * @return Always <tt>false</tt>.
     */
    public boolean hasNext()
    {
        return false;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return Never returns anything, this will always throw NoSuchElementException.
     *
     * @throws NoSuchElementException iteration has no more elements.
     */
    public E next()
    {
        throw new NoSuchElementException("EmptyIterator never has any elements.");
    }

    /**
     * Removes from the underlying collection the last element returned by the iterator (optional operation). This
     * method can be called only once per call to <tt>next</tt>. The behavior of an iterator is unspecified if the
     * underlying collection is modified while the iteration is in progress in any way other than by calling this
     * method.
     *
     * @throws UnsupportedOperationException Always throws this.
     */
    public void remove()
    {
        throw new UnsupportedOperationException("EmptyIterator does not have any elements to remove.");
    }
}
