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
package com.thesett.aima.learning;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.log4j.NDC;

import com.thesett.aima.state.State;

/**
 * LearningMethodUnitTestBase is a pure unit test base class for deriving test classes for implementations of the
 * {@link LearningMethod} interface. The class name deliberately does not end in Test so that it will not be run as a
 * unit test by default. There is no suitable constructor for building and calling objects of this class from JUnit. It
 * is designed to be called explicitly from sub-classes that implement unit tests for specific learning methods that
 * re-use the tests defined here.
 *
 * <p>Classifying machines and the algorithms that train them are somewhat inseperable; only certain learning algorithms
 * can be applied to certain machines. For testing purposes this makes the complete seperation of these classes a little
 * complicated. Certainly it is possible to create a classifying machine without a learning method, just by setting it
 * up by hand. It is not usually possible to test a learning method without a machine to operate on. It may be possible
 * to really isolate the learning method by providing a mock machine to train and writing tests to check that its
 * methods are correctly called. Often a learning method and a classifying machine will be taken together and tested in
 * a pair. This class provides the learning side of the tests. See {@link ClassifyingMachineUnitTestBase} for the test
 * class for the learning side of the tests.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that the learning method accepts the machine to train.
 * <tr><td> Check that the learning method can learn a machine without failing.
 * <tr><td> Check that the learning method can learn a property other than 'goal'.
 * <tr><td> Check that a learning method fails when trying to learn a property that does not exist.
 * <tr><td> Check that a learning method can learn a restricted set of input properties.
 * <tr><td> Check that a learning method fails when instructed to learn from an input that does not exist.
 * <tr><td> Check that a learning method fails when instructed to learn with no training examples.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class LearningMethodUnitTestBase extends TestCase
{
    /** Used for debugging. */
    /* private static final Logger log = Logger.getLogger(LearningMethodUnitTestBase.class.getName()); */

    /** The {@link LearningMethod} to test. */
    LearningMethod testLearningMethod;

    /** The {@link ClassifyingMachine} to run the test learning method on. */
    ClassifyingMachine testClassifyingMachine;

    /**
     * Builds the tests to be run on a supplied learning method implementation. This allows the tests in this class to
     * be applied to arbitrary implementations of learning methods in sub-classes of this test class. A classification
     * machine that is compatible with the learning method must also be supplied.
     *
     * @param testName           the name of the unit test.
     * @param testLearningMethod the {@link LearningMethod} to test.
     * @param testMachine        the {@link ClassifyingMachine} to be trained by the test learning method.
     */
    public LearningMethodUnitTestBase(String testName, LearningMethod testLearningMethod,
        ClassifyingMachine testMachine)
    {
        super(testName);

        // Keep reference to the learning method to test and machine to train.
        this.testLearningMethod = testLearningMethod;
        this.testClassifyingMachine = testMachine;
    }

    /** Check that the learning method accepts the machine to train. */
    public void testAcceptsMachineToTrain() throws Exception
    {
        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);
    }

    /** Check that the learning method can learn a machine without failing. */
    public void testCanLearnOk() throws Exception
    {
        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Load the training data.
        testLearningMethod.addExampleStates(RestaurantsDataState.trainingData);

        // Get a trained classifying machine from the learning method.
        ClassifyingMachine trainedMachine = testLearningMethod.learn();
    }

    /** Check that the learning method can learn a property other than 'goal'. */
    public void testCanLearnArbitraryProperty() throws Exception
    {
        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Load the training data (the addExampleState method is exercised here).
        for (State example : RestaurantsDataState.trainingData)
        {
            testLearningMethod.addExampleState(example);
        }

        // Set the learning method to learn the 'patrons' property. This will attempt to learn how full restaurantss
        // are given different conditions.
        testLearningMethod.addGoalProperty("patrons");

        // Get a trained classifying machine from the learning method.
        ClassifyingMachine trainedMachine = testLearningMethod.learn();
    }

    /** Check that a learning method fails when trying to learn a property that does not exist. */
    public void testLearningFailsNonExistantProperty() throws Exception
    {
        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Load the training data.
        testLearningMethod.addExampleStates(RestaurantsDataState.trainingData);

        // Set the learning method to learn the 'nonexistant' property.
        testLearningMethod.addGoalProperty("nonexistant");

        // Used to indicate that the expected exception was thrown.
        boolean testPassed = false;

        // Check that an exception is thrown when trying to learn.
        try
        {
            // Get a trained classifying machine from the learning method.
            ClassifyingMachine trainedMachine = testLearningMethod.learn();
        }
        catch (LearningFailureException e)
        {
            testPassed = true;
        }

        // Check that the exception was thrown.
        assertTrue("Learning on a non-existant property should have failed.", testPassed);
    }

    /** Check that a learning method can learn a restricted set of input properties. */
    public void testCanLearnWithRestrictedInputProperties() throws Exception
    {
        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Load the training data.
        testLearningMethod.addExampleStates(RestaurantsDataState.trainingData);

        // Restrict the input properties to a subset of what is available (the addInputProperties method is
        // exercised here).
        Collection<String> inputProperties = new ArrayList<String>();

        inputProperties.add("raining");
        inputProperties.add("price");
        inputProperties.add("type");
        testLearningMethod.addInputProperties(inputProperties);

        testLearningMethod.addInputProperty("friSat");
        testLearningMethod.addInputProperty("patrons");
        testLearningMethod.addInputProperty("wait");

        // Get a trained classifying machine from the learning method.
        ClassifyingMachine trainedMachine = testLearningMethod.learn();
    }

    /**
     * Check that a learning method fails when instructed to learn from an input that does not exist.
     *
     * @todo Decide if it should fail if just one is missing, or if it needs to be all of them. In general learning with
     *       some missing properties is possible. If all examples are missing a property could just drop that property
     *       from the input set.
     */
    public void testLearningFailsNonExistantInputProperties() throws Exception
    {
        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Load the training data.
        testLearningMethod.addExampleStates(RestaurantsDataState.trainingData);

        // Set the learning method to learn the 'nonexistant' property.
        testLearningMethod.addInputProperty("nonexistant");

        // Used to indicate that the expected exception was thrown.
        boolean testPassed = false;

        // Check that an exception is thrown when trying to learn.
        try
        {
            // Get a trained classifying machine from the learning method.
            ClassifyingMachine trainedMachine = testLearningMethod.learn();
        }
        catch (LearningFailureException e)
        {
            testPassed = true;
        }

        // Check that the exception was thrown.
        assertTrue("Learning on a non-existant input property should have failed.", testPassed);
    }

    /** Check that a learning method fails when instructed to learn with no training examples. */
    public void testLearningFailsNoExamples() throws Exception
    {
        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Don't load the training data.

        // Used to indicate that the expected exception was thrown.
        boolean testPassed = false;

        // Check that an exception is thrown when trying to learn.
        try
        {
            // Get a trained classifying machine from the learning method.
            ClassifyingMachine trainedMachine = testLearningMethod.learn();
        }
        catch (LearningFailureException e)
        {
            testPassed = true;
        }

        // Check that the exception was thrown.
        assertTrue("Learning with no training examples should have failed.", testPassed);
    }

    /** @throws Exception */
    protected void setUp() throws Exception
    {
        NDC.push(getName());

        // Reset the learning method.
        testLearningMethod.reset();
    }

    /** @throws Exception */
    protected void tearDown() throws Exception
    {
        NDC.pop();
    }

    /*
     * Compile all the tests for the default test implementation of a learning method into a test suite.
     */
    /*
     * public static Test suite() { // Build a new test suite TestSuite suite = new TestSuite("LearningMethod Tests");
     *
     * // Add all the tests defined in this class (using the default constructor)
     * //suite.addTestSuite(LearningMethodTest.class);
     *
     * return suite;}*/
}
