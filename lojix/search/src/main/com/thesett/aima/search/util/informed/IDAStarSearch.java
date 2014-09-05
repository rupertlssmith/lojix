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

import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.impl.IterativeBoundAlgorithm;
import com.thesett.aima.search.spi.BoundProperty;
import com.thesett.common.util.StackQueue;

/**
 * Implements an iterative deepening A* search. This proceeds depth first but is bounded by iteratively increasing
 * values of f (f = heuristic + cost).
 *
 * <p>There are two version of this search. One increases the f-value in fixed increments rather than always taking the
 * next largest. Use the constructor that accepts an epsilon-value to use this version. The other will go to the next
 * largest f-value seen beyond the f-value bound on the previous iteration. If f only increases a few times accross a
 * search space this may be a usuable search. If it is different for every node then it will need to examine N^2 nodes
 * so it is too slow.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Perform an optimal complete iterative deepening A* search.
 * <tr><td> Perform an epsilon admissable iterative deepening A* search.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Complete this comment about epsilon-admissable searches. If the heuristic is admissable then the IDA* search
 *         that uses the next f-value will be optimal and comlete. If an epsilon value is set then it is said to be
 *         epsilon-admissable which means the the cost of the solution will be???
 */
public class IDAStarSearch<O, T extends Traversable<O>> extends BaseHeuristicSearch<O, T> implements BoundProperty<O, T>
{
    /**
     * Creates a new IDAStarSearch object.
     *
     * @param heuristic The heuristic to use to compute heuristic values of search nodes with.
     * @param startF    The value of f to start the first iteration at.
     */
    public IDAStarSearch(Heuristic<O, T> heuristic, float startF)
    {
        super(heuristic);

        // Create an f increasing iterative algorithm by specializing the bounded algorithm with an
        // f-value extracting method.
        IterativeBoundAlgorithm<O, T> fIncreasingAlgorithm = new IterativeBoundAlgorithm<O, T>(startF);

        fIncreasingAlgorithm.setBoundPropertyExtractor(this);

        // Replace the search algorithm with the f-value increasing version.
        setQueueSearchAlgorithm(fIncreasingAlgorithm);
    }

    /**
     * Creates a new IDAStarSearch object.
     *
     * @param heuristic The heuristic to use to compute heuristic values of search nodes with.
     * @param startF    The value of f to start the first iteration at.
     * @param epsilon   The value to increase the f-limit by at each iteration.
     */
    public IDAStarSearch(Heuristic<O, T> heuristic, float startF, float epsilon)
    {
        super(heuristic);

        // Create an f increasing iterative algorithm by specializing the bounded algorithm with an
        // f-value extracting method.
        IterativeBoundAlgorithm<O, T> fIncreasingAlgorithm = new IterativeBoundAlgorithm<O, T>(startF, epsilon);

        fIncreasingAlgorithm.setBoundPropertyExtractor(this);

        // Replace the search algorithm with the f-value increasing version.
        setQueueSearchAlgorithm(fIncreasingAlgorithm);
    }

    /**
     * Provides an implementation of the {@link BoundProperty} interface to extract the cost as the bound property.
     *
     * @param  searchNode The search node to extract the bound property for.
     *
     * @return The f value of the supplied heuristic search node. The bound property to control the iterative deepening
     *         algorithm is f.
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
