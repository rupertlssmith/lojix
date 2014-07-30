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
package com.thesett.aima.search.spi;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.Traversable;

/**
 * BoundProperty is the interface of a function to extract the value of some property as a float from a SearchNode. This
 * is used by bounded searches that are restricted to only searching amongst nodes within a bounded value.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Extract the value the value of a bounded property of a search node as a float.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface BoundProperty<O, T extends Traversable<O>>
{
    /**
     * Should extract the value the value of a bounded property of a search node as a float.
     *
     * @param  searchNode The search node to get the bounded value of.
     *
     * @return The bounded value of the search node as a float.
     */
    public float getBoundProperty(SearchNode<O, T> searchNode);
}
