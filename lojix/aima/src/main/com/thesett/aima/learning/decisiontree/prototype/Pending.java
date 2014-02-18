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

import java.util.Collection;

import com.thesett.aima.state.OrdinalAttribute;
import com.thesett.aima.state.State;

/**
 * Pending represents a node on a decision tree that has not been built yet. A pending node is a temporary place-holder
 * in a decision tree that is created when a learning algorithm must stop creating a branch of the decision tree in
 * order to create other branches but wants to later come back an finish the pending branch. It contains enough
 * information to allow a decision tree learning algorithm to later come back and finish the job.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Act as temporary place holder for a set of example training data that reaches this node.
 * <tr><td> Act as temporary place holder for the set of properties that have still to be decided on at this node.
 * <tr><td> Act as temporary place holder for the majority decision outcome for the training data that reaches
 *          this node.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Pending extends DecisionTreeElement
{
    /** The training examples that reach this node. */
    Collection<State> examples;

    /** The properties that have not already been decided on. */
    Collection<String> undecidedProperties;

    /** The default majority-vote classification at this node. */
    OrdinalAttribute majorityVote;

    /**
     * Creates a pending node from a set of examples, a set of properties to classify the examples by and an attribute
     * value which the majority of the examples are classified as (to be used as a default when there are no more
     * properties to classify by.
     *
     * @param examples   A collection of state objects that hold the data to be classified.
     * @param properties The names of the properties of the state objects that can be used to determine its
     *                   classification.
     * @param majority   The majority classification for (the goal property of) the examples.
     * @param matching   The matching attribute value that a parent decision must match in order to arrive at this
     *                   assignment.
     */
    public Pending(Collection<State> examples, Collection<String> properties, OrdinalAttribute majority,
        OrdinalAttribute matching)
    {
        this.examples = examples;
        this.undecidedProperties = properties;
        this.majorityVote = majority;
        attributeValue = matching;
    }

    /**
     * Gets the example training data points that reach this node.
     *
     * @return The example training states that reach this node.
     */
    public Collection<State> getExamples()
    {
        return examples;
    }

    /**
     * Gets the list of properties that are still available to decide on by this node.
     *
     * @return The undecided properties at this node.
     */
    public Collection<String> getUndecidedProperties()
    {
        return undecidedProperties;
    }

    /**
     * Gets the default majority assignment outcome to the decision for the training points that reach this node.
     *
     * @return The majority classification of the data points at this node.
     */
    public OrdinalAttribute getDefault()
    {
        return majorityVote;
    }
}
