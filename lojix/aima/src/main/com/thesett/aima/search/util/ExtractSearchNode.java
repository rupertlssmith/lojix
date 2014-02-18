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
package com.thesett.aima.search.util;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;
import com.thesett.common.util.Function;

/**
 * ExtractSearchNode is a {@link Function} that extracts the underlying state from a search node. It is usefull for
 * transforming the outputs of searches.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Extract the state from a search node.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ExtractSearchNode<O, T extends Traversable<O>> implements Function<SearchNode<O, T>, T>
{
    /**
     * Extracts the state from a search node.
     *
     * @param  searchNode The search node.
     *
     * @return The state of the search node.
     */
    public T apply(SearchNode<O, T> searchNode)
    {
        return searchNode.getState();
    }
}
