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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.thesett.common.tx.SessionFactory;
import com.thesett.common.tx.SessionFactoryImpl;
import com.thesett.common.tx.TxManager;
import com.thesett.common.tx.TxSession;
import com.thesett.common.util.Queue;
import com.thesett.common.util.ReQueue;
import com.thesett.common.util.Sizeable;
import com.thesett.common.util.SizeableQueue;
import com.thesett.common.util.StackQueue;

/**
 * Tests the wrapper queue for its ability to make queues transactional and sizeable.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check that elements queued into a non-transactional queue can be dequeued immediately.
 * <tr><td>Check that elements queued into a transactional queue can only be dequeued after a commit.
 * <tr><td>Check that elements queued into a transactional queue cannot be dequeued after a rollback.
 * <tr><td>Check that elements dequeued from a transactional queue can be dequeued again after a rollback.
 * <tr><td>Check that attempting to access a transactional queue outside of a transaction fails.
 * <tr><td>Check that a sizeable queue of sizeable elements reports it size correctly.
 * <tr><td>Check that a non-transactional sizeable queue updates its size immediately.
 * <tr><td>Check that a transactional sizeable queue updates its size and count on commit only.
 * <tr><td>Check that a queue reports its element count correctly.
 * <tr><td>Check that a non-transactional queue updates its element count immediately.
 * <tr><td>Check that a transactional atomically counted queue updates its element count on commit only.
 * <tr><td>Check that an iterator over the queue sees all enqueued elements.
 * <tr><td>Check that an iterator over the queue sees all enqueued and committed elements.
 * <tr><td>Check that an iterator over the queue sees all dequeued but rolled back elements.
 * <tr><td>Check that a two-step acquire and accept removes an element from the queue.
 * <tr><td>Check that a two-step acquire and release leaves an element on the queue.
 * <tr><td>Check that a committed acquire accept removes an element from the queue.
 * <tr><td>Check that a rolled back acquire accept leaves an element on the queue.
 * <tr><td>Check that an unacquired element cannot be accepted.
 * <tr><td>Check that an unacquired element cannot be released.
 * <tr><td>Check that only the owner of an element can accept it.
 * <tr><td>Check that only the owner of an element can release it.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class WrapperQueueTest extends TestCase
{
    /** Random number source for the tests. */
    protected static Random random = new Random();

    /** Holds the queue to test. */
    java.util.Queue testQueue;

    /** Holds the requeue to test. */
    Collection testReQueue;

    /** Holds the session factory for creating transactional sessions. */
    SessionFactory sessionFactory = new SessionFactoryImpl();

    /** Holds the number of items to enqueue and dequeue in typical tests. */
    int testSize = 100;

    /**
     * Creates a wrapper queue test on a linked list as the default queue type.
     *
     * @param name        The name of the test.
     * @param testQueue   The underlying queue implementation to test.
     * @param testReQueue The requeue buffer implementation to test.
     */
    public WrapperQueueTest(String name, java.util.Queue testQueue, Collection testReQueue)
    {
        super(name);

        // Keep the queue to test.
        this.testQueue = testQueue;
        this.testReQueue = testReQueue;
    }

    /**
     * Creates a wrapper queue test on the specifed queue.
     *
     * @param queue The queue to test as a wrapped queue.
     * @param name  The name of the test.
     */
    public WrapperQueueTest(Queue<Integer> queue, String name)
    {
        super(name);

        // Keep the queue to test.
        testQueue = queue;
    }

    /**
     * Compile all the tests for the default test for unifiers into a suite, plus the tests defined in this class.
     *
     * @return A suite of tests.
     */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("WrapperQueue Tests");

        // The test cases.
        String[] testCases =
            {
                "testNoTxDequeue", "testTxDequeueOnlyAfterCommit", "testTxNoDequeueAfterRollback",
                "testTxReDequeueAfterRollback", "testTxAccessOutsideSessionFails", "testSizeOk",
                "testNonTxSizeImmediateOk", "testTxSizeOnCommitOk", "testCountOk", "testNonTxCountImmediateOk",
                "testTxCountOnCommitOk", "testNoTxBrowseOk", "testTxBrowseOk", "testTxBrowseRollbacksOk",
                "testAcquireAcceptOk", "testAcquireReleaseLeavesElementOnQueue", "testAcquireAcceptTxOk",
                "testAcquireAcceptRollbackLeavesElementOnQueue", "testAcceptNotAcquiredElementFails",
                "testReleaseNotAcquiredElementFails", "testNonOwnerAcceptElementFails",
                "testNonOwnerReleaseElementFails"
            };

        // The queue implementations to test.
        java.util.Queue[] queues =
            { new LinkedList(), new ConcurrentLinkedQueue(), new PriorityQueue(), /*new FibonacciHeap(),*/ new StackQueue() };

        // The requeue implementations to test.
        Collection[] requeues =
            { new LinkedList(), new ArrayList(), new HashSet(), new TreeSet(), new LinkedHashSet() };

        // Test suite made of all combinations.
        for (java.util.Queue queue : queues)
        {
            for (Collection requeue : requeues)
            {
                for (String testCase : testCases)
                {
                    suite.addTest(new WrapperQueueTest(testCase, queue, requeue));
                }
            }
        }

        return suite;
    }

    /** Check that elements queued into a non-transactional queue can be dequeued immediately. */
    public void testNoTxDequeue()
    {
        String errorMessages = "";

        Queue<Integer> notxTestQueue = new WrapperQueue<Integer>(testQueue, testReQueue, false, false, false);

        // Enqueue some items onto the test queue.
        Set<Integer> testItems = enqueueTestItems(notxTestQueue, 0, testSize);

        // Check that all of the enqueued items can be dequeued.
        errorMessages += checkAndDequeueQueueContents(notxTestQueue, testItems);

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that elements queued into a transactional queue can only be dequeued after a commit. */
    public void testTxDequeueOnlyAfterCommit()
    {
        String errorMessages = "";

        Queue<Integer> txTestQueue = Queues.getTransactionalReQueue(testQueue, testReQueue);

        // Ensure that the current thread has a transaction id associated with it.
        TxSession session = sessionFactory.createAndBindSession();

        // Enqueue some items onto the test queue.
        Set<Integer> testItems = enqueueTestItems(txTestQueue, 0, testSize);

        // Check that no items are available to dequeue.
        Integer head = txTestQueue.poll();

        if (head != null)
        {
            errorMessages +=
                "Should not be able to dequeue until the enqueued items are committed, but got " + head + ".\n";
        }

        // Commit the transactions.
        session.commit();

        // Check that all of the enqueued items can be dequeued.
        errorMessages += checkAndDequeueQueueContents(txTestQueue, testItems);

        // Remove the transactional association from this thread.
        session.unbind();

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that elements queued into a transactional queue cannot be dequeued after a rollback. */
    public void testTxNoDequeueAfterRollback()
    {
        String errorMessages = "";

        Queue<Integer> txTestQueue = Queues.getTransactionalReQueue(testQueue, testReQueue);

        // Ensure that the current thread has a transaction id associated with it.
        TxSession session = sessionFactory.createAndBindSession();

        // Enqueue some items onto the test queue.
        enqueueTestItems(txTestQueue, 0, testSize);

        // Check that no items are available to dequeue prior to commit.
        Integer head = txTestQueue.poll();

        if (head != null)
        {
            errorMessages +=
                "Should not be able to dequeue until the enqueued items are committed, but got " + head + ".\n";
        }

        // Rollback the transaction.
        session.rollback();

        // Check that no items are available to dequeue after rollback.
        head = txTestQueue.poll();

        if (head != null)
        {
            errorMessages +=
                "Should not be able to dequeue after the enqueued items are rolled back, but got " + head + ".\n";
        }

        // Remove the transactional association from this thread.
        session.unbind();

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that elements dequeued from a transactional queue can be dequeued again after a rollback. */
    public void testTxReDequeueAfterRollback()
    {
        String errorMessages = "";

        Queue<Integer> txTestQueue = Queues.getTransactionalReQueue(testQueue, testReQueue);

        // Ensure that the current thread has a transaction id associated with it.
        TxSession session = sessionFactory.createAndBindSession();

        // Enqueue some items onto the test queue, taking a second copy of the test items for the second dequeueing.
        Set<Integer> testItems = enqueueTestItems(txTestQueue, 0, testSize);

        // Commit the transaction.
        session.commit();

        // Check that all of the enqueued items can be dequeued.
        String message = checkAndDequeueQueueContents(txTestQueue, testItems);

        if (!"".equals(message))
        {
            errorMessages += "Before rollback: " + message;
        }

        // Rollback the dequeueing.
        session.rollback();

        // Check that all of the enqueued items can be dequeued a second time.
        message = checkAndDequeueQueueContents(txTestQueue, testItems);

        if (!"".equals(message))
        {
            errorMessages += "After rollback: " + message;
        }

        // Commit the transaction.
        session.commit();

        // Remove the transactional association from this thread.
        session.unbind();

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that attempting to access a transaction queue outside of a transaction fails. */
    public void testTxAccessOutsideSessionFails()
    {
        String errorMessages = "";

        Queue<Integer> txTestQueue = Queues.getTransactionalReQueue(testQueue, testReQueue);

        // Ensure non-transactional access to a transactional queue fails.
        boolean testPassed = false;

        try
        {
            txTestQueue.offer(1);
        }
        catch (IllegalStateException e)
        {
            testPassed = true;
        }

        if (!testPassed)
        {
            errorMessages += "Was expecting IllegalStateException for access to a tx resouce outside a transaction.";
        }

        // Ensure unbound session removes transaction context.
        TxSession session = sessionFactory.createAndBindSession();
        session.unbind();

        testPassed = false;

        try
        {
            txTestQueue.offer(1);
        }
        catch (IllegalStateException e)
        {
            testPassed = true;
        }

        if (!testPassed)
        {
            errorMessages +=
                "Was expecting IllegalStateException for access to a tx resouce outside a transaction " +
                "because session was unbound.";
        }

        // Ensure bound session generates transaction context.
        session.bind();

        testPassed = true;

        try
        {
            txTestQueue.offer(1);
        }
        catch (IllegalStateException e)
        {
            testPassed = false;
        }

        if (!testPassed)
        {
            errorMessages += "Got IllegalStateException for access to a tx resouce when session is bound.";
        }

        // Remove the transactional association from this thread.
        session.unbind();

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that a sizeable queue of sizeable elements reports its size correctly. */
    public void testSizeOk()
    {
        SizeableQueue<Sizeable> szTestQueue = Queues.getSizeableQueue(testQueue);

        // Enqueue some items onto the test queue.
        long size = enqueueTestItemsReportingSize(szTestQueue, testSize);

        // Check that the queue reports its size correctly.
        assertEquals("Sizeable queue does not report its size correctly.", size, szTestQueue.sizeof());
    }

    /** Check that a non transactional sizeable queue updates its size immediately. */
    public void testNonTxSizeImmediateOk()
    {
        String errorMessages = "";

        SizeableQueue<Sizeable> szTestQueue = Queues.getSizeableQueue(testQueue);

        // Enqueue some items onto the test queue.
        long expectedSize = enqueueTestItemsReportingSize(szTestQueue, testSize);

        // Check that the queue reports its expectedSize correctly after enqueueing.
        long size = szTestQueue.sizeof();

        if (size != expectedSize)
        {
            errorMessages += "Was expecting size " + expectedSize + " after enqueuing, but was: " + size + ".\n";
        }

        // Dequeue all the elements off the queue.
        while (szTestQueue.poll() != null)
        {
        }

        // Check that the queue reports its expectedSize correctly after dequeueing.
        size = szTestQueue.sizeof();

        if (size != 0)
        {
            errorMessages += "Was expecting size zero after dequeueing, but was: " + size + ".\n";
        }

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that a transactional sizeable queue updates its size and count on commit only. */
    public void testTxSizeOnCommitOk()
    {
        String errorMessages = "";

        SizeableQueue<Sizeable> szTxTestQueue = Queues.getSizeableTransactionalReQueue(testQueue, testReQueue);

        // Ensure that the current thread has a transaction id associated with it.
        TxSession session = sessionFactory.createAndBindSession();

        // Enqueue some items onto the test queue.
        long size = enqueueTestItemsReportingSize(szTxTestQueue, testSize);

        // Check that the queue reports its size as zero before commit.
        long sizeBeforeCommit = szTxTestQueue.sizeof();

        if (sizeBeforeCommit != 0)
        {
            errorMessages += "Was expecting size zero before enqueue commit, but was: " + sizeBeforeCommit + ".\n";
        }

        // Check that the queue reports its count as zero before commit.
        long countBeforeCommit = szTxTestQueue.size();

        if (countBeforeCommit != 0)
        {
            errorMessages += "Was expecting count zero before commit, but was: " + countBeforeCommit + ".\n";
        }

        // Commit the enqueues.
        session.commit();

        // Check that the queue reports its size correctly after commit.
        long sizeAfterCommit = szTxTestQueue.sizeof();

        if (sizeAfterCommit != size)
        {
            errorMessages +=
                "Was expecting size " + size + " after enqueue commit, but was: " + sizeAfterCommit + ".\n";
        }

        // Check that the queue reports its count correctly after commit.
        long countAfterCommit = szTxTestQueue.size();

        if (countAfterCommit != testSize)
        {
            errorMessages += "Was expecting count " + testSize + " after commit, but was: " + countAfterCommit + ".\n";
        }

        // Dequeue all the elements off the queue.
        while (szTxTestQueue.poll() != null)
        {
        }

        // Check that the queue reports its size correctly before commit.
        sizeBeforeCommit = szTxTestQueue.sizeof();

        if (sizeBeforeCommit != size)
        {
            errorMessages +=
                "Was expecting size " + size + " before dequeue commit, but was: " + sizeBeforeCommit + ".\n";
        }

        // Check that the queue reports its count correctly before commit, this should be zero, because the queue is
        // not atomically counted.
        countBeforeCommit = szTxTestQueue.size();

        if (countBeforeCommit != 0)
        {
            errorMessages +=
                "Was expecting count zero before dequeue commit, as queue is not atomically counted, but was: " +
                countBeforeCommit + ".\n";
        }

        // Commit the dequeues.
        session.commit();

        // Check that the queue reports its size correctly after commit.
        sizeAfterCommit = szTxTestQueue.sizeof();

        if (sizeAfterCommit != 0)
        {
            errorMessages += "Was expecting size zero after dequeue commit, but was: " + sizeAfterCommit + ".\n";
        }

        // Check that the queue reports its count correctly after commit.
        countAfterCommit = szTxTestQueue.size();

        if (countAfterCommit != 0)
        {
            errorMessages += "Was expecting count zero after dequeue commit, but was: " + countAfterCommit + ".\n";
        }

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that a queue reports its element count correctly. */
    public void testCountOk()
    {
        Queue<Integer> ctTestQueue = Queues.getAtomicCountedQueue(testQueue);

        // Enqueue some items onto the test queue.
        enqueueTestItems(ctTestQueue, 0, testSize);

        // Check that the queue reports its size correctly.
        assertEquals("Sizeable queue does not report its count correctly.", testSize, ctTestQueue.size());
    }

    /** Check that a non-transactional queue updates its element count immediately. */
    public void testNonTxCountImmediateOk()
    {
        String errorMessages = "";

        Queue<Integer> ctTestQueue = Queues.getAtomicCountedQueue(testQueue);

        // Enqueue some items onto the test queue.
        enqueueTestItems(ctTestQueue, 0, testSize);

        // Check that the queue reports its count correctly after enqueueing.
        long count = ctTestQueue.size();

        if (count != testSize)
        {
            errorMessages += "Was expecting count " + testSize + " after enqueuing, but was: " + count + ".\n";
        }

        // Dequeue all the elements off the queue.
        while (ctTestQueue.poll() != null)
        {
        }

        // Check that the queue reports its expectedSize correctly after dequeueing.
        count = ctTestQueue.size();

        if (count != 0)
        {
            errorMessages += "Was expecting count zero after dequeueing, but was: " + count + ".\n";
        }

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that a transactional queue updates its element count on commit only. */
    public void testTxCountOnCommitOk()
    {
        String errorMessages = "";

        Queue<Integer> ctTxTestQueue = Queues.getAtomicCountedSizeableTransactionalReQueue(testQueue, testReQueue);

        // Ensure that the current thread has a transaction id associated with it.
        TxSession session = sessionFactory.createAndBindSession();

        // Enqueue some items onto the test queue.
        enqueueTestItems(ctTxTestQueue, 0, testSize);

        // Check that the queue reports its size as zero before commit.
        long countBeforeCommit = ctTxTestQueue.size();

        if (countBeforeCommit != 0)
        {
            errorMessages += "Was expecting count zero before commit, but was: " + countBeforeCommit + ".\n";
        }

        // Commit the enqueues.
        session.commit();

        // Check that the queue reports its size correctly after commit.
        long countAfterCommit = ctTxTestQueue.size();

        if (countAfterCommit != testSize)
        {
            errorMessages += "Was expecting count " + testSize + " after commit, but was: " + countAfterCommit + ".\n";
        }

        // Dequeue all the elements off the queue.
        while (ctTxTestQueue.poll() != null)
        {
        }

        // Check that the queue reports its count correctly before commit.
        countBeforeCommit = ctTxTestQueue.size();

        if (countBeforeCommit != testSize)
        {
            errorMessages +=
                "Was expecting count " + testSize + " before dequeue commit, but was: " + countBeforeCommit + ".\n";
        }

        // Commit the dequeues.
        session.commit();

        // Check that the queue reports its count correctly after commit.
        countAfterCommit = ctTxTestQueue.size();

        if (countAfterCommit != 0)
        {
            errorMessages += "Was expecting count zero after dequeue commit, but was: " + countAfterCommit + ".\n";
        }

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that an iterator over the queue sees all enqueued elements. */
    public void testNoTxBrowseOk()
    {
        String errorMessages = "";

        Queue<Integer> notxTestQueue = new WrapperQueue<Integer>(testQueue, testReQueue, false, false, false);

        // Enqueue some items onto the test queue.
        Set<Integer> testItems = enqueueTestItems(notxTestQueue, 0, testSize);

        // Check that all of the enqueued items can be browsed.
        errorMessages += checkAndBrowseQueueContents(notxTestQueue, testItems);

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that an iterator over the queue sees all enqueued and committed elements. */
    public void testTxBrowseOk()
    {
        String errorMessages = "";

        Queue<Integer> txTestQueue = Queues.getTransactionalReQueue(testQueue, testReQueue);

        // Ensure that the current thread has a transaction id associated with it.
        TxSession session = sessionFactory.createAndBindSession();

        // Enqueue some items onto the test queue and commit them.
        Set<Integer> testItems = enqueueTestItems(txTestQueue, 0, testSize);
        session.commit();

        // Enqueue some more items onto the test queue but don't commit them.
        enqueueTestItems(txTestQueue, testSize, 2 * testSize);

        // Check that all of the enqueued items can be browsed.
        errorMessages += checkAndBrowseQueueContents(txTestQueue, testItems);

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that an iterator over the queue sees all dequeued but rolled back elements. */
    public void testTxBrowseRollbacksOk()
    {
        String errorMessages = "";

        Queue<Integer> txTestQueue = Queues.getTransactionalReQueue(testQueue, testReQueue);

        // Ensure that the current thread has a transaction id associated with it.
        TxSession session = sessionFactory.createAndBindSession();

        // Enqueue some items onto the test queue and commit them.
        Set<Integer> testItems = enqueueTestItems(txTestQueue, 0, testSize);
        session.commit();

        // Consume all of the enqueued items, but roll-back the whole consume.
        String message = checkAndDequeueQueueContents(txTestQueue, testItems);

        if (!"".equals(message))
        {
            errorMessages += "Before rollback: " + message;
        }

        session.rollback();

        // Check that all of the enqueued items can be browsed.
        errorMessages += checkAndBrowseQueueContents(txTestQueue, testItems);

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that a two-step acquire and accept removes an element from the queue. */
    public void testAcquireAcceptOk()
    {
        String errorMessages = "";

        WrapperQueue<Integer> queue = new WrapperQueue<Integer>(testQueue, testReQueue, false, false, false);

        // Enqueue some items onto the test queue and commit them.
        Set<Integer> testItems = enqueueTestItems(queue, 0, testSize);

        // Take all items from the queue non-tx.
        Object owner = new Object();
        Set<Integer> acquired = acquireQueueContents(queue, owner);
        acceptQueueContents(queue, acquired, owner);

        // Check that no items are available to dequeue after.
        Integer head = queue.poll();

        if (head != null)
        {
            errorMessages +=
                "Should not be able to dequeue after the enqueued items are taken, but got " + head + ".\n";
        }

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that a two-step acquire and release leaves an element on the queue. */
    public void testAcquireReleaseLeavesElementOnQueue()
    {
        String errorMessages = "";

        WrapperQueue<Integer> queue = new WrapperQueue<Integer>(testQueue, testReQueue, false, false, false);

        // Enqueue some items onto the test queue and commit them.
        Set<Integer> testItems = enqueueTestItems(queue, 0, testSize);

        // Take then cancel a message from the queue non-tx.
        Object owner = new Object();
        Set<Integer> acquired = acquireQueueContents(queue, owner);
        releaseQueueContents(queue, acquired, owner);

        // Check all items are back on the queue again.
        errorMessages += checkAndDequeueQueueContents(queue, testItems);

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that a committed acquire accept removes an element from the queue. */
    public void testAcquireAcceptTxOk()
    {
        String errorMessages = "";

        WrapperQueue<Integer> queue = new WrapperQueue<Integer>(testQueue, testReQueue, true, false, false);

        // Set up tx context.
        SessionFactory sessionFactory = new SessionFactoryImpl();
        TxSession session = sessionFactory.createAndBindSession();

        // Enqueue some items onto the test queue and commit them.
        Set<Integer> testItems = enqueueTestItems(queue, 0, testSize);
        session.commit();

        // Acquire and accept items in tx.
        Set<Integer> acquired = acquireQueueContents(queue);
        acceptQueueContents(queue, acquired);

        session.commit();

        // Check that no items are available to dequeue after.
        Integer head = queue.poll();

        if (head != null)
        {
            errorMessages +=
                "Should not be able to dequeue after the enqueued items are taken, but got " + head + ".\n";
        }

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that a rolled back acquire accept leaves an element on the queue. */
    public void testAcquireAcceptRollbackLeavesElementOnQueue()
    {
        String errorMessages = "";

        WrapperQueue<Integer> queue = new WrapperQueue<Integer>(testQueue, testReQueue, true, false, false);

        // Set up tx context.
        SessionFactory sessionFactory = new SessionFactoryImpl();
        TxSession session = sessionFactory.createAndBindSession();

        // Enqueue some items onto the test queue and commit them.
        Set<Integer> testItems = enqueueTestItems(queue, 0, testSize);
        session.commit();

        // Acquire and accept items in tx, but roll back.
        Set<Integer> acquired = acquireQueueContents(queue);
        acceptQueueContents(queue, acquired);

        session.rollback();

        // Check all items are back on the queue again.
        errorMessages += checkAndDequeueQueueContents(queue, testItems);

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that an unacquired element cannot be accepted. */
    public void testAcceptNotAcquiredElementFails()
    {
        String errorMessages = "";

        WrapperQueue<Integer> queue = new WrapperQueue<Integer>(testQueue, testReQueue, false, false, false);

        // Attempt to accept a message not acquired.
        Object owner = new Object();

        queue.accept(owner, -1); // fail.

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that an unacquired element cannot be released. */
    public void testReleaseNotAcquiredElementFails()
    {
        String errorMessages = "";

        WrapperQueue<Integer> queue = new WrapperQueue<Integer>(testQueue, testReQueue, false, false, false);

        // Attempt to release a message not acquired.
        Object owner = new Object();

        queue.release(owner, -1); // fail.

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that only the owner of an element can accept it. */
    public void testNonOwnerAcceptElementFails()
    {
        String errorMessages = "";

        WrapperQueue<Integer> queue = new WrapperQueue<Integer>(testQueue, testReQueue, false, false, false);

        // Attempt to accept a message not owned.
        Object owner = new Object();

        Integer item = queue.pollAccept(owner);
        queue.accept(new Object(), item); // fail, not owner.

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that only the owner of an element can release it. */
    public void testNonOwnerReleaseElementFails()
    {
        String errorMessages = "";

        WrapperQueue<Integer> queue = new WrapperQueue<Integer>(testQueue, testReQueue, false, false, false);

        // Attempt to release a message not owned.
        Object owner = new Object();

        Integer item = queue.pollAccept(owner);
        queue.accept(new Object(), item); // fail, not owner.

        // Assert on and report any errors accumulated during the test.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /**
     * Creates transactional, non-transactional, countable and sizeable queues based on the underlying queue
     * implementation and requeue buffer implementation to test.
     */
    protected void setUp()
    {
        // Clear the test queue and requeue buffer.
        testQueue.clear();
        testReQueue.clear();

    }

    /**
     * Enqueues the requested number of integers onto the specified queue and returns the actual values enqueued in a
     * set. All enqueued values wil be guaranteed to be different.
     *
     * @param  queue The queue to enqueue the integers onto.
     * @param  from  The first integer to add (inclusive).
     * @param  to    The last integer to add (non-inclusive).
     *
     * @return A set of the enqueued values.
     */
    private Set<Integer> enqueueTestItems(Queue<Integer> queue, int from, int to)
    {
        Set<Integer> testItems = new HashSet<Integer>();

        for (int i = from; i < to; i++)
        {
            queue.offer(i);
            testItems.add(i);
        }

        return testItems;
    }

    /**
     * Enqueues the requested number of randomly sized test sizeables onto the specified queue and returns the total
     * size enqueued.
     *
     * @param  queue  The queue to enqueue the integers onto.
     * @param  number The number of values to enqueue.
     *
     * @return The total enqueued size.
     */
    private long enqueueTestItemsReportingSize(Queue<Sizeable> queue, int number)
    {
        long size = 0L;

        for (int i = 0; i < number; i++)
        {
            Sizeable sizeable = new TestSizeable();
            queue.offer(sizeable);
            size += sizeable.sizeof();
        }

        return size;
    }

    /**
     * Dequeues all items from a queue, and checks that they all exist in a set of items to compare to. If there are any
     * items in the comparison set not found in the queue, these are reported as errors too.
     *
     * @param  queue     The queue to compare to a set.
     * @param  testItems The set of expected items to find on the queue.
     *
     * @return The empty string if there are no comparison errors, error messages describing the differences between the
     *         queue and the set if there are.
     */
    private String checkAndDequeueQueueContents(Queue<Integer> queue, Set<Integer> testItems)
    {
        String errorMessages = "";
        Integer nextHead;

        // Copy the comparison set so as to not destroy it.
        Set<Integer> testItemsCopy = new HashSet<Integer>(testItems);

        // Count the number of comparisons, to ensure that this does not loop forever. There can be at most the same
        // number of items dequeued as there are in the set of comparison items.
        int i = testItemsCopy.size();

        do
        {
            nextHead = queue.poll();
            i--;

            if (nextHead != null)
            {
                if (!testItemsCopy.remove(nextHead))
                {
                    errorMessages += "Dequeued an item that was never enqueued: " + nextHead + ".\n";
                }
            }
        }
        while ((nextHead != null) && (i >= 0));

        if (!testItemsCopy.isEmpty())
        {
            errorMessages += "There were items enqueued that could not be dequeued: " + testItemsCopy + ".\n";
        }

        return errorMessages;
    }

    /**
     * Uses an iterator to 'peek' at all items in a queue, and checks that they all exist in a set of items to compare
     * to. If there are any items in the comparison set not found in the queue, these are reported as errors too.
     *
     * @param  queue     The queue to compare to a set.
     * @param  testItems The set of expected items to find on the queue.
     *
     * @return The empty string if there are no comparison errors, error messages describing the differences between the
     *         queue and the set if there are.
     */
    private String checkAndBrowseQueueContents(Queue<Integer> queue, Set<Integer> testItems)
    {
        String errorMessages = "";

        // Copy the comparison set so as to not destroy it.
        Set<Integer> testItemsCopy = new HashSet<Integer>(testItems);

        for (Integer next : queue)
        {
            if (next != null)
            {
                if (!testItemsCopy.remove(next))
                {
                    errorMessages += "Browsed an item that is not in the comparison set: " + next + ".\n";
                }
            }
        }

        if (!testItemsCopy.isEmpty())
        {
            errorMessages +=
                "There were items in the comparison set that could not be browsed: " + testItemsCopy + ".\n";
        }

        return errorMessages;
    }

    private Set<Integer> acquireQueueContents(ReQueue<Integer> queue, Object owner)
    {
        Set<Integer> results = new HashSet<Integer>();

        Integer nextHead;

        do
        {
            nextHead = queue.pollAcquire(owner);

            if (nextHead != null)
            {
                results.add(nextHead);
            }
        }
        while ((nextHead != null));

        return results;
    }

    private void acceptQueueContents(ReQueue<Integer> queue, Set<Integer> accepts, Object owner)
    {
        for (Integer accept : accepts)
        {
            queue.accept(owner, accepts);
        }
    }

    private void releaseQueueContents(ReQueue<Integer> queue, Set<Integer> releases, Object owner)
    {
        for (Integer release : releases)
        {
            queue.release(owner, release);
        }
    }

    private Set<Integer> acquireQueueContents(ReQueue<Integer> queue)
    {
        Set<Integer> results = new HashSet<Integer>();

        Integer nextHead;

        do
        {
            nextHead = queue.pollAcquire(TxManager.getCurrentSession());

            if (nextHead != null)
            {
                results.add(nextHead);
            }
        }
        while ((nextHead != null));

        return results;
    }

    private void acceptQueueContents(ReQueue<Integer> queue, Set<Integer> accepts)
    {
        for (Integer accept : accepts)
        {
            queue.accept(TxManager.getCurrentSession(), accept);
        }
    }

    private void releaseQueueContents(ReQueue<Integer> queue, Set<Integer> releases)
    {
        for (Integer release : releases)
        {
            queue.release(TxManager.getCurrentSession(), release);
        }
    }

    private String compareSets(Set<Integer> original, Set<Integer> comparison)
    {
        String errorMessages = "";

        // Copy the comparison set so as to not destroy it.
        Set<Integer> originalCopy = new HashSet<Integer>(original);
        Set<Integer> comparisonCopy = new HashSet<Integer>(comparison);

        for (Integer next : originalCopy)
        {
            if (next != null)
            {
                if (!comparisonCopy.remove(next))
                {
                    errorMessages += "Found an item that is not in the comparison set: " + next + ".\n";
                }
            }
        }

        if (!comparisonCopy.isEmpty())
        {
            errorMessages +=
                "There were items in the comparison set that could not be browsed: " + comparisonCopy + ".\n";
        }

        return errorMessages;
    }

    /**
     * Source of randomly sized sizeables for test purposes. These are also Comparable, so that they can be used in
     * tests over queues with ordered elements.
     */
    private static class TestSizeable implements Sizeable, Comparable
    {
        /** Holds the size. */
        long size;

        /** Creates a randomly sized sizeable. */
        TestSizeable()
        {
            size = random.nextInt(100);
        }

        /**
         * Calculates the size of this object in bytes.
         *
         * @return The size of this object in bytes.
         */
        public long sizeof()
        {
            return size;
        }

        /**
         * Compares this object with the specified object for order.
         *
         * @param  o The Object to be compared.
         *
         * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater
         *         than the specified object.
         *
         * @throws ClassCastException If the specified object's type prevents it from being compared to this Object.
         */
        public int compareTo(Object o)
        {
            return new Long(size).compareTo(((TestSizeable) o).size);
        }
    }
}
