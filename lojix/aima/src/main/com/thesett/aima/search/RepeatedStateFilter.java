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

import com.thesett.common.util.logic.BinaryPredicate;

/**
 * RepeatedStateFilter defines an interface that repeated state filtering strategies should implement to filter states
 * from a {@link QueueBasedSearchMethod}. All search methods use the {@link SearchNode#expandSuccessors} method to get
 * the search nodes for the successor states to a node. This method will call the repeated state filter if one has been
 * attached to the search method and if the repeated state filtering flag has not been set to false by a search
 * algorithm that explicitly wishes to avoid using repeated state filters (for example, because the algorithm already
 * has its own strategy for handling this built into it). See the {@link SearchNode} class for more information about
 * this.
 *
 * <p>In addition to the state to be filtered the filter method is passed the current search node that is the parent of
 * the potential new state. This is passed because the filtering algorithm may wish to examine the parent node (and its
 * ancestors) to implement some filtering strategies. For example to avoid loops it is necessary to look for the state
 * to be repeated all the way up the search nodes ancestral lineage.
 *
 * <p>Filtering strategies may have an internal state, such as a hash map of filtered nodes. Every time a search
 * algorithm is started it will call the {@link #reset} method to clear any internal state that may exist from previous
 * searched.
 *
 * <p>Some suggested filtering strategies are:
 *
 * <ol>
 * <li>Do not return to the parent state. No local looping.</li>
 * <li>Do not create cyclical paths. Don't repeat states in the parent and all ancestors.</li>
 * <li>Never create a state already seen. Use a hash table to retain and quickly look up states.</li>
 * <li>Try not to create states already seen with a bounded amount of available memory. Use a hash table with a bounded
 * size to retain and quickly look up states. Start to forget states once the hash map is full. A strategy to decide
 * which states to forget needs to be devised.</li>
 * </ol>
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Decide whether a state is to be filtered.
 * <tr><td> Reset any internal state.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Could either add a best forgotten child field to SearchNode or to a special subclass of it. It would then be
 *         possible for the filtering strategy to modify the search nodes when search states are forgotten by a memory
 *         bounded strategy so as to insert some information about the heuristics of forgotten child states into the
 *         search tree. The search algorithm could then make use of this to decide whether or not to re-expand a portion
 *         of the tree. In this case the filter needs to remember the parent node as well as the state in order to be
 *         able to figure out which parent to store the best forgotten child value in.
 */
public interface RepeatedStateFilter<O, T extends Traversable<O>> extends BinaryPredicate<T, SearchNode<O, T>>
{
    /**
     * Called by a search algorithm for a decision whether or not the specified state should be searched.
     *
     * @param  state      The state to filter.
     * @param  parentNode The parent search node that generated the state.
     *
     * @return <tt>true</tt> if the state should be searched, <tt>false</tt> is it should be eliminated by this filter.
     */
    public boolean evaluate(T state, SearchNode<O, T> parentNode);

    /**
     * Called every time a search algorithm is started using this filter. Should clear any internal state that may exist
     * from previous searches.
     */
    public void reset();
}
