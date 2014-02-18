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

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import com.thesett.common.error.NotImplementedException;
import com.thesett.common.util.Function;

/**
 * A LockFreeNQueue is a priority queue based on n linked lists joined together end-to-end that supports lock free
 * concurrent access. The number of lists, n, must be an integer, usually of a fairly restricted range, where it is
 * expected that there will be many items placed into the queue for each value of n. The value of n should be derived as
 * a function of the elements that are held in the queue. For example, if each of the elements being placed in the queue
 * has an integer priority value that determines how near to the front of the queue it should be placed then this is
 * ideal to use as the value of n.
 *
 * <p/>Unlike the java.util.PriorityQueue implementation the iterator over this type of queue <u>is guaranteed to
 * iterate over its elements in the priority ordering</u>, or in a LIFO ordering where elements have the same priority.
 * It is able to take advantage of the restricted discrete priority range to provide an efficiency boost too; accessing,
 * adding and removing elements from this priority queue type takes constant time, as opposed to log time for heap based
 * queues.
 *
 * <p/>A heap based priority queue loses its efficiency when too many values with the same key are inserted into it
 * because pointless work is done forming identical keys into a heap structure and maintaining that structure. This data
 * structure cannot support arbitrary keys; a mapping onto the integers must be used. In some cases, enumerating
 * arbitrary priority levels will be an acceptable solution. These are some points to consider when choosing between
 * this implementation and a heap based priority queue.
 *
 * <p/>There is a degree of similarity between this implementation and a skip list. Effectively each list has a fast
 * lane node at its head that is used to quickly skip down to that priority level.
 *
 * <p/>This implementation employs an efficient "wait-free" algorithm based on one described in "Simple, Fast, and
 * Practical Non-Blocking and Blocking Concurrent Queue Algorithms" by Maged M. Michael and Michael L. Scott. The
 * fundamental idea of this algorithm is to use atomic references to the head and tail of the list and to repeatedly
 * attempt to update the head or tail position in conflict with other threads that may be attempting to do the same
 * thing. Threads actively loop while doing this and under low contention they will manage to make their updates with
 * little looping. Under high contention this type of algorithm will become inefficient when compared with a locking and
 * waiting type algorithm because threads will do too much active waiting on the head and tail references.
 *
 * <p/>This queue works with lowest and highest priority levels and maintains linked lists for these and all levels in
 * between. When an element is inserted into the queue that lies outside of this range its priority is rounded up or
 * down to the bottom or top of this range as appropriate. The alternative would be to raise an exception, but as this
 * does not seem very usefull, the failure free approach is used.
 *
 * <p/>The remove method will set the data item on its node to null if it finds a matching data item to remove. It will
 * leave this null node in the queue, rather than trying to clean it up straight away.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Implement a priority queue with constant time, concurrent access and priority/FIFO ordererd iterators.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Linked lists can support hand-over-hand, fine grained locking. Can this be usefull for algorithms that want
 *         to traverse the queue and lock elements whilst operating on them? More appropriate for a locking type
 *         implementation I think. For example, could have lockingIterator method that returns an iterator that locks as
 *         it goes, guaranteeing that the element returned by next is locked until next is called again. Would need some
 *         way to signal that the last element should be released too, perhaps if hasNext returns false it will also
 *         release the last element.
 * @todo   Where contention is too high locking begins to become more efficient. A seperate locking implementation needs
 *         to be written. If possible factor shared code into a common root for the two implementations.
 * @todo   Add some active loop counters to make it possible to monitor how much looping is done. These stats could be
 *         usefull when profiling an app to make the choice between locking and non-locking queues.
 * @todo   Consider how a low-garbage generating version could be written. This would call new to create nodes when the
 *         list grows beyond its previous max size, but when they are removed it keeps the references to the nodes so
 *         that they can be re-used. Effectively a node pool. This may not be worth the hassle.
 * @todo   The poll method always has to loop down a series of marker nodes on every iteration when there are empty
 *         lists (and marker nodes) at the start of the queue. Consider how to remove these empty marker nodes, perhaps
 *         maintaining an atomic reference to the first list with data in it, so that poll can always jump straight
 *         there. The work of removing unused marker nodes could be done by the poll method.
 * @todo   Write a skip list implementation for ordering of unbounded keys. It may also be possible to optimize a skip
 *         list implementation to work efficiently where there are many keys of the same value by automatically having
 *         fast lane nodes at the head of each key value. That idea is actually very similar to this implemention. A
 *         skip list implementation would have fast insert and remove operations, log time to find a specific key, and
 *         be able to support fully ordered iteration. A skip list would also make an excellent key ordering backing
 *         list for a LinkedHashMap implementation, providing better random access performance than a LinkedHashMap but
 *         without compromising on random element removal times like HashArray does.
 * @todo   Write an EscalatorQueue implementation. This is the same as this but has an extra 'head' queue at the highest
 *         priority position, before the first marker node. Upon an escalation condition the first marker node is
 *         removed, adding the top priority queue onto the tail of the head queue. This maker node is then recycled to
 *         form a new lowest priority queue at the end and a cyclic offset is incremented to bump all the queue
 *         priorities up by one. The escalation condition can be time or quantity based. This has the effect of making
 *         this data structure ordered by a blend of LIFO and priority orderings. Pure LIFO is usefull because low
 *         priority elements will not be starved at the back of the queue. Pure priority is usefull because high priorty
 *         elements always take precendence. Escalator queue provides a degree of prioirity ordering without starvation.
 *         Also, little work is done bumping the positions up by one because it is simply a question of moving a marker
 *         node.
 */
