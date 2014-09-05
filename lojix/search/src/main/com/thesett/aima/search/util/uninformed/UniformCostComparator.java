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

import java.util.Comparator;

import com.thesett.aima.search.SearchNode;

/**
 * UniformCostComparator is a Comparator that compare the path costs for two search nodes.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Order search nodes by their path cost.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public final class UniformCostComparator implements Comparator<SearchNode>
{
    /**
     * Compares two search nodes by their path cost.
     *
     * @param  object1 The first search node to compare.
     * @param  object2 The second search node to compare.
     *
     * @return 1 if the first node has a higher path cost than the second, -1 if it has a lower path cost and 0 if they
     *         have the same path cost.
     */
    public int compare(SearchNode object1, SearchNode object2)
    {
        float cost1 = object1.getPathCost();
        float cost2 = object2.getPathCost();

        return (cost1 > cost2) ? 1 : ((cost1 < cost2) ? -1 : 0);
    }
}
