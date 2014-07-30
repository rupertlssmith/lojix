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
package com.thesett.aima.logic.fol.prolog.builtins;

import com.thesett.aima.logic.fol.Functor;

/**
 * NonUnifies is the ISO Prolog built in operator '\='/2. It performs a standard unification (no occurrs check) on its
 * left and right arguments, possibly binding variables as a result of the unification, and fails iff the unification
 * succeeds. As failure will cause this proof step to be undone, any variable bindings resulting from the unification
 * will immediately be discarded.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check if unification of the left and right arguments of the non-unify operator fails.
 *     <td> {@link Unifies}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class NonUnifies extends Unifies
{
    /**
     * Creates a non-unifies built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built in.
     */
    public NonUnifies(Functor functor)
    {
        super(functor);
    }

    /**
     * Attemps to make a single step in the 'proof' of the current query in the current resolution state by the
     * built-in.
     *
     * @param  state The resolution state to prove against.
     *
     * @return <tt>true</tt> if the proof step succeeded, <tt>false</tt> if it failed. Note that success here only means
     *         that the single proof step was ok, and the proof procedure can continue until it reaches resolution. It
     *         does not necessarily mean that the whole proof procedure has suceeded.
     */
    public boolean proofStep(ResolutionState state)
    {
        return !super.proofStep(state);
    }
}
