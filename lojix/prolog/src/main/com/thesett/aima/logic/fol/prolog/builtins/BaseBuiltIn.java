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
 * BaseBuiltIn provides a base-class on which built-in predicates may be implemented. It provides a default
 * implementation of the {@link BuiltIn#createContinuationStates} method, that looks at the next goal to be proved and
 * generates subsequent resolution states for it.
 *
 * <pre></p><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Create subsequent resolution states for the next goal in the proof.
 *     <td> {@link ResolutionState}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BaseBuiltIn extends BuiltInFunctor
{
    /**
     * Creates the base built in on the specified functor.
     *
     * @param functor The functor to create a built-in for.
     */
    protected BaseBuiltIn(Functor functor)
    {
        super(functor);
    }

    /** {@inheritDoc} */
    public void createContinuationStates(ResolutionState state)
    {
        BuiltInFunctor nextGoal = state.getGoalStack().peek();
        state.createContinuationStatesForGoal(nextGoal);
    }
}
