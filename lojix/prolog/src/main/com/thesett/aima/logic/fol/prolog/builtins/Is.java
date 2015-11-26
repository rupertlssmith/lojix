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

import java.util.LinkedList;
import java.util.List;

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Variable;

/**
 * Is implements the arithmetic evaluation operator in Prolog. The right hand side of the operator is evaluated and must
 * produce a numeric result. The result is then unified against the left hand side to determine whether the 'is'
 * predicate is proveable.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Evaluate an arithemtic expression and unify it with another term.
 *     <td> {@link ResolutionState}, {@link PrologUnifier}, {@link Term},
 *          {@link com.thesett.aima.logic.fol.NumericType}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Is extends BaseBuiltIn
{
    /** Used for debugging purposes. */
    public static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(Is.class.getName());

    /**
     * Creates an is built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built in.
     */
    public Is(Functor functor)
    {
        super(functor);
    }

    /** {@inheritDoc} */
    public boolean proofStep(ResolutionState state)
    {
        Functor isOp = state.getGoalStack().poll().getFunctor();

        // Evaluate the second argument as a fully instantiated numeric value.
        Term expressionValue = BuiltInUtils.evaluateAsNumeric(isOp.getArgument(1));

        // This is used to record variables bound during the unification, so that they may be undone if the resolution
        // state is backtracked over.
        List<Variable> boundVariables = new LinkedList<Variable>();

        // Unify against the LHS.
        boolean matched =
            state.getUnifier().unifyInternal(isOp.getArgument(0), expressionValue, boundVariables, boundVariables);

        for (Variable binding : boundVariables)
        {
            state.getVariableBindings().offer(binding);
        }

        return matched;
    }
}
