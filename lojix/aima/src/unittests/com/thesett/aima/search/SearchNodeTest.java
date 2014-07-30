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
package com.thesett.aima.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

import com.thesett.aima.search.util.OperatorImpl;

/**
 * SearchNodeTest is a pure unit test class for the {@link SearchNode} class.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check start state node always has no parent, no applied operation, depth zero, path cost zero.
 * <tr><td> Check expand successors creates exactly one node for each successor state.
 * <tr><td> Check make node creates a node of the same class as the original.
 * <tr><td> Check make node throws SearchNotExhaustiveException when the search node class has a private constructor.
 * <tr><td> Check make node throws SearchNotExhaustiveException when the search node class has no no arguemtn constructor.
 * <tr><td> Check make node creates a node with the right state, parent, applied operation, depth and path cost.
 * <tr><td> Check make node creates a node the repeated state filter copied.
 * <tr><td> Check expand successors only creates successors allowed through the repeated state filter.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SearchNodeTest extends TestCase
{
    /** The {@link SearchNode} object to test. */
    SearchNode testSearchNode;

    /**
     * The {@link SearchNode} object to use to test that make node creates nodes of the same class as the original. In
     * the default version of this test class the constructor sets a node which is a sub-class of {@link SearchNode}
     * here in order to make the test an effective one.
     */
    SearchNode testMakeNode;

    /**
     * Default constructor that will result in the tests being run on the {@link SearchNode} class. This is used to
     * check the sanity of the tests being run and the default implementation of the search node class.
     */
    public SearchNodeTest(String testName)
    {
        super(testName);

        // Create the test search node to use
        testSearchNode = new SearchNode(new TestTraversableState());

        // Create the sub-class search node for the make node test
        testMakeNode = new SearchNodeSubClass();
    }

    /**
     * Builds the tests to be run on a supplied search node implementation that extends the default one. This allows the
     * default test methods to be applied to arbitrary implementations of search node in sub-classes of this test class.
     */
    public SearchNodeTest(String testName, SearchNode testSearchNode)
    {
        super(testName);

        // Keep reference to the search node to subject to testing, also use this same search node for the make node
        // test.
        this.testSearchNode = testSearchNode;
        this.testMakeNode = testSearchNode;
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("SearchNode Tests");

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(SearchNodeTest.class);

        return suite;
    }

    /** Check expand successors creates exactly one node for each successor state. */
    public void testExpandSuccessorsCreatesOneNodeForEachSuccessor() throws Exception
    {
        String errorMessage = "";
        Map<Operator, SearchNode> testSuccessors = new HashMap<Operator, SearchNode>();

        // Expand all the successors search nodes into a collection and then loop over them all.
        Queue<SearchNode> successors = new LinkedList<SearchNode>();

        testSearchNode.expandSuccessors(successors, false);

        for (SearchNode next : successors)
        {
            // There may be repeated states in the successors but there should never be repeated operations
            // (as tested for in the TraversableStateTest class), so store all the successor search nodes
            // in a hash map keyed by their applied operations.
            testSuccessors.put(next.getAppliedOp(), next);
        }

        // Loop over all the successors of the original state.
        for (Iterator<Successor> j = testSearchNode.getState().successors(false); j.hasNext();)
        {
            Successor nextSuccessor = j.next();

            // Check that a matching successor node can be found for the next successors applied operation.
            if (!testSuccessors.containsKey(nextSuccessor.getOperator()))
            {
                // No matching successor node could be found so add this to the error message.
                errorMessage +=
                    "No successor node was found for the operation " + nextSuccessor.getOperator() +
                    " although the original state generated a successor state for this operation.\n";
            }

            // A matching successor node was found in the hash map.
            {
                // Remove the succesor node from the hash map to keep track of all the matched nodes.
                testSuccessors.remove(nextSuccessor.getOperator());
            }
        }

        // Check that the hash map is empty to prove that all successor nodes were matched
        if (!testSuccessors.isEmpty())
        {
            // The hash map was not empty so add this to the error message.
            errorMessage +=
                "There are successor nodes for which the original state does not generate successor states:\n";

            // Loop over all the remaining successor node in the hash map adding them to the error message.
            for (SearchNode next : testSuccessors.values())
            {
                errorMessage +=
                    "Successor node with state " + next.getState() + " and applied op " + next.getAppliedOp();
            }
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check make node creates a node the repeated state filter copied. */
    public void testMakeNodeCopiesRepeatedStateFilter() throws Exception
    {
        String errorMessage = "";

        // Create a successor to the current state of the test node
        Successor successor = new Successor(new TestTraversableState(), new OperatorImpl<String>("testOp"), 1.0f);

        // Set a repeated state filter on the test node.
        RepeatedStateFilter filter = new BlankFilter();

        testSearchNode.setRepeatedStateFilter(filter);

        // Use make node to generate a successor node for that successor
        SearchNode successorNode = testSearchNode.makeNode(successor);

        // Check that the new node has the correct repeated state filter from the parent node. Note that these should
        // be the same object not just those that are identical through the equals method.
        if (successorNode.getRepeatedStateFilter() != filter)
        {
            // Does not have the correct state, add this to the error message.
            errorMessage += "The successor node does not have the same repeated state filter as the successor.\n";
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check make node creates a node of the same class as the original. */
    public void testMakeNodeCreatesNodesOfOriginalClass() throws Exception
    {
        String errorMessage = "";

        // Get the class of the node to be tested
        String nodeClassName = testMakeNode.getClass().getName();

        // Check that it isn't SearchNode (a subclass must be used for this test)
        if ("com.thesett.aima.search.SearchNode".equals(nodeClassName))
        {
            errorMessage +=
                "The class to be tested must not be com.thesett.aima.search.SearchNode but must be a sub-class of this.\n";
        }

        // Use make node to create a new node
        SearchNode newNode =
            testMakeNode.makeNode(new Successor(new TestTraversableState(), new OperatorImpl<String>(""), 0.0f));

        // Get the class of the new node
        String newNodeClassName = newNode.getClass().getName();

        // Check that the classes match
        if (!nodeClassName.equals(newNodeClassName))
        {
            // The classes don't match so add this to the error message
            errorMessage +=
                "The class created by the newNode method on class " + nodeClassName +
                " does not match this class but is a SearchNode of class " + newNodeClassName;
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /**
     * Check make node throws SearchNotExhaustiveException when the search node class has no no argument constructor.
     */
    public void testMakeNodeFailsOnNoNoArgConstructorSearchNode() throws Exception
    {
        boolean testPassed = false;

        // Get an instance of the test search node with a private constructor
        SearchNode testNode = new SearchNodeSubClassNoNoArgConstructor(new Object());

        // Check that its make node method raises a SearchNotExhaustiveException
        try
        {
            testNode.makeNode(new Successor(new TestTraversableState(), new OperatorImpl<String>(""), 0.0f));
        }
        catch (RuntimeException e)
        {
            testPassed = true;
        }

        // Check that the exception was raised
        if (!testPassed)
        {
            fail("Calling makeNode on a SearchNode with no no arg constructor " +
                "should have raised a RuntimeException.\n");
        }
    }

    /** Check make node throws SearchNotExhaustiveException when the search node class has a private constructor. */
    public void testMakeNodeFailsPrivateConstructor() throws Exception
    {
        boolean testPassed = false;

        // Get an instance of the test search node with a private constructor
        SearchNode testNode = SearchNodeSubClassPrivateConstructor.getInstance();

        // Check that its make node method raises a SearchNotExhaustiveException
        try
        {
            testNode.makeNode(new Successor(new TestTraversableState(), new OperatorImpl<String>(""), 0.0f));
        }
        catch (RuntimeException e)
        {
            testPassed = true;
        }

        // Check that the exception was raised
        if (!testPassed)
        {
            fail("Calling makeNode on a SearchNode with no public constructor " +
                "should have raised a RuntimeException.\n");
        }
    }

    /** Check make node creates a node with the right state, parent, applied operation, depth and path cost. */
    public void testMakeNodePropertiesOk() throws Exception
    {
        String errorMessage = "";

        // Create a successor to the current state of the test node
        Successor successor = new Successor(new TestTraversableState(), new OperatorImpl<String>("testOp"), 1.0f);

        // Use make node to generate a successor node for that successor
        SearchNode successorNode = testSearchNode.makeNode(successor);

        // Check that the new node has the correct state from the successor.
        if (!successorNode.getState().equals(successor.getState()))
        {
            // Does not have the correct state, add this to the error message.
            errorMessage += "The successor node does not have the same state as the successor.\n";
        }

        // Check that the new node has the correct parent matching its original state.
        if (!successorNode.getParent().equals(testSearchNode))
        {
            // Does not have the correct parent, add this to the error message.
            errorMessage += "The successor node does not have parent equal to its generating search node.\n";
        }

        // Check that the new node has the correct applied operation from the successor.
        if (!successorNode.getAppliedOp().equals(successor.getOperator()))
        {
            // Does not have the correct applied operation, add this to the error message.
            errorMessage += "The sucessor node does not have the same appliled operation as the successor.\n";
        }

        // Check that the new node has the correct depth matching its original depth plus one.
        if (successorNode.getDepth() != (testSearchNode.getDepth() + 1))
        {
            // Does not have the correct depth, add this to the error message.
            errorMessage += "The successor node does not have depth equal to its generating node plus one.\n";
        }

        // Check that the new node has the correct path cost from the successor matching its original path cost plus
        // the successors path cost.
        if (successorNode.getPathCost() != (testSearchNode.getPathCost() + successor.getCost()))
        {
            // Does not have the correct path cost, add this to the error message.
            errorMessage +=
                "The successor node does not have path cost equal to the parent path cost " +
                "plus the successor path cost.\n";
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check expand successors only creates successors allowed through the repeated state filter. */
    public void testRepeatedStateFilterIsApplied() throws Exception
    {
        String errorMessage = "";

        // Get all the successors from test search node by going directly to its state.
        int count = 0;

        for (Iterator<Successor> i = testSearchNode.getState().successors(false); i.hasNext(); i.next())
        {
            count++;
        }

        // Check that there is at least one successor for this test to be able to work, if not then just pass the test.
        if (count == 0)
        {
            return;
        }

        // Check that all successors come through a blank filter.
        Queue<SearchNode> blankFilteredNodes = new LinkedList<SearchNode>();

        testSearchNode.setRepeatedStateFilter(new BlankFilter());
        testSearchNode.expandSuccessors(blankFilteredNodes, false);

        if (blankFilteredNodes.size() != count)
        {
            errorMessage += "Some successors were filtered out by a blank filter.";
        }

        // Check that none come through a completely blocking filter.
        Queue<SearchNode> blockingFilteredNodes = new LinkedList<SearchNode>();

        testSearchNode.setRepeatedStateFilter(new BlockingFilter());
        testSearchNode.expandSuccessors(blockingFilteredNodes, false);

        if (blockingFilteredNodes.size() != 0)
        {
            errorMessage += "Some successors made it through a blocking filter.";
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check start state node always has no parent, no applied operation, depth zero and path cost zero. */
    public void testStartStatePropertiesOk() throws Exception
    {
        String errorMessage = "";

        // Create a start node out of a TestTraversableSate.
        SearchNode testNode = new SearchNode(new TestTraversableState());

        // Check it has no parent.
        if (testNode.getParent() != null)
        {
            // It has a parent so add this to the error message.
            errorMessage += "Start node does not have a null parent.\n";
        }

        // Check it has no applied operation.
        if (testNode.getAppliedOp() != null)
        {
            // It has an applied operation so add this to the error message.
            errorMessage += "Start node does not have a null applied operation.\n";
        }

        // Check it has depth zero
        if (testNode.getDepth() != 0)
        {
            // It does not have depth zero so add this to the error message.
            errorMessage += "Start node does not have zero depth.\n";
        }

        // Check it has path cost zero
        if (testNode.getPathCost() != 0)
        {
            // It does not have path cost zero so add this to the error message.
            errorMessage += "Start node does not have path cost zero.\n";
        }

        // Assert that there were no error messages and print them if there were.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** @throws Exception */
    protected void setUp() throws Exception
    {
        NDC.push(getName());
    }

    /** @throws Exception */
    protected void tearDown() throws Exception
    {
        NDC.pop();
    }
}
