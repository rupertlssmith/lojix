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
package com.thesett.common.util.queues;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.thesett.common.error.NotImplementedException;
import com.thesett.common.tx.GlobalWriteLockWithWriteBehindTxMethod;
import com.thesett.common.tx.Transactional;
import com.thesett.common.tx.TxManager;
import com.thesett.common.tx.TxMethod;
import com.thesett.common.tx.TxOperation;
import com.thesett.common.util.Sizeable;
import com.thesett.common.util.SizeableReQueue;
import com.thesett.common.util.concurrent.Signalable;
import com.thesett.common.util.concurrent.SizeableBlockingQueue;

/**
 * WrapperQueue is a wrapper for queue implementations that is capable of making queues synchronized, transactional,
 * sizeable or atomically counted, or any combination of the above.
 *
 * <p/>Transactional wrapped queues have the transaction isolation level 'ReadUncommitted'. Enqueue operations do not
 * become visible to dequeueing transactions until they are committed which might suggest that the level should be
 * 'ReadCommitted'. However, dequeues are write operations, in the sense that they alter the queue, although they do
 * read data from it too. Multiple transactions that are simultaneously dequeueing from the same queue will 'see' the
 * effect of each others dequeues immediately, before they are committed. One way to prevent this would be enforce
 * serializability by only allowing one consumer transaction to be active on the queue at any one time, with resultant
 * performance penalties.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Make queues sizable.
 * <tr><td>Make queues atomically counted.
 * <tr><td>Make queues transactional.
 * <tr><td>Provide fake transactional queues for transactional code running in a non-transactonal context.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   It may be a bad idea to use queues that can reject offers, such as bounded buffers. Linked lists will be
 *         fine. Ideally don't want transactions to fail to commit or rollback if at all possible. Need a way to query
 *         the queue in advance to see if all of a sequence of offers will be accepted, or else there is the risk of
 *         only partly committing a transaction. The correct failure free behaviour for a commit to a bounded queue,
 *         that does not have sufficient space on it to accept the commit, should be to block until enough space becomes
 *         free.
 * @todo   Transactional efficiency. Note that dequeue is a tx write operation, as it alters the queue. Acquire can be
 *         thought of as purely a read operation, and it simply read-locks an element (although it sets a flag so that
 *         the read makes the record invisibile to other normal consumers). Now only accept is a write operation and is
 *         carried out on an already invisible element. This should be very good news for transactional parallelism, as
 *         commiting the tx should be a case of upgrading the read locks that only that tx holds to write locks. There
 *         may be consumers on the queue that can see these invisible elements, e.g., queue monitors and these may stall
 *         tx completion. One possibility is to write queue browsers, or monitors, in such a way that they can be
 *         notified of the intention to upgrade a read-lock, and are required to release their read-locks in the event.
 */
public class WrapperQueue<E> implements SizeableReQueue<E>, SizeableBlockingQueue<E>
{
    /**
     * AcquireState is used to signal the acquired state of elements in the queue, when using a requeue. Elements may be
     * tentatively acquired by at most one 'owner' prior to being removed or placed back into the queue. Acquire/Release
     * operations are not transactional, but when polling in a transactional context it is helpfull to be able to
     * distinguish between elements that a transaction has acquired, and elements that the transaction has already
     * consumed. Upon rollback, acquisition is not undone, but the consumed elements from the rolled back transaction,
     * should be presented for consumption again. The 'PendingAccept' state allows this to be distinguished from the
     * 'Acquired' state.
     */
    private enum AcquireState
    {
        /** Indicates that an element is not acquired. */
        Free,

        /** Indicates that an element has been acquired by an 'owner'. */
        Acquired,

        /** Indicates that an element has been acquired by an 'owner' and has a pending transaction accept operation. */
        Accepted
    }

    /** Flag to indicate that this wrapper should provide transactional access. */
    private boolean transactional;

    /** Flag to indicate that this wrapper should monitor the queues size. */
    private boolean sizeable;

    /** Flag to indicate that this wrapper should atomically count the number of elements in this queue. */
    private boolean atomicallyCounted;

    /** Holds the underlying queue that is wrapped. */
    private java.util.Queue<E> queue;

