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
package com.thesett.aima.search.spi;

import java.util.Collection;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.Traversable;

/**
 * QueueSearchAlgorithm abstracts the interface of a search method over a queue of {@link SearchNode}s. This allows the
 * actual implementations of such alrogithms to be pluggable into a basic queue search framework.
 *
 * <p/>The peek at head option, allows the search to consider the head node in relation to its successors, rather than
 * to always goal check the head node before its successors. When this option is turned on, the head node is not removed
 * from the queue, until its successors have been added to the queue. The new head node at that point in time is
 * examined, and only goal checked if its successors have been expanded onto the queue already.
 *
 * <p/>The reverse qneue order option, allows the successor nodes of a state to be expanded onto the queue in reverse
 * order. For FIFO based queues, this results in the nodes being seen by the search algorithm in the order in which the
 * parent state presented them (that is, double reversed, so back to original order). This means that depth first
 * searches proceed more intuitively, with the expected left to right order of the successor nodes. If the flag is not
 * set on a depth first search, it is still correct, just a little counter-intuitive.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Search using a queue to order search nodes.
 * <tr><td> Provide option to examine nodes relative to successors, or before successors.
 * <tr><td> Provide option to enqueue successors in reverse, for more intuitive FIFO based searches.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface QueueSearchAlgorithm<O, T extends Traversable<O>>
{
    /**
     * Search using a queue, created with the specified initialization method, to order search nodes.
     *
     * @param  initSearch  The algorithm to turn the start states into the intial set of search nodes, of the correct
     *                     type to begin the search with.
     * @param  startStates The set of start states to begin the search with.
     * @param  maxSteps    The maximum number of steps to search for before giving up. If this is 0 or less then no
     *                     maximum number is enforced.
     * @param  stepsTaken  The number of steps taken so far. This may be greater than 0 if this search is run
     *                     iteratively and previous iterations of it have already been run.
     *
     * @return The first {@link SearchNode} corresponding to a goal state that is found or null if none can be found
     *         after exhaustive searching.
     *
     * @throws SearchNotExhaustiveException SearchNotExhaustiveException If the search terminates prematurely because
     *                                      the maximum number of steps is reached.
     */
    public SearchNode<O, T> search(QueueSearchState<O, T> initSearch, Collection<T> startStates, int maxSteps,
        int stepsTaken) throws SearchNotExhaustiveException;

    /**
     * Sets the peek at head flag. When this is set, the head node of the queue to be examined is not removed from the
     * queue prior to its successors being expanded. The node to goal check is removed after this, which means that the
     * head node is ordered with its successors, rather than always being taken ahead of them. Depending on the queue
     * type, the node to be examined next may not be the same node as was at the head of the queue prior to expanding
     * successors.
     *
     * @param flag The value of the peek at head flag to use.
     */
    public void setPeekAtHead(boolean flag);

    /**
     * Sets the value of the reverse queue flag. This is used to allow FIFO based queues to traverse successors left to
     * right.
     *
     * @param flag The value of the reverse enqueue order flag.
     */
    public void setReverseEnqueueOrder(boolean flag);

    /**
     * Resests the state of this algorithm. Some search algorithms may preserve state between successive invocations.
     * This method is intended to be called when the entire search is reset (see
     * {@link com.thesett.aima.search.SearchMethod#reset}), to clear any such state.
     */
    void reset();
}
