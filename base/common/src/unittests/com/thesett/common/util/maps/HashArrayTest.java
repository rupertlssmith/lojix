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
package com.thesett.common.util.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

import com.thesett.common.util.MapTest;

/**
 * Tests for the HashArray data structure.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Test creation from an indexing map and a data collection
 * <tr><td>Test putting keys on top of existing keys overrides the original ordering
 * <tr><td>Test removing a key does not corrupt the index map
 * <tr><td>Test input ordering is preserved
 * <tr><td>Test input ordering of the key set is preserved
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class HashArrayTest extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(HashArrayTest.class.getName()); */

    /**
     * Creates a new HashArrayTest object.
     *
     * @param name
     */
    public HashArrayTest(String name)
    {
        super(name);
    }

    /** Compile all the tests into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("HashArray Tests");

        // Add all the tests defined in this class
        suite.addTestSuite(HashArrayTest.class);

        // Add all the map tests from the MapTest class
        suite.addTest(new MapTest("testSizeOk", new HashArray()));
        suite.addTest(new MapTest("testIsEmpty", new HashArray()));
        suite.addTest(new MapTest("testIsNotEmpty", new HashArray()));
        suite.addTest(new MapTest("testContainsKeyTrue", new HashArray()));
        suite.addTest(new MapTest("testContainsKeyFalse", new HashArray()));
        suite.addTest(new MapTest("testContainsValueTrue", new HashArray()));
        suite.addTest(new MapTest("testContainsValueFalse", new HashArray()));
        suite.addTest(new MapTest("testPutGetOk", new HashArray()));
        suite.addTest(new MapTest("testPutNewReplace", new HashArray()));
        suite.addTest(new MapTest("testPutGetNullForNonExistantKey", new HashArray()));
        suite.addTest(new MapTest("testPutGetNullForNullValue", new HashArray()));
        suite.addTest(new MapTest("testPutGetNullKeyOk", new HashArray()));
        suite.addTest(new MapTest("testRemoveKeyValue", new HashArray()));
        suite.addTest(new MapTest("testRemoveKeyOk", new HashArray()));
        suite.addTest(new MapTest("testRemoveNonExistantKey", new HashArray()));
        suite.addTest(new MapTest("testPutAllOk", new HashArray()));
        suite.addTest(new MapTest("testClearIsEmpty", new HashArray()));
        suite.addTest(new MapTest("testClearRemovesAllKeys", new HashArray()));
        suite.addTest(new MapTest("testClearRemovesAllValues", new HashArray()));
        suite.addTest(new MapTest("testKeysetContainsOnlyKeysFromMap", new HashArray()));
        suite.addTest(new MapTest("testKeysetContainsAllKeys", new HashArray()));

        //suite.addTest(new MapTest("testRemoveFromKeySet", new HashArray()));
        //suite.addTest(new MapTest("testRemoveFromKeySetIterator", new HashArray()));
        suite.addTest(new MapTest("testRemoveAllFromKeySet", new HashArray()));
        suite.addTest(new MapTest("testClearKeySet", new HashArray()));
        suite.addTest(new MapTest("testRetainAllKeySet", new HashArray()));
        suite.addTest(new MapTest("testValueCollectionContainsOnlyValuesFromMap", new HashArray()));
        suite.addTest(new MapTest("testValueCollectionContainsAllValues", new HashArray()));
        suite.addTest(new MapTest("testRemoveFromValueCollection", new HashArray()));
        suite.addTest(new MapTest("testRemoveFromValueCollectionIterator", new HashArray()));
        suite.addTest(new MapTest("testRemoveAllFromValueCollection", new HashArray()));
        suite.addTest(new MapTest("testClearValueCollection", new HashArray()));
        suite.addTest(new MapTest("testRetainAllValueCollection", new HashArray()));
        suite.addTest(new MapTest("testEntrySetContainsOnlyEntriesFromMap", new HashArray()));
        suite.addTest(new MapTest("testEntrySetContainsAllEntries", new HashArray()));
        suite.addTest(new MapTest("testSetValueInEntrySetUpdatesMap", new HashArray()));
        suite.addTest(new MapTest("testEqualsTrue", new HashArray()));
        suite.addTest(new MapTest("testEqualsFalse", new HashArray()));
        suite.addTest(new MapTest("testHashCodeIsSumOfEntries", new HashArray()));

        return suite;
    }

    /** Tests creating a hash array from an existing Map and Collection. */
    public void testCreateFromMapAndCollection() throws Exception
    {
        HashMap map = new HashMap();

        map.put("Zero", new Integer(0));
        map.put("One", new Integer(1));
        map.put("Two", new Integer(2));

        ArrayList data = new ArrayList();

        data.add("Value0");
        data.add("Value1");
        data.add("Value2");

        HashArray hashArray = new HashArray(map, data);

        // Check that the new hash array contains the correct data
        String value0 = (String) hashArray.get("Zero");
        String value1 = (String) hashArray.get(1);
        String value2 = (String) hashArray.get("Two");

        if (!"Value0".equals(value0))
        {
            fail("value0 is incorrect");
        }

        if (!"Value1".equals(value1))
        {
            fail("value1 is incorrect");
        }

        if (!"Value2".equals(value2))
        {
            fail("value2 is incorrect");
        }
    }

    /**
     * Tests that putting a series of new elements into the map on top of a map that already contains those elements
     * will replace all theold elements with the new ones succesfully. As each new element is put in for a second time
     * then it should be placed at the end (insertion order must always be preserved). This test reinserts the original
     * elements in reverse order and then checks that they are in fact held in reverse order.
     */
    public void testPutReplaceOrdering() throws Exception
    {
        HashArray hashArray = new HashArray();

        // Put 0 to 999 in increasing order
        for (int i = 0; i < 1000; i++)
        {
            hashArray.put(new Integer(i), new Integer(i));
        }

        // Replace original ordering with decreasing order
        for (int k = 999; k >= 0; k--)
        {
            hashArray.put(new Integer(k), new Integer(k));
        }

        // Check the replacement ordering is preserved
        Iterator it = hashArray.values().iterator();

        for (int j = 999; j >= 0; j--)
        {
            int nextValue = ((Integer) it.next()).intValue();

            if (j != nextValue)
            {
                fail("output not in same order as input, failed at " + j + " with value " + nextValue);
            }
        }
    }

    /**
     * Tests that removing a key will remove the key and its data and also that other keys will still work correctly (as
     * they should have their indexes adjusted if they are higher than the current key).
     */
    public void testRemoveByKey() throws Exception
    {
        HashArray hashArray = new HashArray();

        hashArray.put("A", "ValueA");
        hashArray.put("B", "ValueB");
        hashArray.put("C", "ValueC");

        String oldValueB = (String) hashArray.remove("B");

        // Check the values of the keys are now correct
        String valueA = (String) hashArray.get("A");
        String valueB = (String) hashArray.get("B");
        String valueC = (String) hashArray.get("C");

        if (!"ValueB".equals(oldValueB))
        {
            fail("oldValueB does not contain the removed value.");
        }

        if (!"ValueA".equals(valueA))
        {
            fail("a corrupted by removing b");
        }

        if (valueB != null)
        {
            fail("valueB not removed correctly");
        }

        if (!"ValueC".equals(valueC))
        {
            fail("c corrupted by removing b");
        }
    }

    /** Tests that the input ordering is preserved. */
    public void testInputOrdering() throws Exception
    {
        HashArray hashArray = new HashArray();

        for (int i = 0; i < 1000; i++)
        {
            hashArray.put(new Integer(i), new Integer(i));
        }

        // Check that input ordering is preserved
        Iterator it = hashArray.values().iterator();

        for (int j = 0; j < 1000; j++)
        {
            int nextValue = ((Integer) it.next()).intValue();

            if (j != nextValue)
            {
                fail("output not in the same order as input, failed at " + j + " with value " + nextValue);
            }
        }
    }

    /** Tests that the key set iterator iterates over the keys in insertion order. */
    public void testKeySetOrdering() throws Exception
    {
        // Create a hash array to test
        HashArray hashArray = new HashArray();

        for (int i = 0; i < 1000; i++)
        {
            hashArray.put(new Integer(i), new Integer(i));
        }

        // Check that key set ordering is preserved with respect to input order
        Iterator it = hashArray.keySet().iterator();

        for (int j = 0; j < 1000; j++)
        {
            int nextKey = ((Integer) it.next()).intValue();

            if (j != nextKey)
            {
                fail("key set not in the same order as input, failed at " + j + " with key " + nextKey);
            }
        }
    }

    /**  */
    protected void setUp()
    {
        // Push a client identifier onto the Nested Diagnostic Context so that log4j will be able to
        // identifiy all loggin output for the unit tests and distinguish it from other logging output
        NDC.push("HashArrayTest");
    }

    /**  */
    protected void tearDown()
    {
        // Clear the nested diagnostic context
        NDC.pop();
    }
}
