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
import com.thesett.common.util.TimeoutMap;


/**
 * Tests for the TimeoutMap data structure.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check that elements inserted into the cache can be read back before they time out.
 * <tr><td>Check that elements inserted into the cache can be read back again when they have not timed out but the sweep
 * algorithm has had time to run.
 * <tr><td>Check that elements inserted into the cache cannot be read back again once they have timed out.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TimeoutMapTest extends TestCase
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(TimeoutMapTest.class.getName()); */

    public TimeoutMapTest(String name)
    {
        super(name);
    }

    /** Compile all the tests into a test suite. */
    public static Test suite()
    {
        // Build a new test suite.
        TestSuite suite = new TestSuite("TimeoutMap Tests");

        // Add all the tests defined in this class.
        suite.addTestSuite(TimeoutMapTest.class);

        // Ass all the map tests from the MapTest class.
        suite.addTest(new MapTest("testSizeOk", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testIsEmpty", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testIsNotEmpty", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testContainsKeyTrue", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testContainsKeyFalse", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testContainsValueTrue", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testContainsValueFalse", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testPutGetOk", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testPutNewReplace", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testPutGetNullForNonExistantKey", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testPutGetNullForNullValue", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testPutGetNullKeyOk", new TimeoutMap(600000, 1800000)));

        //suite.addTest(new MapTest("testRemoveKeyValue", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testRemoveKeyOk", new TimeoutMap(600000, 1800000)));
        suite.addTest(new MapTest("testRemoveNonExistantKey", new TimeoutMap(600000, 1800000)));

        return suite;
    }

    /** Check that elements inserted into the cache can be read back before they time out. */
    public void testPutGet() throws Exception
    {
        String errorMessage = "";

        // Create a TimeoutMap with a long expiry time and sweep interval.
        TimeoutMap cache = new TimeoutMap(600000, 600000);

        // Insert some elements.
        for (int i = 0; i < 1000; i++)
        {
            cache.put(new Integer(i), new Integer(i));
        }

        // Read back all the elements.
        for (int j = 0; j < 1000; j++)
        {
            Integer next = (Integer) cache.get(new Integer(j));

            // Check that the read back elements are all there and match the original elements.
            if (next == null)
            {
                errorMessage += "Elemement " + j + " could not be read back from the cache.\n";
            }

            if (next.intValue() != j)
            {
                errorMessage += "Read back element " + next + " does not match original element: " + j + "\n";
            }
        }

        // Kill the cache sweep algorithm.
        cache.kill();

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /**
     * Check that elements inserted into the cache can be read back again when they have not timed out but the sweep
     * algorithm has had time to run.
     */
    public void testPutGetSweepsOk() throws Exception
    {
        String errorMessage = "";

        // Create a TimeoutMap with a long expiry time and a short sweep interval.
        TimeoutMap cache = new TimeoutMap(500, 600000);

        // Insert some elements.
        for (int i = 0; i < 1000; i++)
        {
            cache.put(new Integer(i), new Integer(i));
        }

        // Wait long enough for the sweep algorithm to run.
        Thread.sleep(2000);

        // Read back all the elements.
        for (int j = 0; j < 1000; j++)
        {
            Integer next = (Integer) cache.get(new Integer(j));

            // Check that the read back elements are all there and match the original elements.
            if (next == null)
            {
                errorMessage += "Elemement " + j + " could not be read back from the cache.\n";
            }

            if (next.intValue() != j)
            {
                errorMessage += "Read back element " + next + " does not match original element: " + j + "\n";
            }
        }

        // Kill the cache sweep algorithm.
        cache.kill();

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that elements inserted into the cache cannot be read back again once they have timed out. */
    public void testPutGetTimesOut() throws Exception
    {
        String errorMessage = "";

        // Create a TimeoutMap with a short expiry time and sweep interval.
        TimeoutMap cache = new TimeoutMap(500, 1);

        // Insert some elements.
        for (int i = 0; i < 1000; i++)
        {
            cache.put(new Integer(i), new Integer(i));
        }

        // Wait long enough for the sweep algorithm to run.
        Thread.sleep(2000);

        // Read back all the elements and check that they have expired.
        for (int j = 0; j < 1000; j++)
        {
            Integer next = (Integer) cache.get(new Integer(j));

            // Check that the read back elements are all null.
            if (next != null)
            {
                errorMessage += "Elemement " + next + " has not expired from the cache.\n";
            }
        }

        // Check that the cache is empty.
        if (!cache.isEmpty())
        {
            errorMessage += "The cache is not empty.\n";
        }

        // Kill the cache sweep algorithm.
        cache.kill();

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    protected void setUp()
    {
    }

    protected void tearDown()
    {
    }
}
