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
package com.thesett.aima.learning.decisiontree.prototype;

import com.thesett.aima.learning.ClassifyingFailureException;
import com.thesett.aima.learning.ClassifyingMachineUnitTestBase;
import com.thesett.aima.learning.RestaurantsDataState;
import com.thesett.aima.state.State;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * ProtoDTMachineTest is a pure unit test class for the {@link ProtoDTMachine} class.
 *
 * <pre><p/><table id="crc"><caption>CRC Cad</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that the machine throws an exception when run on an unfinished tree.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ProtoDTMachineTest extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(ProtoDTMachineTest.class.getName()); */

    /**
     * Creates a new ProtoDTMachineTest object.
     *
     * @param testName
     */
    public ProtoDTMachineTest(String testName)
    {
        super(testName);
    }

    /**
     * Compile all the tests for the default test implementation of a prototype decision tree learning method into a
     * test suite.
     */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("ProtoDTMachine Tests");

        // Add all tests defined in the ClassifyingMachineUnitTestBase class
        suite.addTest(new ClassifyingMachineUnitTestBase("testCanClassifyOkAllowingEmpty", new ProtoDTLearningMethod(),
                new ProtoDTMachine()));
        suite.addTest(new ClassifyingMachineUnitTestBase("testCanClassifyOk", new ProtoDTLearningMethod(),
                new ProtoDTMachine()));
        suite.addTest(new ClassifyingMachineUnitTestBase("testCorrectFunctionLearnedOnTrainingData",
                new ProtoDTLearningMethod(), new ProtoDTMachine()));

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(ProtoDTMachineTest.class);

        return suite;
    }

    /** Check that the machine throws an exception when run on an unfinished tree. */
    public void testClassifyingFailsOnIncompleteTree() throws Exception
    {
        // Create a classifying machine.
        ProtoDTMachine machine = new ProtoDTMachine();

        // Create a decision tree consisting of a single pending node and set this as the decision tree for the machine
        // to use.
        DecisionTree tree = new DecisionTree(new Pending(null, null, null, null));

        machine.setDecisionTree(tree);

        // Try to classifiy an example and check that it throws a classifying failure exception.
        boolean testPassed = false;

        State testState = RestaurantsDataState.testData.iterator().next();

        try
        {
            machine.classify(testState);
        }
        catch (ClassifyingFailureException e)
        {
            testPassed = true;
        }

        assertTrue("A ClassifyingFailureException should have been thrown when trying to run a classifying machine " +
            "on an incomplete decision tree.", testPassed);
    }
}
