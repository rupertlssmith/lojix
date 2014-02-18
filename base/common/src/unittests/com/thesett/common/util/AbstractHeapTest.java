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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;
import com.thesett.common.util.Queue;

/**
 * AbstractHeapTest is a pure unit test class for the {@link com.thesett.common.util.AbstractHeap} class. It
 * cannot be run directly on this class as it is abstract and cannot therefore be instantiated but it provides a
 * constructor to pass instantiations of classes that extend AbstractHeap to be tested.
 *
 * <p>There is a default constructor for this test class that runs the tests on a java.util.PriorityQueue (which is a
 * binary heap). This tests the sanity of the tests that this class provides.
 *
 * <p>{@link com.thesett.common.util.AbstractHeap}s should be tested for conformance to the collection and queue
 * interfaces by test classes for these. General collection and queue tests are not repeated here. This class only tests
 * that the heap always obeyes the heap property.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Test minimum element is always the smallest.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class AbstractHeapTest extends TestCase
{
    /* private static final Logger log = Logger.getLogger(AbstractHeapTest.class.getName()); */

    /** Used to hold the test heap over doubles. */
    java.util.Queue<Double> testHeap;

    /**
     * This default constructor runs the tests on a java.util.PriorityQueue which is effectively a heap. This tests the
     * sanity of the tests that this class provides.
     */
    public AbstractHeapTest(String testName)
    {
        super(testName);

        // Create an instance of a priority queue to be heap tested.
        this.testHeap = new PriorityQueue<Double>();
    }

    /**
     * Creates a new AbstractHeapTest object.
     *
     * @param testName
     * @param testHeap
     */
    public AbstractHeapTest(String testName, Queue<Double> testHeap)
    {
        super(testName);

        // Keep a copy of the AbstractHeap to test.
        this.testHeap = testHeap;
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("AbstractHeap Tests");

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(AbstractHeapTest.class);

        return suite;
    }

    /**
     * Test minimum element is always the smallest. A fairly random test but one that should end up excercising the heap
     * quite thoroughly. This test inserts 100,000 random numbers onto the heap and then pulls them back one at a time
     * by asking for the minimum element and checks that the numbers never decrease.
     */
    public void testMinimumElementIsSmallest() throws Exception
    {
        List<Double> listNums = new ArrayList<Double>();

        int testSize = 10000;

        // Insert 100,000 random doubles onto the heap.
        // Keep them in a list too, so that as they are extracted from heap they can be ticked off to check that
        // everything that comes out went in and vica versa.
        for (int i = 1; i <= testSize; i++)
        {
            double next = Math.random();

            /*log.fine("Inserting " + i + "th random double with value " + next + ".");*/
            // System.out.println(i);
            testHeap.offer(next);
            listNums.add(next);
        }

        // Sort the list of numbers into ascending order.
        Collections.sort(listNums);

        // Peek at the first minimum element without removing it.
        double lastMin = testHeap.element();

        // Loop over the heap until it is empty, examining the ordering of the numbers.
        Iterator<Double> it = listNums.iterator();
        int j = testSize;

        while (!testHeap.isEmpty())
        {
            // Get the next minimum element.
            double nextMin = testHeap.remove();
            /*log.fine("Removed " + (j--) + "th random double with value " + nextMin + ".");*/

            // Check that the next minimum element is never smaller than the previous.
            assertTrue("Heap minimum violated for nextMin = " + nextMin + " +, lastMin = " + lastMin + "\n",
                nextMin >= lastMin);

            // Check that the next minimum element is equal to the next minimum element from the sorted list
            // of numbers that were inserted.
            double listMin = it.next();

            assertTrue("Heap minimum, " + nextMin + ", does not equal the next min, " + listMin +
                "from the sorted list of numbers that were inserted.", nextMin == listMin);

            // Set the next minimum element as the last one seen for comparison on the next iteration of this loop.
            lastMin = nextMin;
        }
    }

    protected void setUp() throws Exception
    {
        NDC.push(getName());

        // Clear the test heap so it is always empty at the start of a test.
        testHeap.clear();
    }

    protected void tearDown() throws Exception
    {
        NDC.pop();
    }
}
