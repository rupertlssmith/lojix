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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;
import com.thesett.common.util.FibonacciHeap;


/**
 * FibonacciHeapTest is a pure unit test class for the {@link com.thesett.common.util.FibonacciHeap} class.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Test minimum element is always the smallest.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class FibonacciHeapTest extends TestCase
{
    /** Used for debugging. */
    /* private static final Logger log = Logger.getLogger(FibonacciHeapTest.class.getName()); */

    /** Creates a new heap test. */
    public FibonacciHeapTest(String testName)
    {
        super(testName);
    }

    /** Compile all the tests for the fibonacci heap node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite.
        TestSuite suite = new TestSuite("FibonacciHeap Tests");

        // Add all tests defined in the AbstractHeapTest class.
        suite.addTest(new AbstractHeapTest("testMinimumElementIsSmallest", new FibonacciHeap()));

        // Add all the tests defined in this class (using the default constructor)
        // suite.addTestSuite(FibonacciHeapTest.class);

        return suite;
    }

    protected void setUp() throws Exception
    {
        NDC.push(getName());
    }

    protected void tearDown() throws Exception
    {
        NDC.pop();
    }
}
