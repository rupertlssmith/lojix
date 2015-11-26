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

import java.util.Iterator;

import com.thesett.aima.state.OrdinalAttribute;
import com.thesett.aima.state.State;
import com.thesett.common.util.Tree;

/**
 * A Decision is a switch on the {@link OrdinalAttribute} value of the property of a
 * {@link com.thesett.aima.state.State}. For each possible value of the attribute a decision maps onto a decision tree
 * which implements the remainder of the decision process onto a classification. In addition, a Decision also specifies
 * the output attribute value that a preceding decision must find in the input state in order to procede to this
 * decision.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Perform a choice based on the value of a property of a state.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Decision extends DecisionTreeElement
{
    /* Holds the entropy of the decision.*/
    //private double entropy;

    /* Holds the information gain of the decision.*/
    //private double gain;

    /* Holds the gain ratio of the decision.*/
    //private double gainRatio;

    /** Holds the name of the property that this decision acts on. */
    private final String propertyName;

    /** Holds the number of outcomes that the decision can have. */
    private final int numOutcomes;

    /**
     * Holds a lookup table from the ordinal of the possible attribute values to the child decision tree that computes
     * the remainder of the decision.
     */
    private DecisionTree[] decisions;

    /**
     * Builds a decision on the named property with the specified number of outcomes. The number of decision outcomes is
     * equal to the number of possible values that the property can take on.
     *
     * @param property    The property to decide on.
     * @param numOutcomes The number of possible values that the property can take (this must be finite).
     * @param matching    the matching attribute value that a parent decision must match in order to arrive at this
     *                    decision node.
     */
    public Decision(String property, int numOutcomes, OrdinalAttribute matching)
    {
        // Keep the property name and number of possible values.
        this.propertyName = property;
        this.numOutcomes = numOutcomes;

        // Keep the attribute that the parent decision must match to follow this branch in the decision tree.
        attributeValue = matching;

        // Create a lookup table big enough to hold all the possible outcomes.
        decisions = new DecisionTree[numOutcomes];
    }

    /**
     * Gets the number of outcomes that this decision can have.
     *
     * @return The number of possible outcomes that this decision can have.
     */
    public int getNumOutcomes()
    {
        return numOutcomes;
    }

    /**
     * Performs the actual decision based on a property of the state. If the quick lookup table has been initialized
     * then the decision is taken straight from it. If not then the supplied reference to the decision tree at this
     * point is used to find the outcome by scanning over its children.
     *
     * @param  state the {@link com.thesett.aima.state.State} to decide on.
     *
     * @return a {@link DecisionTree} to continue the decision process with.
     */
    public DecisionTree decide(State state)
    {
        // Extract the value of the property being decided from state to be classified.
        OrdinalAttribute attributeValue = (OrdinalAttribute) state.getProperty(propertyName);

        // Extract the child decision tree that matches the property value, using the attributes ordinal for a quick
        // look up.
        return decisions[attributeValue.ordinal()];
    }

    /**
     * Initializes the lookup table for this decision node. The specified decision tree that corresponds to this node is
     * used to extract all the possible outcomes for this decision and these are stored in a lookup table so that future
     * decisions made with this tree will run faster.
     *
     * @param thisNode the decision tree that this decision node is the element from.
     */
    public void initializeLookups(DecisionTree thisNode)
    {
        // Scan over all the decision trees children at this point inserting them into the lookup table depending
        // on the ordinal of the attribute value that matches them.
        for (Iterator<Tree<DecisionTreeElement>> i = thisNode.getChildIterator(); i.hasNext();)
        {
            DecisionTree nextChildTree = (DecisionTree) i.next();

            // Get the matching attribute value from the childs decision tree element.
            OrdinalAttribute matchingValue = nextChildTree.getElement().getAttributeValue();

            // Insert the matching sub-tree into the lookup table.
            decisions[matchingValue.ordinal()] = nextChildTree;
        }
    }
}
