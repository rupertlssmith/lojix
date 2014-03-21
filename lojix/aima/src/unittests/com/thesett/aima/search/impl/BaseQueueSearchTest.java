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
package com.thesett.aima.search.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;

import com.thesett.aima.search.BlankFilter;
import com.thesett.aima.search.GoalState;
import com.thesett.aima.search.InstrumentedSearchNode;
import com.thesett.aima.search.InstrumentedTraversableState;
import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.TestSearch;
import com.thesett.aima.search.TestTraversableState;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

/**
 * BaseQueueSearchTest is a pure unit test class for the {@link BaseQueueSearch} class.
 *
 * <p>The {@link TestTraversableState} is used as a source for generating test states to search over.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that the start node is enqueued by the reset method ready for the search to run.
 * <tr><td> Check that the repeated state filter is reset every time a search is started.
 * <tr><td> Check that the repeated state filter is attached to the start node every time a search is started.
 * <tr><td> Check that runnning a search with no start states fails.
 * <tr><td> Check that a search that cannot find a goal state returns null.
 * <tr><td> Check that a search that finds goal states returns them all on subsequent calls to search.
 * <tr><td> Check that a search that finds a goal state returns it.
 * <tr><td> Check that a search that encounters a non-goal state expands its successors.
 * <tr><td> Check that a search fails if it encounters its maximum number of steps.
 * <tr><td> Check that a search returns null if it encounters its maximum number of steps but the search has completed.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BaseQueueSearchTest extends TestCase
{
    /** The BaseQueueSearch based search algorithm to test. */
    private BaseQueueSearch testSearch;

    /** Used to hold the start state. */
    GoalState testStartState;

    /**
     * Default constructor that will result in the tests being run on the {@link TestSearch} class. This is used to
     * check the sanity of the tests being run and the implementation of the base search class on a default search
     * method implementation.
     */
    public BaseQueueSearchTest(String testName)
    {
        super(testName);

        // Create a new base queue search out of the test search implementation.
        testSearch = new TestSearch();
    }

    /**
     * Creates a new BaseQueueSearchTest object.
     *
     * @param testName
     * @param testSearch
     */
    public BaseQueueSearchTest(String testName, BaseQueueSearch<String, TestTraversableState> testSearch)
    {
        super(testName);

        // Keep a copy of the BaseQueueSearch based search method to test.
        this.testSearch = testSearch;
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("BaseQueueSearch Tests");

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(BaseQueueSearchTest.class);

        return suite;
    }

    /** Check that the start node is enqueued by the reset method ready for the search to run. */
    public void testStartNodeEnqueued() throws Exception
    {
        // Create a start queue by enqueueing a single start state.
        Collection<TestTraversableState> startStates = new ArrayList<TestTraversableState>();
        TestTraversableState startState = new TestTraversableState(1, 1);

        startStates.add(startState);

        Queue<SearchNode<String, TestTraversableState>> queue = testSearch.enqueueStartStates(startStates);

        // Check that the start state was enqueued.
        assertTrue("The start state was not enqueued.", queue.element().getState().equals(startState));
    }

    /** Check that the repeated state filter is reset every time a search is reset. */
    public void testRepeatedStateFilterReset() throws Exception
    {
        // Add a repeated state filter.
        BlankFilter filter = new BlankFilter();

        testSearch.setRepeatedStateFilter(filter);

        // Set up a very simple search space over a tree of depth 1 branching factor 3, no goal states.
        testSearch.addStartState(new TestTraversableState(3, 1));

        // Reset the search.
        testSearch.reset();

        // Check that the repeated state filter was reset.
        assertTrue("The repeated state filter was not reset.", filter.resetCalled);
    }

    /** Check that the repeated state filter is attached to the start node every time a search is started. */
    public void testRepeatedStateFilterAttached() throws Exception
    {
        String errorMessage = "";

        // Set up a simple search space over a tree of depth 4 branching factor 4 with an easy goal state at level 4.
        TestTraversableState testState = new TestTraversableState(4, 4);

        testState.addGoalSequence("0;3;3;3;3");

        // Configure the search with this start state.
        testSearch.addStartState(testState);

        // Reset the instrumented search node stats to ensure they are fresh for this test.
        InstrumentedSearchNode.resetStats();

        // Attach a dummy repeated state filter to the search.
        BlankFilter filter = new BlankFilter();

        testSearch.setRepeatedStateFilter(filter);

        // Run the search.
        testSearch.findGoalPath();

        // Check that some instumented search node were created.
        if (InstrumentedSearchNode.createdNodes.isEmpty())
        {
            errorMessage += "No instrumented search nodes were created.";
        }

        // Loop through all the created search nodes and ensure that the repeated state filter was attached to each one.
        for (InstrumentedSearchNode nextNode : InstrumentedSearchNode.createdNodes)
        {
            // Check that the filter was attached.
            if (!nextNode.filterAttached)
            {
                errorMessage += "The filter was not attached to the node: ";
            }

            // Check that it is really the dummy filter.
            if (nextNode.getRepeatedStateFilter() != filter)
            {
                errorMessage += "The attached filter was not the test filter on node: " + nextNode;
            }
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that runnning a search with no start states fails. */
    public void testNoStartStateFails() throws Exception
    {
        // Used to indicate that the expected SearchNotExhaustiveException was thrown.
        boolean exceptionThrown = false;

        // Use a try block to catch the expected SearchNotExhaustiveException
        try
        {
            // Start the search without setting up any start states.
            testSearch.findGoalPath();
        }
        catch (IllegalStateException e)
        {
            // Check that the failure was because no start states were set up.
            assertTrue("IllegalStateException thrown but not because no start states were set up." +
                " Message given in the exception is " + e.getMessage(),
                e.getMessage().equals(
                    "Cannot start the search because there are no start states defined. " +
                    "Queue searches require some start states."));

            // Set the exception flag to indicate that the test has passed as expected.
            exceptionThrown = true;
        }

        // Assert that the excepted exception was thrown.
        assertTrue("IllegalStateException was not thrown.", exceptionThrown);
    }

    /** Check that a search that cannot find a goal state returns null. */
    public void testNoGoalSearchReturnsNull() throws Exception
    {
        // Set up a very simple search space over a tree of depth 1 branching factor 3, no goal states.
        testSearch.addStartState(new TestTraversableState(3, 1));

        // Run the search and check it returns null
        assertNull("The test search over TestTraversableState did not return null " +
            "but TestTraversableState has no goal states.", testSearch.findGoalPath());
    }

    /** Check that a search that finds a goal state returns it. */
    public void testSearchReturnsGoal() throws Exception
    {
        // Set up a simple search space over a tree of depth 4 branching factor 4 with an easy goal state at level 4.
        TestTraversableState testState = new TestTraversableState(4, 4);

        testState.addGoalSequence("0;3;3;3;3");

        // Configure the search with this start state.
        testSearch.addStartState(testState);

        // Run it and check that it returns a goal state.
        SearchNode goal = testSearch.findGoalPath();

        assertTrue("The search returned a goal node but its state did not pass the isGoal test.",
            ((GoalState) goal.getState()).isGoal());
    }

    /**
     * Check that a search that finds goal states returns them all on subsequent calls to search.
     *
     * <p/>In this test the goal states have been set up so that they deliberately occur with one directly on the path
     * to the other, to ensure that a goal state with subsequent goal states beyond it does not block those subsequent
     * states.
     */
    public void testSearchReturnsMultipleGoals() throws Exception
    {
        String errorMessage = "";

        // Set up a simple search space over a tree of depth 4 branching factor 4 with two easy goal states.
        // The goal states have been set up so that they deliberately occur with one directly on the path to the
        // other, to ensure that a goal state with subsequent goal states beyond it does not block those subsequent
        // states.
        TestTraversableState testState = new TestTraversableState(4, 4);

        testState.addGoalSequence("0;3");
        testState.addGoalSequence("0;3;3;3;2");

        // Configure the search with this start state.
        testSearch.addStartState(testState);

        Collection<SearchNode> goals = new ArrayList<SearchNode>();
        SearchNode goal = null;

        // Keep running the search to find all goals.
        do
        {
            goal = testSearch.findGoalPath();

            if (goal != null)
            {
                goals.add(goal);
            }
        }
        while (goal != null);

        // Check that both goals were found.
        if (goals.size() != 2)
        {
            errorMessage += "Excepected 2 goal states but got: " + goals.size() + ".\n";
        }

        // Check that all goal states found are goals.
        for (SearchNode goalNode : goals)
        {
            if (!((GoalState) goalNode.getState()).isGoal())
            {
                errorMessage = "A non goal state, " + goalNode.getState() + ", was found.\n";
            }
        }

        // Report any errors.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that a search that encounters a non-goal state expands its successors. */
    public void testEncounteredNonGoalStatesExpanded() throws Exception
    {
        String errorMessage = "";

        // Set up a simple search space over a tree of depth 4 branching factor 4 with an easy goal state at level 4.
        TestTraversableState testState = new TestTraversableState(4, 4);

        testState.addGoalSequence("0;3;3;3;3");

        // Wrap the test search space in the traversable state test rig to gather stats on the test calls made to it.
        InstrumentedTraversableState<String> instrumentedTestState =
            new InstrumentedTraversableState<String>(testState);

        InstrumentedTraversableState.resetStats();

        // Configure the search with this start state.
        testSearch.addStartState(instrumentedTestState);

        // Run the search.
        testSearch.findGoalPath();

        // Check that all non-goal examined states were expanded.
        for (InstrumentedTraversableState nextState : InstrumentedTraversableState.examinedStates)
        {
            // Check that the state is a non-goal state using the helper method to avoid a concurrent modification
            // exception.
            if (!nextState.isGoalHelper())
            {
                // Check that the state was expanded.
                if (!nextState.expanded)
                {
                    errorMessage +=
                        "The state " + nextState + " was goal tested and found to be a non-goal but its " +
                        "successors were not expanded.\n";
                }
            }
        }

        // Assert that there were no error messages and print them if there were
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that a search fails if it encounters its maximum number of steps. */
    public void testMaximumStepsReachedFails() throws Exception
    {
        // Used to indicate that the expected SearchNotExhaustiveException was thrown.
        boolean exceptionThrown = false;

        // Set up a simple search space over a tree of depth 4 branching factor 4, no goal states.
        testSearch.addStartState(new TestTraversableState(4, 4));

        // There are (4^5-1)/(4-1) = 1023/3 = 341 states in the space. Set a maximum step count just less than this, so
        // that there should be just one element left in the queue when the max steps is encountered.
        testSearch.setMaxSteps(340);

        // Use a try block to catch the expected SearchNotExhaustiveException
        try
        {
            // Run the search.
            testSearch.findGoalPath();
        }
        catch (SearchNotExhaustiveException e)
        {
            // Check that the failure was because the maximum number of steps was reached.
            assertTrue("SearchNotExhaustiveException thrown but not because the maximum number of steps was reached." +
                " Message given in the exception is " + e.getMessage(),
                "Maximum number of steps reached.".equals(e.getMessage()));

            // Set the exception flag to indicate that the test has passed as expected.
            exceptionThrown = true;
        }

        // Assert that the expected exception was thrown.
        assertTrue("SearchNotExhaustiveException was not thrown.", exceptionThrown);
    }

    /**
     * Check that a search returns null if it encounters its maximum number of steps but the search has completed.
     * Although the max is reached if the search has succesfully completed it should return null and not throw a search
     * failure exception as it does normally when the max is reached.
     */
    public void testMaximumStepsReachedSearchCompletedReturnsNull() throws Exception
    {
        // Set up a simple search space over a tree of depth 4 branching factor 4, no goal states.
        testSearch.addStartState(new TestTraversableState(4, 4));

        // There are (4^5-1)/(4-1) = 1023/3 = 341 states in the space. Set a maximum step count equal to this, so
        // that there should be no elements left in the queue when the max steps is encountered. Although the
        // max is reached the search has succesfully completed so it should return null and not throw a search
        // failure exception.
        testSearch.setMaxSteps(341);

        // Run the search and assert that it returns null.
        assertNull("The test search over TestTraversableState with 341 states, all non-goal, and a maximum step size " +
            "of 341 did not return null but the test should have succesfully completed.", testSearch.findGoalPath());
    }

    protected void setUp() throws Exception
    {
        NDC.push(getName());

        // Reset the search and clear any repeated state filters from it.
        testSearch.reset();
        testSearch.setRepeatedStateFilter(null);
    }

    protected void tearDown() throws Exception
    {
        NDC.pop();
    }
}
