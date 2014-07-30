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
package com.thesett.aima.search.util.informed;

import java.util.PriorityQueue;
import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;

/**
 * Implements a greedy search algorithm. This is one that always follows the search node that has the highest heuristic
 * value.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do a greedy search.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class GreedySearch<O, T extends Traversable<O>> extends BaseHeuristicSearch<O, T>
{
    /**
     * Creates a new GreedySearch object.
     *
     * @param heuristic The heuristic to use to compute heuristic values of search nodes with.
     */
    public GreedySearch(Heuristic<O, T> heuristic)
    {
        super(heuristic);
    }

    /**
     * Creates the correct type of queue for this search. This search uses a priority queue ordered by heuristic value.
     *
     * @return An empty priority queue with heuristic ordering.
     */
    public Queue<SearchNode<O, T>> createQueue()
    {
        return new PriorityQueue<SearchNode<O, T>>(11, new GreedyComparator());
    }
}
