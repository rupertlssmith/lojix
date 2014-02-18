/*
 * Copyright The Sett Ltd.
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
package com.thesett.aima.search.util.informed;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import org.apache.log4j.NDC;

import com.thesett.aima.search.SearchNodeTest;
import com.thesett.aima.search.Successor;
import com.thesett.aima.search.TestTraversableState;
import com.thesett.aima.search.util.OperatorImpl;

/**
 * HeuristicSearchNodeTest is a pure unit test class for the {@link HeuristicSearchNode} class. HeuristicSearchNode
 * extends {@link com.thesett.aima.search.SearchNode} and so the class is subjected to the same tests as a search
 * node, using the {@link SearchNodeTest} test class suite.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check make node creates a heuristic search node with the correct heuristic value.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class HeuristicSearchNodeTest extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(HeuristicSearchNodeTest.class.getName()); */

    /** The {@link com.thesett.aima.search.SearchNode} object to test. */
    HeuristicSearchNode<String, TestTraversableState> testSearchNode;

    /**
     * Default constructor that will result in the tests being run on the
     * {@link com.thesett.aima.search.SearchNode} class. This is used to check the sanity of the tests being run
     * and the default implementation of the heuristic search node class.
     */
    public HeuristicSearchNodeTest(String testName)
    {
        super(testName);

        // Create the test search node to use
        testSearchNode =
            new HeuristicSearchNode<String, TestTraversableState>(new TestTraversableState(), new TestHeuristic());
    }

    /**
     * Builds the tests to be run on a supplied search node implementation that extends the default one. This allows the
     * default test methods to be applied to arbitrary implementations of search node in sub-classes of this test class.
     */
    public HeuristicSearchNodeTest(String testName, HeuristicSearchNode<String, TestTraversableState> testSearchNode)
    {
        super(testName);

        // Keep reference to the search node to subject to testing, also use this same search node for the make node
        // test
        this.testSearchNode = testSearchNode;
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("HeuristicSearchNode Tests");

        // Add all tests defined in the SearchNodeTest class
        suite.addTest(new SearchNodeTest("testStartStatePropertiesOk",
                new HeuristicSearchNode<String, TestTraversableState>(new TestTraversableState(),
                    new TestHeuristic())));
        suite.addTest(new SearchNodeTest("testExpandSuccessorsCreatesOneNodeForEachSuccessor",
                new HeuristicSearchNode<String, TestTraversableState>(new TestTraversableState(),
                    new TestHeuristic())));
        suite.addTest(new SearchNodeTest("testMakeNodeCreatesNodesOfOriginalClass",
                new HeuristicSearchNode<String, TestTraversableState>(new TestTraversableState(),
                    new TestHeuristic())));
        suite.addTest(new SearchNodeTest("testMakeNodePropertiesOk",
                new HeuristicSearchNode<String, TestTraversableState>(new TestTraversableState(),
                    new TestHeuristic())));
        suite.addTest(new SearchNodeTest("testMakeNodeCopiesRepeatedStateFilter",
                new HeuristicSearchNode<String, TestTraversableState>(new TestTraversableState(),
                    new TestHeuristic())));
        suite.addTest(new SearchNodeTest("testRepeatedStateFilterIsApplied",
                new HeuristicSearchNode<String, TestTraversableState>(new TestTraversableState(),
                    new TestHeuristic())));

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(HeuristicSearchNodeTest.class);

        return suite;
    }

    /** Check make node creates a heuristic search node with the correct heuristic value. */
    public void testMakeNodeHasCorrectHeuristic() throws Exception
    {
        // Create a successor state to the current state of the test node
        float p = 1.0f;
        Successor<String> successor =
            new Successor<String>(new TestTraversableState(), new OperatorImpl<String>("testOp"), p);

        // Apply the heuristic to the successor state
        Heuristic<String, TestTraversableState> heuristic = testSearchNode.getHeuristic();
        float h1 = heuristic.computeH((TestTraversableState) successor.getState(), testSearchNode);

        // Use make node to generate a successor node for the test successor state
        HeuristicSearchNode successorNode = testSearchNode.makeNode(successor);

        // Get the new nodes heuristic value
        float h2 = successorNode.getH();

        // Check that the two heuristic values match
        assertEquals("The two heuristic values are not equal.", h1, h2);

        // Check that the f value (heuristic + path cost) has the correct value
        float f = successorNode.getF();

        assertEquals("The f value has the incorrect value " + f + " it should have the value " + (p + h1), f, p + h1);
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

    /**
     * A test heuristic, always returns the value -1.0f.
     */
    static class TestHeuristic implements Heuristic<String, TestTraversableState>
    {
        /**
         * Return heuristic evaluation as a real value.
         *
         * @param  state      the state to be evaluated.
         * @param  searchNode the {@link HeuristicSearchNode} for the state.
         *
         * @return a real valued heuristic evaluation of the state.
         */
        public float computeH(TestTraversableState state, HeuristicSearchNode<String, TestTraversableState> searchNode)
        {
            return -1.0f;
        }
    }
}
