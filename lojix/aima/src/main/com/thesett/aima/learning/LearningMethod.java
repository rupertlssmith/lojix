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

import java.util.Collection;

import com.thesett.aima.state.State;

/**
 * Interface for any class that implements a learning method. A learning method is an object that takes a collection of
 * example data points {@link com.thesett.aima.state.State} and a {@link ClassifyingMachine} for classifying the data
 * points and adjusts the classifying machine in order to improve its classifying accuracy over the data set. In the
 * case of supervised learning a method may also take a set of properties of the example data points that it is to learn
 * how to classify.
 *
 * <p>Learning methods accept a classifying machine to train. They also provide a method to extract the trained machine
 * from the learning method (once it has been trained). The machine returned by this method does not have to be the same
 * object as the original input machine. It could be the original machine object with its parameters tuned. It could
 * also be a completely new object built out of the old machine. This could be very useful, for example, for producing a
 * version of a classifying machine which once trained is then compiled into byte code which although it is the same
 * kind of machine as the original is an optimized compiled version of it.
 *
 * <p>LearningMethods in general canot be applied to just any {@link ClassifyingMachine}. Usually learning methods are
 * specific to particular machines or classes of machines.
 *
 * <p>Sometimes learning algorithms do not converge and for this reason learning methods should accept a maximum number
 * of steps parameter. If the maximum number of steps is reached before an algorithm has finished training a machine it
 * can either return the best complete machine it has encountered so far or throw an execption. The maximum steps
 * parameter may need to be interpreted differently for different kinds of algorithm but it is intended to correspond to
 * the total number of training examples examined by the learning process.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Accept an input example set.
 * <tr><td> Accept a property set to learn how to classify.
 * <tr><td> Accept a property set to use to learn the classification from.
 * <tr><td> Accept a classifying machine to train.
 * <tr><td> Generate an improved classifier.
 * <tr><td> Accept a maximum number of learning steps allowable.
 * <tr><td> Reset the learning method.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Consider adding a method to inject a derived feature generator. This will compute derived features of example
 *         states turning the original examples into ones with an enhanced property set.
 */
public interface LearningMethod
{
    /*
     * Sets the maximum number of steps that a learning method may take. If it fails to find a solution before
     * this number of steps has been reached its {@link #learn} method should either return the best complete classifying
     * machine that it can or fail with an exception. What exactly constitutes a single step, and the granularity of the
     * step size, is open to different interpretation by different algorithms. The recommended guideline is that
     * this is the maximum number of test examples that the learning algorithm should examine.
     *
     * @param max the maximum number of test examples that the learning algorithm should examine before failing.
     */
    // public void setMaxSteps(int max);

    /**
     * Adds an example state to learn from to this learning method.
     *
     * @param exampleState An example state to learn from.
     */
    public void addExampleState(State exampleState);

    /**
     * Adds many examples states to learn from to this learning method.
     *
     * @param exampleStates A collection of example states to learn from.
     */
    public void addExampleStates(Collection<? extends State> exampleStates);

    /**
     * Adds a property of the example states that the learning algorithm should use to learn its classification from.
     * Generally, algorithms are allowed to use any of the example states properties (except for cheating by using the
     * goal properties). Setting input properties with this method should restrict the learning algorithm to using just
     * those properties to learn its classification from.
     *
     * @param property The name of a property in the example states that is to be considered as input to the learning
     *                 process.
     */
    public void addInputProperty(String property);

    /**
     * Adds many properties of the example states that the learning algorithm should use to learn its classification
     * from. Generally, algorithms are allowed to use any of the example states properties (except for cheating by using
     * the goal properties). Setting input properties with this method should restrict the learning algorithm to using
     * just those properties to learn its classification from.
     *
     * @param properties A collection of property names in the example states that are to be considered as input to the
     *                   learning process.
     */
    public void addInputProperties(Collection<String> properties);

    /**
     * Adds a property of the example states that a classification is to be learnt for. If this is an unsupervised
     * learning method then it may not accept some properties to classify by as its goal will be to find clusterings of
     * the example states by the properties it chooses. If this is the case then the method should throw an exception to
     * indicate that it is not supported.
     *
     * <p>All states support the 'goal' boolean property. Generally, algorithms will assume that this is the property to
     * learn how to classify for unless different properties are set by calling this method.
     *
     * @param  property The name of a propert in the set of example states that is to be considered the output to learn
     *                  for.
     *
     * @throws UnsupportedOperationException if the learning method does not accept goal properties.
     */
    public void addGoalProperty(String property);

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
    public void addGoalProperties(Collection<String> properties);

    /**
     * Accepts a classifying machine to train.
     *
     * @param machineToTrain A classifying machine that is to be trained by this learning method.
     */
    public void setMachineToTrain(ClassifyingMachine machineToTrain);

    /**
     * Resets the learning method. This should clear all the examples, properties to learn from and for and the input
     * machine to train.
     */
    public void reset();

    /**
     * Trains the input classifying machine to produce one that is a better classifier.
     *
     * @return A trained {@link ClassifyingMachine}.
     *
     * @throws LearningFailureException if the learning process fails for any reason.
     */
    public ClassifyingMachine learn() throws LearningFailureException;
}