public class LockFreeNQueue<E> extends AbstractQueue<E>
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(LockFreeNQueue.class.getName()); */

    /** Holds references to the head and tail marker elements of the lists for each priority level. */
    private Marker<E>[] markers;

    /** Holds the priority mapping function to extract priority values from data elements. */
    Function<E, Integer> p;

    /** The lowest priority value this queue accepts. */
    private int lowest;

    /** The highest priority value this queue accepts. */
    private int highest;

    /** The number of linked lists maintained by this nqueue. */
    private int n;

    /** Holds the count of the number of elements in the queue. */
    private AtomicInteger count = new AtomicInteger();

    /**
     * Creates a lock free n queue with n priority levels (from 0 to n - 1).
     *
     * @param lowest   The lowest priority level accepted by this queue.
     * @param highest  The highest priority level accepted by this queue.
     * @param priority A priority function mapping data elements into the priority range of the queue.
     */
    public LockFreeNQueue(int lowest, int highest, Function<E, Integer> priority)
    {
        /*log.fine("public LockFreeNQueue(int lowest, int highest, Function<E, Integer> priority): called");*/

        // Ensure that lowest is below highest.
        if (highest < lowest)
        {
            throw new IllegalArgumentException("The highest argument cannot be smaller than the lowest one.");
        }

        p = priority;
        this.lowest = lowest;
        this.highest = highest;
        n = highest - lowest + 1;

        /*log.fine("lowest = " + lowest);*/
        /*log.fine("highest = " + highest);*/
        /*log.fine("n = " + n);*/

        // Create and chain together the marker nodes, that mark the head and tail of each of the n lists. There will
        // be n + 1 marker nodes because the data sequences sit between them with a single marker node between each
        // data sequence and additional one at the head and tail of the queue. The head of the first list and tail of
        // the last list will only make use of one of their head and tail references, setting the other one to null.
        markers = new Marker[n + 1];

        // Create them.
        for (int i = 0; i < (n + 1); i++)
        {
            markers[i] = new Marker<E>(null, null);
        }

        // Chain them together.
        for (int i = 0; i < (n + 1); i++)
        {
            if (i < n)
            {
                markers[i].setNext(markers[i + 1]);
            }

            if (i > 0)
            {
                markers[i].setTail(markers[i - 1]);
            }

            /*log.fine("markers[" + i + "] = " + markers[i]);*/
        }
    }

    /**
     * Adds the specified element to the tail of this queue.
     *
     * @param  o The element to add.
     *
     * @return <tt>true</tt> (as per the general contract of <tt>Collection.add</tt>).
     */
    public boolean add(E o)
    {
        return offer(o);
    }

    /**
     * Inserts the specified element onto the tail of this queue.
     *
     * @param  o the element to add, may not be null.
     *
     * @return <tt>true</tt> (as per the general contract of <tt>Queue.offer</tt>).
     *
     * @todo   Compare and set possibly not needed after the succesfull tail join. Straight update looks like it should
     *         work? I may have missed something here.
     */
    public boolean offer(E o)
    {
        /*log.fine("public boolean offer(E o): called");*/
        /*log.fine("o = " + o);*/

        // Ensure that the item to add is not null.
        if (o == null)
        {
            throw new IllegalArgumentException("The 'o' parameter may not be null.");
        }

        // Derive the integer priority of the element using the priority function, shift it into this queues range if
        // necessary and adjust it to any offset caused by lowest priority not equal to zero.
        int level = priorityToLevel(p.apply(o));
        /*log.fine("offer level = " + level);*/

        // Create a new node to hold the new data element.
        Node<E> newNode = new DataNode<E>(o, markers[level + 1]);

        // Add the element to the tail of the queue with matching level, looping until this can complete as an atomic
        // operation.
        while (true)
        {
            // Get tail and next ref. Would expect next ref to be null, but other thread may update it.
            Node<E> t = markers[level + 1].getTail();
            Node<E> s = t.getNext();

            /*log.fine("t = " + t);*/
            /*log.fine("s = " + s);*/

            // Recheck the tail ref, to ensure other thread has not already moved it. This can potentially prevent
            // a relatively expensive compare and set from failing later on, if another thread has already shited
            // the tail.
            if (t == markers[level + 1].getTail())
            {
                /*log.fine("t is still the tail.");*/

                // Check that the next element reference on the tail is the tail marker, to confirm that another thread
                // has not updated it. Again, this may prevent a cas from failing later.
                if (s == markers[level + 1])
                {
                    /*log.fine("s is  the tail marker.");*/

                    // Try to join the new tail onto the old one.
                    if (t.casNext(s, newNode))
                    {
                        // The tail join was succesfull, so now update the queues tail reference. No conflict should
                        // occurr here as the tail join was succesfull, so its just a question of updating the tail
                        // reference. A compare and set is still used because ...
                        markers[level + 1].casTail(t, newNode);

                        // Increment the queue size count.
                        count.incrementAndGet();

                        return true;
                    }
                }

                // Update the tail reference, other thread may also be doing the same.
                else
                {
                    // Why bother doing this at all? I suppose, because another thread may be stalled and doing this
                    // will enable this one to keep running.
                    // Update the tail reference for the queue because another thread has already added a new tail
                    // but not yet managed to update the tail reference.
                    markers[level + 1].casTail(t, s);
                }
            }
        }
    }

    /**
     * Retrieves and removes the head of this queue, or null if this queue is empty.
     *
     * <p/>This method will make a single pass down the queue, starting from the highest priority and working down to
     * the lowest. If it fails to extract an element from the queue on this single pass, it will return null. It is
     * possible that the queue contains transient data during this pass that this method does not manage to extract,
     * because other threads managed to take the data, or because other threads inserted higher priority data once this
     * pass was already further down the queue.
     *
     * @return The head of this queue, or null if this queue is empty.
     */
    public E poll()
    {
        /*log.fine("public E poll(): called");*/

        // This is used to keep track of the level of the list that is found to have data in it.
        int currentLevel = 0;

        while (true)
        {
            // This is used to locate the marker head of a list that contains data.
            Marker<E> h = null;

            // This is used to locate the potential data node of a list with data in it. Another thread may already
            // have taken this data.
            Node<E> first = null;

            // Second data item, may also be tail marker, first data item of next list, or null at end of last list.
            Node<E> second = null;

            // Loop down any empty lists at the front of the queue until a list with data in it is found.
            for (; currentLevel < n; currentLevel++)
            {
                h = markers[currentLevel];
                first = h.getNext();
                second = first.getNext();

                // Check if the list at the current level is not empty and should be tried for data.
                if (!h.isEmpty(markers[currentLevel + 1]))
                {
                    break;
                }

                // Check if the current level is empty and is the last level, in which case return null.
                else if (currentLevel == (n - 1))
                {
                    // log.info("returning null from level loop.");

                    return null;
                }

                // Else if the current level is empty loop to the next one to see if it has data.
            }

            /*log.fine("current poll level = " + currentLevel);*/

            // This is used to locate the tail of the list that has been found with data in it.
            Node<E> t = markers[currentLevel + 1].getTail();

            // Check that the first data item has not yet been taken. Another thread may already have taken it,
            // in which case performing a relatively expensive cas on the head will fail. If first is still intact
            // then second will be intact too.
            if (first == h.getNext())
            {
                // Check if the queue has become empty.
                if (h.isEmpty(markers[currentLevel + 1]))
                {
                    // Another thread has managed to take data from the queue, leaving it empty.

                    // First won't be null. It may point to tail though...
                    if (first == null)
                    {
                        // Don't want to return here, want to try the next list. The list loop has a return null
                        // once it gets to the end to take care of that.
                        // log.info("returning null as first == null");

                        return null;
                    }
                    else
                    {
                        // Not sure yet why castail here? Does this repair a broken tail ref left after the last item
                        // was taken?
                        markers[currentLevel + 1].casTail(t, first);
                    }
                }

                // The queue contains data, so try to move its head marker reference from the first data item, onto the
                // second item (which may be data, or the tail marker). If this succeeds, then the first data node
                // has been atomically extracted from the head of the queue.
                else if (h.casNext(first, second))
                {
                    // h Does not refer to an empty queue, so first must be a data node.
                    DataNode<E> firstDataNode = ((DataNode<E>) first);
                    E item = firstDataNode.getItem();

                    // Even though the empty test did not indicate that the list was empty, it may contain null
                    // data items, because the remove method doesn't extract nodes on a remove. These need to be skipped
                    // over. Could they be removed here?
                    if (item != null)
                    {
                        firstDataNode.setItem(null);

                        /*log.fine("returing item = " + item);*/

                        // Decrement the queue size count.
                        count.decrementAndGet();

                        return item;
                    }

                    // else skip over deleted item, continue trying at this level. Go back an retry starting from same
                    // level. List at this level may now be empty, or may get the next item from it.

                    // else skip over marker element. just make markers return null for item to skip them? No, because
                    // need to advance currentLevel and get head and tail markers for the next level. but then, next
                    // level advance will occur when this level is retried and found to be empty won't it?
                }
            }
        }
    }

    /**
     * Retrieves, but does not remove, the head of this queue, returning null if this queue is empty.
     *
     * @return The head of this queue, or null if this queue is empty.
     */
    public E peek()
    {
        throw new NotImplementedException();

        /*
        while (true)
        {
            Node<E> h = head;
            Node<E> t = tail;
            Node<E> first = h.getNext();
            if (h == head)
            {
                if (h == t)
                {
                    if (first == null)
                    {
                        return null;
                    }
                    else
                    {
                        casTail(t, first);
                    }
                }
                else
                {
                    E item = first.getItem();
                    if (item != null)
                    {
                        return item;
                    }
                    else // remove deleted node and continue
                    {
                        casHead(h, first);
                    }
                }
            }
        }*/
    }

    /**
     * Checks if this queue is empty.
     *
     * @return <tt>true</tt> if this queue is empty, <tt>false</tt> otherwise.
     */
    public boolean isEmpty()
    {
        return count.intValue() == 0;
    }

    /**
     * Returns the number of elements in this queue.
     *
     * @return the number of elements in this queue.
     */
    public int size()
    {
        return count.intValue();
    }

    /**
     * Scans down the queue till a match is found.
     *
     * @param  o The object to try and find in the queue.
     *
     * @return <tt>true</tt> if the element was found in the queue, <tt>false</tt> otherwise.
     */
    public boolean contains(Object o)
    {
        throw new NotImplementedException();

        /*
        if (o == null)
        {
            return false;
        }

        for (Node<E> p = first(); p != null; p = p.getNext())
        {
            E item = p.getItem();
            if ((item != null) && o.equals(item))
            {
                return true;
            }
        }

        return false;
         */
    }

    /**
     * Removes the specified element from this queue.
     *
     * @param  o The element to remove from this queue.
     *
     * @return <tt>true</tt> if the element was found and removed from the queue, <tt>false</tt> otherwise.
     */
    public boolean remove(Object o)
    {
        throw new NotImplementedException();
        /*
        if (o == null)
        {
            return false;
        }

        for (Node<E> p = first(); p != null; p = p.getNext())
        {
            E item = p.getItem();
            if ((item != null) && o.equals(item) && p.casItem(item, null))
            {
                return true;
            }
        }

        return false;*/
    }

    /**
     * Copies the entire contents of this queue into an array.
     *
     * @return The entire contents of this queue as an array.
     */
    public Object[] toArray()
    {
        throw new NotImplementedException();

        /*
        // Use ArrayList to deal with resizing.
        ArrayList<E> al = new ArrayList<E>();
        for (Node<E> p = first(); p != null; p = p.getNext())
        {
            E item = p.getItem();
            if (item != null)
            {
                al.add(item);
            }
        }

        return al.toArray();*/
    }

    /**
     * Copies the entire contents of this queue into an array.
     *
     * @param  a   The array to copy into.
     * @param  <T> The type of the array.
     *
     * @return The entire contents of this queue as an array.
     */
    public <T> T[] toArray(T[] a)
    {
        throw new NotImplementedException();

        /*
        // try to use sent-in array
        int k = 0;
        Node<E> p;
        for (p = first(); (p != null) && (k < a.length); p = p.getNext())
        {
            E item = p.getItem();
            if (item != null)
            {
                a[k++] = (T) item;
            }
        }

        if (p == null)
        {
            if (k < a.length)
            {
                a[k] = null;
            }

            return a;
        }

        // If won't fit, use ArrayList version
        ArrayList<E> al = new ArrayList<E>();
        for (Node<E> q = first(); q != null; q = q.getNext())
        {
            E item = q.getItem();
            if (item != null)
            {
                al.add(item);
            }
        }

        return (T[]) al.toArray(a);*/
    }

    /**
     * Returns an iterator over the elements in this queue in proper sequence. The returned iterator is a "weakly
     * consistent" iterator that will never throw {@link java.util.ConcurrentModificationException}, and guarantees to
     * traverse elements as they existed upon construction of the iterator, and may (but is not guaranteed to) reflect
     * any modifications subsequent to construction.
     *
     * @return an iterator over the elements in this queue in proper sequence.
     */
    public Iterator<E> iterator()
    {
        throw new NotImplementedException();
        /*
        return new Itr();
         */
    }

    /**
     * Prints out the entire queue for debugging purposes.
     *
     * @return The entire queue for debugging purposes.
     */
    /*public String toString()
    {
        String result = "LockFreeNQueue [";

        Node i = markers[0];

        do
        {
            result += i.toString();

            i = i.getNext();

            result += (i == null) ? "" : ",\n";
        }
        while (i != null);

        result += "]";

        return result;
    }*/

    /**
     * Returns the first actual (non-header) node on the queue. This is yet another variant of poll/peek; here returning
     * out the first node, not element (so cannot collapse with peek() without introducing a race.)
     *
     * @return The first actual non-header node on the queue.
     */
    Node<E> first()
    {
        throw new NotImplementedException();

        /*while (true)
        {
            Node<E> h = head;
            Node<E> t = tail;
            Node<E> first = h.getNext();
            if (h == head)
            {
                if (h == t)
                {
                    if (first == null)
                    {
                        return null;
                    }
                    else
                    {
                        casTail(t, first);
                    }
                }
                else
                {
                    if (first.getItem() != null)
                    {
                        return first;
                    }
                    else // remove deleted node and continue
                    {
                        casHead(h, first);
                    }
                }
            }
        } */
    }

    /**
     * Shifts the priority into this queues range if it is outside it and adjusts it to any offset caused by lowest
     * priority not equal to zero.
     *
     * @param  priority The priority.
     *
     * @return The number, n, of the queue that holds elements with the specified priority.
     */
    private int priorityToLevel(int priority)
    {
        return (priority < lowest) ? 0 : ((priority > highest) ? n : (priority - lowest));
    }

    /**
     * Implements an iterator over lock free n queues.
     */
    private class Itr // implements Iterator<E>
    {
        /** Next node to return item for. */
        private Node<E> nextNode;

        /**
         * nextItem holds on to item fields because once we claim that an element exists in hasNext(), we must return it
         * in the following next() call even if it was in the process of being removed when hasNext() was called.
         */
        private E nextItem;

        /** Node of the last returned item, to support remove. */
        private Node<E> lastRet;

        /*
        Itr()
        {
            advance();
        } */

        /**
         * Reports whether or not this iterator has more elements.
         *
         * @return <tt>true</tt> if there are more elements in this iterator, <tt>false</tt> if not.
         */
        public boolean hasNext()
        {
            return nextNode != null;
        }

        /*
        public E next()
        {
            if (nextNode == null)
            {
                throw new NoSuchElementException();
            }

            return advance();
        } */

        /*
        public void remove()
        {
            Node<E> l = lastRet;
            if (l == null)
            {
                throw new IllegalStateException();
            }
            // rely on a future traversal to relink.
            l.setItem(null);
            lastRet = null;
        } */

        /**
         * Moves to next valid node and returns item to return for
         * next(), or null if no such.
         */
        /*
        private E advance()
        {
            lastRet = nextNode;
            E x = nextItem;

            Node<E> p = (nextNode == null) ? first() : nextNode.getNext();
            while (true)
            {
                if (p == null)
                {
                    nextNode = null;
                    nextItem = null;

                    return x;
                }

                E item = p.getItem();
                if (item != null)
                {
                    nextNode = p;
                    nextItem = item;

                    return x;
                }
                else // skip over nulls
                {
                    p = p.getNext();
                }
            }
        }   */
    }

    /**
     * Node is an element of the queues linked list structure. There are two possible kinds of nodes, data nodes that
     * hold the queued data items, and marker nodes that mark the head and tail of each priority level of the list.
     *
     * <p/>Nodes form a linked list structure, with forward references to the next element of the list only. This class
     * supplies methods to atomically update the next reference.
     */
    private abstract static class Node<E>
    {
        /** Atomic reference updater for the next reference. */
        protected static final AtomicReferenceFieldUpdater<Node, Node> NEXT_UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");

        /** Holds a reference to the next node in the linked list. */
        protected volatile Node<E> next;

        /**
         * Gets the next node in the queue, or null if this is the tail of the last queue.
         *
         * @return The next node in the queue, or null if this is the tail of the last queue.
         */
        Node<E> getNext()
        {
            return next;
        }

        /**
         * Updates the next node in the queue.
         *
         * @param val The new next node, after this one.
         */
        void setNext(Node<E> val)
        {
            NEXT_UPDATER.set(this, val);
        }

        /**
         * Performs a compare and set on the next node the queue. The next node is only updated if it is equals (by
         * reference) to the specified value.
         *
         * @param  cmp The next node reference to compare to.
         * @param  val The new next node value.
         *
         * @return <tt>true</tt> if the next node reference was updated, <tt>false</tt> otherwise.
         */
        boolean casNext(Node<E> cmp, Node<E> val)
        {
            return NEXT_UPDATER.compareAndSet(this, cmp, val);
        }
    }

    /**
     * A Marker node is part of the queues linked list structure. In addition to the forward next element reference
     * inherited from its {@link Node} parent, it also has a reverse reference element that referes to the tail of the
     * queue before the queue that it is the head of. In the Marker nodes that are the head of the first queue or tail
     * of the last queue, one of the forward or reverse references will be null.
     *
     * <p/>This class supplies methods to atomically update the tail reference.
     */
    private static class Marker<E> extends Node<E>
    {
        /** Atomic reference updater for the tail reference. */
        private static final AtomicReferenceFieldUpdater<Marker, Node> TAIL_UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(Marker.class, Node.class, "tail");

        /** Holds a reference to the tail node of the queue before this one. */
        private volatile Node<E> tail;

        /**
         * Creates a new marker node with the specified previous and next references.
         *
         * @param p A reference to the tail node of the queue before this one.
         * @param n A reference to the next node in this queue.
         */
        Marker(Node<E> p, Node<E> n)
        {
            this.tail = p;
            this.next = n;
        }

        /**
         * Checks to see if this marker is the head of an empty queue with respect to the given marker which should be
         * the head of the queue immediately after this one.
         *
         * @param  m The marker node at the head of the next queue.
         *
         * @return <tt>true</tt> if this marker nodes next reference refers directly to the next marker node, in which
         *         case there are no data elements between them, so this is the head of an empty queue.
         */
        public boolean isEmpty(Marker<E> m)
        {
            return next == m;
        }

        /**
         * Mainly used for generating better debugging log output.
         *
         * @return The node as a pretty printed string.
         */
        public String toString()
        {
            return "Marker [ this = " + hashCode() + ", next = " + ((next != null) ? ("" + next.hashCode()) : "null") +
                ", tail = " + ((tail != null) ? ("" + tail.hashCode()) : "null") + "]";
        }

        /**
         * Gets the tail node of the queue before this one, or null if this is the head of the first queue.
         *
         * @return The tail node of the queue before this one, or null if this is the head of the first queue.
         */
        Node<E> getTail()
        {
            return tail;
        }

        /**
         * Updates the tail node of the queue before this one.
         *
         * @param val The new tail node, before this queue.
         */
        void setTail(Node<E> val)
        {
            TAIL_UPDATER.set(this, val);
        }

        /**
         * Performs a compare and set on the tail node of the queue before this one. The tail node is only updated if it
         * is equal (by reference) to the specified value.
         *
         * @param  cmp The tail node reference to compare to.
         * @param  val The new tail node value.
         *
         * @return <tt>true</tt> if the tail node reference was updated, <tt>false</tt> otherwise.
         */
        boolean casTail(Node<E> cmp, Node<E> val)
        {
            return TAIL_UPDATER.compareAndSet(this, cmp, val);
        }
    }

    /**
     * DataNode is an element wrapper that holds an element of a queue in a linked list structure. It provides methods
     * to atomically update the data element reference.
     */
    private static class DataNode<E> extends Node<E>
    {
        /** Atomic reference updater for the data element. */
        private static final AtomicReferenceFieldUpdater<DataNode, Object> ITEM_UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(DataNode.class, Object.class, "item");

        /** Holds a reference to the data element managed by this node. */
        private volatile E item;

        /**
         * Creates a new node to manage the specified data element.
         *
         * @param x The data element to capture in the node.
         */
        DataNode(E x)
        {
            item = x;
        }

        /**
         * Creates a new node to manage the specified data element, adding it to the the start of the specified list.
         *
         * @param x The data element to capture in the node.
         * @param n The head node of the list to add to.
         */
        DataNode(E x, Node<E> n)
        {
            item = x;
            next = n;
        }

        /**
         * Mainly used for generating better debugging log output.
         *
         * @return The node as a pretty printed string.
         */
        public String toString()
        {
            return "DataNode [ this = " + hashCode() + " next = " + ((next != null) ? ("" + next.hashCode()) : "null") +
                ", item = " + item + "]";
        }

        /**
         * Gets the data item from this node.
         *
         * @return The data item from this node.
         */
        E getItem()
        {
            return item;
        }

        /**
         * Updates the data item.
         *
         * @param val The new data item.
         */
        void setItem(E val)
        {
            ITEM_UPDATER.set(this, val);
        }

        /**
         * Performs a compare and set on the data element. The data element is only updated if it is equal (by
         * reference) to the specified value.
         *
         * @param  cmp The data element to compare to.
         * @param  val The new value to set.
         *
         * @return <tt>true</tt> if the data element was updated, <tt>false</tt> otherwise.
         */
        boolean casItem(E cmp, E val)
        {
            return ITEM_UPDATER.compareAndSet(this, cmp, val);
        }
    }
}
