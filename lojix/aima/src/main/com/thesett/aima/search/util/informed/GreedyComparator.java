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

import java.util.Comparator;

import com.thesett.aima.search.SearchNode;

/**
 * GreedyComparator is a Comparator that compares the heuristic values of heuristic search nodes. This can be used to
 * order the search nodes in a greedy search.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Order heuristic search nodes by heuristic value.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public final class GreedyComparator implements Comparator<SearchNode>
{
    /**
     * Compares two heuristic search nodes by their heuristic values.
     *
     * @param  object1 The first search node to compare.
     * @param  object2 The second search node to compare.
     *
     * @return 1 if the first search node has a larger heuristic value than the second, -1 if it has a smaller heuristic
     *         value and 0 if they are the same.
     */
    public int compare(SearchNode object1, SearchNode object2)
    {
        float h1 = ((HeuristicSearchNode) object1).getH();
        float h2 = ((HeuristicSearchNode) object2).getH();

        return (h1 > h2) ? 1 : ((h1 < h2) ? -1 : 0);
    }
}