    /** Flag that indicates that the underlying queue implements java.util.concurrent.BlockingQueue. */
    private boolean isBlockingQueue = false;

    /** Holds the queue of requeued items, in the event of roll-backs. */
    private Collection<RequeueElementWrapper<E>> requeue;

    /** Holds a mapping from elements to the requeue wrapped versions. */
    private Map<E, RequeueElementWrapper<E>> requeuedElementMap = new HashMap<E, RequeueElementWrapper<E>>();

    /** Used to keep track of the size of the queue. */
    private AtomicLong dataSize = new AtomicLong(0L);

    /** Used to keep track of the count of elements in the queue. */
    private AtomicInteger count = new AtomicInteger(0);

    /** Holds the transactional method implementation strategy for transactional queues. */
    private TxMethod txMethod;

    /** Holds the signalable resource to notify when the size of the queue changes. */
    private Signalable signalable;

    /** Holds the high water size threshold to wake up one waiting thread on. */
    private long highWaterSizeThreshold;

    /** Holds the low water size threshold to wake up one waiting thread on. */
    private long lowWaterSizeThreshold;

    /**
     * Creates a wrapped queue that provides the requested extended behaviour.
     *
     * @param queue             The queue to wrap.
     * @param requeue           The requeue buffer to use.
     * @param transactional     <tt>true</tt> to make the queue transactional.
     * @param sizeable          <tt>true</tt> to monitor the queue data size.
     * @param atomicallyCounted <tt>true</tt> to make the queue atomically counted.
     */
    public WrapperQueue(java.util.Queue<E> queue, Collection requeue, boolean transactional, boolean sizeable,
        boolean atomicallyCounted)
    {
        this.queue = queue;
        this.transactional = transactional;
        this.sizeable = sizeable;
        this.atomicallyCounted = atomicallyCounted;

        this.requeue = requeue;

        // Set up the transactional method depending on whether on not transactions are to be used.
        txMethod = new GlobalWriteLockWithWriteBehindTxMethod();

        if (transactional)
        {
            txMethod.setIsolationLevel(Transactional.IsolationLevel.ReadUncommitted);
        }
        else
        {
            txMethod.setIsolationLevel(Transactional.IsolationLevel.None);
        }

        // Set the blocking queue flag if the queue is a blocking one.
        isBlockingQueue = (queue instanceof java.util.concurrent.BlockingQueue);
    }

    /** {@inheritDoc} */
    public long sizeof()
    {
        return dataSize.get();
    }

    /** {@inheritDoc} */
    public boolean offer(E o)
    {
        if (transactional)
        {
            // Delegate the offer operation to the wrapped queue.
            txMethod.requestWriteOperation(new EnqueueRecord(o));

            // return success;
            return true;
        }
        else
        {
            boolean success = queue.offer(o);

            // Update the queue size if the offer was succesfull.
            if (success)
            {
                incrementSizeAndCount(o);
            }

            return success;
        }
    }

    /** {@inheritDoc} */
    public E poll()
    {
        Object owner;

        if (transactional)
        {
            owner = TxManager.getCurrentSession();
        }
        else
        {
            owner = null;
        }

        // Attempt to acquire an available element from the queue for the current transaction.
        return pollAccept(owner);
    }

    /** {@inheritDoc} */
    public E remove()
    {
        E result = poll();

        if (result == null)
        {
            throw new NoSuchElementException("The queue is empty.");
        }

        return result;
    }

    /** {@inheritDoc} */
    public E peek()
    {
        return queue.peek();
    }

    /** {@inheritDoc} */
    public E element()
    {
        return queue.element();
    }

    /** {@inheritDoc} */
    public int size()
    {
        // Use the atomic count for atomic counted queues.
        if (atomicallyCounted)
        {
            return count.get();
        }

        // Delegate to the wrapped queue for non-atomic counted queues.
        else
        {
            return queue.size();
        }
    }

    /** {@inheritDoc} */
    public boolean isEmpty()
    {
        return queue.isEmpty();
    }

    /** {@inheritDoc} */
    public boolean contains(Object o)
    {
        return queue.contains(o);
    }

