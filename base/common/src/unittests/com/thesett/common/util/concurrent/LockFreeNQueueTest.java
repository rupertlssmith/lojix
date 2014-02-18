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
import java.util.Random;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import junit.framework.TestCase;



import com.thesett.common.properties.ParsedProperties;
import com.thesett.common.util.concurrent.LockFreeNQueue;
import com.thesett.common.util.Function;

/**
 * Tests the {@link LockFreeNQueue} class.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check that a lock free n queue can be created.
 * <tr><td>Check that a lock free n queue can be created with a non zero offset basis.
 * <tr><td>Check that creating a lock free n queue with lowest priority greater than highest fails.
 * <tr><td>Check that a sequence of random values can be inserted onto a lock free n queue.
 * <tr><td>Check that a sequence of random values can be inserted onto a lock free n queue and read back in priority order.
 * <tr><td>Check that a queue reports the correct size.
 * <tr><td>Check that peek always return the head of the queue.
 * <tr><td>Check that contains finds elements in the queue.
 * <tr><td>Check that contains does not find elements not in the queue.
 * <tr><td>Check that remove really removes elements from the queue.
 * <tr><td>Check that remove does not alter the queue for elements not in the queue.
 * <tr><td>Check that toArray outputs elements iff they are in the queue.
 * <tr><td>Check that iterator scans every elements in the queue in the correct order.
 * <tr><td>Check that iterator can remove elements from the queue.
 * <tr><td>Check that a queue can be serialized and deserialized.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class LockFreeNQueueTest extends TestCase
{
    public static java.util.logging.Logger log = java.util.logging.Logger.getLogger(LockFreeNQueueTest.class.getName());

    public static Random random = new Random();

    /** Atomic reference updater for the test queue. */
    protected static final AtomicReferenceFieldUpdater<LockFreeNQueueTest, Queue> nextUpdater =
        AtomicReferenceFieldUpdater.newUpdater(LockFreeNQueueTest.class, Queue.class, "testQueue");

    /**
     * The tests insert integers into the priority queue and this identity function uses the integer value as the
     * priority value.
     */
    public static Function<Integer, Integer> id =
        new Function<Integer, Integer>()
        {
            public Integer apply(Integer integer)
            {
                return integer;
            }
        };

    /** Used to read the tests configurable parameters through. */
    protected ParsedProperties testProps = new ParsedProperties();

    /** Holds the size of the queue data structure to work with in tests from test parameters, or sets up a default. */
    private int testSize = testProps.setPropertyIfNull("testSize", 1000);

    /** The queue implementation to test. */
    private volatile Queue<Integer> testQueue;

    public LockFreeNQueueTest(String name)
    {
        super(name);

        // Clear the test queue, so that it is create freshly for each test method.
        testQueue = null;
    }

    /** Check that a lock free n queue can be created. */
    public void testCreateOk() throws Exception
    {
        new LockFreeNQueue<Integer>(0, 10, id);
    }

    /** Check that a lock free n queue can be created with a non zero offset basis. */
    public void testCreateWithNonZeroBasisOk() throws Exception
    {
        new LockFreeNQueue<Integer>(-20, 20, id);
    }

    /** Check that creating a lock free n queue with lowest priority greater than highest fails. */
    public void testCreateWithLowestGreaterThanHighestFails() throws Exception
    {
        boolean testPassed = false;

        try
        {
            new LockFreeNQueue<Integer>(20, -20, id);
        }
        catch (IllegalArgumentException e)
        {
            testPassed = true;
        }

        assertTrue("Creating lock free n queue with lowest priority > highest did not fail.", testPassed);
    }

    /** Check that a sequence of random values can be inserted onto a lock free n queue. */
    public void testOfferOk() throws Exception
    {
        // Insert some values into the queue
        for (int i = 0; i < testSize; i++)
        {
            testQueue.offer(random.nextInt(10));
        }
    }

    /**
     * Check that a sequence of random values can be inserted onto a lock free n queue and read back in priority order.
     */
    public void testOfferThenPollInOrderOk() throws Exception
    {
        String errorMessages = "";

        // Insert some values into the queue
        for (int i = 0; i < testSize; i++)
        {
            testQueue.offer(random.nextInt(10));
        }

        // Extract all values back off the queue and check they are in order.
        int currentPriority = -1;

        for (int i = 0; i < testSize; i++)
        {
            Integer next = testQueue.poll();

            // Check that value returned from the queue is never null.
            if (next == null)
            {
                errorMessages += "Got null value.\n";

                break;
            }

            // Advance to next priority level as the queue makes it available.
            if (next > currentPriority)
            {
                currentPriority = next;
            }

            // Check that the priority level never goes backwards.
            if (next < currentPriority)
            {
                errorMessages +=
                    "Got lower priority value " + next + " than the current one " + currentPriority + ".\n";
            }
        }

        // Check for and report any test errors.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that a queue reports the correct size. */
    public void testSizeOk() throws Exception
    {
        String errorMessages = "";

        Queue testQueue = new LockFreeNQueue<Integer>(0, 10, id);

        for (int i = 0; i < testSize; i++)
        {
            int size = testQueue.size();

            if (size != i)
            {
                errorMessages += "Queue with " + i + " elements inserted has size " + size + ".\n";
            }

            if ((i == 0) && !testQueue.isEmpty())
            {
                errorMessages += "Queue with " + i + " elements does not report as emtpy.\n";
            }

            if ((i != 0) && testQueue.isEmpty())
            {
                errorMessages += "Queue with " + i + " elements reports as empty.\n";
            }

            testQueue.offer(random.nextInt(10));
        }

        // Check for and report any test errors.
        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /** Check that peek always return the head of the queue. */
    /*public void testPeekGetsHeadOk()
    {
        String errorMessages = "";

        Queue<Integer> testQueue = new LockFreeNQueue<Integer>(0, 10, id);

        // Holds the smallest value inserted so far, which will be at the head of the queue.
        int smallest = 10;

        for (int i = 0; i < testSize; i++)
        {
            Integer peek = testQueue.peek();
            int size = testQueue.size();

            if ((i == 0) && (peek != null))
            {
                errorMessages += "Peek returned non-null for empty queue.\n";
            }

            if ((i > 0) && (peek == null))
            {
                errorMessages += "Peek returned null for empty queue.\n";
            }

            if ((i > 0) && (peek != smallest))
            {
                errorMessages += "Peek did not return smallest, " + smallest + ", value on the queue but " + peek
                                 + " instead.\n";
            }

            // Insert more data onto the queue, keeping track of the head. This inserts to the back of the queue first
            // but moves towards the front as the test progresses but randomly too.
            int nextValue = 10 - (int) (random.nextFloat() * 10 * (testSize - i) / testSize);
            smallest = (nextValue < smallest) ? nextValue : smallest;
            testQueue.offer(nextValue);
        }

        // Check for and report any test errors.
        assertTrue(errorMessages, errorMessages.equals(""));
    }*/

    /** Check that contains finds elements in the queue. */
    /*public void testContainsOk() throws Exception
    {
        String errorMessages = "";

        Queue<Integer> testQueue = new LockFreeNQueue<Integer>(0, 10, id);

        // Insert values onto the queue.
        for (int i = 0; i < 10; i++)
        {
            testQueue.offer(i);
        }

        // Check all inserted values are on the queue.
        for (int i = 0; i < 10; i++)
        {
            if (!testQueue.contains(i))
            {
                errorMessages += "Inserted value, " + i + ", but contains cannot find it.\n";
            }
        }

        // Check for and report any test errors.
        assertTrue(errorMessages, errorMessages.equals(""));
    }*/

    /** Check that contains does not find elements not in the queue. */
    /*public void testContainsNotTrueOnNonQueuedElement() throws Exception
    {
        String errorMessages = "";

        Queue<Integer> testQueue = new LockFreeNQueue<Integer>(0, 10, id);

        // Insert even values onto the queue.
        for (int i = 0; i < 10; i += 2)
        {
            testQueue.offer(i);
        }

        // Check no odd values are on the queue.
        for (int i = 1; i < 10; i += 2)
        {
            if (testQueue.contains(i))
            {
                errorMessages += "Did not insert value, " + i + ", but contains found it.\n";
            }
        }

        // Check for and report any test errors.
        assertTrue(errorMessages, errorMessages.equals(""));
    }*/

    /** Check that remove really removes elements from the queue. */
    /*public void testRemoveOk() throws Exception
    {
        String errorMessages = "";

        Queue<Integer> testQueue = new LockFreeNQueue<Integer>(0, 10, id);

        // Insert values onto the queue.
        for (int i = 0; i < 10; i++)
        {
            testQueue.offer(i);
        }

        // Remove all the values from the queue.
        for (int i = 0; i < 10; i++)
        {
            if (!testQueue.remove(i))
            {
                errorMessages += "Remove did not return true for value, " + i + ".\n";
            }
        }

        // Check no inserted values are on the queue.
        for (int i = 0; i < 10; i++)
        {
            if (testQueue.contains(i))
            {
                errorMessages += "Removed value, " + i + ", but contains found it.\n";
            }
        }

        // Check for and report any test errors.
        assertTrue(errorMessages, errorMessages.equals(""));
    }*/

    /** Check that remove does not alter the queue for elements not in the queue. */
    /*public void testRemoveNonQueuedElementNoChange() throws Exception
    {
        String errorMessages = "";

        Queue<Integer> testQueue = new LockFreeNQueue<Integer>(0, 10, id);

        // Insert even values onto the queue.
        for (int i = 0; i < 10; i += 2)
        {
            testQueue.offer(i);
        }

        // Remove odd values from the queue.
        for (int i = 1; i < 10; i += 2)
        {
            if (testQueue.remove(i))
            {
                errorMessages += "Remove did not return false for value that was never inserted, " + i + ".\n";
            }
        }

        // Check all even values are still on the queue.
        for (int i = 0; i < 10; i += 2)
        {
            if (!testQueue.contains(i))
            {
                errorMessages += "Inserted value, " + i + ", but contains cannot find it.\n";
            }
        }

        // Check no odd values are on the queue.
        for (int i = 1; i < 10; i += 2)
        {
            if (testQueue.contains(i))
            {
                errorMessages += "Did not insert value, " + i + ", but contains found it.\n";
            }
        }

        // Check for and report any test errors.
        assertTrue(errorMessages, errorMessages.equals(""));
    }*/

    /** Check that toArray outputs elements iff they are in the queue. */
    /*public void testToArrayOk() throws Exception
    {
        String errorMessages = "";

        Queue<Integer> testQueue = new LockFreeNQueue<Integer>(0, 10, id);

        // Insert values onto the queue.
        for (int i = 0; i < testSize; i++)
        {
            testQueue.offer(random.nextInt(10));
        }

        // Generate an array from the queue.
        Integer[] array = new Integer[testQueue.size()];
        testQueue.toArray(array);

        // Poll every item off of the queue and check it is in the array in the same order.
        for (int i = 0; i < array.length; i++)
        {
            int nextFromArray = array[i];
            int nextFromQueue = testQueue.poll();

            if (nextFromArray != nextFromQueue)
            {
                errorMessages += "Next value from array, " + nextFromArray
                                 + ", does not equal the next value from the queue, " + nextFromQueue + ".\n";
            }
        }

        // Check for and report any test errors.
        assertTrue(errorMessages, errorMessages.equals(""));
    }*/

    /** Check that iterator scans every elements in the queue in the correct order. */
    /*public void testIteratorOk() throws Exception
    {
        String errorMessages = "";

        Queue<Integer> testQueue = new LockFreeNQueue<Integer>(0, 10, id);

        // Insert values onto the queue.
        for (int i = 0; i < testSize; i++)
        {
            testQueue.offer(random.nextInt(10));
        }

        // Generate an iterator over the queue and check every element in it is in the queue in the same order.
        for (int nextFromIterator : testQueue)
        {
            int nextFromQueue = testQueue.poll();

            if (nextFromIterator != nextFromQueue)
            {
                errorMessages += "Next value from iterator, " + nextFromIterator
                                 + ", does not equal the next value from the queue, " + nextFromQueue + ".\n";
            }
        }

        // Check for and report any test errors.
        assertTrue(errorMessages, errorMessages.equals(""));
    }*/

    /** Check that iterator can remove elements from the queue. */
    /*public void testIteratorRemoveOk() throws Exception
    {
        String errorMessages = "";

        Queue<Integer> testQueue = new LockFreeNQueue<Integer>(0, 10, id);

        // Insert values onto the queue.
        for (int i = 0; i < testSize; i++)
        {
            testQueue.offer(random.nextInt(10));
        }

        // Generate an iterator over the queue and remove all odd elements from it.
        for (Iterator<Integer> i = testQueue.iterator(); i.hasNext();)
        {
            int n = i.next();

            if ((n % 2) == 1)
            {
                i.remove();
            }
        }

        // Check that the queue contains no odd elements.
        for (int i = 1; i < 10; i += 2)
        {
            if (testQueue.contains(i))
            {
                errorMessages += "Did not insert value, " + i + ", but contains found it.\n";
            }
        }

        // Check for and report any test errors.
        assertTrue(errorMessages, errorMessages.equals(""));
    }*/

    /** Check that a queue can be serialized and deserialized. */
    /*public void testSerializedDeserializedOk() throws Exception
    {
        String errorMessages = "";

        Queue<Integer> testQueue = new LockFreeNQueue<Integer>(0, 10, id);

        // Insert values onto the queue.
        for (int i = 0; i < testSize; i++)
        {
            testQueue.offer(random.nextInt(10));
        }

        // Create a copy of the queue by serializing/deserializing it.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(testQueue);

        InputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        Queue<Integer> copyQueue = (Queue<Integer>) ois.readObject();

        // Iterate over both queues together, checking that they are equal.
        Iterator<Integer> tqIterator = testQueue.iterator();
        Iterator<Integer> cqIterator = copyQueue.iterator();

        for (; tqIterator.hasNext();)
        {
            int nextFromTestQueue = tqIterator.next();
            int nextFromCopyQueue = cqIterator.next();

            if (nextFromTestQueue != nextFromCopyQueue)
            {
                errorMessages += "Next value from copy queue, " + nextFromCopyQueue
                                 + ", does not equal the next value from the original queue, " + nextFromTestQueue + ".\n";
            }
        }

        // Check for and report any test errors.
        assertTrue(errorMessages, errorMessages.equals(""));
    }*/

    protected void setUp()
    {
        // Create the test queue, but only if it has not already been created. Also ensure that it is only created
        // by one of the test threads.
        while (testQueue == null)
        {
            nextUpdater.compareAndSet(this, null, new LockFreeNQueue<Integer>(0, 9, id));
        }
    }

    protected void tearDown()
    {
    }
}
