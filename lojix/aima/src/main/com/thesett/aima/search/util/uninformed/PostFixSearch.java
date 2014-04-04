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

import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.spi.QueueSearchAlgorithm;

/**
 * Implements a post-fix ordered search. This is done by passing a FIFO stack to the {@link QueueSearchAlgorithm}
 * implementation in combination with the 'peek at head' flag. The use of the peek at head flag ensures that successor
 * nodes are examined before parent nodes, resulting in a post-fix ordering of the search.
 *
 * <p/>This type of search is not usefull on unbounded state spaces, because the leaf nodes will never be reached. It is
 * possible for it to be usefull on an unbounded state space when used in combination with a bound. The effect of this
 * type of search, is to reach out to the edge of the available state space and then to work back in to the starting
 * point.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do a post-fix ordered search.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PostFixSearch<O, T extends Traversable<O>> extends DepthFirstSearch<O, T>
{
    /** Creates a new postfix search. */
    public PostFixSearch()
    {
        super();
    }

    /**
     * Allows different queue search algorithms to replace the default one. This overidden method ensures that the peek
     * at head flag is always set on the search algorithm and that it expands it successor nodes in reverse, as for
     * depth first searches.
     *
     * @param algorithm The search algorithm to use.
     */
    protected void setQueueSearchAlgorithm(QueueSearchAlgorithm<O, T> algorithm)
    {
        algorithm.setPeekAtHead(true);
        algorithm.setReverseEnqueueOrder(true);
        super.setQueueSearchAlgorithm(algorithm);
    }
}