    /** {@inheritDoc} */
    public Iterator<E> iterator()
    {
        // Iterate over the requeue till exhausted. Then start pulling stuff off the queue and putting it onto the
        // requeue, until exhausted.
        return new Iterator<E>()
            {
                Iterator<RequeueElementWrapper<E>> requeueIterator = requeue.iterator();
                boolean usingRequeue = true;

                public boolean hasNext()
                {
                    boolean result = false;

                    if (usingRequeue)
                    {
                        result = requeueIterator.hasNext();

                        if (!result)
                        {
                            usingRequeue = false;
                        }
                    }

                    if (!usingRequeue)
                    {
                        result = !queue.isEmpty();
                    }

                    return result;
                }

                public E next()
                {
                    E result;

                    if (usingRequeue)
                    {
                        result = requeueIterator.next().element;
                    }
                    else
                    {
                        result = queue.poll();
                        requeue(result);
                    }

                    return result;
                }

                public void remove()
                {
                    throw new NotImplementedException();
                }
            };
    }

    /** {@inheritDoc} */
    public Object[] toArray()
    {
        return queue.toArray();
    }

    /** {@inheritDoc} */
    public <T> T[] toArray(T[] a)
    {
        return queue.toArray(a);
    }

    /** {@inheritDoc} */
    public boolean add(E o)
    {
        return queue.add(o);
    }

    /** {@inheritDoc} */
    public boolean remove(Object o)
    {
        return queue.remove(o);
    }

    /** {@inheritDoc} */
    public boolean containsAll(Collection<?> c)
    {
        return queue.containsAll(c);
    }

    /** {@inheritDoc} */
    public boolean addAll(Collection<? extends E> c)
    {
        return queue.addAll(c);
    }

    /** {@inheritDoc} */
    public boolean removeAll(Collection<?> c)
    {
        return queue.removeAll(c);
    }

    /** {@inheritDoc} */
    public boolean retainAll(Collection<?> c)
    {
        return queue.retainAll(c);
    }

    /** {@inheritDoc} */
    public void clear()
    {
        queue.clear();
    }

    /** {@inheritDoc} */
    public E pollAccept(Object owner)
    {
        if (transactional)
        {
            E element = null;
            RequeueElementWrapper<E> record = null;

            // Find an element on the requeue that is free, or has already been acquired by the owner but not accepted.
            // Mark the element as acquired by the owner as necessary.
            if (!requeue.isEmpty())
            {
                for (RequeueElementWrapper<E> nextRecord : requeue)
                {
                    if (AcquireState.Free.equals(nextRecord.state))
                    {
                        record = nextRecord;
                        record.state = AcquireState.Acquired;
                        record.owner = owner;

                        element = record.element;

                        break;
                    }
                    else if (AcquireState.Acquired.equals(nextRecord.state) && owner.equals(nextRecord.owner))
                    {
                        record = nextRecord;
                        element = record.element;

                        break;
                    }
                }
            }

            // If an element cannot be found on the requeue, poll an element from the main queue, and place it onto the
            // requeue.
            if (record == null)
            {
                element = queue.poll();
            }

            // If no element at all can be found return null.
            if (element == null)
            {
                return element;
            }

            // Check that an element was actually available on the queue before creating a new acquired record for it
            // on the requeue.
            if (record == null)
            {
                record = requeue(element, owner, AcquireState.Acquired);
            }

            // Accept the element and create a transaction operation to remove it upon commit or unnaccept it upon
            // rollback.
            record.state = AcquireState.Accepted;
            txMethod.requestWriteOperation(new AcceptRecord(record));

            return record.element;
        }
        else
        {
            E element;

            // Find an element on the requeue that is free. Remove it and return it.
            if (!requeue.isEmpty())
            {
                for (RequeueElementWrapper<E> nextRecord : requeue)
                {
                    if (AcquireState.Free.equals(nextRecord.state))
                    {
                        requeue.remove(nextRecord);
                        requeuedElementMap.remove(nextRecord.element);

                        return nextRecord.element;
                    }
                }
            }

            // Or poll an element from the main queue and return it.
            element = queue.poll();

            if (element != null)
            {
                decrementSizeAndCount(element);
            }

            return element;
        }
    }

