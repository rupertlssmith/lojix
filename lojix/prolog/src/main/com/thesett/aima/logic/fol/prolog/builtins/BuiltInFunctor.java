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
 * BuiltInFunctor provides a mapping from a functor onto its built-in implementation, in the case of built-in predicates
 * in Prolog.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide a mapping from a functor to its built-in implementation. <td> {@link BuiltIn}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BuiltInFunctor implements BuiltIn
{
    /** Holds the parent state that is the most recent choice point that led to this state. */
    protected ResolutionState parentChoicePointState;

    /** Holds the functor that this is a built in for. */
    protected Functor functor;

    /**
     * Creates a built-in for the specified functor.
     *
     * @param functor The functor to create a built-in for.
     */
    public BuiltInFunctor(Functor functor)
    {
        this.functor = functor;
    }

    /**
     * Provides the functor that this is a built-in for.
     *
     * @return The functor that this is a built-in for.
     */
    public Functor getFunctor()
    {
        return functor;
    }

    /**
     * Provides the resolution state that is the parent choice point that led to this the state for this built-in
     * functor.
     *
     * @return The resolution state that is the parent choice point that led to this the state for this built-in
     *         functor.
     */
    public ResolutionState getParentChoicePointState()
    {
        return parentChoicePointState;
    }

    /**
     * Allows a resolution state this is the parent choice point state that led to the state for this built-in functor
     * to be stored against the built-in functor. This allows the built-in functor to access its parent choice point in
     * its execution implementation if required.
     *
     * @param parentChoicePointState The resolution state that is the parent choice point that led to this the state for
     *                               this built-in functor.
     */
    public void setParentChoicePointState(ResolutionState parentChoicePointState)
    {
        this.parentChoicePointState = parentChoicePointState;
    }
}
