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

import com.thesett.common.util.maps.SequentialCuckooFunction;

/**
 * SequentialCuckooFunctionTest test the {@link SequentialCuckooFunction} over object keys.
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
public class SequentialCuckooFunctionTest extends SequentialFunctionTestBase<TestObject>
{
    /**
     * Creates a test with the specified name.
     *
     * @param name The name of the test.
     */
    public SequentialCuckooFunctionTest(String name)
    {
        super(name, new SequentialCuckooFunction<TestObject>());
    }

    /**
     * Creates new objects as test values to insert into the function.
     *
     * @return New objects as test values to insert into the function.
     */
    protected TestObject createTestValue()
    {
        return new TestObject();
    }

    /** Resets the sequence function. */
    protected void setUp() throws Exception
    {
        super.setUp();

        ((SequentialCuckooFunction<TestObject>) testFunction).clear();
    }
}

/**
 * Defines a simple test object to test insertion into a sequential function.
 */
class TestObject
{
    /** Generates unique ids for the test objects. */
    private static int nextId = 0;

    /** Holds a unique id for the test object. */
    private final int id = nextId++;

    /**
     * Pretty prints the test objects id for debugging purposes.
     *
     * @return The test objects id for debugging purposes.
     */
    public String toString()
    {
        return "" + id;
    }

    /** {@inheritDoc} */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        TestObject that = (TestObject) o;

        if (id != that.id)
        {
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    public int hashCode()
    {
        return id;
    }
}