    /** {@inheritDoc} */
    public E pollAcquire(Object owner)
    {
        E element;

        // Find an element on the requeue that is free and return it.
        if (!requeue.isEmpty())
        {
            for (RequeueElementWrapper<E> nextRecord : requeue)
            {
                if (AcquireState.Free.equals(nextRecord.state))
                {
                    nextRecord.state = AcquireState.Acquired;
                    nextRecord.owner = owner;

                    return nextRecord.element;
                }
            }
        }

        // Nothing could be found on the requeue, so attempt to poll an element off the main queue and acquire
        // it on the requeue.
        element = queue.poll();

        if (element != null)
        {
            requeue(element, owner, AcquireState.Acquired);
        }

        return element;
    }

    /** {@inheritDoc} */
    public boolean acquire(Object owner, Object o)
    {
        // Look up the element wrapper record for the element to be accepted.
        RequeueElementWrapper<E> record = requeuedElementMap.get(o);

        // Check if the element is currently free, and acquire it if so.
        if (AcquireState.Free.equals(record.state))
        {
            record.state = AcquireState.Acquired;
            record.owner = owner;

            return true;
        }

        return false;
    }

    /** {@inheritDoc} */
    public void release(Object owner, Object o)
    {
        // Look up the element wrapper record for the element to be released, and release it.
        RequeueElementWrapper<E> record = requeuedElementMap.get(o);

        if (record != null)
        {
            record.state = AcquireState.Free;
            record.owner = null;
        }
    }

    /** {@inheritDoc} */
    public void accept(Object owner, Object o)
    {
        // Look up the element wrapper record for the element to be accepted.
        RequeueElementWrapper<E> record = requeuedElementMap.get(o);

        if (record != null)
        {
            // If running in a transaction, create an accept operation to accept the item only upon commit of the
            // transaction.
            if (transactional)
            {
                record.state = AcquireState.Accepted;
                txMethod.requestWriteOperation(new AcceptRecord(record));
            }
            else
            {
                requeuedElementMap.remove(o);
                requeue.remove(record);
            }
        }
    }

    /**
     * Prints the contents of this queue as a string for debugging purposes.
     *
     * @return The contents of this queue as a string.
     */
    public String toString()
    {
        return "WrapperQueue: [ dataSize = " + dataSize + ", count = " + count + "]";
    }

    /**
     * Equality based on object reference.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt> if the comparator is this.
     */
    public boolean equals(Object o)
    {
        return (this == o);
    }

    /**
     * Default hash code based on object reference.
     *
     * @return The default hash code.
     */
    public int hashCode()
    {
        return super.hashCode();
    }

    /** {@inheritDoc} */
    public E poll(long timeout, TimeUnit unit) throws InterruptedException
    {
        if (!isBlockingQueue)
        {
            throw new UnsupportedOperationException("This operation is only supported on blocking queues.");
        }

        return ((java.util.concurrent.BlockingQueue<E>) queue).poll(timeout, unit);
    }

    /** {@inheritDoc} */
    public E take() throws InterruptedException
    {
        if (!isBlockingQueue)
        {
            throw new UnsupportedOperationException("This operation is only supported on blocking queues.");
        }

        return ((java.util.concurrent.BlockingQueue<E>) queue).take();
    }

    /** {@inheritDoc} */
    public int drainTo(Collection<? super E> c)
    {
        if (!isBlockingQueue)
        {
            throw new UnsupportedOperationException("This operation is only supported on blocking queues.");
        }

        return ((java.util.concurrent.BlockingQueue<E>) queue).drainTo(c);
    }

    /** {@inheritDoc} */
    public int drainTo(Collection<? super E> c, int maxElements)
    {
        if (!isBlockingQueue)
        {
            throw new UnsupportedOperationException("This operation is only supported on blocking queues.");
        }

        return ((java.util.concurrent.BlockingQueue<E>) queue).drainTo(c, maxElements);
    }

    /** {@inheritDoc} */
    public boolean offer(E o, long timeout, TimeUnit unit) throws InterruptedException
    {
        if (!isBlockingQueue)
        {
            throw new UnsupportedOperationException("This operation is only supported on blocking queues.");
        }

        return ((java.util.concurrent.BlockingQueue<E>) queue).offer(o, timeout, unit);
    }

