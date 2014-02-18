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
import com.thesett.aima.search.impl.IterativeBoundAlgorithm;

/**
 * Implements an iterative deepening search. This procedes depth first but is bounded by itertively increasing depths.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Do an iterative deepening search.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class IterativeDeepeningSearch<O, T extends Traversable<O>> extends DepthFirstSearch<O, T>
{
    /**
     * Creates a new IterativeDeepeningSearch object.
     *
     * @param startDepth The starting depth for the first iteration.
     * @param increment  The depth to increase by at each iteration.
     */
    public IterativeDeepeningSearch(int startDepth, int increment)
    {
        // Replace the search algorithm with an iterative deepening version.
        setQueueSearchAlgorithm(new IterativeBoundAlgorithm<O, T>((float) startDepth, (float) increment));
    }
}
