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

import com.thesett.aima.search.impl.BaseQueueSearchTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

/**
 * AStarSearchTest is a pure unit test class for the {@link AStarSearch} class.
 *
 * @author Rupert Smith
 */
public class AStarSearchTest extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(AStarSearchTest.class.getName()); */

    /** Default constructor that will result in the tests being run on a depth first search. */
    public AStarSearchTest(String testName)
    {
        super(testName);
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("AStarSearch Tests");

        // Add all tests defined in the SearchNodeTest class
        suite.addTest(new BaseQueueSearchTest("testStartNodeEnqueued", new AStarSearch(new DummyHeuristic())));
        suite.addTest(new BaseQueueSearchTest("testRepeatedStateFilterReset", new AStarSearch(new DummyHeuristic())));
        suite.addTest(new BaseQueueSearchTest("testNoStartStateFails", new AStarSearch(new DummyHeuristic())));
        suite.addTest(new BaseQueueSearchTest("testNoGoalSearchReturnsNull", new AStarSearch(new DummyHeuristic())));
        suite.addTest(new BaseQueueSearchTest("testSearchReturnsGoal", new AStarSearch(new DummyHeuristic())));
        suite.addTest(new BaseQueueSearchTest("testEncounteredNonGoalStatesExpanded",
                new AStarSearch(new DummyHeuristic())));
        suite.addTest(new BaseQueueSearchTest("testMaximumStepsReachedFails", new AStarSearch(new DummyHeuristic())));
        suite.addTest(new BaseQueueSearchTest("testMaximumStepsReachedSearchCompletedReturnsNull",
                new AStarSearch(new DummyHeuristic())));

        // Add all the tests defined in this class (using the default constructor)
        // suite.addTestSuite(AStarSearchTest.class);

        return suite;
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
