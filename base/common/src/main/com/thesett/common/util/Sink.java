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

/**
 * A Sink is the point at which a producer may add items into a queue. This interface is compatable with
 * java.util.Queue, in that any Queue implementation can also implement this interface with no further work. Sink has
 * been created as a seperate interface, to inroduce the possibility of a data structure that only exposes its Sink, and
 * hides the remainder of its operations.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Add elements to a queue.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Sink<E>
{
    /**
     * Inserts the specified element into this queue, if possible. When using queues that may impose insertion
     * restrictions (for example capacity bounds), method <tt>offer</tt> is generally preferable to method
     * {@link java.util.Collection#add}, which can fail to insert an element only by throwing an exception.
     *
     * @param  o T The element to insert.
     *
     * @return <tt>true</tt> if it was possible to add the element to this queue, else <tt>false</tt>.
     */
    boolean offer(E o);
}
