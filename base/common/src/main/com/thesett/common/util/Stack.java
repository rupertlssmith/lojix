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

/**
 * A generic interface for stacked collections. This interface prescribes methods that let you access objects in a
 * collection based on some rule of order.
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
public interface Stack<E> extends java.util.Collection<E>
{
    /**
     * Checks if the stack is empty.
     *
     * @return True if there are no objects on the stack
     */
    boolean isEmpty();

    /**
     * Provides a look at the next object on the stack without removing it.
     *
     * @return the next object on the stack
     */
    E peek();

    /**
     * Removes the next object on the stack and returns it.
     *
     * @return the next object on the stack
     */
    E pop();

    /**
     * Places an object on the stack.
     *
     * @param  ob The object to be placed on the stack.
     *
     * @return The object placed on the stack
     */
    E push(E ob);

    /**
     * Provides the location of the specified object on the stack. The number 1 means the first object, 2 the second,
     * and so on.
     *
     * @param  ob The object to try to find on the stack.
     *
     * @return The location of the object on the stack or -1 if it is not on the stack.
     */
    int search(E ob);

    /**
     * Gets the number of elements on the stack.
     *
     * @return the number of elements on the stack.
     */
    int size();
}
