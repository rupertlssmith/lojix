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

import java.util.ArrayList;
import java.util.Collection;

/**
 * An unsynchronized FIFO stack. This class provides easy access to pushing and popping objects to and from a stack
 * where the rule is that the first object in is the first object out. As with most Java collections, this class is
 * wholly unsynchronized.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check if stack is empty
 * <tr><td>Peek at top element
 * <tr><td>Pop top element from stack
 * <tr><td>Push element onto stack
 * <tr><td>Search stack for element location
 * <tr><td>Calculate size of stack
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class FifoStack<E> extends ArrayList<E> implements Stack<E>
{
    /** Constructs an empty FIFO stack. */
    public FifoStack()
    {
        super();
    }

    /**
     * Builds a FIFO stack from another collection.
     *
     * @param c The collection to copy into this stack.
     */
    public FifoStack(Collection c)
    {
        super(c);
    }

    /**
     * Provides a look at the first object placed on the stack, since it will be the first one out. This method does not
     * change the contents of the stack. Because this class is unsynchronized, applications using this class are
     * responsible for making sure that a <CODE>peek()</CODE> followed by a <CODE>pop()</CODE> returns the same value.
     *
     * @return The first object on the top of the stack.
     */
    public E peek()
    {
        E ob;

        if (size() == 0)
        {
            return null;
        }

        ob = get(0);

        return ob;
    }

    /**
     * Pops the first object placed on the stack off of it and returns it.
     *
     * @return The first object placed on the stack.
     */
    public E pop()
    {
        E ob;

        if (size() == 0)
        {
            return null;
        }

        ob = get(0);
        remove(0);

        return ob;
    }

    /**
     * Pushes a new object onto the end of stack.
     *
     * @param  ob The new object.
     *
     * @return The new object.
     */
    public E push(E ob)
    {
        add(ob);

        return ob;
    }

    /**
     * Searches the stack for the specified object. Returns the location of the object with respect to the first object
     * on the stack or -1.
     *
     * @param  ob The object being sought.
     *
     * @return The index of the object on the stack or -1.
     */
    public int search(E ob)
    {
        int i = indexOf(ob);

        if (i == -1)
        {
            return -1;
        }
        else
        {
            return (i + 1);
        }
    }
}
