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

import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.impl.BaseQueueSearch;
import com.thesett.aima.search.impl.MaxStepsAlgorithm;
import com.thesett.aima.search.spi.QueueSearchAlgorithm;
import com.thesett.common.util.StackQueue;

/**
 * Implements a Depth-first search. This is done by passing a LIFO stack to the {@link BaseQueueSearch} implementation.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do a depth first search.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DepthFirstSearch<O, T extends Traversable<O>> extends BaseQueueSearch<O, T>
{
    /** Creates a new depth first search. */
    public DepthFirstSearch()
    {
        // The set queue search algorithm method ensures that the reverse enqueing of successors is used.
        setQueueSearchAlgorithm(new MaxStepsAlgorithm<O, T>());
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
     * Creates the correct type of queue for this search. This search uses a LIFO stack so that the child nodes most
     * recently added to the queue are examined first, ensuring that the search always goes deeper if it can.
     *
     * @return An empty LIFO stack.
     */
    public Queue<SearchNode<O, T>> createQueue()
    {
        return new StackQueue<SearchNode<O, T>>();
    }

    /**
     * Allows different queue search algorithms to replace the default one. This overidden method ensures that it
     * expands it successor nodes in reverse, which provides a more intuituve left-to-right goal checking order, through
     * the LIFO statck.
     *
     * @param algorithm The search algorithm to use.
     */
    protected void setQueueSearchAlgorithm(QueueSearchAlgorithm<O, T> algorithm)
    {
        algorithm.setReverseEnqueueOrder(true);
        super.setQueueSearchAlgorithm(algorithm);
    }
}
