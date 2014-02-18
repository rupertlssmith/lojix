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

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.impl.BaseQueueSearch;

/**
 * Provides a base class for implementation queue-based heuristic searched.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do a heauristic search.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BaseHeuristicSearch<O, T extends Traversable<O>> extends BaseQueueSearch<O, T>
{
    /** Used to hold the heuristic function. */
    Heuristic<O, T> heuristic;

    /**
     * Creates a base heuristic search using the specified heuristic to guide the search.
     *
     * @param heuristic The heursitic to use to guide the search.
     */
    public BaseHeuristicSearch(Heuristic<O, T> heuristic)
    {
        this.heuristic = heuristic;
    }

    /**
     * Creates the correct type of search nodes for this search. This search uses heuristic search nodes.
     *
     * @param  state The search space state to create a search node for.
     *
     * @return The state encapsulated in a heuristic search node.
     */
    public SearchNode<O, T> createSearchNode(T state)
    {
        return new HeuristicSearchNode<O, T>(state, heuristic);
    }
}
