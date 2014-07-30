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
 * A ReQueue is a {@link Source} that allows elements to be tentatively dequeued from it, with the possibility that the
 * dequeue operation may be cancelled, and the dequeued element replaced onto the queue. It is a fault tolerant
 * extensions of the dequeue concept, whereby the consumer of elements from a dequeue, does so as a two-step operation;
 * one operation to read the element, and a second to confirm that the read was succesfull and that the element may now
 * be deleted from the queue.
 *
 * <p/>A Source is a data structure that allows elements to be removed one at a time. It does not expose its internal
 * implementation, but its implementation may impose a certain ordering on the elements that it contains. For example,
 * FIFO and LIFO buffers can be seen as sources (insertion order based ordering), as can a heap (element value based
 * ordering, or priority ordering).
 *
 * <p/>As a Source hides its internal representation, it is not possible in general, to say how re-queued elements
 * should be ordered wrt elements still buffered in it, except to say that it would often be helpfull if the ordering
 * imposed by the underlying queue could be maintained in the event of cancelled dequeues. A ReQueue provides an
 * approach to this problem by only dequeueing each element once from the underlying queue implementation and providing
 * a collection at the dequeue end upon which elements from cancelled dequeues are placed. Source operations on a
 * ReQueue will first take elements from this collection if any are available, before dequeueing from the underlying
 * queue. If the collection is a FIFO buffer, then the ordering is maintained.
 *
 * <p/>The advantages of this arrangement are that different orderings can be plugged into a ReQueue by choosing
 * different underlying queue implementations, without having to expose what those implementations are. Orderings are
 * applied only once, on demand, as elements are pulled through the dequeue operation. The re-queue collection type is
 * fully exposed, so that specific properties of the re-queue implementation can be relied upon. For example, by
 * exposing it as an indexable array or list. It replaces the 'peek' operation with a more powerfull iterator, because
 * iterating over a ReQueue will pull* elements through the dequeue, applying the queue ordering, but not remove them
 * from the queue altogether. 'Peek' can therefore look below just the head element of the queue, without exposing the
 * queues implementation.
 *
 * <p/>Tentative dequeueing is made available through three operations, 'acquire', 'accept' and 'release'. There are two
 * different versions of this set of operations, one for transactions where the owner of acquired items defaults to the
 * current transactional session, and one where an explicit owner can be specified. Acquiring and accepting an element
 * under the same owner dequeues it. Acquiring and releasing an element pulls it through the dequeue, but makes it
 * available again on the requeue. Acquire/accept and acquire/release pairings must be carried out under the same
 * ownership or else the operation will fail.
 *
 * <p/>It is possible to acquire elements under 'null' ownership, either by explicitly specifying it, or by using the
 * transaction methods without a current transactional context. Elements acquired under 'null' ownership may be accepted
 * or released by anyone.
 *
 * <p/>Note that 'acquire' and 'release' are not a transactional operations, in the sense that if their owner is a
 * transaction and it is rolled back, the effect of the operation will not be undone. The queue element will still be
 * acquired or unacquired. The 'accept' operation is transactional, in that a transactional implementation would undo
 * the effect of this operation
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Tentatively dequeue elements under an objects ownership.
 * <tr><td>Requeue elements, checking validity of ownership.
 * <tr><td>Fully dequeue elements, checking validitiy of ownership.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Because some operations are transactional and some are not, I think the methods that do not take an owner an
 *         automatically assign the owner as the curent transaction are confusing. Ownership is a concept that can span
 *         multiple transactions, so it does not seem sensible to use the transactional context of a thread itself as an
 *         owner. Methods deprecated pending removal.
 */
public interface ReQueue<E> extends Source<E>
{
    /**
     * Polls for next available unacquired element, and once one is found accepts it for the specified owner. Depending
     * on the underlying queue implementation, this method may block until an item becomes available. If the queue is
     * empty, this method may return <tt>null</tt>
     *
     * @param  owner The object to assign acquisition ownership of the item to. May be 'null' if it is un-owned.
     *
     * @return <tt>null</tt> if the queue is empty, or an acquired element.
     */
    public E pollAccept(Object owner);

    /**
     * Polls for next available unacquired element, and once one is found acquires it for the specified owner. Depending
     * on the underlying queue implementation, this method may block until an item becomes available. If the queue is
     * empty, this method may return <tt>null</tt>
     *
     * @param  owner The object to assign acquisition ownership of the item to. May be 'null' if it is un-owned.
     *
     * @return <tt>null</tt> if the queue is empty, or an acquired element.
     */
    public E pollAcquire(Object owner);

    /**
     * Acquires the specified element under the requested ownership, providing that it is not already owned.
     *
     * @param  owner The object to assign acquisition ownership of the item to. May be 'null' if it is un-owned.
     * @param  o     The element to acquire.
     *
     * @return <tt>true</tt> if the element could be acquired, <tt>false</tt> if it could not be.
     */
    public boolean acquire(Object owner, Object o);

    /**
     * Puts a previously acquired element back onto the requeue. The owner of the element must have previously acquired
     * it. Ownership is checked by using
     *
     * <pre>Object.equals()</pre>
     *
     * between the potential owners. The object to release does not have to be fully specified, so long as the element
     * matches the specified object 'o' by hash code and equality, that is,
     *
     * <pre>element.equals(o)</pre>
     *
     * returns true, it will be released.
     *
     * @param owner The owner of the element to release.
     * @param o     The object to release.
     */
    public void release(Object owner, Object o);

    /**
     * Accepts responsibility for an element that has been previously acquired. The owner of the element must have
     * previously acquired it. Ownership is checked by using
     *
     * <pre>Object.equals()</pre>
     *
     * between the potential owners. The object to accept does not have to be fully specified, so long as the element
     * matches the specified object 'o' by hash code and equality, that is,
     *
     * <pre>element.equals(o)</pre>
     *
     * returns true, it will be accepted. Once an element is accepted, it is completely removed from the requeue.
     *
     * @param owner The owner of the element to accept.
     * @param o     The object to accept.
     */
    public void accept(Object owner, Object o);
}
