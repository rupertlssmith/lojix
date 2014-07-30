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

import com.thesett.aima.search.impl.BaseQueueSearchTest;

/**
 * CostBoundedSearchTst is a pure unit test class for the {@link CostBoundedSearch} class.
 *
 * @author Rupert Smith
 */
public class CostBoundedSearchTest extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(CostBoundedSearchTest.class.getName()); */

    /** Default constructor that will result in the tests being run on a depth first search. */
    public CostBoundedSearchTest(String testName)
    {
        super(testName);
    }

    /** Compile all the tests for the default test implementation of a search node into a test suite. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("CostBoundedSearch Tests");

        // Add all tests defined in the BaseQueueSearcTest class.
        // These all go up to depth 4 and have cost of 1.0f per level so set 5.0f as the max so the cost bound doesn't
        // interfere with those test results.
        suite.addTest(new BaseQueueSearchTest("testStartNodeEnqueued", new CostBoundedSearch(5.0f)));
        suite.addTest(new BaseQueueSearchTest("testRepeatedStateFilterReset", new CostBoundedSearch(5.0f)));
        suite.addTest(new BaseQueueSearchTest("testNoStartStateFails", new CostBoundedSearch(5.0f)));
        suite.addTest(new BaseQueueSearchTest("testNoGoalSearchReturnsNull", new CostBoundedSearch(5.0f)));
        suite.addTest(new BaseQueueSearchTest("testSearchReturnsGoal", new CostBoundedSearch(5.0f)));
        suite.addTest(new BaseQueueSearchTest("testSearchReturnsMultipleGoals", new CostBoundedSearch(5.0f)));
        suite.addTest(new BaseQueueSearchTest("testEncounteredNonGoalStatesExpanded", new CostBoundedSearch(5.0f)));
        suite.addTest(new BaseQueueSearchTest("testMaximumStepsReachedFails", new CostBoundedSearch(5.0f)));
        suite.addTest(new BaseQueueSearchTest("testMaximumStepsReachedSearchCompletedReturnsNull",
                new CostBoundedSearch(5.0f)));

        // Add all the tests defined in this class (using the default constructor)
        // suite.addTestSuite(CostBoundedSearchTest.class);

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
