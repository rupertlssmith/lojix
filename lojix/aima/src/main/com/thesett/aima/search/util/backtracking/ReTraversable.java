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
package com.thesett.aima.search.util.backtracking;

import com.thesett.aima.search.Traversable;

/**
 * Reversable is a {@link Traversable} state that can undo the effect of an {@link com.thesett.aima.search.Operator}
 * applied to its predecessor to generate this state. This forms the basic state type definition for
 * {@link DepthFirstBacktrackingSearch}es, which must be able to undo choices they have made when back-tracking.
 *
 * <p/>By undoing globally visible state changes, it is meant that the effect of state changes outside of this state
 * object are undone. For example, if a state takes a fresh copy of a set of variable from a parent state and modifies
 * them, then there is not state change outside of the state to undo. If a state operates on a set of variables shared
 * by all states, then there is state change to undo.
 *
 * <p/>This interface provides two lifecycle methods; one for making the effects of global state changes visible and one
 * for undoing them. The {@link #applyOperator()} method will be called prior to this state being goal evaluated. The
 * {@link #undoOperator()} method will be called when the search back-tracks over this state to explore other areas of
 * the search space. Note that the applied operator itself is passed to the {@link Traversable#getChildStateForOperator}
 * method on this states predecessor. The value passed to this method needs to be passed on the child states, so that
 * the {@link #applyOperator()} method knows which operator to apply.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Undo any globally visible state changes made when generating this state from an operator and a parent state.
 *     <td> {@link Traversable}
 * <tr><td> Apply globally visible state changes on request.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface ReTraversable<O> extends Traversable<O>, Reversable
{
}
