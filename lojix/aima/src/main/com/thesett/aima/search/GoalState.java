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
package com.thesett.aima.search;

import com.thesett.aima.state.State;

/**
 * GoalState represents a state in a search space of states. The actual meaning of what is modeled as a state is
 * completely abstracted and defined by concrete implementations of {@link State}. GoalState is intended to be used by
 * search methods that seek goal states. A goal state should provide a method to indicate whether or not it is a goal.
 *
 * <p/>The same search can be run over a state space to search for different goals by using
 * {@link com.thesett.common.util.logic.UnaryPredicate}s on the state space. This goal state interface is optional, and
 * can be implented by search spaces that define default goals. If this is the case, a default unary predicate can be
 * constructed from this goal state.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Report goal status of a state.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface GoalState extends State
{
    /**
     * Returns whether this state is a goal node.
     *
     * @return True if this is a goal state, false otherwise.
     */
    public boolean isGoal();
}
