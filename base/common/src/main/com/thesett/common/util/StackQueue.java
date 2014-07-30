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

import java.util.LinkedList;

/**
 * The Stack class in java.util (most unhelpfully) does not implement the Queue interface. This is an implementation of
 * a stack that does implement the Queue interface.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide a queue that works as a LIFO stack.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class StackQueue<E> extends LinkedList<E> implements Queue<E>
{
    /**
     * Inserts the specified element into this queue, if possible.
     *
     * @param  o The data element to push onto the stack.
     *
     * @return True if it was added to the stack, false otherwise (this implementation always returns true).
     */
    public boolean offer(E o)
    {
        super.add(0, o);

        return true;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @param  e element to be appended to this list
     *
     * @return <tt>true</tt> (as specified by {@link java.util.Collection#add})
     */
    public boolean add(E e)
    {
        super.add(0, e);

        return true;
    }
}
