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
package com.thesett.common.util.concurrent;

import java.util.concurrent.TimeUnit;

import com.thesett.common.util.Sink;

/**
 * A BlockingSink is the point at which a producer may add items into a queue, optionally waiting until space is
 * available. This interface is compatable with java.util.concurrent.BlockingQueue, in that any BlockingQueue
 * implementation can also implement this interface with no further work. BlockingSink has been created as a seperate
 * interface, to inroduce the possibility of a data structure that only exposes its BlockingSink, and hides the
 * remainder of its operations.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Add elements to a queue waiting up till a time limit for space.
 * <tr><td>Add elements to a queue waiting until space is available.
 * <tr><td>Query the immediate space available in a queue.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface BlockingSink<E> extends Sink<E>, Capacity
{
    /**
     * Inserts the specified element into this queue, if possible. When using queues that may impose insertion
     * restrictions (for example capacity bounds), method offer is generally preferable to method Collection.add(E),
     * which can fail to insert an element only by throwing an exception.
     *
     * @param  o       The element to add.
     * @param  timeout The maximum time to wait for space.
     * @param  unit    The unit of time used in the timeout.
     *
     * @return <tt>true</tt> if the element was accepted, <tt>false</tt> if it could not be accepted for any reason
     *         other than this operation being interrupted.
     *
     * @throws InterruptedException If the blocking thread is interrupted whilst waiting for space.
     */
    boolean offer(E o, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Adds the specified element to this queue, waiting if necessary for space to become available.
     *
     * @param  o The element to add.
     *
     * @throws InterruptedException If the blocking thread is interrupted whilst waiting for space.
     */
    void put(E o) throws InterruptedException;
}
