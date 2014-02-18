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
package com.thesett.aima.search;

import java.util.Iterator;
import java.util.Queue;

/**
 * SearchNode represents a node in a search tree over a state space and supplies utility functions needed for
 * implementing a search. A search node consists of a state and parent search node. SearchNode objects also keep track
 * of the depth of the current state, the previous operation to get to this state and the cost of the path to this
 * state.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Record parent, state, most recent operation, depth and total cost
 * <tr><td> Provide queue of all successor states <td> {@link Successor}
 * <tr><td> Provide utility method to generate new search node from a successor <td> {@link Successor}
 * <tr><td> Apply repeated state filtering <td> {@link RepeatedStateFilter}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SearchNode<O, T extends Traversable<O>>
{
    /* Used for logging. */
    /* private static final Logger log = Logger.getLogger(SearchNode.class.getName()); */

    /** State at this node. */
    protected T state;

    /** Reference back to parent node. This will be null if this is the root node. */
    protected SearchNode<O, T> parent;

    /** Operation that was applied to the parent node. */
    protected Operator<O> appliedOp;

    /** Depth of this node. */
    protected int depth;

    /** Cost of getting to this node. */
    protected float pathCost;

    /** Holds a reference to the repeated state filtering strategy. */
    protected RepeatedStateFilter<O, T> repeatedStateFilter;

    /** Holds a flag that indicates that this node has had its successors expanded. */
    protected boolean expanded = false;

    /** Can be used to hold a count of unexamined successors. */
    public int unexaminedSuccessorCount;

    /**
     * Constructor takes a state and makes it a parentless (or root) search node. The intial depth and cost are set to
     * zero.
     *
     * @param startState Creates a search node out of a start state.
     */
    public SearchNode(T startState)
    {
        state = startState;
        parent = null;
        appliedOp = null;
        depth = 0;
        pathCost = 0;
    }

    /** No-argument constructor needed for newInstance(). */
    protected SearchNode()
    {
    }

    /**
     * Returns state of this node.
     *
     * @return The state covered by this search node.
     */
    public T getState()
    {
        return state;
    }

    /**
     * Returns parent of this node.
     *
     * @return The parent search node that led to this one.
     */
    public SearchNode<O, T> getParent()
    {
        return parent;
    }

    /**
     * Returns applied operation for this node.
     *
     * @return The traversal operation that was applied to the parent state to get to the state of this search node.
     */
    public Operator<O> getAppliedOp()
    {
        return appliedOp;
    }

    /**
     * Returns depth of this node.
     *
     * @return The search depth from the start node to this one.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Returns cost of getting to this node.
     *
     * @return The path cost from the start node to this one.
     */
    public float getPathCost()
    {
        return pathCost;
    }

    /**
     * Attaches the specified repeated state filtering strategy to the search node. This strategy is propagated into all
     * successor nodes generated from this one.
     *
     * @param filter The state filter to prune the search space of repeated states with.
     */
    public void setRepeatedStateFilter(RepeatedStateFilter<O, T> filter)
    {
        // Keep the reference to the repeated state filter
        repeatedStateFilter = filter;
    }

    /**
     * Reports the repeated state filter in use if there is one.
     *
     * @return The state filter to prune the search space of repeated states with.
     */
    public RepeatedStateFilter<O, T> getRepeatedStateFilter()
    {
        return repeatedStateFilter;
    }

    /**
     * Cheks if this node has already had its successor expanded.
     *
     * @return <tt>true</tt> if this node has had its successors expanded, <tt>false</tt> otherwise.
     */
    public boolean isExpanded()
    {
        return expanded;
    }

    /**
     * Expands a node into its successors. The successors are added to the specified collection which is returned as the
     * result of this function. The reason that the collection is passed as an argument is that there are many different
     * styles of buffer (fifo, lifo, etc) that search functions can use to implement different kinds of search and it is
     * more efficient to expand directly into the buffer than to have to copy the results into it after this method is
     * called, which would be the case if this method returned a collection.
     *
     * <p>If a repeated state filter has been attached to the search nodes then this method applies it here to all the
     * potential successor states. Only those that pass the filter are expanded into the collection.
     *
     * @param  expandInto The collection to place the successors in.
     * @param  reverse    When set, indicates that the expansion should be done backwards.
     *
     * @return The number of successor expanded.
     *
     * @throws SearchNotExhaustiveException If the successor nodes cannot be created. This may happen when using a
     *                                      custom node implementation that is not publicly accessible or that causes
     *                                      other class loading/creation errors.
     */
    public int expandSuccessors(Queue<SearchNode<O, T>> expandInto, boolean reverse) throws SearchNotExhaustiveException
    {
        // Used to keep count of the number of successors.
        int numSuccessors = 0;

        for (Iterator<Successor<O>> successors = getState().successors(reverse); successors.hasNext();)
        {
            numSuccessors++;

            // Get the next successor state
            Successor<O> next = successors.next();

            // Check if a repeated state filter is to be applied
            if (repeatedStateFilter != null)
            {
                // Filter the successor state and check if it should be accepted
                if (repeatedStateFilter.evaluate((T) next.getState(), this))
                {
                    // Add the filtered state to the successors
                    expandInto.offer(makeNode(next));
                }
            }

            // No repeated state filter is to be applied so add the successor state
            else
            {
                expandInto.offer(makeNode(next));
            }
        }

        // Successors have been expanded.
        expanded = true;

        return numSuccessors;
    }

    /**
     * Makes a new node of the same type as this one from a Successor state.
     *
     * @param  successor The successor state to turn into a search node.
     *
     * @return A search node covering the successor state.
     *
     * @throws SearchNotExhaustiveException If the successor nodes cannot be created. This may happen when using a
     *                                      custom node implementation that is not publicly accessible or that causes
     *                                      other class loading/creation errors.
     */
    public SearchNode<O, T> makeNode(Successor successor) throws SearchNotExhaustiveException
    {
        SearchNode newNode;

        try
        {
            // Create a new instance of this class
            newNode = getClass().newInstance();

            // Set the state, operation, parent, depth and cost for the new search node
            newNode.state = successor.getState();
            newNode.parent = this;
            newNode.appliedOp = successor.getOperator();
            newNode.depth = depth + 1;
            newNode.pathCost = pathCost + successor.getCost();

            // Check if there is a repeated state filter and copy the reference to it into the new node if so
            if (repeatedStateFilter != null)
            {
                newNode.setRepeatedStateFilter(repeatedStateFilter);
            }

            return newNode;
        }
        catch (InstantiationException e)
        {
            // In practice this should never happen but may if the nodes if some class loader error were to occur whilst
            // using a custom node implementation. Rethrow this as a RuntimeException.
            throw new RuntimeException("InstantiationException during creation of new search node.", e);
        }
        catch (IllegalAccessException e)
        {
            // In practice this should never happen but may if the nodes to use are not public whilst using a custom node
            // implementation. Rethrow this as a RuntimeException.
            throw new RuntimeException("IllegalAccessException during creation of new search node.", e);
        }
    }
}
