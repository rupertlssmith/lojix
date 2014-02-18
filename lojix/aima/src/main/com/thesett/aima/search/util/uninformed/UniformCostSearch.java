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
package com.thesett.aima.search.util.uninformed;

import java.util.PriorityQueue;
import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.impl.BaseQueueSearch;

/**
 * Implements a Uniform-cost search. This is one that always follows the search node that has the lowest path cost. It
 * is called a uniform cost search because the boundary of the search will have a roughly uniform cost as the search
 * space is searched by increasing cost.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Perform a uniform cost search.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class UniformCostSearch<O, T extends Traversable<O>> extends BaseQueueSearch<O, T>
{
    /** Creates a new UniformCostSearch object. */
    public UniformCostSearch()
    {
    }

    /**
     * Creates the correct type of search nodes for this search. This search uses ordinary search nodes.
     *
     * @param  state The search space state to create a search node for.
     *
     * @return The state encapsulated in an ordinary search node.
     */
    public SearchNode<O, T> createSearchNode(T state)
    {
        return new SearchNode<O, T>(state);
    }

    /**
     * Creates the correct type of queue for this search. This search uses a priority queue ordered by path cost.
     *
     * @return An empty priority queue with path cost.
     */
    public Queue<SearchNode<O, T>> createQueue()
    {
        return new PriorityQueue<SearchNode<O, T>>(11, new UniformCostComparator());
    }
}
