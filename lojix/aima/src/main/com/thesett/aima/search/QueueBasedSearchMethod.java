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
package com.thesett.aima.search;

import com.thesett.common.util.logic.UnaryPredicate;

/**
 * Interface for any class that implements a search method. A search method is an object that discovers a
 * {@link SearchNode} that corresponds to a goal state.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Calculate a {@link SearchNode} corresponding to a goal state.
 * <tr><td> Attach a repeated state filtering strategy to a search algorithm.
 * <tr><td> Set up the start states for a search.
 * <tr><td> Set a maximum number of search steps allowable.
 * <tr><td> Reset a search algorithm.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Add more methods to this interface to make monitoring/controlling searches in progress possible. A running
 *         search should be capable of generating events, for when the search is started, completes, and for each step
 *         taken. A listener may be attached to this event model to monitor search progress, or to stop/restart searches
 *         at the single step granularity. Heuristic searches should report their heuristic evaluations as events on
 *         each step. For example in an A* search, search progress could be monitored by the ratio of cost/f as an
 *         approximation of the percent complete.
 * @todo   Usually a heuristic assumes a goal state, or gets it from the state. Allowing goal predicates to be set in
 *         the search algorithm means that they will have to be passed to the heuristic for consideration too. Also if
 *         there are multiple goals, what should the heuristic calculate? the estimated cost to the nearest one? Might
 *         need to think about how the goal predicate is comunicated to heuristics. One way to do his might be to
 *         parameterize the heuristic over goal predicates, and perform a check at the start of the search to ensure
 *         that the goal predicate being used matches the one the heuristic works with.
 */
public interface QueueBasedSearchMethod<O, T extends Traversable<O>> extends SearchMethod<T>
{
    /**
     * Some search algorithms may accept definitions of the goal state in which case the most recent call to this method
     * is taken as the definition of the goal state. In some searches the goal state is not known in advance, in others
     * the purpose of the search is to find a path from the start to the goal. If the search algorithm does not accept a
     * goal state it should throw an exception indicating that this method is not supported by that algorithm.
     *
     * <p/>Note that the {@link GoalState} interface itself defines the method {@link GoalState#isGoal} which tests if a
     * state is a goal state. It is therefore not necessary to call this method to set goal states where the goal
     * definition is defined already in the states. Setting goal states through this method, when it is supported by an
     * algorithm overrides the goal definition in the states themselves.
     *
     * @param  goalPredicate The predicate that evaluates search states to check if they are goals.
     *
     * @throws UnsupportedOperationException If the search algorithm does not accept goal predicates.
     */
    public void setGoalPredicate(UnaryPredicate<T> goalPredicate) throws UnsupportedOperationException;

    /**
     * Perform the search. This can be called multiple times to get successive results where more than one goal can be
     * found, if the algorithm supports this. In this case it should return null once no more goals can be found.
     *
     * @return The first {@link SearchNode} corresponding to a goal state that is found or null if none can be found
     *         after exhaustive searching. A return value of null should really mean that exhaustive searching has been
     *         completed rather than some premature termination condition being reached. See the comment below about the
     *         SearchNotExhaustiveException.
     *
     * @throws SearchNotExhaustiveException If the search stops before finding a solution or exhausting the search
     *                                      space. Different search algorithms may fail for different reasons but in
     *                                      particular it it is worth noting that if the maximum number of steps is
     *                                      reached without exhausting the search space this will cause an exception to
     *                                      be thrown rather than for this method to return null.
     */
    public SearchNode<O, T> findGoalPath() throws SearchNotExhaustiveException;

    /**
     * Attaches the specified repeated state filtering strategy to the search algorithm.
     *
     * @param filter The repeated state filter to prune the search space with.
     */
    public void setRepeatedStateFilter(RepeatedStateFilter<O, T> filter);
}
