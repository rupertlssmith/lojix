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
package com.thesett.aima.search.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.spi.BoundProperty;
import com.thesett.aima.search.spi.QueueSearchState;
import com.thesett.common.util.logic.UnaryPredicate;

/**
 * Implements a bounded search from any queue based search method. This prevents the search from going beyond some
 * bounded property of the search nodes (or underlying states). This is a generalisation of the depth-bounded algorithm
 * where the bounded property is always the search node depth to a bounded algorithm that can work with any property.
 *
 * <p>If a search exhausts its search space without finding a solution it must return null. If it cannot find a solution
 * but knows that there are more states still to search beyond the boundary then it must fail with a
 * {@link MaxBoundException} to disinguish this from the exhausted search space case. In order to do this the algorithm
 * must peek beyond the fringe set by the bounded property of the search to see if there are successor states there.
 *
 * <p>Sometimes it is not enough to know that there are search nodes beyond the fringe it is also necessary to know what
 * the minimum value of the bounded property is beyond the fringe. For example, this may be used by an iterative
 * deepening algorithm that steps iteratively onto the next limit of the bounded property. Whilst peeking for nodes
 * beyond the fringe this algorithm also calculates their bound property values and keeps track of the minimum.
 *
 * <p>BoundedAlgorithm can accept different implementations of the {@link BoundProperty} interface to work with
 * different bound values. It implements this interface itself in order to provide a default behaviour as a depth
 * bounded search.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Transform any {@link BaseQueueSearch} into a bounded search.
 *     <td> {@link BaseQueueSearch}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BoundedAlgorithm<O, T extends Traversable<O>> extends BaseQueueSearchAlgorithm<O, T>
    implements BoundProperty<O, T>
{
    /** The maximum bound to search to. */
    protected float maxBound;

    /* Used to hold the switch for the minimum bounded property beyond the fringe tracking operation. */
    // protected boolean minBoundFlag = false;

    /** Used to hold a reference to the bounded property calculation function. */
    BoundProperty<O, T> boundPropertyExtractor = this;

    /** Used to keep track of the minimum bounded property that has been seen beyond the boundary fringe. */
    float minBeyondBound = Float.POSITIVE_INFINITY;

    /**
     * Creates a bounded algorithm with the specified bound.
     *
     * @param maxBound The maximum value that the bounded property is allowed to take for the search to consider a
     *                 search node.
     */
    public BoundedAlgorithm(float maxBound)
    {
        this.maxBound = maxBound;
    }

    /**
     * Provides a default implementation of the {@link BoundProperty} interface to extract the depth as the bound
     * property.
     *
     * @param  searchNode The search node to check the bounded property of.
     *
     * @return The search depth of the node.
     */
    public float getBoundProperty(SearchNode searchNode)
    {
        return (float) searchNode.getDepth();
    }

    /**
     * Accepts a bound property extractor to use to extract the property from a search node that is to be compared with
     * the maximal bound.
     *
     * @param extractor The bound property calculation function to use to work out the bound property value of search
     *                  nodes.
     */
    public void setBoundPropertyExtractor(BoundProperty<O, T> extractor)
    {
        this.boundPropertyExtractor = extractor;
    }

    /**
     * Returns the minimum bound property value that was seen on a node that is beyond the current boundary fringe on
     * the last run of the search method. This is designed to be used by iterative deepening algorithms that take this
     * value to be the next boundary to search under.
     *
     * @return The minimum bound property value seen beyond the current max bound fringe on a search run.
     */
    public float getMinBeyondBound()
    {
        return minBeyondBound;
    }

    /**
     * Searches all SearchNodes less than the maximum bound for some property of the nodes.
     *
     * @param  initSearch  The algorithm to turn the start states into the intial set of search nodes, of the correct
     *                     type to begin the search with.
     * @param  startStates The set of start states to begin the search with.
     * @param  maxSteps    The maximum number of steps to search for before giving up. If this is 0 or less then no
     *                     maximum number is enforced.
     * @param  searchSteps The number of steps taken so far. This may be greater than 0 if this search is run
     *                     iteratively and previous iterations of it have already been run.
     *
     * @return The first {@link SearchNode} corresponding to a goal state that is found or null if none can be found
     *         after exhaustive searching.
     *
     * @throws SearchNotExhaustiveException If the search terminates prematurely because the maximum number of steps is
     *                                      reached or because the maximum bound is reached and no more nodes can be
     *                                      found that are below it.
     */
    public SearchNode search(QueueSearchState<O, T> initSearch, Collection<T> startStates, int maxSteps,
        int searchSteps) throws SearchNotExhaustiveException
    {
        // Initialize the queue with the start states set up in search nodes.
        Queue<SearchNode<O, T>> queue = initSearch.enqueueStartStates(startStates);

        // Get the goal predicate configured as part of the enqueuing start states process.
        UnaryPredicate<T> goalPredicate = initSearch.getGoalPredicate();

        // Flag used to indicate whether there are unexplored successor states known to exist beyond the max depth
        // fringe.
        boolean beyondFringe = false;

        // Reset the minimum beyond the fringe boundary value.
        minBeyondBound = Float.POSITIVE_INFINITY;

        // Keep running until the queue becomes empty or a goal state is found.
        while (!queue.isEmpty())
        {
            // Extract the head element from the queue.
            SearchNode<O, T> headNode = peekAtHead ? queue.peek() : queue.remove();

            // Expand the successors into the queue whether the current node is a goal state or not.
            // This prepares the queue for subsequent searches, ensuring that goal states do not block
            // subsequent goal states that exist beyond them.
            // Add the successors to the queue provided that they are below or at the maximum bounded property.

            // Get all the successor states.
            Queue<SearchNode<O, T>> successors = new LinkedList<SearchNode<O, T>>();

            headNode.expandSuccessors(successors, reverseEnqueue);

            // Loop over all the successor states checking how they stand with respect to the bounded property.
            for (SearchNode<O, T> successor : successors)
            {
                // Get the value of the bound property for the successor node.
                float boundProperty = boundPropertyExtractor.getBoundProperty(successor);

                // Check if the successor is below or on the bound.
                if (boundProperty <= maxBound)
                {
                    // Add it to the queue to be searched.
                    queue.offer(successor);
                }

                // The successor state is above the bound.
                else
                {
                    // Set the flag to indicate that there is at least one search node known to exist beyond the
                    // bound.
                    beyondFringe = true;

                    // Compare to the best minimum beyond the bound property found so far to see if
                    // this is a new minimum and update the minimum to the new minimum if so.
                    minBeyondBound = (boundProperty < minBeyondBound) ? boundProperty : minBeyondBound;
                }
            }

            // Get the node to be goal checked, either the head node or the new top of queue, depending on the
            // peek at head flag.
            SearchNode<O, T> currentNode = peekAtHead ? queue.remove() : headNode;

            // Check if the current node is a goal state.
            // Only goal check leaves, or nodes already expanded. (The expanded flag will be set on leaves anyway).
            if (currentNode.isExpanded() && goalPredicate.evaluate(currentNode.getState()))
            {
                return currentNode;
            }

            // Check if there is a maximum number of steps limit and increase the step count and check the limit if so.
            if (maxSteps > 0)
            {
                // Increase the search step count because a goal test was performed.
                searchSteps++;

                // Update the search state with the number of steps taken so far.
                initSearch.setStepsTaken(searchSteps);

                if (searchSteps >= maxSteps)
                {
                    // The maximum number of steps has been reached, however if the queue is now empty then the search
                    // has just completed within the maximum. Check if the queue is empty and return null if so.
                    if (queue.isEmpty())
                    {
                        return null;
                    }

                    // Quit without a solution as the max number of steps has been reached but because there are still
                    // more states in the queue then raise a search failure exception.
                    else
                    {
                        throw new SearchNotExhaustiveException("Maximum number of steps reached.", null);
                    }
                }
            }
        }

        // No goal state was found. Check if there known successors beyond the max depth fringe and if so throw
        // a SearchNotExhaustiveException to indicate that the search failed rather than exhausted the search space.
        if (beyondFringe)
        {
            throw new MaxBoundException("Max bound reached.", null);
        }
        else
        {
            // The search space was exhausted so return null to indicate that no goal could be found.
            return null;
        }
    }
}
