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
import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;
import com.thesett.common.util.logic.UnaryPredicate;

/**
 * QueueSearchState is used to abstract the queue creation and search node creation process out of a search algorithm.
 * An argument that implements this interface is passed to classes that implement the
 * {@link QueueSearchAlgorithm#search} method so that they may create start queues without being aware of the specific
 * implementation of the queue and search nodes types being used.
 *
 * <p/>The enqueueing of start states usually takes place when a {@link QueueBasedSearchMethod#search} method is called,
 * however, subsequent calls to this method to generate further goal states after the first one is found should not
 * re-enqueue the start states but should maintain the existing search position to carry on from. For this reason there
 * is a flag method {@link #isEnqueuedOnce} that returns true once the {@link #enqueueStartStates} method has been
 * called.
 *
 * <p/>The goal predicate for the queue based search, and the number of search steps taken are also encapsulated by this
 * state. Specific search algorithms must interface to the queue based search, in order to obtain the goal predicate,
 * and keep count of the number of steps taken, through this interface.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Turn start states into search nodes of the correct type for a search algorithm.
 * <tr><td> Record and supply statistics about the number of search steps taken during the running of a search
 *          algorithm.
 * <tr><td> Indicate whether or not start states have already been enqueued.
 * <tr><td> Provide the goal predicate for the search.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface QueueSearchState<O, T extends Traversable<O>>
{
    /**
     * Transform the start states into search nodes and place them into a queue. Different implemtations of this can use
     * differnt sorts of queues and search nodes to control the ordering in which search algorithms get nodes from the
     * queue.
     *
     * @param  startStates a collection of start states to enqueue.
     *
     * @return A queue of search nodes built from the start states.
     */
    public Queue<SearchNode<O, T>> enqueueStartStates(Collection<T> startStates);

    /**
     * This provides a call back that search algorithms use to update the number of search steps taken. They can do this
     * at the end of the search, so that it is only updated once to save on the number of calls, or as the search is
     * running, which will be slower but provide runtime statistics.
     *
     * @param steps The number of states examined in a search so far.
     */
    public void setStepsTaken(int steps);

    /**
     * This provides a call back that search algorithms use to get the number of search steps taken so far.
     *
     * @return The number of states examined in a search so far.
     */
    public int getStepsTaken();

    /**
     * Indicates whether or not the start states have already been enqueued.
     *
     * @return <tt>true</tt> if the start states have already been enqueued, <tt>false</tt> otherwise.
     */
    public boolean isEnqueuedOnce();

    /**
     * Resets the flag that indicates whether or not start states have already been enqueued. Iterative algorithms will
     * use this, for example, to allow their search to be reset to the begining before searching to a higher search
     * bound.
     */
    public void resetEnqueuedOnceFlag();

    /**
     * Provides the goal predicate for the search.
     *
     * @return The goal predicate for the search.
     */
    public UnaryPredicate<T> getGoalPredicate();
}
