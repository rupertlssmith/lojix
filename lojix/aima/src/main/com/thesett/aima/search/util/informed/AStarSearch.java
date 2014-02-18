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
package com.thesett.aima.search.util.informed;

import java.util.PriorityQueue;
import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;

/**
 * Implements an A* search algorithm. This is one that always follows the search node that has the highest f value (f =
 * heuristic + cost).
 *
 * <p>The A* search is created by using a {@link BaseHeuristicSearch} combined with {@link HeuristicSearchNode}s and a
 * priority queue ordered with an {@link AStarComparator} that ensures that nodes with the highest f value are always at
 * the front of the queue. The default {@link com.thesett.aima.search.impl.MaxStepsAlgorithm} is not overridden.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do an A* search.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Provide an A* implementation that uses a fibonacci heap as its priority queue.
 */
public class AStarSearch<O, T extends Traversable<O>> extends BaseHeuristicSearch<O, T>
{
    /**
     * Creates a new AStarSearch object.
     *
     * @param heuristic The heuristic to use to guide the search.
     */
    public AStarSearch(Heuristic<O, T> heuristic)
    {
        super(heuristic);
    }

    /**
     * Creates a priority queue with an {@link AStarComparator} to control the search ordering of the basic queue search
     * algorithm.
     *
     * @return The A* priority queue.
     */
    public Queue<SearchNode<O, T>> createQueue()
    {
        return new PriorityQueue<SearchNode<O, T>>(11, new AStarComparator());
    }
}
