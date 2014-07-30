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
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

/**
 * SimpleTreeTest is a pure unit test class for the {@link SimpleTree} class.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check pre-order iteration ok.
 * <tr><td>Check in-order iteration ok.
 * <tr><td>Check post-order iteration ok.
 * <tr><td>Check iterator only returns available elements.
 * <tr><td>Check remove on iterator disabled.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SimpleTreeTest extends TestCase
{
    /** Used for debugging. */
    /* private static final Logger log = Logger.getLogger(SimpleTreeTest.class.getName()); */

    /** Used to hold the test tree over strings. */
    SimpleTree<String> testTree;

    /**
     * This default constructor runs the tests on a {@link SimpleTree}. This checks the sanity of the tests that this
     * class provides.
     */
    public SimpleTreeTest(String testName)
    {
        super(testName);

        testTree = new SimpleTree<String>();
    }

    /** Compile all the tests for the default test implementation of a tree into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("SimpleTree Tests");

        // Add all the tests from the TreeTestBase class.
        Collection<SimpleTree<String>> children = new ArrayList<SimpleTree<String>>();

        children.add(new SimpleTree<String>("Child One"));

        suite.addTest(new TreeTestBase("testLeafReportsIsLeaf", new SimpleTree<String>()));
        suite.addTest(new TreeTestBase("testNodeReportsIsNoLeaf", new SimpleTree<String>()));
        suite.addTest(new TreeTestBase("testNullParentForRoot", new SimpleTree<String>()));
        suite.addTest(new TreeTestBase("testParentOkNonRoot", new SimpleTree<String>()));
        suite.addTest(new TreeTestBase("testElementOk", new SimpleTree<String>()));
        suite.addTest(new TreeTestBase("testAddChildOk", new SimpleTree<String>()));
        suite.addTest(new TreeTestBase("testAddChildMakesNode", new SimpleTree<String>()));
        suite.addTest(new TreeTestBase("testChildIteratorOk", new SimpleTree<String>()));
        suite.addTest(new TreeTestBase("testClearChildrenOk", new SimpleTree<String>("Test")));
        suite.addTest(new TreeTestBase("testClearChildrenMakesLeaf", new SimpleTree<String>("Test", children)));

        // Add all the tests defined in this class (using the default constructor).
        suite.addTestSuite(SimpleTreeTest.class);

        return suite;
    }

    /** Check pre-order iteration ok. */
    public void testPreOrderIteration() throws Exception
    {
        String errorMessage = "";

        // Create two children and set the data element of the tree.
        testTree.addChild(new SimpleTree<String>("firstChild"));
        testTree.addChild(new SimpleTree<String>("secondChild"));
        testTree.setElement("element");

        // Check the iterator gives back the data element first and then the children. The iterator should give back
        // three elements in total and the ordering of the children is not specified.
        int elementCount = 0;
        boolean firstChildSeen = false;
        boolean secondChildSeen = false;

        for (Iterator<SimpleTree<String>> i = testTree.iterator(Tree.IterationOrder.PreOrder); i.hasNext();)
        {
            SimpleTree<String> nextSubTree = i.next();
            String nextElement = nextSubTree.getElement();

            // Keep count of how many elements the iterator has produced.
            elementCount++;

            // Check if it is the first element that is matches the element value for the root node.
            if ((elementCount == 1) && !"element".equals(nextElement))
            {
                errorMessage += "The first element must match the root element \"element\".\n";
            }

            // Check it matches one of the child element values.
            if ("firstChild".equals(nextElement))
            {
                // Flag the element as having been seen.
                firstChildSeen = true;
            }
            else if ("secondChild".equals(nextElement))
            {
                // Flag the element as having been seen.
                secondChildSeen = true;
            }
        }

        // Check that both child elements were seen.
        if (!firstChildSeen)
        {
            errorMessage += "The first child element was not seen.\n";
        }

        if (!secondChildSeen)
        {
            errorMessage += "The second child element was not seen.\n";
        }

        // Check that the total element count was 3.
        if (elementCount != 3)
        {
            errorMessage += "Should have seen 3 elements but " + elementCount + " were seen.\n";
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check in-order iteration ok. */
    public void testInOrderIteration() throws Exception
    {
        String errorMessage = "";

        // Create two children and set the data element of the tree.
        testTree.addChild(new SimpleTree<String>("firstChild"));
        testTree.addChild(new SimpleTree<String>("secondChild"));
        testTree.setElement("element");

        // Check the iterator gives back the data element in the middle of the children. The iterator should give back
        // three elements in total and the ordering of the children is not specified.
        int elementCount = 0;
        boolean firstChildSeen = false;
        boolean secondChildSeen = false;

        for (Iterator<SimpleTree<String>> i = testTree.iterator(Tree.IterationOrder.InOrder); i.hasNext();)
        {
            SimpleTree<String> nextSubTree = i.next();
            String nextElement = nextSubTree.getElement();

            // Keep count of how many elements the iterator has produced.
            elementCount++;

            // Check if it is the middle element that it matches the element value for the root node.
            if ((elementCount == 2) && !"element".equals(nextElement))
            {
                errorMessage += "The middle element must match the root element \"element\".\n";
            }

            // Check it matches one of the child element values.
            if ("firstChild".equals(nextElement))
            {
                // Flag the element as having been seen.
                firstChildSeen = true;
            }
            else if ("secondChild".equals(nextElement))
            {
                // Flag the element as having been seen.
                secondChildSeen = true;
            }
        }

        // Check that both child elements were seen.
        if (!firstChildSeen)
        {
            errorMessage += "The first child element was not seen.\n";
        }

        if (!secondChildSeen)
        {
            errorMessage += "The second child element was not seen.\n";
        }

        // Check that the total element count was 3.
        if (elementCount != 3)
        {
            errorMessage += "Should have seen 3 elements but " + elementCount + " were seen.\n";
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check post-order iteration ok. */
    public void testPostOrderIteration() throws Exception
    {
        String errorMessage = "";

        // Create two children and set the data element of the tree.
        testTree.addChild(new SimpleTree<String>("firstChild"));
        testTree.addChild(new SimpleTree<String>("secondChild"));
        testTree.setElement("element");

        // Check the iterator gives back the data element last and then the children. The iterator should give back
        // three elements in total and the ordering of the children is not specified.
        int elementCount = 0;
        boolean firstChildSeen = false;
        boolean secondChildSeen = false;

        for (Iterator<SimpleTree<String>> i = testTree.iterator(Tree.IterationOrder.PostOrder); i.hasNext();)
        {
            SimpleTree<String> nextSubTree = i.next();
            String nextElement = nextSubTree.getElement();

            // Keep count of how many elements the iterator has produced.
            elementCount++;

            // Check if it is the last element that is matches the element value for the root node.
            if ((elementCount == 3) && !"element".equals(nextElement))
            {
                errorMessage += "The last element must match the root element \"element\".\n";
            }

            // Check it matches one of the child element values.
            if ("firstChild".equals(nextElement))
            {
                // Flag the element as having been seen.
                firstChildSeen = true;
            }
            else if ("secondChild".equals(nextElement))
            {
                // Flag the element as having been seen.
                secondChildSeen = true;
            }
        }

        // Check that both child elements were seen.
        if (!firstChildSeen)
        {
            errorMessage += "The first child element was not seen.\n";
        }

        if (!secondChildSeen)
        {
            errorMessage += "The second child element was not seen.\n";
        }

        // Check that the total element count was 3.
        if (elementCount != 3)
        {
            errorMessage += "Should have seen 3 elements but " + elementCount + " were seen.\n";
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check iterator only returns available elements. */
    public void testIteratorNextFailsNoMoreElements() throws Exception
    {
        // Create two children and set the data element of the tree.
        testTree.addChild(new SimpleTree<String>("firstChild"));
        testTree.addChild(new SimpleTree<String>("secondChild"));
        testTree.setElement("element");

        // Iterate over all the elements.
        Iterator<SimpleTree<String>> i = testTree.iterator(Tree.IterationOrder.InOrder);

        for (; i.hasNext();)
        {
            i.next();
        }

        // Now try to get one more and check that this fails.
        boolean testPassed = false;

        try
        {
            i.next();
        }
        catch (NoSuchElementException e)
        {
            testPassed = true;
        }

        assertTrue("NoSuchElementException should have been thrown whilst calling .next() on an iterator that " +
            "has no more elements.", testPassed);
    }

    /** Check remove on iterator disabled. */
    public void testIteratorRemoveFails() throws Exception
    {
        // Create two children and set the data element of the tree.
        testTree.addChild(new SimpleTree<String>("firstChild"));
        testTree.addChild(new SimpleTree<String>("secondChild"));
        testTree.setElement("element");

        // Iterate over all the elements.
        Iterator<SimpleTree<String>> i = testTree.iterator(Tree.IterationOrder.InOrder);

        // Advance the iterator onto its first element
        i.next();

        // Try to remove this element and check that this operation is not supported.
        boolean testPassed = false;

        try
        {
            i.remove();
        }
        catch (UnsupportedOperationException e)
        {
            testPassed = true;
        }

        assertTrue("UnsupportedOperationException should have been thrown when calling .remove() with the " +
            "SimpleTreeIterator.", testPassed);
    }

    /** Check that a child iterator can be used as a place holder, as the tree is modified. */
    public void testChildIteratorPlaceHolderOk()
    {
        String errorMessage = "";

        // Add some children to the tree.
        testTree.addChild(new SimpleTree<String>("one"));

        // Iterate over them.
        Iterator<Tree<String>> i = testTree.getChildIterator();

        if (!"one".equals(i.next().getElement()))
        {
            errorMessage += "Iterator did not get first child.\n";
        }

        if (i.hasNext())
        {
            errorMessage += "Iterator reports it has next but already iterated over all.\n";
        }

        // Add some more children to the tree.
        testTree.addChild(new SimpleTree<String>("two"));

        // Iterate over the remainder.
        if (!i.hasNext())
        {
            errorMessage += "Iterator reports it has no more but more children added.\n";
        }

        try
        {
            if (!"two".equals(i.next().getElement()))
            {
                errorMessage += "Iterator did not get second child.\n";
            }
        }
        catch (ConcurrentModificationException e)
        {
            errorMessage += "Got concurrent modification exception on iterating to child two.\n";
        }

        // Report any errors as failures.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** @throws Exception */
    protected void setUp() throws Exception
    {
        NDC.push(getName());

        // Clear all the children from the test tree.
        testTree.clearChildren();

        // Set the test trees data element to null.
        testTree.setElement(null);
    }

    /** @throws Exception */
    protected void tearDown() throws Exception
    {
        NDC.pop();
    }
}
