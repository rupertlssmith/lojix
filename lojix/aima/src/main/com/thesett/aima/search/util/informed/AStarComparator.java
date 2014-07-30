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

import java.util.Comparator;

import com.thesett.aima.search.SearchNode;

/**
 * AStarComparator is a Comparator that compares the f values (f = heuristic + cost) of heuristic search nodes. This can
 * be used to oder the search nodes in an A* Search.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Compare heuristic search nodes by f value.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public final class AStarComparator implements Comparator<SearchNode>
{
    /**
     * Compares two heuristic search nodes by their f values.
     *
     * @param  object1 The first heuristic search node to compare.
     * @param  object2 The second heuristic search node to compare.
     *
     * @return 1 If the first node has a higher f value than the second, 0 if they have the same f value, -1 if the
     *         second has a higher f value than the first.
     */
    public int compare(SearchNode object1, SearchNode object2)
    {
        float f1 = ((HeuristicSearchNode) object1).getF();
        float f2 = ((HeuristicSearchNode) object2).getF();

        return (f1 > f2) ? 1 : ((f1 < f2) ? -1 : 0);
    }
}
