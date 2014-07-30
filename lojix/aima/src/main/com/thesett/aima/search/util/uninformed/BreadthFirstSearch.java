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
package com.thesett.aima.search.util.uninformed;

import java.util.LinkedList;
import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.impl.BaseQueueSearch;

/**
 * Implements a Breadth-first search. This is done by passing a FIFO queue to the {@link BaseQueueSearch}
 * implementation.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do a breadth first search.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BreadthFirstSearch<O, T extends Traversable<O>> extends BaseQueueSearch<O, T>
{
    /** Creates a new breadth first search. */
    public BreadthFirstSearch()
    {
    }

    /**
     * Creates the correct type of search nodes for this search. This search uses ordinary search nodes.
     *
     * @param  state The search space state to create a search node for.
     *
     * @return The state encapsulated in a search node.
     */
    public SearchNode createSearchNode(T state)
    {
        return new SearchNode(state);
    }

    /**
     * Creates the correct type of queue for this search. This search uses a FIFO queue so that the child nodes most
     * recently added to the queue are examined last, ensuring that the search always examines parent nodes first.
     *
     * @return An empty FIFO queue.
     */
    public Queue<SearchNode<O, T>> createQueue()
    {
        return new LinkedList<SearchNode<O, T>>();
    }
}
