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
package com.thesett.aima.learning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.thesett.aima.state.State;

/**
 * AbstractLearningMethod provides an implementation of the convenience methods of {@link LearningMethod} and provides a
 * base class from which to derive learning methods.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Accept an input example set.
 * <tr><td> Accept a property set to learn how to classify.
 * <tr><td> Accept a property set to use to learn the classification from.
 * <tr><td> Accept a classifying machine to train.
 * <tr><td> Accept a maximum number of learning steps allowable.
 * <tr><td> Reset the learning method.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class AbstractLearningMethod implements LearningMethod
{
    /* Used for logging. */
    /* private static final Logger log = Logger.getLogger(AbstractLearningMethod.class.getName()); */

    /** Holds the maximum number of steps that the learning algorithm should attempt before quiting. */
    protected int maxSteps;

    /** Holds the classifying machine to train. */
    protected ClassifyingMachine machineToTrain;

    /** Holds the input example collection. */
    protected Collection<State> inputExamples;

    /** Holds the set of input properties to use. */
    protected Set<String> inputProperties;

    /** Holds the set of output properties to learn. */
    protected Set<String> outputProperties;

    /**
     * Used to indicate that a specific set of input properties has been set, rather than the default of all properties
     * of the example states (minus the goal ones).
     */
    protected boolean inputPropertiesSet = false;

    /** Used to indicate that properties other than the 'goal' property have been set as the output properties. */
    protected boolean outputPropertiesSet = false;

    /** Default constructor that initializes the data structures. */
    public AbstractLearningMethod()
    {
        inputExamples = new ArrayList<State>();
        inputProperties = new HashSet<String>();
        outputProperties = new HashSet<String>();
    }

    /**
     * Implement this abstract method to provide the actual learning algorithm.
     *
     * @return A trained {@link ClassifyingMachine}.
     *
     * @throws LearningFailureException if the learning process fails for any reason.
     */
    public abstract ClassifyingMachine learn() throws LearningFailureException;

    /*
     * Sets the maximum number of search steps that a learning method may take. If it fails to find a solution before
     * this number of steps has been reached its {@link #learn} method should either return the best complete classifying
     * machine that it can or fail with an exception. What exactly constitutes a single step, and the granularity of the
     * step size, is open to different interpretation by different search algorithms. The recommended guideline is that
     * this is the maximum number of test examples that the learning algorithm should examine.
     *
     * @param max the maximum number of test examples that the learning algorithm should examine before failing.
     */
    // public void setMaxSteps(int max) { // Keep the maximum steps this.maxSteps = max;}

    /**
     * Adds an example state to learn from to this learning method.
     *
     * @param exampleState An example state to learn from.
     */
    public void addExampleState(State exampleState)
    {
        // Add the example to the existing ones.
        inputExamples.add(exampleState);
    }

    /**
     * Adds many examples states to learn from to this learning method.
     *
     * @param exampleStates A collection of example states to learn from.
     */
    public void addExampleStates(Collection<? extends State> exampleStates)
    {
        // Add the examples to the existing ones.
        inputExamples.addAll(exampleStates);
    }

    /**
     * Adds a property of the example states that the learning algorithm should use to learn its classification from.
     * Generally, algorithms are allowed to use any of the example states properties (except for cheating by using the
     * goal properties). Setting input properties with this method should restrict the learning algorithm to using just
     * those properties to learn its classification from.
     *
     * @param property The name of a property in the example states that is to be considered as input to the learning
     *                 process.
     */
    public void addInputProperty(String property)
    {
        inputPropertiesSet = true;

        // Add the input property to the existing ones.
        inputProperties.add(property);
    }

    /**
     * Adds many properties of the example states that the learning algorithm should use to learn its classification
     * from. Generally, algorithms are allowed to use any of the example states properties (except for cheating by using
     * the goal properties). Setting input properties with this method should restrict the learning algorithm to using
     * just those properties to learn its classification from.
     *
     * @param properties A collection of property names in the example states that are to be considered as input to the
     *                   learning process.
     */
    public void addInputProperties(Collection<String> properties)
    {
        inputPropertiesSet = true;

        // Add the input properties to the existing ones.
        inputProperties.addAll(properties);
    }

    /**
     * Adds a property of the example states that a classification is to be learnt for. If this is an unsupervised
     * learning method then it may not accept some properties to classify by as its goal will be to find clusterings of
     * the example states by the properties it chooses. If this is the case then the method should throw an exception to
     * indicate that it is not supported.
     *
     * <p>All states support the 'goal' boolean property. Generally, algorithms will assume that this is the property to
     * learn how to classify for unless different properties are set by calling this method.
     *
     * @param  property The name of a property in the set of example states that is to be considered the output to learn
     *                  for.
     *
     * @throws UnsupportedOperationException if the learning method does not accept goal properties.
     */
    public void addGoalProperty(String property)
    {
        outputPropertiesSet = true;

        // Add the goal property to the existing ones.
        outputProperties.add(property);
    }

    /**
     * Adds many properties of the example states that a classification is to be learnt for. If this is an unsupervised
     * learning method then it may not accept some properties to classify by as its goal will be to find clusterings of
     * the example states by the properties it chooses. If this is the case then the method should throw an exception to
     * indicate that it is not supported.
     *
     * <p>All states support the 'goal' boolean property. Generally, algorithms will assume that this is the property to
     * learn how to classify for unless different properties are set by calling this method.
     *
     * @param  properties A collection of property names in the set of example state that are considered to be the
     *                    output to learn for.
     *
     * @throws UnsupportedOperationException if the learning method does not accept goal properties.
     */
    public void addGoalProperties(Collection<String> properties)
    {
        outputPropertiesSet = true;

        // Add the goal properties to the existing ones.
        outputProperties.addAll(properties);
    }

    /**
     * Accepts a classifying machine to train. Not all learning methods can train all classifying machines. Generally,
     * the learning method and the machine must be compatible.
     *
     * @param machineToTrain A classifying machine that is to be trained by this learning method.
     */
    public void setMachineToTrain(ClassifyingMachine machineToTrain)
    {
        // Keep the machine to train.
        this.machineToTrain = machineToTrain;
    }

    /**
     * Resets the learning method. This should clear all the examples, properties to learn from and for and the input
     * machine to train.
     */
    public void reset()
    {
        maxSteps = 0;
        machineToTrain = null;
        inputExamples = new ArrayList<State>();
        inputProperties = new HashSet<String>();
        outputProperties = new HashSet<String>();
        inputPropertiesSet = false;
        outputPropertiesSet = false;
    }

    /**
     * This should be called at the start of the learn method to initialize the input and output property sets.
     *
     * @throws LearningFailureException If the set of training data and input and output properties are malformed.
     *                                  Either because the training data set is empty or because there are input or
     *                                  output properties that are not found in the training data set.
     */
    protected void initialize() throws LearningFailureException
    {
        // Check that at least one training example has been set.
        if (inputExamples.isEmpty())
        {
            throw new LearningFailureException("No training examples to learn from.", null);
        }

        // Check if an output property set to override the default was not set.
        if (!outputPropertiesSet)
        {
            // Set the 'goal' property as the default.
            addGoalProperty("goal");
        }

        // Check if an input property set to override the default was not set.
        if (!inputPropertiesSet)
        {
            // Extract all properties from the first example in the training data set as the input property set,
            // automatically excluding any properties which are in the output set.
            State example = inputExamples.iterator().next();
            Set<String> allProperties = example.getComponentType().getAllPropertyNames();

            inputProperties = new HashSet<String>(allProperties);
            inputProperties.removeAll(outputProperties);

            inputPropertiesSet = true;
        }

        // Check all the training examples have all the required input and output properties.
        for (State example : inputExamples)
        {
            Set<String> properties = example.getComponentType().getAllPropertyNames();

            String errorMessage = "";

            for (String inputProperty : inputProperties)
            {
                if (!properties.contains(inputProperty))
                {
                    errorMessage +=
                        "The training example, " + example + " does not contain the specified input property, " +
                        inputProperty + "\n";
                }
            }

            for (String outputProperty : outputProperties)
            {
                if (!properties.contains(outputProperty))
                {
                    errorMessage +=
                        "The training example, " + example + " does not contain the specified output property, " +
                        outputProperty + "\n";
                }
            }

            if (!"".equals(errorMessage))
            {
                throw new LearningFailureException(errorMessage, null);
            }
        }
    }
}
