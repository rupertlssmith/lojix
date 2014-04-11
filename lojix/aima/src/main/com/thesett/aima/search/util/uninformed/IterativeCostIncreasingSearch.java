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

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.impl.IterativeBoundAlgorithm;
import com.thesett.aima.search.spi.BoundProperty;

/**
 * Implements an iterative cost increasing search. This proceeds depth first but is bounded by iteratively increasing
 * maximum costs.
 *
 * <p>There are two version of this search. One is very similar to a {@link UniformCostSearch} but the costs increase in
 * fixed increments rather than always taking the next largest. Use the constructor that accepts an increment to use
 * this version. The other will to the next largest cost seen beyond the cost bound on the previous iteration. The
 * behaviour of this version should be identical to a uniform cost search but working iteratively so it examines some
 * nodes many times. If cost only increases a few times accross a search space this may be a usuable search. If it is
 * different for every node then it will need to examine N^2 nodes so it is too slow.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do an iterative cost increasing search incrementing by a fixed cost each time.
 * <tr><td> Do an iteartive cost increating search increasing to the next cost each time.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class IterativeCostIncreasingSearch<O, T extends Traversable<O>> extends DepthFirstSearch<O, T>
    implements BoundProperty
{
    /**
     * Creates a new IterativeCostIncreasingSearch object.
     *
     * @param startCost The starting cost for the first iterations.
     */
    public IterativeCostIncreasingSearch(float startCost)
    {
        // Create a cost increasing iterative algorithm by specializing the iterative algorithm with a
        // cost extracting method.
        IterativeBoundAlgorithm<O, T> costIncreasingAlgorithm = new IterativeBoundAlgorithm<O, T>(startCost);

        costIncreasingAlgorithm.setBoundPropertyExtractor(this);

        // Replace the search algorithm with the cost increasing version.
        setQueueSearchAlgorithm(costIncreasingAlgorithm);
    }

    /**
     * Creates a new IterativeCostIncreasingSearch object.
     *
     * @param startCost The starting cost for the first iterations.
     * @param increment The amount to increase the cost limit by at each iteration.
     */
    public IterativeCostIncreasingSearch(float startCost, float increment)
    {
        // Create a cost increasing iterative algorithm by specializing the iterative algorithm with a
        // cost extracting method.
        IterativeBoundAlgorithm<O, T> costIncreasingAlgorithm = new IterativeBoundAlgorithm<O, T>(startCost, increment);

        costIncreasingAlgorithm.setBoundPropertyExtractor(this);

        // Replace the search algorithm with the cost increasing version.
        setQueueSearchAlgorithm(costIncreasingAlgorithm);
    }

    /**
     * Provides an implementation of the {@link BoundProperty} interface to extract the cost as the bound property.
     *
     * @param  searchNode The search node to extract the bound property for.
     *
     * @return The path cost of the supplied search node. The bound property to control the bounded algorithm is the
     *         path cost.
     */
    public float getBoundProperty(SearchNode searchNode)
    {
        return searchNode.getPathCost();
    }
}
