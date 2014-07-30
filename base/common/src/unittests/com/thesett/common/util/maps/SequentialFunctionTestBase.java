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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.thesett.common.util.Function;

/**
 * Tests that sequential mapping functions always produce sequences, and correctly map keys onto them.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check that the function produces sequential values.
 * <tr><td>Check that the function does not forget values.
 * <tr><td>Check that input values can be removed from the function.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class SequentialFunctionTestBase<K> extends TestCase
{
    /** Defines the default size of tests. */
    protected static final int TEST_SIZE = 1000;

    /** Holds the function under test. */
    protected Function<K, Integer> testFunction;

    /**
     * Creates a test with the specified name, over the given sequential function.
     *
     * @param name         The name of the test.
     * @param testFunction The function to test.
     */
    public SequentialFunctionTestBase(String name, SequentialFunction<K> testFunction)
    {
        super(name);

        this.testFunction = testFunction;
    }

    /** Check that the function produces sequential values. */
    public void testSequential()
    {
        int last = testFunction.apply(createTestValue());

        for (int i = 0; i < TEST_SIZE; i++)
        {
            int next = testFunction.apply(createTestValue());

            assertEquals("Function is not sequential. 'next' must be equals to 'last' + 1", next, last + 1);
            last = next;
        }
    }

    /** Check that the function produces sequential values. */
    public void testComplete()
    {
        String errorMessage = "";

        Map<K, Integer> values = new HashMap<K, Integer>();

        for (int i = 0; i < TEST_SIZE; i++)
        {
            K value = createTestValue();
            int seq = testFunction.apply(value);
            values.put(value, seq);
        }

        for (Map.Entry<K, Integer> entry : values.entrySet())
        {
            K value = entry.getKey();
            int expectedSeq = entry.getValue();

            int seq = testFunction.apply(value);

            if (expectedSeq != seq)
            {
                errorMessage +=
                    "Function is not reproducable, expected sequence number (" + expectedSeq +
                    ") not recovered, got (" + seq + ") instead.\n";
            }
        }

        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that input values can be removed from the function. */
    public void testRemove()
    {
        String errorMessage = "";

        Map<K, Integer> values = new HashMap<K, Integer>();

        for (int i = 0; i < TEST_SIZE; i++)
        {
            K value = createTestValue();
            int seq = testFunction.apply(value);
            values.put(value, seq);
        }

        // Remove all odd values from the function.
        for (Map.Entry<K, Integer> entry : values.entrySet())
        {
            int seq = entry.getValue();
            K key = entry.getKey();

            if ((seq % 2) == 1)
            {
                ((SequentialCuckooFunction<TestObject>) testFunction).remove(key);
            }
        }

        // Check all even values are in the function.
        // Check that all odd values behave as if never seen by the function.
        for (Map.Entry<K, Integer> entry : values.entrySet())
        {
            K value = entry.getKey();
            int expectedSeq = entry.getValue();

            int seq = testFunction.apply(value);

            if (((expectedSeq % 2) == 0) && (expectedSeq != seq))
            {
                errorMessage +=
                    "Function is not reproducable, expected sequence number (" + expectedSeq +
                    ") not recovered, got (" + seq + ") instead.\n";
            }
            else if (((expectedSeq % 2) == 1) && (seq < TEST_SIZE))
            {
                errorMessage += "Function remembered " + expectedSeq + " after it was removed.\n";
            }
        }

        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /**
     * Concrete test implementations should override this to supply test values. All generated values should be
     * different.
     *
     * @return A unique test value.
     */
    protected abstract K createTestValue();
}
