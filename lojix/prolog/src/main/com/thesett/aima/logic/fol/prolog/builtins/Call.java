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

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.Term;

/**
 * Call implements the Prolog 'call' operator. Call resolves its argument as a query. The argument to call may be a
 * variable, but must be bound to a callable functor or atom at the time the call is made in order to be valid.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Call a callable functor or atom as a query for resolution. <td> {@link ResolutionState}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Call extends BaseBuiltIn implements BuiltIn
{
    /**
     * Creates a call built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built-in.
     */
    public Call(Functor functor)
    {
        super(functor);
    }

    /** {@inheritDoc} */
    public boolean proofStep(ResolutionState state)
    {
        Functor goalTerm = state.getGoalStack().poll().getFunctor();
        Term argument = goalTerm.getArgument(0).getValue();

        // Check that the argument is not a free variable.
        if (argument.isVar())
        {
            throw new RuntimeException(
                "instantiation_error, 'call' expects a fully instantiated term to unify against.");
        }

        // Check that the argument is callable.
        if (!argument.isFunctor() && !argument.isAtom())
        {
            throw new RuntimeException("type_error, callable expected as argument to 'call'.");
        }

        // Set up the argument to call as a new goal.
        state.getGoalStack().offer(state.getBuiltInTransform().apply((Functor) argument));

        return true;
    }
}
