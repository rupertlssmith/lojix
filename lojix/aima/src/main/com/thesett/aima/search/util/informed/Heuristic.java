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

import com.thesett.aima.search.Traversable;

/**
 * A heuristic is a 'rule of thumb' for evaluating a state. This interface defines methods to produce real valued
 * heuristic evaluations. This interface is intended to be implemented by {@link com.thesett.aima.search.GoalState}s
 * which provide heuristic evaluations.
 *
 * <p>Heuristics can often be calculated incrementally from the heurstic for the parent state. This may offer a
 * significant reduction in the amount of work that the heuristic evaluation has to do. For this reason the
 * {@link HeuristicSearchNode} corresponding to the state to be evaluated is also passed as an argument to the
 * {@link #computeH} method.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Calculate the heuristic value (of a state)
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Heuristic<O, T extends Traversable<O>>
{
    /**
     * Return heuristic evaluation as a real value.
     *
     * @param  state      the state to be evaluated.
     * @param  searchNode the {@link HeuristicSearchNode} for the state.
     *
     * @return a real valued heuristic evaluation of the state.
     */
    public float computeH(T state, HeuristicSearchNode<O, T> searchNode);
}
