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
import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.spi.QueueSearchState;
import com.thesett.common.util.logic.UnaryPredicate;

/**
 * Implements the simplest queue based search algorithm that checks the next element on the queue until the search space
 * is exhausted, or a maximum number of steps is reached.
 *
 * <p/>By default the next element is removed from the queue and has its successors expanded on the queue (provided they
 * have not already been) and is then goal checked. The peek-at-head flag may be set to alter this behaviour (through
 * the {@link #setPeekAtHead} method), so that the next element on the queue has its successors expanded onto the queue
 * without being removed itself. The head element of the queue is then removed and goal checked. This head element may
 * be different to the next element that had its successors expanded where an ordered queue is used and the ordering is
 * such that some of the successors were placed before their parent node. This is usefull for producing searches that
 * examine child nodes before parent nodes, for example a post-fix search.
 *
 * <p/>There is a reverse successors option that may be set (through the {@link #setReverseEnqueueOrder} method), which
 * causes the successors to be expanded onto the queue in reverse order to which they are presented. This is usefull for
 * stack based searches, such as depth first search, which by the nature of the stack reverse the order of elements.
 * Setting this flag will do a double reverse of the element order and restore an intuituve left-to-right search order
 * over elements. Generally speaking, set this flag whenever using a LIFO based queue.
 *
 * <p/>A maximum number of node expansions may be set. If the search does not find a goal within this maximum number
 * then a {@link SearchNotExhaustiveException} is raised.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Search based on the queue ordering until a goal node is found or the maximum allowed number of steps is
 *          reached.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class MaxStepsAlgorithm<O, T extends Traversable<O>> extends BaseQueueSearchAlgorithm<O, T>
{
    /**
     * Search until a goal state is found or the maximum allowed number of steps is reached.
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
        // Initialize the queue with the start states set up in search nodes if this has not already been done.
        // This will only be done on the first call to this method, as enqueueStartStates sets a flag when it is
        // done. Subsequent searches continue from where the previous search left off. Have to call reset on
        // the search method to really start the search again from the start states.
        Queue<SearchNode<O, T>> queue = initSearch.enqueueStartStates(startStates);

        // Get the goal predicate configured as part of the enqueuing start states process.
        UnaryPredicate<T> goalPredicate = initSearch.getGoalPredicate();

        // Keep running until the queue becomes empty or a goal state is found.
        while (!queue.isEmpty())
        {
            // Extract or peek at the head element from the queue.
            SearchNode<O, T> headNode = peekAtHead ? queue.peek() : queue.remove();

            // Expand the successors into the queue whether the current node is a goal state or not.
            // This prepares the queue for subsequent searches, ensuring that goal states do not block
            // subsequent goal states that exist beyond them.
            if (!headNode.isExpanded())
            {
                headNode.expandSuccessors(queue, reverseEnqueue);
            }

            // Get the node to be goal checked, either the head node or the new top of queue, depending on the
            // peek at head flag. Again this is only a peek, the node is only to be removed if it is to be
            // goal checked.
            SearchNode<O, T> currentNode = peekAtHead ? queue.peek() : headNode;

            // Only goal check leaves, or nodes already expanded. (The expanded flag will be set on leaves anyway).
            if (currentNode.isExpanded())
            {
                // If required, remove the node to goal check from the queue.
                currentNode = peekAtHead ? queue.remove() : headNode;

                // Check if the current node is a goal state.
                if (goalPredicate.evaluate(currentNode.getState()))
                {
                    return currentNode;
                }
            }

            // Check if there is a maximum number of steps limit and increase the step count and check the limit if so.
            if (maxSteps > 0)
            {
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

        // No goal state was found so return null
        return null;
    }
}
