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

import java.util.ArrayList;

import com.thesett.common.util.SimpleTree;

/**
 * A DecisionTree is a tree, the nodes of which contain {@link Decision}s and the leaves of which contain
 * {@link Assignment}s. A DecisionTree can be interpreted by a {@link ProtoDTMachine} to assign a classification to
 * {@link com.thesett.aima.state.State} by taking decisions based on the properties of that state.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Build a tree for a leaf assignment <td> {@link Assignment}
 * <tr><td> Build a tree for a decision node.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DecisionTree extends SimpleTree<DecisionTreeElement>
{
    /**
     * Builds a leaf of a decision tree. This is a concrete assignment of an attribute to a property (a classification).
     *
     * @param assignment The leaf assignment that is the outcome of the decision when this leaf is reached.
     */
    public DecisionTree(Assignment assignment)
    {
        // Create an assignment and call the super constuctor for a leaf with it.
        super(assignment);
    }

    /**
     * Builds a leaf of a decision tree that is actually a special marker for a piece of the tree that is still under
     * construction.
     *
     * @param pending The pending node that contains enough information to later continue construction of the decision
     *                tree from this point onwards.
     */
    public DecisionTree(Pending pending)
    {
        // Create an assignment and call the super constuctor for a leaf with it.
        super(pending);
    }

    /**
     * Builds a decision node of a decision tree.
     *
     * @param decision The decision node to place at this point in the tree.
     */
    public DecisionTree(Decision decision)
    {
        // Call the super constructor for a node, creating enough place holders for all the decisions children.
        super(decision, new ArrayList<DecisionTree>(decision.getNumOutcomes()));
    }
}