    /** {@inheritDoc} */
    public void put(E o) throws InterruptedException
    {
        if (!isBlockingQueue)
        {
            throw new UnsupportedOperationException("This operation is only supported on blocking queues.");
        }

        ((java.util.concurrent.BlockingQueue<E>) queue).put(o);
    }

    /** {@inheritDoc} */
    public int remainingCapacity()
    {
        if (!isBlockingQueue)
        {
            throw new UnsupportedOperationException("This operation is only supported on blocking queues.");
        }

        return ((java.util.concurrent.BlockingQueue<E>) queue).remainingCapacity();
    }

    /** {@inheritDoc} */
    public void setSignalableResource(Signalable signalable)
    {
        this.signalable = signalable;
    }

    /** {@inheritDoc} */
    public void setHighWaterThreshold(long size)
    {
        this.highWaterSizeThreshold = size;
    }

    /** {@inheritDoc} */
    public void setLowWaterThreshold(long size)
    {
        this.lowWaterSizeThreshold = size;
    }

    /**
     * Places an element onto the requeue buffer.
     *
     * @param element The element to place onto the requeue buffer.
     */
    private void requeue(E element)
    {
        RequeueElementWrapper<E> record = new RequeueElementWrapper<E>(element);
        requeue.add(record);
        requeuedElementMap.put(element, record);
    }

    /**
     * Places an element onto the requeue buffer, in the acquired state by the specified owner.
     *
     * @param  element  The element to place onto the requeue buffer.
     * @param  owner    The owner of the acquired element.
     * @param  acquired The acquired state to set on the element in the requeue.
     *
     * @return The requeue element wrapper for the requeued element.
     */
    private RequeueElementWrapper<E> requeue(E element, Object owner, AcquireState acquired)
    {
        RequeueElementWrapper<E> record = new RequeueElementWrapper<E>(element);
        record.state = acquired;
        record.owner = owner;
        requeue.add(record);
        requeuedElementMap.put(element, record);

        return record;
    }

    /**
     * Atomically adds to the size and count, if the queue is running in atomic counting mode, or sizeable mode and the
     * element is sizeable.
     *
     * @param record The record to update the size and count for.
     */
    private void incrementSizeAndCount(E record)
    {
        // Update the count for atomically counted queues.
        if (atomicallyCounted)
        {
            count.incrementAndGet();
        }

        // Update the size for sizeable elements and sizeable queues.
        if (sizeable && (record instanceof Sizeable))
        {
            dataSize.addAndGet(((Sizeable) record).sizeof());
        }
        else if (sizeable)
        {
            dataSize.incrementAndGet();
        }
    }

    /**
     * Atomically subtracts from the size and count, if the queue is running in atomic counting mode, or sizeable mode
     * and the element is sizeable.
     *
     * @param record The record to update the size and count for.
     */
    private void decrementSizeAndCount(E record)
    {
        // Update the count for atomically counted queues.
        if (atomicallyCounted)
        {
            count.decrementAndGet();
        }

        // Update the size for sizeable elements and sizeable queues.
        if (sizeable && (record instanceof Sizeable))
        {
            long recordSize = -((Sizeable) record).sizeof();
            long oldSize = dataSize.getAndAdd(recordSize);
            long newSize = oldSize + recordSize;

            signalOnSizeThresholdCrossing(oldSize, newSize);
        }
        else if (sizeable)
        {
            long oldSize = dataSize.getAndDecrement();
            long newSize = oldSize - 1;

            signalOnSizeThresholdCrossing(oldSize, newSize);
        }
    }

    /**
     * Signals the signallable resource if the size crosses a threshold boundary in a downward direction.
     *
     * @param oldSize The old size.
     * @param newSize The new size.
     */
    private void signalOnSizeThresholdCrossing(long oldSize, long newSize)
    {
        if (signalable != null)
        {
            if ((oldSize >= lowWaterSizeThreshold) && (newSize < lowWaterSizeThreshold))
            {
                signalable.signalAll();
            }
            else if ((oldSize >= highWaterSizeThreshold) && (newSize < highWaterSizeThreshold))
            {
                signalable.signal();
            }
        }
    }

