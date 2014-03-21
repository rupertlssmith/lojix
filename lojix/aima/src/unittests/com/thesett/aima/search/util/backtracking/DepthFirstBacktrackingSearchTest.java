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
package com.thesett.aima.search.util.backtracking;

import java.util.HashSet;
import java.util.Set;

import com.thesett.aima.search.InstrumentedTraversableState;
import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.TestTraversableState;
import com.thesett.aima.search.impl.BaseQueueSearch;
import com.thesett.aima.search.impl.BaseQueueSearchTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

/**
 */
public class DepthFirstBacktrackingSearchTest extends TestCase
{
    public DepthFirstBacktrackingSearchTest(String testName)
    {
        super(testName);
    }

    /** Compiles all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("DepthFirstBacktrackingSearchTest Tests");

        // Add all tests defined in the SearchNodeTest class
        suite.addTest(new BaseQueueSearchTest("testStartNodeEnqueued", new DepthFirstBacktrackingSearch()));
        suite.addTest(new BaseQueueSearchTest("testRepeatedStateFilterReset", new DepthFirstBacktrackingSearch()));
        suite.addTest(new BaseQueueSearchTest("testNoStartStateFails", new DepthFirstBacktrackingSearch()));
        suite.addTest(new BaseQueueSearchTest("testNoGoalSearchReturnsNull", new DepthFirstBacktrackingSearch()));
        suite.addTest(new BaseQueueSearchTest("testSearchReturnsGoal", new DepthFirstBacktrackingSearch()));
        suite.addTest(new BaseQueueSearchTest("testSearchReturnsMultipleGoals", new DepthFirstBacktrackingSearch()));
        suite.addTest(new BaseQueueSearchTest("testEncounteredNonGoalStatesExpanded",
                new DepthFirstBacktrackingSearch()));
        suite.addTest(new BaseQueueSearchTest("testMaximumStepsReachedFails", new DepthFirstBacktrackingSearch()));
        suite.addTest(new BaseQueueSearchTest("testMaximumStepsReachedSearchCompletedReturnsNull",
                new DepthFirstBacktrackingSearch()));

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(DepthFirstBacktrackingSearchTest.class);

        return suite;
    }

    /** Check that all goal-path examined states were have had their operator applied and not yet backtracked. */
    public void testGoalPathStatesHaveOperatorAppliedAndNotBacktracked() throws Exception
    {
        String errorMessage = "";

        BaseQueueSearch<String, ReTraversable<String>> testSearch =
            new DepthFirstBacktrackingSearch<String, ReTraversable<String>>();
        testSearch.reset();

        // Set up a simple search space over a tree of depth 4 branching factor 4.
        TestTraversableState testState = new TestTraversableState(4, 4);

        // Set up a variety of goal states over the search space. Some on unrelated branches. Some nested below others.
        testState.addGoalSequence("0;3;3");
        testState.addGoalSequence("0;3;3;1;3");

        // Wrap the test search space in the traversable state test rig to gather stats on the test calls made to it.
        InstrumentedTraversableState instrumentedTestState = new InstrumentedTraversableState(testState);
        InstrumentedTraversableState.resetStats();

        // Configure the search with the instrumented start state.
        testSearch.addStartState(instrumentedTestState);

        // Run the search until no more goal nodes are found.
        SearchNode goalNode = null;

        do
        {
            goalNode = testSearch.findGoalPath();

            // If a goal was found.
            if (goalNode != null)
            {
                // Check that all goal-path examined states have had their operator applied and not yet backtracked.
                SearchNode checkNode = goalNode;

                while (checkNode != null)
                {
                    InstrumentedTraversableState state = (InstrumentedTraversableState) checkNode.getState();

                    if (!state.operatorApplied)
                    {
                        errorMessage += "Operator was not applied for state " + state + ".";
                    }

                    if (state.correctNodeVisitedOnBacktracking)
                    {
                        errorMessage +=
                            "State " + state +
                            " on the goal path should not have been backtracked over yet but it has.";
                    }

                    checkNode = checkNode.getParent();
                }
            }
        }
        while (goalNode != null);

        // Check that all non-goal-path examined states were correctly backtracked.

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that all non-goal-path examined states were correctly backtracked. */
    public void testNonGoalPathStatesAreBacktrackedInCorrectOrder() throws Exception
    {
        String errorMessage = "";

        BaseQueueSearch<String, ReTraversable<String>> testSearch =
            new DepthFirstBacktrackingSearch<String, ReTraversable<String>>();
        testSearch.reset();

        // Set up a simple search space over a tree of depth 4 branching factor 4.
        TestTraversableState testState = new TestTraversableState(4, 4);

        // Set up a variety of goal states over the search space. Some on unrelated branches. Some nested below others.
        testState.addGoalSequence("0;3;3");
        testState.addGoalSequence("0;3;3;1;3");

        // Wrap the test search space in the traversable state test rig to gather stats on the test calls made to it.
        InstrumentedTraversableState instrumentedTestState = new InstrumentedTraversableState(testState);
        InstrumentedTraversableState.resetStats();

        // Configure the search with the instrumented start state.
        testSearch.addStartState(instrumentedTestState);

        // Run the search until no more goal nodes are found.
        SearchNode goalNode = null;

        do
        {
            goalNode = testSearch.findGoalPath();

            // Work out what all the goal-path examined states are in order to eliminate them from checking
            // examined states that should have been backtracked over.
            Set<InstrumentedTraversableState> goalPathSet = new HashSet<InstrumentedTraversableState>();

            if (goalNode != null)
            {
                SearchNode goalPathNode = goalNode;

                while (goalPathNode != null)
                {
                    InstrumentedTraversableState state = (InstrumentedTraversableState) goalPathNode.getState();
                    goalPathSet.add(state);

                    goalPathNode = goalPathNode.getParent();
                }
            }

            // Check that all non-goal-path examined states were correctly backtracked.
            for (InstrumentedTraversableState nextState : InstrumentedTraversableState.examinedStates)
            {
                if (!goalPathSet.contains(nextState))
                {
                    // Check that the state was expanded.
                    if (!nextState.correctNodeVisitedOnBacktracking)
                    {
                        errorMessage += "The state " + nextState + " was not backtracked in the correct order.\n";
                    }
                }
            }
        }
        while (goalNode != null);

        // Check that all non-goal-path examined states were correctly backtracked.

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    protected void setUp() throws Exception
    {
        NDC.push(getName());
    }

    protected void tearDown() throws Exception
    {
        NDC.pop();
    }
}
