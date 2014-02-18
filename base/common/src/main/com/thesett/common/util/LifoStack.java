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

import java.util.ArrayList;
import java.util.Collection;

/**
 * An unsynchronized LIFO stack. This class provides easy access to pushing and popping objects to and from a stack
 * where the rule is that the last object in is the first object out. As with most Java collections, this class is
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
public class LifoStack<E> extends ArrayList<E> implements Stack<E>
{
    /** Constructs an empty LIFO stack. */
    public LifoStack()
    {
        super();
    }

    /**
     * Builds a LIFO stack from another collection.
     *
     * @param c The collection to copy into this stack.
     */
    public LifoStack(Collection c)
    {
        super(c);
    }

    /**
     * Provides a look at the last object placed on the stack, since it will be the first one out. This method does not
     * change the contents of the stack. Because this class is unsynchronized, applications using this class are
     * responsible for making sure that a <CODE>peek()</CODE> followed by a <CODE>pop()</CODE> returns the same value.
     *
     * @return the object on the top of the stack
     */
    public E peek()
    {
        int last = size() - 1;
        E ob;

        if (last == -1)
        {
            return null;
        }

        ob = get(last);

        return ob;
    }

    /**
     * Pops the last object placed on the stack off of it and returns it.
     *
     * @return the last object placed on the stack
     */
    public E pop()
    {
        int last = size() - 1;
        E ob;

        if (last == -1)
        {
            return null;
        }

        ob = get(last);
        remove(last);

        return ob;
    }

    /**
     * Pushes a new object onto the stack.
     *
     * @param  ob the new object
     *
     * @return the new object
     */
    public E push(E ob)
    {
        add(ob);

        return ob;
    }

    /**
     * Searches the stack for the specified object. Returns the location of the object with respect to the top of the
     * stack or -1.
     *
     * @param  ob the object being sought
     *
     * @return the index of the object on the stack or -1.
     */
    public int search(E ob)
    {
        int i = lastIndexOf(ob);

        if (i == -1)
        {
            return -1;
        }
        else
        {
            return (size() - i);
        }
    }
}
