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

/**
 * SearchMethod provides an abstract interface for performing searches over a searchable space 'T'.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Allow a limit to the extent of the search to be specified.
 * <tr><td> Allow starting points of the search to be specified.
 * <tr><td> Allow the search to be reset to its begining.
 * <tr><td> Perform a search, reporting as an exception failure to find a solution or exhaust the search space.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface SearchMethod<T>
{
    /**
     * Sets the maximum number of search steps that a search method may take. If it fails to find a solution before this
     * number of steps has been reached its {@link #search} method should fail and return null. What exactly constitutes
     * a single step, and the granularity of the step size, is open to different interpretation by different search
     * algorithms. The guideline is that this is the maximum number of states on which the goal test should be
     * performed.
     *
     * @param max The maximum number of states to goal test. If this is zero or less then the maximum number of steps
     *            will not be checked for.
     */
    void setMaxSteps(int max);

    /**
     * Adds a start state to the search. Many search algorithms will only accept a single start state in which case the
     * most recent call to this method is taken as the definition of the single start state to use. Some search
     * algorithms may take multiple start states in which case successive calls to this method should be used to
     * establish all the start states.
     *
     * @param startState The start state to add to the collection of start states to begin the search with.
     */
    void addStartState(T startState);

    /**
     * Resets the search. This should clear any start and goal states, any maximum step count limit and any repeated
     * state filters that have been set and leave the search in a state in which it is ready to be run.
     */
    void reset();

    /**
     * Perform the search. This can be called multiple times to get successive results where more than one goal can be
     * found, if the algorithm supports this. In this case it should return null once no more goals can be found.
     *
     * @return The first state corresponding to a goal state that is found or null if none can be found after exhaustive
     *         searching. A return value of null should really mean that exhaustive searching has been completed rather
     *         than some premature termination condition being reached. See the comment below about the
     *         SearchNotExhaustiveException.
     *
     * @throws SearchNotExhaustiveException If the search stops before finding a solution or exhausting the search
     *                                      space. Different search algorithms may fail for different reasons but in
     *                                      particular it it is worth noting that if the maximum number of steps is
     *                                      reached without exhausting the search space this will cause an exception to
     *                                      be thrown rather than for this method to return null.
     */
    T search() throws SearchNotExhaustiveException;
}
