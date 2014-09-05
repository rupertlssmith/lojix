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
package com.thesett.aima.learning.decisiontree.prototype;

import java.util.HashMap;
import java.util.Map;

import com.thesett.aima.learning.ClassifyingFailureException;
import com.thesett.aima.learning.decisiontree.DecisionTreeMachine;
import com.thesett.aima.state.OrdinalAttribute;
import com.thesett.aima.state.State;

/**
 * ProtoDTMachine is a classifying machine that is backed by a decision tree. It takes a state and classifies it by
 * passing the state down its decision tree, branching on the assigned values of its properties at each decision node,
 * until is reaches a leaf that assigns a classification to the state. It returns this as its belief about the
 * classification of the state.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Perform a classification on a state using a decision tree <td> {@link DecisionTree}
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Decide what happens if a state is missing a decision property?
 * @todo   Decide what happens if a state has a property but its attribute value does not match the type expected by the
 *         decision tree. Could either throw an exception or behave as if the property is missing.
 */
public class ProtoDTMachine implements DecisionTreeMachine
{
    /** Used to hold the decision tree that backs the machine. */
    private DecisionTree dt;

    /**
     * Sets the decision tree that the machine operates on.
     *
     * @param tree The decision tree to control the dt machine with.
     */
    public void setDecisionTree(DecisionTree tree)
    {
        this.dt = tree;
    }

    /**
     * Classifies a state using the decision tree.
     *
     * @param  state The input data point to classify.
     *
     * @return A classification of the data point as an mapping from the output property that the decision tree was
     *         trained on to this classifiers belief abouts its value.
     *
     * @throws ClassifyingFailureException If the decision tree is not completed and this classifier encounters a
     *                                     pending node in the tree that is still to be built.
     */
    public Map<String, OrdinalAttribute> classify(State state) throws ClassifyingFailureException
    {
        // Start at the root of the decision tree.
        DecisionTree currentNode = dt;

        // Loop down the decision tree until a leaf node is found.
        while (true) // !currentNode.isLeaf())
        {
            DecisionTreeElement element = currentNode.getElement();

            // Check that the current element really is a decision.
            if (element instanceof Decision)
            {
                Decision decision = (Decision) element;

                // Apply the decision at the current node to the state to be classified to get a new tree.
                currentNode = decision.decide(state); // , currentNode);
            }
            else if (element instanceof Assignment)
            {
                // Cast the element to an Assignment as this is the only type of leaf that is possible.
                Assignment assignment = (Assignment) element;

                // Return the assignment in a map.
                Map<String, OrdinalAttribute> assignmentMap = new HashMap<String, OrdinalAttribute>();

                assignmentMap.put(assignment.getPropertyName(), assignment.getAttribute());

                return assignmentMap;
            }

            // It is possible that a node may be of type Pending if an incomplete tree has been used to
            // run this classification on.
            else
            {
                // Throw a classification exception due to an incomplete decision tree.
                throw new ClassifyingFailureException("A node which is not a decision was encountered.", null);
            }
            // What happens if the decision could not operate on the state, either because of a missing property or
            // because its property was not of the type that the decision was expecting? Can either throw an exception,
            // return an empty assignment, or implement an algorithm for coping with missing properties.
        }
    }
}
