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

import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.impl.BoundedAlgorithm;
import com.thesett.aima.search.spi.BoundProperty;
import com.thesett.common.util.StackQueue;

/**
 * Implements an F-bounded search. This proceeds depth first but is bounded by a maximum f value where f = heuristic +
 * cost.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do an F-bounded search.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class FBoundedSearch<O, T extends Traversable<O>> extends BaseHeuristicSearch<O, T>
    implements BoundProperty<O, T>
{
    /**
     * Creates a new FBoundedSearch object.
     *
     * @param heuristic The heuristic to quide the search.
     * @param maxF      The maximum value of F to bound the search with.
     */
    public FBoundedSearch(Heuristic<O, T> heuristic, float maxF)
    {
        super(heuristic);

        // Create a cost bounded algorithm by specializing the bounded algorithm with a cost extracting method.
        BoundedAlgorithm<O, T> fBoundedAlgorithm = new BoundedAlgorithm<O, T>(maxF);

        fBoundedAlgorithm.setBoundPropertyExtractor(this);

        // Replace the search algorithm with the cost bounded version.
        setQueueSearchAlgorithm(fBoundedAlgorithm);
    }

    /**
     * Provides an implementation of the {@link BoundProperty} interface to extract f as the bound property.
     *
     * @param  searchNode The search node to extract a bound property from.
     *
     * @return The F value of the node.
     */
    public float getBoundProperty(SearchNode<O, T> searchNode)
    {
        return ((HeuristicSearchNode<O, T>) searchNode).getF();
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
}