    /**
     * Wraps elements in the requeue buffer, to add ownership fields to them.
     */
    private class RequeueElementWrapper<E> implements Comparable
    {
        /** Holds the acquisition state of this element. */
        public AcquireState state = AcquireState.Free;

        /** Holds the unique owner of this element when it has been acquired. */
        public Object owner = null;

        /** Holds the element data. */
        public E element;

        /**
         * Creates a new requeue element.
         *
         * @param element The data element to wrap as a requeue element.
         */
        public RequeueElementWrapper(E element)
        {
            this.element = element;
        }

        /**
         * Compares this requeue element to another. They are equal when their data elements are equal.
         *
         * @param  o The element to compare to.
         *
         * @return <tt>true</tt> if the comparator is a RequeueElementWrapper with data element that is equal to this
         *         ones data element.
         */
        public boolean equals(Object o)
        {
            return (o instanceof RequeueElementWrapper) ? element.equals(((RequeueElementWrapper) o).element)
                                                        : element.equals(o);
        }

        /**
         * Computes a hash code based on the data element to be compatable with the equals method.
         *
         * @return A hash code based on the data element.
         */
        public int hashCode()
        {
            return ((element != null) ? element.hashCode() : 0);
        }

        /**
         * Compares this requeue element to another if the data element implements Comparable.
         *
         * @param  o The element to compare to.
         *
         * @return The result of comparing the data element of the comparator with this ones data element, if the
         *         element implements Comparable. If the element type does not implement Comparable an exception will be
         *         thrown.
         *
         * @throws UnsupportedOperationException If the element type does not implement Comparable.
         */
        public int compareTo(Object o)
        {
            if (element instanceof Comparable)
            {
                Comparable compElement = (Comparable) element;

                return (o instanceof RequeueElementWrapper) ? compElement.compareTo(
                    ((RequeueElementWrapper) o).element) : compElement.compareTo(o);
            }
            else
            {
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * Encapsulates an enqueue as a transactional write-behind operation.
     */
    public class EnqueueRecord extends TxOperation
    {
        /** Holds the record to be added to the queue. */
        E record;

        /**
         * Creates an enqueue operation that is delayed until the transaction is committed.
         *
         * @param record The record to add to the queue.
         */
        public EnqueueRecord(E record)
        {
            this.record = record;
        }

        /**
         * {@inheritDoc}
         *
         * <p/>This operation must not fail (other than critical error conditions like out of memory). If the underlying
         * queue is bounded, or cannot accept the enqueue for any reason this method will raise an
         * IllegalStateException. Use bounded wrapper queues for blocking commits when there is insufficient space on
         * the queue.
         */
        public void execute()
        {
            boolean success = queue.offer(record);

            // Update the queue size of the offer was succesfull.
            if (success)
            {
                incrementSizeAndCount(record);
            }

            // Raise an illegal state exception if the offer was refused.
            else
            {
                throw new IllegalStateException(
                    "Wrapped queues must accept all 'enqueues' when running transactionally.");
            }
        }
    }

    /**
     * Encapsulates an accept as a transactional write-behind operation.
     */
    public class AcceptRecord extends TxOperation
    {
        /** Holds the record to be removed from the queue. */
        RequeueElementWrapper<E> record;

        /**
         * Creates an accept operation that is delayed until the transaction is committed.
         *
         * @param record The record to accept from the queue.
         */
        public AcceptRecord(RequeueElementWrapper<E> record)
        {
            this.record = record;
        }

        /**
         * {@inheritDoc}
         *
         * <p/>There is nothing to do at commit time for a dequeue, other than to update the queues size, as the data
         * item will already have been taken from the queue, and placed in this operation. This operation can safely be
         * deleted after the commit.
         */
        public void execute()
        {
            // Update the size and count on commit only.
            decrementSizeAndCount(record.element);

            requeuedElementMap.remove(record.element);
            requeue.remove(record);
        }

        /** {@inheritDoc} */
        public boolean cancel(boolean mayInterruptIfRunning)
        {
            //release(TxManager.getTxIdFromThread(), record);
            record.state = AcquireState.Acquired;

            return true;
        }
    }
}
