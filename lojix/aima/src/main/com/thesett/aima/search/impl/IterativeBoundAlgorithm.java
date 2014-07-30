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
package com.thesett.aima.search.impl;

import java.util.Collection;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.spi.QueueSearchState;

/**
 * Implements an iterative-bound search from any queue based search method. This progressively re-runs the search at
 * ever increasing bounds until a solution is found. It extends {@link BoundedAlgorithm} which implements the
 * {@link com.thesett.aima.search.spi.BoundProperty} interface to provide a depth bound by default. This algorithm
 * therefore defaults to being an iterative-deepening algorithm, if a different bound property extractor is not
 * substituted.
 *
 * <p>The algorithm can either increase the bound by a fixed amount, epsilon, at each iteration or can increase the
 * bound to the next smallest bound property value that was seen beyond the bound of the previous iteration on search
 * nodes just over the fringe of the search bound. The latter case only produces good search algorithms where the bound
 * property can only take a small number of values. In general if search nodes have real valued heuristics that are
 * mostly different for each node then this strategy will result in N^2 nodes being examined. Use the constructor that
 * accepts a value for epsilon to use the fixed increase strategy, use the other constructor to use the next minimal
 * bound beyond the fringe strategy.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Transform any {@link BaseQueueSearch} into an iterative deepening search.
 *     <td> {@link BaseQueueSearch}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class IterativeBoundAlgorithm<O, T extends Traversable<O>> extends BoundedAlgorithm<O, T>
{
    /** Holds the bound to start at. */
    protected float startBound;

    /** Holds the amount to increase bound by at every iteration. */
    protected float epsilon;

    /** Indicates whether the epsilon increase strategy should be used or not. */
    protected boolean useEpsilon;

    /**
     * Creates a new IterativeBoundAlgorithm object.
     *
     * @param startBound The starting value of the maximum bound value for the first iteration.
     */
    public IterativeBoundAlgorithm(float startBound)
    {
        // Instantiate the super class with a bound of 0.0. This will be overriden with the correct iterative
        // bounds in the search algorithm anyway.
        super(0.0f);

        // Keep the start bound and increment.
        this.startBound = startBound;

        // Epsilon is not set so use the next minimal bund value beyond the fringe.
        useEpsilon = false;
    }

    /**
     * Creates a new IterativeBoundAlgorithm object.
     *
     * @param startBound The starting value of the maximum bound value for the first iteration.
     * @param epsilon    The amount to increase the maximum bound by on each sarch iteration.
     */
    public IterativeBoundAlgorithm(float startBound, float epsilon)
    {
        // Instantiate the super class with a bound of 0.0. This will be overriden with the correct iterative
        // bounds in the search algorithm anyway.
        super(0.0f);

        // Keep the start bound and increment.
        this.startBound = startBound;
        this.epsilon = epsilon;

        // Epsilon is set so used fixed increments.
        useEpsilon = true;
    }

    /**
     * Search iteratively on increasing maximum bound limits until the search space is exhausted or a goal state is
     * found.
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
     * @throws SearchNotExhaustiveException SearchNotExhaustiveException If the search terminates prematurely because
     *                                      the maximum number of steps is reached.
     */
    public SearchNode search(QueueSearchState<O, T> initSearch, Collection<T> startStates, int maxSteps,
        int searchSteps) throws SearchNotExhaustiveException
    {
        // Iteratively increase the bound until a search succeeds.
        for (float bound = startBound;;)
        {
            // Set up the maximum bound for this iteration.
            maxBound = bound;

            // Use a try block as the depth bounded search will throw a MaxBoundException if it fails but there
            // are successors states known to exist beyond the current max depth fringe.
            try
            {
                // Get the number of search steps taken so far and pass this into the underlying depth bounded search
                // so that the step count limit carries over between successive iterations.
                int numStepsSoFar = initSearch.getStepsTaken();

                // Call the super class search method to perform a depth bounded search on this maximum bound starting
                // from the initial search state.
                initSearch.resetEnqueuedOnceFlag();

                SearchNode node = super.search(initSearch, startStates, maxSteps, numStepsSoFar);

                // Check if the current depth found a goal node
                if (node != null)
                {
                    return node;
                }

                // The depth bounded search returned null, so it has exhausted the search space. Return with null.
                else
                {
                    return null;
                }
            }

            // The depth bounded search failed but it knows that there are more successor states at deeper levels.
            catch (MaxBoundException e)
            {
                // Do nothing, no node found at this depth so continue at the next depth level
                e = null;
            }

            // Check if the bound should be increased by epsilon or to the next smallest bound property value
            // beyond the fringe and update the bound for the next iteration.
            if (useEpsilon)
            {
                bound = bound + epsilon;
            }
            else
            {
                bound = getMinBeyondBound();
            }
        }
    }
}
