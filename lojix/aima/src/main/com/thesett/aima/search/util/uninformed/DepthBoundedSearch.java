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

import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.impl.BoundedAlgorithm;

/**
 * Implements a Depth-bounded search. This proceeds depth first but is bounded.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do a depth bounded search.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DepthBoundedSearch<O, T extends Traversable<O>> extends DepthFirstSearch<O, T>
{
    /**
     * Creates a new DepthBoundedSearch object.
     *
     * @param maxDepth The maximum search depth to search to.
     */
    public DepthBoundedSearch(int maxDepth)
    {
        // Replace the default search algorithm with a depth bounded version.
        setQueueSearchAlgorithm(new BoundedAlgorithm((float) maxDepth));
    }
}
