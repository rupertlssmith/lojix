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
package com.thesett.aima.logic.fol.prolog.builtins;

/**
 * BuiltIn defines the behaviour of Prolog built-in predicates. A regular predicate, when encountered on the resolution
 * goal stack, is handled by attempting to find a unification with it against the current domain, and expanding the
 * right hand side of any matching domain clause as further goals to solve. That is, regular predicates are implicitly
 * handled by a unify and goal-expand operation, which can also be viewed as the 'default built-in'. There are also a
 * variety of built-in predicates to perform tasks such as comparisons and meta-level queries and so on. This interface
 * defines the operations that a built-in must perform, and its relationship to {@link ResolutionState} which provides
 * the current state of the resolver on which the built-in may operate.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Attempt to resolve a single functor against a resolution state.
 * <tr><td> Create the resolution states that follow the proof step, if the proof step succeeded.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface BuiltIn
{
    /**
     * Attempts to make a single step in the 'proof' of the current query in the current resolution state by the
     * built-in. This should consist of consuming a single goal predicate from the goal stack, or failing.
     * Implementations may add more goals to the goal stack too.
     *
     * <p/>Note that success here only means that the single proof step was ok, and the proof procedure can continue
     * until it reaches resolution. It does not necessarily mean that the whole proof procedure has suceeded.
     *
     * @param  state The resolution state to prove against.
     *
     * @return <tt>true</tt> if the proof step succeeded, <tt>false</tt> if it failed.
     */
    public boolean proofStep(ResolutionState state);

    /**
     * Creates the continuation states that a built in generates in a given resolution state for subsequent resolution.
     * This will only be called if the {@link #proofStep(ResolutionState)} method succeeded.
     *
     * @param state The state to generate continuation states for.
     */
    public void createContinuationStates(ResolutionState state);
}
