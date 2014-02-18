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
package com.thesett.aima.search.util.backtracking;

import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.impl.BaseQueueSearch;
import com.thesett.common.util.StackQueue;

/**
 * Implements a Depth-first search with back-tracking. This is done by passing a LIFO stack to the
 * {@link BaseQueueSearch} implementation, and using the {@link BacktrackingAlgorithm}, all over a {@link ReTraversable}
 * state space.
 *
 * <p/>When a reversable search state is encountered its {@link ReTraversable#applyOperator()} method is called, when it
 * is back tracked over its {@link ReTraversable#undoOperator()} method is called. Between these two calls a state is
 * said to be 'active'. Due to the depth first ordering, this search has the property that only the search path from the
 * root of the search to the currently examined state will be active at any time.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do a depth first search with back-tracking over a reversable state space.
 *     <td> {@link ReTraversable}, {@link BacktrackingAlgorithm}, {@link StackQueue}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DepthFirstBacktrackingSearch<O, T extends ReTraversable<O>> extends BaseQueueSearch<O, T>
{
    /** Creates a new depth first ordered backtracking search. */
    public DepthFirstBacktrackingSearch()
    {
        setQueueSearchAlgorithm(new BacktrackingAlgorithm<O, T>());
    }

    /**
     * This abstract method should be overriden to turn states into search nodes. Different implementation of queue
     * based algorithms can control the way they search by using different search node implementations. This method
     * allows the type of search node to be abstracted out of this search method to be supplied by concrete
     * implementations.
     *
     * @param  state The state to create a {@link SearchNode} from.
     *
     * @return The {@link SearchNode} for the specified state.
     */
    public SearchNode<O, T> createSearchNode(T state)
    {
        return new SearchNode<O, T>(state);
    }

    /**
     * This abstract method should be overriden to return an empty queue of search nodes. Different implementations of
     * queue search algorithms can control the way the search procedes by using different queue implementations. This
     * method allows the type of queue to be abstracted out of this search method to be supplied by concrete
     * implementations.
     *
     * @return An empty queue of search nodes.
     */
    public Queue<SearchNode<O, T>> createQueue()
    {
        return new StackQueue<SearchNode<O, T>>();
    }
}
