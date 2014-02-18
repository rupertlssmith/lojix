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
package com.thesett.common.util.concurrent;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.thesett.common.util.Source;

/**
 * A BlockingSource is the point at which a consumer may take items from a queue, optionally waiting until some items
 * are available to take. This interface is compatable with java.util.concurrent.BlockingQueue, in that any
 * BlockingQueue implementation can also implement this interface with no further work. BlockingSource has been created
 * as a seperate interface, to inroduce the possibility of a data structure that only exposes its BlockingSource, and
 * hides the remainder of its operations.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Take elements from a queue waiting up till a time limit for space.
 * <tr><td>Take elements from a queue waiting until there are some to take.
 * <tr><td>Take all elements immediately available from a queue.
 * <tr><td>Take all elements up to a maximum that are immediately available from a queue.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface BlockingSource<E> extends Source<E>
{
    /**
     * Retrieves and removes the head of this queue, waiting if necessary up to the specified wait time if no elements
     * are present on this queue.
     *
     * @param  timeout The maximum time to wait for space.
     * @param  unit    The unit of time used in the timeout.
     *
     * @return The head of this queue, or <tt>null</tt> if the specified waiting time elapses before an element is
     *         present.
     *
     * @throws InterruptedException If the blocking thread is interrupted whilst waiting for an element.
     */
    E poll(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Retrieves and removes the head of this queue, waiting if necessary for elements to become available.
     *
     * @return The head of this queue.
     *
     * @throws InterruptedException If the blocking thread is interrupted whilst waiting for an element.
     */
    E take() throws InterruptedException;

    /**
     * Removes all available elements from this queue and adds them into the given collection. This operation may be
     * more efficient than repeatedly polling this queue. A failure encountered while attempting to add elements to
     * collection c may result in elements being in neither, either or both collections when the associated exception is
     * thrown.
     *
     * <p/>The behavior of this operation is undefined if the specified collection is modified while the operation is in
     * progress.
     *
     * @param  c The collection to drain to.
     *
     * @return The number of elements transferred.
     */
    int drainTo(Collection<? super E> c);

    /**
     * Removes at most the specified number of elements from this queue and adds them into the given collection. This
     * operation may be more efficient than repeatedly polling this queue. A failure encountered while attempting to add
     * elements to collection c may result in elements being in neither, either or both collections when the associated
     * exception is thrown.
     *
     * <p/>The behavior of this operation is undefined if the specified collection is modified while the operation is in
     * progress.
     *
     * @param  c           The collection to drain to.
     * @param  maxElements The maximum number of elements to transfer.
     *
     * @return The number of elements transferred.
     */
    int drainTo(Collection<? super E> c, int maxElements);
}
