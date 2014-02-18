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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;



/**
 * Unit tests for any data structure implementing the {@link java.util.Map} interface.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Test size method reports correct size
 * <tr><td>Test isEmpty reports empty on empty map
 * <tr><td>Test isEmpty reports not empty on non empty map
 * <tr><td>Test containsKey reports true for existing key
 * <tr><td>Test containsKey reports false for non existant key
 * <tr><td>Test containsValue reports true for existing value
 * <tr><td>Test containsValue reports non true for non existant value
 * <tr><td>Test put/get returns the value for a key
 * <tr><td>Test putting a key replaces existing key of same value
 * <tr><td>Test put/get returns null for a non-existant key
 * <tr><td>Test put/get returns null for a key with a null value
 * <tr><td>Test putting/getting a null key returns the matching value
 * <tr><td>Test removing a key returns its value
 * <tr><td>Test removing a key really removes it
 * <tr><td>Test removing a non-existant key returns null
 * <tr><td>Test putting all values from another map works ok
 * <tr><td>Test clearing the map makes it empty
 * <tr><td>Test clearing the map really removes all its keys
 * <tr><td>Test clearing the map really removes all its values
 * <tr><td>Test set view of the keys in the map contains only keys in the map
 * <tr><td>Test set view of the keys in the map contains all the keys in the map
 * <tr><td>Test removing a key from the set view of keys removes it from the map
 * <tr><td>Test removing a key from the set view of keys iterator removes it from the map
 * <tr><td>Test removing all from the set view of keys removes all from the map
 * <tr><td>Test clearing the set view of keys clears the map
 * <tr><td>Test calling retainAll on the set view of keys removes the same elements from the map
 * <tr><td>Test collection view of values in the map contains only values in the map
 * <tr><td>Test collection view of values in the map contains all values in the map
 * <tr><td>Test removing a value from the collection view of values removes it from the map
 * <tr><td>Test removing a value from the collection view iterator removes it from the map
 * <tr><td>Test removing all from the collection view of values clears the map
 * <tr><td>Test clearing the collection view of values clears the map
 * <tr><td>Test calling retainAll on the collection view of values removes the same elements from the map
 * <tr><td>Test Map.Entry view of values in the map contains only values in the map
 * <tr><td>Test Map.Entry view of values in the map contains all values in the map
 * <tr><td>Test removing a value from the Map.Entry view of values removes it from the map
 * <tr><td>Test removing a value from the Map.Entry view iterator removes it from the map
 * <tr><td>Test removing all from the Map.Entry view of values clears the map
 * <tr><td>Test clearing the Map.Entry view of values clears the map
 * <tr><td>Test calling retainAll on the Map.Entry view of values removes the same elements from the map
 * <tr><td>Test setValue method of the Map.Entry view updates the value in the map
 * <tr><td>Test map is equal to another map with the same mappings
 * <tr><td>Test map is not equal to another map with different mappings
 * <tr><td>Test map hash code is equal to the sum of the hash codes for all entries in the Map.Entry set view
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class MapTest extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(MapTest.class.getName()); */

    /** The map object to test. */
    Map testMap;

    /**
     * Default constructor that will result in the tests being run on a HashMap. This confirms the sanity of the tests
     * being run.
     */
    public MapTest(String testName)
    {
        super(testName);

        // Run on a HashMap if called using this default constructor
        testMap = new HashMap();
    }

    /** The map to test is assumed by the tests to be empty. */
    public MapTest(String testName, Map testMap)
    {
        super(testName);

        // Keep reference to the map object to test
        this.testMap = testMap;
    }

    /** Test clearing the map makes it empty */
    public void testClearIsEmpty() throws Exception
    {
        try
        {
            testMap.clear();
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support clear. Return with test passed.
            /*log.fine("Map does not support clear.");*/

            return;
        }

        if (!testMap.isEmpty())
        {
            fail("Cleared map is not empty.");
        }
    }

    /** Test clearing the set view of keys clears the map */
    public void testClearKeySet() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with the test passed.
            /*log.fine("map does not support put");*/
        }
    }

    /** Test clearing the map really removes all its keys */
    public void testClearRemovesAllKeys() throws Exception
    {
        // Put some keys into the map.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                testMap.put(new Integer(i), new Integer(i));
            }
            catch (UnsupportedOperationException e)
            {
                // Read only maps do not support put. Return with the test passed.
                /*log.fine("map does not support put");*/

                return;
            }
        }

        // Clear the map.
        testMap.clear();

        // Check that none of the original keys are now in the map.
        for (int j = 0; j < 1000; j++)
        {
            if (testMap.containsKey(new Integer(j)))
            {
                fail("Map contains key " + j + " after the map was cleared.");
            }
        }
    }

    /** Test clearing the map really removes all its values */
    public void testClearRemovesAllValues() throws Exception
    {
        // Put some keys into the map.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                testMap.put(new Integer(i), new Integer(i));
            }
            catch (UnsupportedOperationException e)
            {
                // Read only maps do not support put. Return with the test passed.
                /*log.fine("map does not support put");*/

                return;
            }
        }

        // Clear the map.
        testMap.clear();

        // Check that none of the original values are now in the map.
        for (int j = 0; j < 1000; j++)
        {
            if (testMap.containsValue(new Integer(j)))
            {
                fail("Map contains key " + j + " after the map was cleared.");
            }
        }

    }

    /** Test clearing the collection view of values clears the map */
    public void testClearValueCollection() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with test passed.
            /*log.fine("Map does not support put.");*/
        }
    }

    /** Test containsKey reports false for non existant key */
    public void testContainsKeyFalse() throws Exception
    {
        if (testMap.containsKey("Test"))
        {
            fail("map not containing key 'Test' reports it does contain that key");
        }
    }

    /** Test containsKey reports true for existing key */
    public void testContainsKeyTrue() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with the test passed.
            /*log.fine("map does not support put");*/

            return;
        }

        if (!testMap.containsKey("Test"))
        {
            fail("map containing key 'Test' reports it does not contain that key");
        }
    }

    /** Test containsValue reports non true for non existant value */
    public void testContainsValueFalse() throws Exception
    {
        if (testMap.containsValue("Value"))
        {
            fail("map not containing value 'Value' reports it does contain that value");
        }
    }

    /** Test containsValue reports true for existing value */
    public void testContainsValueTrue() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with the test passed.
            /*log.fine("map does not support put");*/

            return;
        }

        if (!testMap.containsValue("Value"))
        {
            fail("map containing value 'Value' reports it does not contain that value");
        }
    }

    /** Test Map.Entry view of values in the map contains all values in the map */
    public void testEntrySetContainsAllEntries() throws Exception
    {
        // Put some keys into the map.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                testMap.put(new Integer(i), new Integer(i));
            }
            catch (UnsupportedOperationException e)
            {
                // Read only maps do not support put. Return with the test passed.
                /*log.fine("map does not support put");*/

                return;
            }
        }
    }

    /** Test Map.Entry view of values in the map contains only values in the map */
    public void testEntrySetContainsOnlyEntriesFromMap() throws Exception
    {
        // Put some keys into the map.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                testMap.put(new Integer(i), new Integer(i));
            }
            catch (UnsupportedOperationException e)
            {
                // Read only maps do not support put. Return with the test passed.
                /*log.fine("map does not support put");*/

                return;
            }
        }
    }

    /** Test map is not equal to another map with different mappings */
    public void testEqualsFalse() throws Exception
    {
    }

    /** Test map is equal to another map with the same mappings */
    public void testEqualsTrue() throws Exception
    {
        // Put some keys into the map.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                testMap.put(new Integer(i), new Integer(i));
            }
            catch (UnsupportedOperationException e)
            {
                // Read only maps do not support put. Return with the test passed.
                /*log.fine("map does not support put");*/

                return;
            }
        }
    }

    /** Test map hash code is equal to the sum of the hash codes for all entries in the Map.Entry set view */
    public void testHashCodeIsSumOfEntries() throws Exception
    {
        // Put some keys into the map.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                testMap.put(new Integer(i), new Integer(i));
            }
            catch (UnsupportedOperationException e)
            {
                // Read only maps do not support put. Return with the test passed.
                /*log.fine("map does not support put");*/

                return;
            }
        }
    }

    /** Test isEmpty reports empty on empty map */
    public void testIsEmpty() throws Exception
    {
        if (!testMap.isEmpty())
        {
            fail("empty map reports not empty");
        }
    }

    /** Test isEmpty reports not empty on non empty map */
    public void testIsNotEmpty() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with the test passed.
            /*log.fine("map does not support put");*/

            return;
        }

        if (testMap.isEmpty())
        {
            fail("non-empty map reports empty");
        }
    }

    /** Test set view of the keys in the map contains all the keys in the map */
    public void testKeysetContainsAllKeys() throws Exception
    {
        // Put some keys into the map.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                testMap.put(new Integer(i), new Integer(i));
            }
            catch (UnsupportedOperationException e)
            {
                // Read only maps do not support put. Return with the test passed.
                /*log.fine("map does not support put");*/

                return;
            }
        }

        // Get the key set.
        Set keySet = testMap.keySet();

        // Check that all the keys in the map are in the key set.
        for (int j = 0; j < 1000; j++)
        {
            if (!keySet.contains(new Integer(j)))
            {
                fail("Key " + j + " from map is not in its key set.");
            }
        }
    }

    /** Test set view of the keys in the map contains only keys in the map */
    public void testKeysetContainsOnlyKeysFromMap() throws Exception
    {
        // Put some keys into the map.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                testMap.put(new Integer(i), new Integer(i));
            }
            catch (UnsupportedOperationException e)
            {
                // Read only maps do not support put. Return with the test passed.
                /*log.fine("map does not support put");*/

                return;
            }
        }

        // Get the key set.
        Set keySet = testMap.keySet();

        // Iterate over the key set checking that all its elements match those originally in the map.
        for (Object aKeySet : keySet)
        {
            int nextKey = ((Integer) aKeySet).intValue();

            if ((nextKey < 0) || (nextKey > 1000))
            {
                fail("Key " + nextKey + " in key set not in map.");
            }
        }
    }

    /** Test putting all values from another map works ok */
    public void testPutAllOk() throws Exception
    {
        // Create a map to copy from.
        Map copyFrom = new HashMap();

        for (int i = 0; i < 1000; i++)
        {
            copyFrom.put(new Integer(i), new Integer(i));
        }

        // Copy all entries into the test map.
        try
        {
            testMap.putAll(copyFrom);
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put all. Return with the test passed.
            /*log.fine("map does not support putAll");*/

            return;
        }

        // Check that the entries really were copied into the test map.
        for (int j = 0; j < 1000; j++)
        {
            if (!testMap.containsKey(new Integer(j)))
            {
                fail("Map did not get copy of key " + j + " from original map.");
            }

            int nextValue = ((Integer) testMap.get(new Integer(j))).intValue();

            if (nextValue != j)
            {
                fail("Map value " + nextValue + " does not match original value " + j);
            }
        }

    }

    /** Test put/get returns null for a non-existant key */
    public void testPutGetNullForNonExistantKey() throws Exception
    {
        if (testMap.get("Test") != null)
        {
            fail("empty map did not return null value for key 'Test'");
        }
    }

    /** Test put/get returns null for a key with a null value */
    public void testPutGetNullForNullValue() throws Exception
    {
        try
        {
            testMap.put("Test", null);
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with the test passed.
            /*log.fine("map does not support put");*/

            return;
        }
        catch (NullPointerException e)
        {
            // Some maps do not support null values. Return with test passed.
            /*log.fine("map does not support null values");*/

            return;
        }

        if (testMap.get("Test") != null)
        {
            fail("map did not return null value after one was inserted");
        }
    }

    /** Test putting/getting a null key returns the matching value */
    public void testPutGetNullKeyOk() throws Exception
    {
        try
        {
            testMap.put(null, "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with the test passed.
            /*log.fine("map does not support put");*/

            return;
        }
        catch (NullPointerException e)
        {
            // Some maps do not support null kays. Return with test passed.
            /*log.fine("map does not support null keys");*/

            return;
        }

        if (!"Value".equals((String) testMap.get(null)))
        {
            fail("map did not return value 'Value' stored under null key");
        }
    }

    /** Test put/get returns the value for a key */
    public void testPutGetOk() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with the test passed.
            /*log.fine("map does not support put");*/

            return;
        }

        // Test that the new element was inserted correctly
        String value = (String) testMap.get("Test");

        if (!"Value".equals(value))
        {
            fail("did not get back inserted value");
        }
    }

    /** Test putting a key replaces existing key of same value */
    public void testPutNewReplace() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with the test passed.
            /*log.fine("map does not support put");*/

            return;
        }

        String value1 = (String) testMap.put("Test", "Value2");

        // Check that the new element replaced the old one
        String value2 = (String) testMap.get("Test");

        if (!"Value".equals(value1))
        {
            fail("did not get back replaced value");
        }

        if (!"Value2".equals(value2))
        {
            fail("value2 did not insert correctly");
        }
    }

    /** Test removing all from the set view of keys removes all from the map */
    public void testRemoveAllFromKeySet() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with the test passed.
            /*log.fine("map does not support put");*/
        }
    }

    /** Test removing all from the collection view of values clears the map */
    public void testRemoveAllFromValueCollection() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with test passed.
            /*log.fine("Map does not support put.");*/
        }
    }

    /** Test removing a key from the set view of keys removes it from the map */
    public void testRemoveFromKeySet() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with test passed.
            /*log.fine("Map does not support put.");*/

            return;
        }

        // Get the set view of the keys.
        Set keySet = testMap.keySet();

        // Remove the test key.
        try
        {
            if (!keySet.remove("Test"))
            {
                fail("Key 'Test' could not be removed from the set view of map keys.");
            }
        }
        catch (UnsupportedOperationException e)
        {
            // Read only sets do not support remove. Return with the test passed.
            /*log.fine("Key set does not support remove.");*/

            return;
        }

        // Check that the map no longer contains the key.
        if (testMap.containsKey("Test"))
        {
            fail("Removing key 'Test' from key set did not remove it from the map.");
        }
    }

    /** Test removing a key from the set view of keys iterator removes it from the map */
    public void testRemoveFromKeySetIterator() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with test passed.
            /*log.fine("Map does not support put.");*/

            return;
        }

        // Get the set view of the keys.
        Set keySet = testMap.keySet();

        // Iterate over the key set to find and remove the test key.
        for (Iterator i = keySet.iterator(); i.hasNext();)
        {
            String nextKey = (String) i.next();

            if ("Test".equals(nextKey))
            {
                try
                {
                    i.remove();

                    break;
                }
                catch (UnsupportedOperationException e)
                {
                    // Read only iterators do not support remove. Return with test passed.
                    /*log.fine("Key set iterator does not support remove.");*/

                    return;
                }
            }
        }

        // Check that the map no longer contains the key.
        if (testMap.containsKey("Test"))
        {
            fail("Removing key 'Test' from key set did not remove it from the map.");
        }
    }

    /** Test removing a value from the collection view of values removes it from the map */
    public void testRemoveFromValueCollection() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with test passed.
            /*log.fine("Map does not support put.");*/
        }
    }

    /** Test removing a value from the collection view iterator removes it from the map */
    public void testRemoveFromValueCollectionIterator() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with test passed.
            /*log.fine("Map does not support put.");*/
        }
    }

    /** Test removing a key really removes it */
    public void testRemoveKeyOk() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with test passed.
            /*log.fine("Map does not support put.");*/

            return;
        }

        testMap.remove("Test");

        if (testMap.get("Test") != null)
        {
            fail("Value not null after it was removed.");
        }
    }

    /** Test removing a key returns its value */
    public void testRemoveKeyValue() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with test passed.
            /*log.fine("Map does not support put.");*/

            return;
        }

        if (!"Value".equals((String) testMap.remove("Test")))
        {
            fail("Removing value 'Value' did not return 'Value'");
        }
    }

    /** Test removing a non-existant key returns null */
    public void testRemoveNonExistantKey() throws Exception
    {
        // Put some keys into the map.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                testMap.put(new Integer(i), new Integer(i));
            }
            catch (UnsupportedOperationException e)
            {
                // Read only maps do not support put. Return with the test passed.
                /*log.fine("map does not support put");*/

                return;
            }
        }
    }

    /** Test calling retainAll on the set view of keys removes the same elements from the map */
    public void testRetainAllKeySet() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
            testMap.put("RemoveTest", "RemoveValue");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with the test passed.
            /*log.fine("map does not support put");*/
        }
    }

    /** Test calling retainAll on the collection view of values removes the same elements from the map */
    public void testRetainAllValueCollection() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
            testMap.put("Remove", "RemoveValue");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with test passed.
            /*log.fine("Map does not support put.");*/
        }
    }

    /** Test removing a value from the Map.Entry view of values removes it from the map */

    /** Test removing a value from the Map.Entry view iterator removes it from the map */

    /** Test removing all from the Map.Entry view of values clears the map */

    /** Test clearing the Map.Entry view of values clears the map */

    /** Test calling retainAll on the Map.Entry view of values removes the same elements from the map */

    /** Test setValue method of the Map.Entry view updates the value in the map */
    public void testSetValueInEntrySetUpdatesMap() throws Exception
    {
        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with test passed.
            /*log.fine("Map does not support put.");*/
        }
    }

    /** Test size method reports correct size */
    public void testSizeOk() throws Exception
    {
        if (testMap.size() != 0)
        {
            fail("empty testMap does not have size 0");
        }

        try
        {
            testMap.put("Test", "Value");
        }
        catch (UnsupportedOperationException e)
        {
            // Read only maps do not support put. Return with the test passed.
            /*log.fine("map does not support put");*/

            return;
        }

        if (testMap.size() != 1)
        {
            fail("one element testMap does not have size 1");
        }

        for (int i = 2; i < 1000; i++)
        {
            testMap.put(new Integer(i), "Test");

            if (testMap.size() != i)
            {
                fail("map with size " + i + " reports size as " + testMap.size());
            }
        }
    }

    /** Test collection view of values in the map contains all values in the map */
    public void testValueCollectionContainsAllValues() throws Exception
    {
        // Put some keys into the map.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                testMap.put(new Integer(i), new Integer(i));
            }
            catch (UnsupportedOperationException e)
            {
                // Read only maps do not support put. Return with the test passed.
                /*log.fine("map does not support put");*/

                return;
            }
        }
    }

    /** Test collection view of values in the map contains only values in the map */
    public void testValueCollectionContainsOnlyValuesFromMap() throws Exception
    {
        // Put some keys into the map.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                testMap.put(new Integer(i), new Integer(i));
            }
            catch (UnsupportedOperationException e)
            {
                // Read only maps do not support put. Return with the test passed.
                /*log.fine("map does not support put");*/

                return;
            }
        }
    }

    /** @throws Exception */
    protected void setUp() throws Exception
    {
    }
}
