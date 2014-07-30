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
package com.thesett.common.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

/**
 * Unit tests for the {@link PagedList} data structure.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check that the get method returns the correct page.
 * <tr><td>Check that bound checkings throws exceptions for index out of bounds.
 * <tr><td>Check that the size method correctly reports the number of pages.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PagedListTest extends TestCase
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(PagedListTest.class.getName()); */

    /** Used to hold the paged list under test. */
    PagedList pagedList;

    /**
     * Creates a new paged list test.
     *
     * @param name The name of the test method being run.
     */
    public PagedListTest(String name)
    {
        super(name);
    }

    /** Compile all the tests into a test suite. */
    public static Test suite()
    {
        // Build a new test suite.
        TestSuite suite = new TestSuite("PagedList Tests");

        // Add all the tests defined in this class.
        suite.addTestSuite(PagedListTest.class);

        return suite;
    }

    /** Check that the get method returns the correct page. */
    public void testGet()
    {
        String errorMessage = "";

        // Try extracting the second page.
        List test = (List) pagedList.get(1);

        // Check that it is a page of size 3.
        if (test.size() != 3)
        {
            errorMessage += "Size is not 3, it is: " + test.size() + "\n";
        }

        // Check that it starts at the right list element.
        if (!"Four".equals(test.get(0)))
        {
            errorMessage += "Wrong page, should be Four but got: " + test.get(0) + "\n";
        }

        // Try extracting the fourth page which should only contain one element.
        test = (List) pagedList.get(3);

        // Check that it is a page of size 1.
        if (test.size() != 1)
        {
            errorMessage += "Size is not 1, it is: " + test.size() + "\n";
        }

        // Check that it starts at the last element.
        if (!"Ten".equals(test.get(0)))
        {
            errorMessage += "Wrong page, should be Ten but got: " + test.get(0) + "\n";
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that bound checkings throws exceptions for index out of bounds. */
    public void testGetFailBadIndex()
    {
        // Try extracting the fifth page.
        try
        {
            pagedList.get(4);
        }
        catch (IndexOutOfBoundsException e)
        {
            return;
        }

        fail("Did not throw IndexOutOfBoundsException.");
    }

    /** Check that the size method correctly reports the number of pages and not the number of elements. */
    public void testSize()
    {
        // There should be four pages.
        if (pagedList.size() != 4)
        {
            fail("Size is not 4");
        }
    }

    /** Creates a sample paged list to run tests against. */
    protected void setUp()
    {
        // Push a client identifier onto the Nested Diagnostic Context so that log4j will be able to
        // identifiy all logging output for the unit tests and distinguish it from other logging output.
        NDC.push("HashArrayTest");

        // Build a list to page.
        List list = new ArrayList();
        list.add("One");
        list.add("Two");
        list.add("Three");
        list.add("Four");
        list.add("Five");
        list.add("Six");
        list.add("Seven");
        list.add("Eight");
        list.add("Nine");
        list.add("Ten");

        // Build a paged list around this list of numbers, dividing it into pages of three.
        pagedList = new PagedList(list, 3);
    }

    /** Cleans up after running each test method. Removes the nested diagnostic context. */
    protected void tearDown()
    {
        // Clear the nested diagnostic context
        NDC.pop();
    }
}
