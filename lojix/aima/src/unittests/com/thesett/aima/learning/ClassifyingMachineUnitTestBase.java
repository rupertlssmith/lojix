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

import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.NDC;

import com.thesett.aima.state.OrdinalAttribute;
import com.thesett.aima.state.State;

/**
 * ClassifyingMachineUnitTestBase is a pure unit test base class for deriving test classes for implementations of the
 * {@link ClassifyingMachine} interface. The class name deliberately does not end in Test so that it will not be run as
 * a unit test be default. There is no suitable constructor for building and calling objects of this class from JUnit.
 * It is designed to be called explicitly from sub-classes that implement unit tests for specific classifying machines
 * that re-use the tests defined here.
 *
 * <p>Classifying machines and the algorithms that train them are somewhat inseperable; only certain learning algorithms
 * can be applied to certain machines. For testing purposes this makes the complete seperation of these classes a little
 * complicated. Certainly it is possible to create a classifying machine without a learning method, just by setting it
 * up by hand. It is not usually possible to test a learning method without a machine to operate on. It may be possible
 * to really isolate the learning method by providing a mock machine to train and writing tests to check that its
 * methods are correctly called. Often a learning method and a classifying machine will be taken together and tested in
 * a pair. This class provides the classifying machine side of the tests. See {@link LearningMethodUnitTestBase} for the
 * test class for the learning side of the tests.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that the machine, once trained, can classify examples (possible empty classification).
 * <tr><td> Check that the machine, once trained, can classify examples (classificaton never empty).
 * <tr><td> Check that the machine, once trained, scores 100% correct on its training data.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ClassifyingMachineUnitTestBase extends TestCase
{
    /** Used for debugging. */
    /* private static final Logger log = Logger.getLogger(ClassifyingMachineUnitTestBase.class.getName()); */

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
    public ClassifyingMachineUnitTestBase(String testName, LearningMethod testLearningMethod,
        ClassifyingMachine testMachine)
    {
        super(testName);

        // Keep reference to the learning method to test and machine to train.
        this.testLearningMethod = testLearningMethod;
        this.testClassifyingMachine = testMachine;
    }

    /**
     * Check that the learning method produces a trained machine that can classify for its learned goal property.
     *
     * <p>This test skips checking that the classification returned by the machine is not empty. Use
     * {@link #testCanClassifyOkAllowingEmpty} instead to perform this check on machines that should never return empty
     * classifications.
     */
    public void testCanClassifyOkAllowingEmpty() throws Exception
    {
        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Load the training data.
        testLearningMethod.addExampleStates(RestaurantsDataState.trainingData);

        // Get a trained classifying machine from the learning method. This could actually be a brand new machine
        // not even of the same class as the one passed in to the test.
        ClassifyingMachine trainedMachine = testLearningMethod.learn();

        // Check that the machine can classify.
        for (State testState : RestaurantsDataState.testData)
        {
            Map<String, OrdinalAttribute> classification = trainedMachine.classify(testState);

            // Allow the classification to be empty.
        }
    }

    /**
     * Check that the learning method produces a trained machine that can classify for its learned goal property.
     *
     * <p>This test may not be correct for some machine that might properly give no assignments at all in cases where
     * they legitimately decide that no classification is appropriate. If this is the case for a particular type of
     * machine then do not run this test on the machine. Use {@link #testCanClassifyOkAllowingEmpty} instead.
     */
    public void testCanClassifyOk() throws Exception
    {
        String errorMessage = "";

        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Load the training data.
        testLearningMethod.addExampleStates(RestaurantsDataState.trainingData);

        // Get a trained classifying machine from the learning method. This could actually be a brand new machine
        // not even of the same class as the one passed in to the test.
        ClassifyingMachine trainedMachine = testLearningMethod.learn();

        // Check that the machine can classify.
        for (State testState : RestaurantsDataState.testData)
        {
            Map<String, OrdinalAttribute> classification = trainedMachine.classify(testState);

            // Check that the classification is not empty. At least one property assignment should be given
            // by the machine.
            if (classification == null)
            {
                errorMessage += "The classification was null for the test example, " + testState + ".\n";
            }

            if (classification.size() < 1)
            {
                errorMessage += "The classificaiton was empty for the test example, " + testState + ".\n";
            }

            // Check that the classification contains the goal property that was to be learnt.
            if (classification.get("goal") == null)
            {
                errorMessage +=
                    "The classificiation did not contain the goal property for test example, " + testState + ".\n";
            }
        }

        // Assert that there are no error messages and fail with them if there are.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /**
     * Check that the learning method produces a trained machine that correctly classifies the training examples it was
     * built on. Not all learning methods/data will produce 100% correct learning on their test data. For example, the
     * test data may be noisy, making 100% impossible. The learning algorithm may only be approximate. Use this test
     * only on learning methods with non-noisy data that are expected to be 100% correct on their training data.
     */
    public void testCorrectFunctionLearnedOnTrainingData() throws Exception
    {
        String errorMessage = "";

        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Load the training data.
        testLearningMethod.addExampleStates(RestaurantsDataState.trainingData);

        // Get a trained classifying machine from the learning method. This could actually be a brand new machine
        // not even of the same class as the one passed in to the test.
        ClassifyingMachine trainedMachine = testLearningMethod.learn();

        // Check that the machine classifies 100% correctly on its training data.
        for (State testState : RestaurantsDataState.trainingData)
        {
            Map<String, OrdinalAttribute> classification = trainedMachine.classify(testState);

            // Extract the goal property from the training data.
            OrdinalAttribute goalAttribute = (OrdinalAttribute) testState.getProperty("goal");

            // Extract the goal property from the classifying machine.
            OrdinalAttribute testAttribute = classification.get("goal");

            // Check that they match.
            if (!goalAttribute.equals(testAttribute))
            {
                errorMessage +=
                    "The training example, " + testState + ", with goal property, " + goalAttribute +
                    " is not correctly classified by the trained machine. It gives, " + testAttribute + " instead.\n";
            }
        }

        // Assert that there are no error messages and fail with them if there are.
        assertTrue(errorMessage, "".equals(errorMessage));
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
}
