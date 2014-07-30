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
package com.thesett.aima.search.util.informed;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.Successor;
import com.thesett.aima.search.Traversable;

/**
 * HeuristicSearchNode extends {@link SearchNode} with heuristic evaluations of search states. In addition to the
 * properties provided by SearchNode it provides two values, h and f. Both are real valued (floats). h is the heuristic
 * value and f is the heuristic value plus the search path cost required to reach a given state.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Calculate h and f
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class HeuristicSearchNode<O, T extends Traversable<O>> extends SearchNode<O, T>
{
    /** Value of heuristic for search node. */
    protected float h;

    /** Used to hold a reference to the heuristic evaluator for the states being searched. */
    private Heuristic<O, T> heuristic;

    /**
     * No-argument constructor needed for Class.newInstance() call in the {@link SearchNode#makeNode} method to work.
     *
     * <p>Note that this constructor does not set up the heuristic evaluator or calculate the value of h. These steps
     * are performed by the {@link #makeNode} method which should always be called after using this constructor.
     */
    public HeuristicSearchNode()
    {
    }

    /**
     * Makes a search node for startState that uses the supplied heuristic.
     *
     * @param startState The search state space to create a heuristic search node for.
     * @param heuristic  The heuristic calculation to use for the search node.
     */
    public HeuristicSearchNode(T startState, Heuristic<O, T> heuristic)
    {
        super(startState);

        // Keep the reference to the heuristic evaluator
        this.heuristic = heuristic;

        // Calculate the heuristic evaluation for the start state.
        computeH();
    }

    /**
     * Returns value of the heuristic function. This method assumes that h has already been computed.
     *
     * @return The value of the heuristic function applied to this search node.
     */
    public float getH()
    {
        return h;
    }

    /**
     * Returns f(node), defined as the heuristic + path cost to the node.
     *
     * @return The f value of this search node, where f = heuristic value + path cost to the node.
     */
    public float getF()
    {
        return pathCost + h;
    }

    /**
     * Returns a reference to the heuristic function.
     *
     * @return The heuristic function used to calculate the heuristic for this node.
     */
    public Heuristic<O, T> getHeuristic()
    {
        return heuristic;
    }

    /**
     * Returns a new node based on a successor of this node. This new node will also be a HeuristicSearchNode.
     *
     * @param     successor The successor state in the search space to create a new node for.
     *
     * @return    A completely new heuristic search node for the successor state. This node will be of the correct
     *            class, even for classes that sub-class this one.
     *
     * @exception SearchNotExhaustiveException If a new node cannot be created. This may happen due to class visibility
     *                                         or class loading problems.
     */
    public HeuristicSearchNode<O, T> makeNode(Successor successor) throws SearchNotExhaustiveException
    {
        HeuristicSearchNode<O, T> node = (HeuristicSearchNode<O, T>) super.makeNode(successor);

        // Make sure the new node has a reference to the heuristic evaluator
        node.heuristic = this.heuristic;

        // Compute h for the new node
        node.computeH();

        return node;
    }

    /** Computes and stores the heuristic function for the state. */
    protected void computeH()
    {
        h = heuristic.computeH(state, this);
    }
}
