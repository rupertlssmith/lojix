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

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Unit tests for any data structure implementing the {@link com.thesett.common.util.maps.Dictionary} interface.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Test size method reports correct size.
 * <tr><td>Test isEmpty reports empty on empty dictionary.
 * <tr><td>Test isEmpty reports not empty on non empty dictionary.
 * <tr><td>Test containsKey reports true for existing key.
 * <tr><td>Test containsKey reports false for non existent key.
 * <tr><td>Test put/get returns the value for a key.
 * <tr><td>Test putting a key replaces existing key of same value.
 * <tr><td>Test put/get returns null for a non-existent key.
 * <tr><td>Test put/get returns null for a key with a null value.
 * <tr><td>Test putting/getting a null key returns the matching value.
 * <tr><td>Test removing a key returns its value.
 * <tr><td>Test removing a key really removes it.
 * <tr><td>Test clearing the dictionary makes it empty.
 * <tr><td>Test clearing the dictionary really removes all its keys.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class DictionaryTestBase<K, V> extends TestCase
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(DictionaryTestBase.class.getName()); */

    /** The dictionary object to test. */
    Dictionary<K, V> testDictionary;

    /** The dictionary to test is assumed by the tests to be empty. */
    public DictionaryTestBase(String testName, Dictionary<K, V> testDictionary)
    {
        super(testName);

        // Keep reference to the dictionary object to test
        this.testDictionary = testDictionary;
    }

    /** Test clearing the dictionary makes it empty */
    public void testClearIsEmpty() throws Exception
    {
        try
        {
            testDictionary.clear();
        }
        catch (UnsupportedOperationException e)
        {
            // Read only dictionarys do not support clear. Return with test passed.
            /*log.fine("Dictionary does not support clear.");*/

            return;
        }

        if (!testDictionary.isEmpty())
        {
            fail("Cleared dictionary is not empty.");
        }
    }

    /** Test clearing the dictionary really removes all its keys */
    public void testClearRemovesAllKeys() throws Exception
    {
        List<K> testKeys = new LinkedList<K>();

        // Put some keys into the dictionary.
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                K testKey = createTestKey();
                testDictionary.put(testKey, createTestValue());
                testKeys.add(testKey);
            }
            catch (UnsupportedOperationException e)
            {
                // Read only dictionaries do not support put. Return with the test passed.
                /*log.fine("dictionary does not support put");*/

                return;
            }
        }

        // Clear the dictionary.
        testDictionary.clear();

        // Check that none of the original keys are now in the dictionary.
        for (K testKey : testKeys)
        {
            if (testDictionary.containsKey(testKey))
            {
                fail("Dictionary contains key " + testKey + " after the dictionary was cleared.");
            }
        }
    }

    /** Test containsKey reports false for non existant key */
    public void testContainsKeyFalse() throws Exception
    {
        if (testDictionary.containsKey(createTestKey()))
        {
            fail("dictionary not containing key 'Test' reports it does contain that key");
        }
    }

    /** Test containsKey reports true for existing key */
    public void testContainsKeyTrue() throws Exception
    {
        K testKey;

        try
        {
            testKey = createTestKey();
            testDictionary.put(testKey, createTestValue());
        }
        catch (UnsupportedOperationException e)
        {
            // Read only dictionarys do not support put. Return with the test passed.
            /*log.fine("dictionary does not support put");*/

            return;
        }

        if (!testDictionary.containsKey(testKey))
        {
            fail("dictionary containing key " + testKey + " reports it does not contain that key");
        }
    }

    /** Test isEmpty reports empty on empty dictionary */
    public void testIsEmpty() throws Exception
    {
        if (!testDictionary.isEmpty())
        {
            fail("empty dictionary reports not empty");
        }
    }

    /** Test isEmpty reports not empty on non empty dictionary */
    public void testIsNotEmpty() throws Exception
    {
        try
        {
            testDictionary.put(createTestKey(), createTestValue());
        }
        catch (UnsupportedOperationException e)
        {
            // Read only dictionarys do not support put. Return with the test passed.
            /*log.fine("dictionary does not support put");*/

            return;
        }

        if (testDictionary.isEmpty())
        {
            fail("non-empty dictionary reports empty");
        }
    }

    /** Test put/get returns null for a non-existant key */
    public void testPutGetNullForNonExistantKey() throws Exception
    {
        if (testDictionary.get(createTestKey()) != null)
        {
            fail("empty dictionary did not return null value for key 'Test'");
        }
    }

    /** Test put/get returns null for a key with a null value */
    public void testPutGetNullForNullValue() throws Exception
    {
        K testKey;

        try
        {
            testKey = createTestKey();
            testDictionary.put(testKey, null);
        }
        catch (UnsupportedOperationException e)
        {
            // Read only dictionarys do not support put. Return with the test passed.
            /*log.fine("dictionary does not support put");*/

            return;
        }
        catch (NullPointerException e)
        {
            // Some dictionarys do not support null values. Return with test passed.
            /*log.fine("dictionary does not support null values");*/

            return;
        }

        if (testDictionary.get(testKey) != null)
        {
            fail("dictionary did not return null value after one was inserted");
        }
    }

    /** Test putting/getting a null key returns the matching value */
    public void testPutGetNullKeyOk() throws Exception
    {
        V testValue;

        try
        {
            testValue = createTestValue();
            testDictionary.put(null, testValue);
        }
        catch (UnsupportedOperationException e)
        {
            // Read only dictionarys do not support put. Return with the test passed.
            /*log.fine("dictionary does not support put");*/

            return;
        }
        catch (NullPointerException e)
        {
            // Some dictionarys do not support null kays. Return with test passed.
            /*log.fine("dictionary does not support null keys");*/

            return;
        }

        if (!testValue.equals(testDictionary.get(null)))
        {
            fail("dictionary did not return value 'Value' stored under null key");
        }
    }

    /** Test put/get returns the value for a key */
    public void testPutGetOk() throws Exception
    {
        K testKey;
        V testValue;

        try
        {
            testKey = createTestKey();
            testValue = createTestValue();
            testDictionary.put(testKey, testValue);
        }
        catch (UnsupportedOperationException e)
        {
            // Read only dictionarys do not support put. Return with the test passed.
            /*log.fine("dictionary does not support put");*/

            return;
        }

        // Test that the new element was inserted correctly
        V value = testDictionary.get(testKey);

        assertEquals("Did not get back inserted value.", testValue, value);
    }

    /** Test putting a key replaces existing key of same value */
    public void testPutNewReplace() throws Exception
    {
        K testKey;
        V testValue1;

        try
        {
            testKey = createTestKey();
            testValue1 = createTestValue();
            testDictionary.put(testKey, testValue1);
        }
        catch (UnsupportedOperationException e)
        {
            // Read only dictionarys do not support put. Return with the test passed.
            /*log.fine("dictionary does not support put");*/

            return;
        }

        V testValue2 = createTestValue();

        V value1 = testDictionary.put(testKey, testValue2);

        // Check that the new element replaced the old one
        V value2 = testDictionary.get(testKey);

        if (!testValue1.equals(value1))
        {
            fail("did not get back replaced value.");
        }

        if (!testValue2.equals(value2))
        {
            fail("value2 did not insert correctly.");
        }
    }

    /** Test removing a key really removes it */
    public void testRemoveKeyOk() throws Exception
    {
        K testKey;
        V testValue;

        try
        {
            testKey = createTestKey();
            testValue = createTestValue();
            testDictionary.put(testKey, testValue);
        }
        catch (UnsupportedOperationException e)
        {
            // Read only dictionarys do not support put. Return with test passed.
            /*log.fine("Dictionary does not support put.");*/

            return;
        }

        testDictionary.remove(testKey);

        if (testDictionary.get(testKey) != null)
        {
            fail("Value not null after it was removed.");
        }
    }

    /** Test removing a key returns its value */
    public void testRemoveKeyValue() throws Exception
    {
        K testKey;
        V testValue;

        try
        {
            testKey = createTestKey();
            testValue = createTestValue();
            testDictionary.put(testKey, testValue);
        }
        catch (UnsupportedOperationException e)
        {
            // Read only dictionarys do not support put. Return with test passed.
            /*log.fine("Dictionary does not support put.");*/

            return;
        }

        V removedValue = testDictionary.remove(testKey);

        assertEquals("Removing did not return previously set value.", testValue, removedValue);
    }

    /** Test size method reports correct size */
    public void testSizeOk() throws Exception
    {
        assertEquals("Empty testDictionary does not have size 0.", 0, testDictionary.size());

        try
        {
            testDictionary.put(createTestKey(), createTestValue());
        }
        catch (UnsupportedOperationException e)
        {
            // Read only dictionarys do not support put. Return with the test passed.
            /*log.fine("dictionary does not support put");*/

            return;
        }

        if (testDictionary.size() != 1)
        {
            fail("one element testDictionary does not have size 1");
        }

        for (int i = 2; i < 1000; i++)
        {
            testDictionary.put(createTestKey(), createTestValue());

            if (testDictionary.size() != i)
            {
                fail("Dictionary with size " + i + " reports size as " + testDictionary.size());
            }
        }
    }

    /**
     * Concrete test implementations should override this to supply test keys. All generated keys should be different.
     *
     * @return A unique test key.
     */
    protected abstract K createTestKey();

    /**
     * Concrete test implementations should override this to supply test values. All generated values do not have to be
     * different and can be random.
     *
     * @return A test value.
     */
    protected abstract V createTestValue();
}
