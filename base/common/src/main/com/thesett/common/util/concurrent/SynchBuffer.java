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

import java.util.Queue;

import com.thesett.common.error.NotImplementedException;

/**
 * SynchBuffer completes the {@link BatchSynchQueueBase} abstract class by providing an implementation of the underlying
 * queue as an array. This uses FIFO ordering for the queue but restricts the maximum size of the queue to a fixed
 * amount. It also has the advantage that, as the buffer does not grow and shrink dynamically, memory for the buffer is
 * allocated up front and does not create garbage during the operation of the queue.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide array based FIFO queue to create a batch synched queue around.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Write an array based buffer implementation that implements Queue.
 */
public class SynchBuffer<E> extends BatchSynchQueueBase<E>
{
    /**
     * Returns an empty queue, implemented as an array.
     *
     * @param  <T> The type of the queue to create.
     *
     * @return An empty queue, implemented as an array.
     */
    protected <T> Queue<T> createQueue()
    {
        throw new NotImplementedException();
    }
}
