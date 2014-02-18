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
package com.thesett.common.util.queues;

import java.util.Collection;
import java.util.LinkedList;

import com.thesett.common.error.NotImplementedException;
import com.thesett.common.util.Queue;
import com.thesett.common.util.Sizeable;
import com.thesett.common.util.SizeableQueue;
import com.thesett.common.util.SizeableReQueue;
import com.thesett.common.util.concurrent.BlockingQueue;
import com.thesett.common.util.concurrent.SizeableBlockingQueue;

/**
 * Queues is a utility class for providing specialized queue implementations on top of underlying queue types. It is
 * similar in its conception to the java.util.Collections class, that provides collection specializations.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Make queues sizable.
 * <tr><td>Make queues atomically counted.
 * <tr><td>Make queues synchronized.
 * <tr><td>Make queues transactional.
 * <tr><td>Provide fake transactional queues for transactional code running in a non-transactonal context.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Queues
{
    /**
     * Wraps a java.util.concurrent.BlockingQueue as a {@link com.thesett.common.util.concurrent.BlockingQueue}
     * implementation.
     *
     * @param  queue The blocking queue to wrap.
     * @param  <E>   The type of queue element.
     *
     * @return The queue as a {@link com.thesett.common.util.concurrent.BlockingQueue} implementation.
     */
    public static <E> BlockingQueue<E> getBlockingQueue(java.util.concurrent.BlockingQueue<E> queue)
    {
        return new WrapperQueue<E>(queue, new LinkedList<E>(), false, false, false);
    }

    /**
     * Wraps a java.util.concurrent.BlockingQueue as a {@link com.thesett.common.util.concurrent.BlockingQueue}
     * implementation.
     *
     * @param  queue The blocking queue to wrap.
     * @param  <E>   The type of queue element.
     *
     * @return The queue as a {@link com.thesett.common.util.concurrent.BlockingQueue} implementation.
     */
    public static <E> SizeableBlockingQueue<E> getSizeableBlockingQueue(java.util.concurrent.BlockingQueue<E> queue)
    {
        return new WrapperQueue<E>(queue, new LinkedList<E>(), false, false, false);
    }

    /**
     * Wraps a queue with a sizeable implementation, that keeps track of the size of the queue, provided that the
     * elements being inserted into the queue are {@link com.thesett.common.util.Sizeable}. As elements are
     * added/removed on a queue, the queues size is incremented/decrements accordingly.
     *
     * @param  queue The queue to make sizeable.
     * @param  <E>   The type of queue element.
     *
     * @return A sizeable queue from an ordinary queue.
     */
    public static <E extends Sizeable> SizeableQueue<E> getSizeableQueue(java.util.Queue<E> queue)
    {
        return new WrapperQueue<E>(queue, new LinkedList<E>(), false, true, false);
    }

    /**
     * Wraps a queue with a queue that counts the number of elements in it atomically, as elements are inserted/removed
     * on the queue. This is usefull for non-locking queue implementations, that can only estimate their size, at the
     * momemnt the 'size' operation is called, because many threads may be in the process of altering the queue at once.
     *
     * @param  queue The queue to provide atomic counting of.
     * @param  <E>   The type of queue element.
     *
     * @return An atomicically counted queue.
     */
    public static <E> Queue<E> getAtomicCountedQueue(java.util.Queue<E> queue)
    {
        return new WrapperQueue<E>(queue, new LinkedList<E>(), false, false, true);
    }

    /**
     * Wraps queue as a sizeable, atomically counted queue, as per the {@link #getSizeableQueue} and
     * {@link #getAtomicCountedQueue} methods.
     *
     * @param  queue The queue to provide atomic counting and sizing of.
     * @param  <E>   The type of queue element.
     *
     * @return An atomicically counted, sizeable queue.
     */
    public static <E extends Sizeable> SizeableQueue<E> getAtomicCountedSizeableQueue(java.util.Queue<E> queue)
    {
        return new WrapperQueue<E>(queue, new LinkedList<E>(), false, true, true);
    }

    /**
     * Turns non-synched queue into a synched queue for concurrent use. The resulting queue uses blocking
     * synchronization on its methods to ensure that only one thread at a time can access the queue. The resulting queue
     * may not provide the best performance, especially on multi-processor machines. A non-locking implementation may be
     * consideredd instead.
     *
     * @param  queue The queue to synchronize for concurrent use.
     * @param  <E>   The type of queue element.
     *
     * @return A synchronized queue.
     */
    public static <E> Queue<E> synchronizeQueue(java.util.Queue<E> queue)
    {
        throw new NotImplementedException();
    }

    /**
     * Provides a transactional queue, that delays all queue manipulation operations until the transaction is committed,
     * or erases them if it is rolled back.
     *
     * @param  queue The queue to make transactional.
     * @param  <E>   The type of queue element.
     *
     * @return A transactional queue.
     */
    public static <E> Queue<E> getTransactionalQueue(java.util.Queue<E> queue)
    {
        return new WrapperQueue<E>(queue, new LinkedList<E>(), true, false, false);
    }

    /**
     * Provides a transactional requeue, that delays all queue manipulation operations until the transaction is
     * committed, or erases them if it is rolled back. As this is a requeue, the requeue buffer may be examined directly
     * and the queue fully supports browsing with iterators.
     *
     * @param  queue   The queue to use as the underlying queue implementation for the requeue.
     * @param  requeue The collection to use for the requeue buffer.
     * @param  <E>     The type of queue element.
     *
     * @return A transactional requeue.
     */
    public static <E> WrapperQueue<E> getTransactionalReQueue(java.util.Queue<E> queue, Collection<E> requeue)
    {
        return new WrapperQueue<E>(queue, requeue, true, false, false);
    }

    /**
     * Wraps queue as a sizeable, transactional requeue, as per the {@link #getSizeableQueue} and
     * {@link #getTransactionalReQueue} methods.
     *
     * @param  queue   The queue to provide sizing and transactional operations on.
     * @param  requeue The requeue buffer implementation to use.
     * @param  <E>     The type of queue element.
     *
     * @return An atomicically counted, sizeable queue.
     */
    public static <E extends Sizeable> SizeableReQueue<E> getSizeableTransactionalReQueue(java.util.Queue<E> queue,
        Collection<E> requeue)
    {
        return new WrapperQueue<E>(queue, requeue, true, true, false);
    }

    /**
     * Wraps queue as a sizeable, atomically counted queue, transactional requeue, as per the {@link #getSizeableQueue},
     * {@link #getAtomicCountedQueue}, and {@link #getTransactionalReQueue} methods.
     *
     * @param  queue   The queue to provide atomic counting, sizing and transactional operations on.
     * @param  requeue The requeue buffer implementation to use.
     * @param  <E>     The type of queue element.
     *
     * @return An atomicically counted, sizeable queue.
     */
    public static <E extends Sizeable> SizeableReQueue<E> getAtomicCountedSizeableTransactionalReQueue(
        java.util.Queue<E> queue, Collection<E> requeue)
    {
        return new WrapperQueue<E>(queue, requeue, true, true, true);
    }
}
