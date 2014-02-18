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
package com.thesett.common.util.maps;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * CircularArrayMapTest checks the the {@link CircularArrayMap} functions correctly as a
 * {@link com.thesett.common.util.maps.Dictionary}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check against all dictionary tests.
 * <tr><td>Test clearing up to a key in the dictionary really removes all keys before it, and none after it.
 * <tr><td>Check that the circular array can wrap around and grow.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class CircularArrayMapTest extends DictionaryTestBase<Integer, Object>
{
    /** Used to generate new sequential test keys. */
    private int nextTestKey;

    /**
     * Creates a test with the specified name.
     *
     * @param testName The name of the test.
     */
    public CircularArrayMapTest(String testName)
    {
        super(testName, new CircularArrayMap<Object>());
    }

    /** Test clearing up to a key in the dictionary really removes all keys before it, and none after it. */
    public void testClearUpToRemovesCorrectKeys() throws Exception
    {
        List<Integer> testKeys = new LinkedList<Integer>();
        CircularArrayMap testMap = (CircularArrayMap) testDictionary;

        // Put some keys into the dictionary.
        for (int i = 0; i < 1000; i++)
        {
            Integer testKey = createTestKey();
            testMap.put(testKey, createTestValue());
            testKeys.add(testKey);
        }

        // Clear the dictionary up to a point.
        int clearPoint = 500;
        testMap.clearUpTo(clearPoint);

        // Check that none of the original lower keys are now in the dictionary, but all of the higher ones are.
        for (Integer testKey : testKeys)
        {
            if ((testKey <= clearPoint) && (testMap.containsKey(testKey)))
            {
                fail("Dictionary contains key " + testKey + " after the dictionary was cleared up to " + clearPoint +
                    ".");
            }

            if ((testKey > clearPoint) && !testMap.containsKey(testKey))
            {
                fail("Dictionary does not contain key " + testKey + " after the dictionary was cleared up to " +
                    clearPoint + ".");
            }
        }
    }

    /** Check that the circular array can wrap around and grow. */
    public void testCircularResizeOk()
    {
        // Add data to map, clear up to n less than all, then add more data and so on. So that the map grows and is
        // repeatedly re-sized.

        LinkedList<Integer> testKeys = new LinkedList<Integer>();
        CircularArrayMap testMap = (CircularArrayMap) testDictionary;

        for (int j = 2; j < 10; j++)
        {
            // Put some more keys into the dictionary.
            int numKeys = j * j;
            Integer testKey = null;

            for (int i = 0; i < numKeys; i++)
            {
                testKey = createTestKey();
                testMap.put(testKey, createTestValue());
                testKeys.add(testKey);
            }

            // Clear the dictionary up to a point.
            int clearPoint = testKey - j;

            //System.out.println();
            //System.out.println(testMap);
            testMap.clearUpTo(clearPoint);

            //System.out.println("Cleared up to " + clearPoint + " on pass " + j + ".");
            //System.out.println(testMap);

            // Check that none of the original lower keys are now in the dictionary, but all of the higher ones are.
            for (Iterator<Integer> i = testKeys.iterator(); i.hasNext();)
            {
                testKey = i.next();

                if ((testKey <= clearPoint) && (testMap.containsKey(testKey)))
                {
                    i.remove();
                    fail("Dictionary contains key " + testKey + " after the dictionary was cleared up to " +
                        clearPoint + " on pass " + j + ".");
                }

                if ((testKey > clearPoint) && !testMap.containsKey(testKey))
                {
                    fail("Dictionary does not contain key " + testKey + " after the dictionary was cleared up to " +
                        clearPoint + " on pass " + j + ".");
                }
            }
        }
    }

    /** {@inheritDoc} */
    protected Integer createTestKey()
    {
        return nextTestKey++;
    }

    /** {@inheritDoc} */
    protected Object createTestValue()
    {
        return new Object();
    }
}
