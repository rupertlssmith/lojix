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
import com.thesett.aima.search.impl.BoundedAlgorithm;
import com.thesett.aima.search.spi.BoundProperty;

/**
 * Implements a Cost-bounded search. This procedes depth first but is bounded by a maximum path cost.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do a cost bounded search.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class CostBoundedSearch<O, T extends Traversable<O>> extends DepthFirstSearch<O, T> implements BoundProperty
{
    /**
     * Creates a new CostBoundedSearch object.
     *
     * @param maxCost The maximum path cost to search to.
     */
    public CostBoundedSearch(float maxCost)
    {
        // Create a cost bounded algorithm by specializing the bounded algorithm with a cost extracting method.
        BoundedAlgorithm costBoundedAlgorithm = new BoundedAlgorithm(maxCost);

        costBoundedAlgorithm.setBoundPropertyExtractor(this);

        // Replace the search algorithm with the cost bounded version.
        setQueueSearchAlgorithm(costBoundedAlgorithm);
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
