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
package com.thesett.aima.search.util.uninformed;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.TestTraversableState;
import com.thesett.aima.search.impl.BaseQueueSearchTest;

/**
 * IterativeCostIncreasingSearchTest is a pure unit test class for the {@link IterativeCostIncreasingSearch} class.
 *
 * @author Rupert Smith
 */
public class IterativeCostIncreasingSearchTest extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(IterativeCostIncreasingSearchTest.class.getName()); */

    /** The IterativeCostIncreasingSearch to test. */
    private IterativeCostIncreasingSearch testSearch;

    /** Default constructor that will result in the tests being run on a depth first search. */
    public IterativeCostIncreasingSearchTest(String testName)
    {
        super(testName);

        // Create an iterative cost increasing search to test that goes 1.0f more costly at every iteration.
        testSearch = new IterativeCostIncreasingSearch(0.0f, 1.0f);
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("IterativeCostIncreasingSearch Tests");

        // Add all tests defined in the BaseQueueSearcTest class.
        // Run these tests starting at cost 1.0f and incrementing by 1.0f. This will affect some of the test
        // results because states will be examined multiple times and these tests rely on knowing some exact
        // numbers, also non-goal states are not expanded below the max depth.
        // For test traversable states the cost always equals the depth.
        suite.addTest(new BaseQueueSearchTest("testStartNodeEnqueued", new IterativeCostIncreasingSearch(0.0f, 1.0f)));
        suite.addTest(new BaseQueueSearchTest("testRepeatedStateFilterReset",
                new IterativeCostIncreasingSearch(0.0f, 1.0f)));
        suite.addTest(new BaseQueueSearchTest("testNoStartStateFails", new IterativeCostIncreasingSearch(0.0f, 1.0f)));
        suite.addTest(new BaseQueueSearchTest("testNoGoalSearchReturnsNull",
                new IterativeCostIncreasingSearch(0.0f, 1.0f)));
        suite.addTest(new BaseQueueSearchTest("testSearchReturnsGoal", new IterativeCostIncreasingSearch(0.0f, 1.0f)));

        // Run these tests starting at cost 1.0f and incrementing to the next minial cost value seen beyond the previous
        // bound. For test traversable states the cost always equals the depth.
        suite.addTest(new BaseQueueSearchTest("testStartNodeEnqueued", new IterativeCostIncreasingSearch(0.0f)));
        suite.addTest(new BaseQueueSearchTest("testRepeatedStateFilterReset", new IterativeCostIncreasingSearch(0.0f)));
        suite.addTest(new BaseQueueSearchTest("testNoStartStateFails", new IterativeCostIncreasingSearch(0.0f)));
        suite.addTest(new BaseQueueSearchTest("testNoGoalSearchReturnsNull", new IterativeCostIncreasingSearch(0.0f)));
        suite.addTest(new BaseQueueSearchTest("testSearchReturnsGoal", new IterativeCostIncreasingSearch(0.0f)));

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(IterativeCostIncreasingSearchTest.class);

        return suite;
    }

    /** Check that a search fails if it encounters its maximum number of steps. */
    public void testMaximumStepsReachedFails() throws Exception
    {
        // Used to indicate that the expected SearchNotExhaustiveException was thrown.
        boolean exceptionThrown = false;

        // Set up a simple search space over a tree of depth 4 branching factor 4, no goal states.
        testSearch.addStartState(new TestTraversableState(4, 4));

        // There are (4^5-1)/(4-1) = 1023/3 = 341 plus           (4^4-1)/(4-1) = 255/3  =  85 plus
        // (4^3-1)/(4-1) = 63/3   =  21 plus           (4^2-1)/(4-1) = 15/3   =   5 plus           (4^1-1)/(4-1) = 3/3
        // =   1                                  = 453 states in the space. Set a maximum step count just less than
        // this, so that there should be just one element left in the queue when the max steps is encountered.
        testSearch.setMaxSteps(452);

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
     * Although the max is reached if the search has successfully completed it should return null and not throw a search
     * failure exception as it does normally when the max is reached.
     */
    public void testMaximumStepsReachedSearchCompletedReturnsNull() throws Exception
    {
        // Set up a simple search space over a tree of depth 4 branching factor 4, no goal states.
        testSearch.addStartState(new TestTraversableState(4, 4));

        // There are (4^5-1)/(4-1) = 1023/3 = 341 plus
        // (4^4-1)/(4-1) = 255/3  =  85 plus
        // (4^3-1)/(4-1) = 63/3   =  21 plus
        // (4^2-1)/(4-1) = 15/3   =   5 plus
        // (4^1-1)/(4-1) = 3/3    =   1
        // = 453 states in the space.
        // Set a maximum step count equal to this, so that there should be no elements left in the queue when the
        // max steps is encountered. Although the max is reached the search has successfully completed so it should
        // return null and not throw a search failure exception.
        testSearch.setMaxSteps(453);

        // Run the search and assert that it returns null.
        assertNull("The test search over TestTraversableState with 341 states, all non-goal, and a maximum step size " +
            "of 341 did not return null but the test should have successfully completed.", testSearch.findGoalPath());
    }

    /** @throws Exception */
    protected void setUp() throws Exception
    {
        NDC.push(getName());

        // Reset the search and clear any repeated state filters from it.
        testSearch.reset();
        testSearch.setRepeatedStateFilter(null);
    }

    /** @throws Exception */
    protected void tearDown() throws Exception
    {
        NDC.pop();
    }
}
