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

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.log4j.NDC;

/**
 * TreeTestBase is a pure unit test class for classes which implement {@link Tree} interface. It cannot be run directly
 * on this as it is an interface but it provides a constructor to pass instantiations of classes that extend
 * {@link Tree} to be tested.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check that leaf correctly reports that it is a leaf.
 * <tr><td>Check that node does not report that it is a leaf.
 * <tr><td>Check parent is null for root.
 * <tr><td>Check parent ok for non-root.
 * <tr><td>Check data element correctly stored and retrieved.
 * <tr><td>Check addition of child ok if supported.
 * <tr><td>Check child addition to a leaf makes it into a node.
 * <tr><td>Check child iterator ok.
 * <tr><td>Check clearing children ok if supported.
 * <tr><td>Check clearing children from a node makes a leaf.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TreeTestBase extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(TreeTestBase.class.getName()); */

    /** Used to hold the test tree over strings. */
    Tree<String> testTree;

    /** This constructor runs the tests on a provided implemenatation of the {@link Tree} interface. */
    public TreeTestBase(String testName, Tree<String> testTree)
    {
        super(testName);

        // Keep a copy of the tree to test.
        this.testTree = testTree;
    }

    /** Check that leaf correctly reports that it is a leaf. */
    public void testLeafReportsIsLeaf() throws Exception
    {
        assertTrue("Test tree has no children so isLeaf should be true.", testTree.isLeaf());
        assertNotNull("Test tree has no children so getAsLeaf should not be null.", testTree.getAsLeaf());
        assertNull("Test tree has no children so getAsNode should be null.", testTree.getAsNode());
    }

    /** Check that node does not report that it is a leaf. */
    public void testNodeReportsIsNoLeaf() throws Exception
    {
        // Add the test tree as a child to itself to ensure it is a node.
        testTree.addChild(testTree); // new SimpleTree<String>());

        // Check that it no longer reports it is a leaf.
        assertFalse("Test tree has children so isLeaf should be false.", testTree.isLeaf());
        assertNull("Test tree has children so getAsLeaf should be null.", testTree.getAsLeaf());
        assertNotNull("Test tree has children so getAsNode should be not null.", testTree.getAsNode());
    }

    /** Check parent is null for root. */
    public void testNullParentForRoot() throws Exception
    {
        assertNull("Test tree has no parent so getParent should be null.", testTree.getParent());
    }

    /** Check parent ok for non-root. */
    public void testParentOkNonRoot() throws Exception
    {
        String errorMessage = "";

        // Add the test tree as a child to itself to ensure it has a parent.
        testTree.addChild(testTree); // new SimpleTree<String>());

        // Check that the parent is not null.
        if (testTree.getParent() == null)
        {
            errorMessage += "Test tree has a parent so getParent should not be null.\n";
        }

        // Check that the parent is really the test tree.
        if (testTree.getParent() != testTree)
        {
            errorMessage +=
                "Test tree has itself as a parent but reports its parent as: " + testTree.getParent() + ".\n";
        }

        // Check that the parent has only one child which is also the test tree.
        Tree.Node<String> parent = testTree.getParent();
        Collection<? extends Tree<String>> children = parent.getChildren();

        if (children.size() != 1)
        {
            errorMessage += "Test tree as parent should only have one child but has: " + children.size() + ".\n";
        }

        if (!children.contains(testTree))
        {
            errorMessage += "Test tree should have itself as its only child but does not.\n";
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check data element correctly stored and retrieved. */
    public void testElementOk() throws Exception
    {
        // Store a data element in the test tree.
        String test = "test";

        testTree.setElement(test);

        // Get it back again.
        String test2 = testTree.getElement();

        // Check it is the same one as was stored. == operator is used and not .equals to ensure that it is really the
        // same object reference.
        assertTrue("Stored and retrieved element do not match.", test == test2);
    }

    /** Check addition of child ok if supported. */
    public void testAddChildOk() throws Exception
    {
        // Add the test tree as a child to itself several times.
        testTree.addChild(testTree); // new SimpleTree<String>());
        testTree.addChild(testTree); // new SimpleTree<String>());
        testTree.addChild(testTree); // new SimpleTree<String>());
    }

    /** Check child addition to a leaf makes it into a node. */
    public void testAddChildMakesNode() throws Exception
    {
        // Add the test tree as a child to itself to ensure it is a node.
        testTree.addChild(testTree); // new SimpleTree<String>());

        // Check that it no longer reports it is a leaf.
        assertFalse("Test tree has children so isLeaf should be false.", testTree.isLeaf());
    }

    /** Check child iterator ok. */
    public void testChildIteratorOk() throws Exception
    {
        String errorMessage = "";

        // Add the test tree as a child to itself 10 times.
        for (int i = 0; i < 10; i++)
        {
            testTree.addChild(testTree);
        }

        // Get an iterator over the children and count how many children it returns and that they match the test tree.
        int count = 0;

        for (Iterator<Tree<String>> i = testTree.getAsNode().getChildIterator(); i.hasNext();)
        {
            Tree<String> nextChild = i.next();

            count++;

            if (nextChild != testTree)
            {
                errorMessage +=
                    "Found a child not matching testTree: " + nextChild + ". testTree is:" + testTree + ".\n";
            }
        }

        if (count != 10)
        {
            errorMessage += "testTree did not have 10 children but had: " + count + ".\n";
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check clearing children ok if supported. */
    public void testClearChildrenOk() throws Exception
    {
        testTree.clearChildren();
    }

    /** Check clearing children from a node makes a leaf. */
    public void testClearChildrenMakesLeaf() throws Exception
    {
        // Add the test tree as a child to itself to ensure it is a node.
        testTree.addChild(testTree); // new SimpleTree<String>());

        // Clear all its children.
        testTree.clearChildren();

        // Make sure its a leaf again.
        assertTrue("Test tree has no children so isLeaf should be true.", testTree.isLeaf());
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
