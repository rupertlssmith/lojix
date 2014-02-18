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

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import org.apache.log4j.NDC;

import com.thesett.aima.attribute.impl.BooleanAttribute;
import com.thesett.aima.attribute.impl.EnumeratedStringAttribute;
import com.thesett.aima.learning.ClassifyingMachine;
import com.thesett.aima.learning.LearningFailureException;
import com.thesett.aima.learning.LearningMethod;
import com.thesett.aima.learning.LearningMethodUnitTestBase;
import com.thesett.aima.learning.RestaurantsDataState;
import com.thesett.aima.state.State;
import com.thesett.aima.state.impl.MapBackedState;

/**
 * ProtoDTLearningMethodTest is a pure unit test class for the {@link ProtoDTLearningMethod} class.
 *
 * <pre><p/><table id="crc"><caption>CRC Cad</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that learning fails if more than one output property to learn is set.
 * <tr><td> Check that a learning method can use a majority classification when not enough input properties to decide on.
 * <tr><td> Check that learning fails when trying to learn an output with infinite possible values.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ProtoDTLearningMethodTest extends TestCase
{
    /**  */
    /* private static final Logger log = Logger.getLogger(ProtoDTLearningMethodTest.class.getName()); */

    /** The {@link LearningMethod} to test. */
    LearningMethod testLearningMethod = new ProtoDTLearningMethod();

    /** The {@link ClassifyingMachine} to run the test learning method on. */
    ClassifyingMachine testClassifyingMachine = new ProtoDTMachine();

    /**
     * Creates a new ProtoDTLearningMethodTest object.
     *
     * @param testName
     */
    public ProtoDTLearningMethodTest(String testName)
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
        TestSuite suite = new TestSuite("HeuristicSearchNode Tests");

        // Add all tests defined in the SearchNodeTest class
        suite.addTest(new LearningMethodUnitTestBase("testAcceptsMachineToTrain", new ProtoDTLearningMethod(),
                new ProtoDTMachine()));

        suite.addTest(new LearningMethodUnitTestBase("testCanLearnOk", new ProtoDTLearningMethod(),
                new ProtoDTMachine()));
        suite.addTest(new LearningMethodUnitTestBase("testCanLearnArbitraryProperty", new ProtoDTLearningMethod(),
                new ProtoDTMachine()));
        suite.addTest(new LearningMethodUnitTestBase("testLearningFailsNonExistantProperty",
                new ProtoDTLearningMethod(), new ProtoDTMachine()));
        suite.addTest(new LearningMethodUnitTestBase("testCanLearnWithRestrictedInputProperties",
                new ProtoDTLearningMethod(), new ProtoDTMachine()));
        suite.addTest(new LearningMethodUnitTestBase("testLearningFailsNonExistantInputProperties",
                new ProtoDTLearningMethod(), new ProtoDTMachine()));
        suite.addTest(new LearningMethodUnitTestBase("testLearningFailsNoExamples", new ProtoDTLearningMethod(),
                new ProtoDTMachine()));

        // Add all the tests defined in this class (using the default constructor)
        suite.addTestSuite(ProtoDTLearningMethodTest.class);

        return suite;
    }

    /** Check that learning fails if more than one output property to learn is set. */
    public void testLearningFailsOnMoreThanOneOutputProperty()
    {
        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Load the training data.
        testLearningMethod.addExampleStates(RestaurantsDataState.trainingData);

        // Set the learning method to learn multiple output properties (the addGoalProperties method is exercised here).
        Collection<String> outputProperties = new ArrayList<String>();

        outputProperties.add("patrons");
        outputProperties.add("wait");
        testLearningMethod.addGoalProperties(outputProperties);

        // Used to indicate that the test has passed.
        boolean testPassed = false;

        // Try to Get a trained classifying machine from the learning method.
        try
        {
            ClassifyingMachine trainedMachine = testLearningMethod.learn();
        }
        catch (LearningFailureException e)
        {
            testPassed = true;
        }

        // Check that the test passed.
        assertTrue("A LearningFailureException should have been thrown because the prototype dt learner cannot " +
            "work with multiple outputs to learn.", testPassed);
    }

    /**
     * Check that a learning method can use a majority classification when not enough input properties to decide on. The
     * input property set is severly restricted to test the part of the algorithm that assigns the majority
     * classification once there are no more input properties to split on.
     */
    public void testCanLearnDefaultWhenTooFewInputProperties() throws Exception
    {
        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Load the training data.
        testLearningMethod.addExampleStates(RestaurantsDataState.trainingData);

        // Restrict the input properties to a subset of what is available.
        testLearningMethod.addInputProperty("raining");
        testLearningMethod.addInputProperty("price");

        // Get a trained classifying machine from the learning method.
        ClassifyingMachine trainedMachine = testLearningMethod.learn();
    }

    /** Check that learning fails when trying to learn an output with infinite possible values. */
    public void testLearningFailsWithInfiniteValuedOutput() throws Exception
    {
        // Set up the machine to train.
        testLearningMethod.setMachineToTrain(testClassifyingMachine);

        // Create an infinite valued attribute (an unfinalized string attribute).
        EnumeratedStringAttribute.EnumeratedStringAttributeFactory factory =
            EnumeratedStringAttribute.getFactoryForClass("infiniteValueTest");

        // Add an infinite valued property to each of the training data.
        Collection<State> trainingData = new ArrayList<State>();
        State testState = new MapBackedState();
        testState.setProperty("infinite", factory.createStringAttribute("test"));
        testState.setProperty("test", new BooleanAttribute(true));

        // Set up the infinite valued property as the goal property to learn.
        testLearningMethod.addGoalProperty("infinite");

        // Load the training data.
        testLearningMethod.addExampleStates(trainingData);

        // Used to indicate that the test has passed.
        boolean testPassed = false;

        // Try to Get a trained classifying machine from the learning method.
        try
        {
            ClassifyingMachine trainedMachine = testLearningMethod.learn();
        }
        catch (LearningFailureException e)
        {
            testPassed = true;
        }

        // Check that the test passed.
        assertTrue("Trying to learn an infinite valued output property should have failed.", true);
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
