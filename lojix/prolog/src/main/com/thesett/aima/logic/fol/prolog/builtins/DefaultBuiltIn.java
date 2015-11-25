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
import com.thesett.aima.logic.fol.Variable;

/**
 * The DefaultBuiltIn implements an implicit default built-in operator that implements the normal processing pathway in
 * a Prolog program. That is, the head of the goal stack is unified against the head of the current clause chosen for
 * examination at this step of the proof, and if a match is found, the functors in the clause body are pushed onto the
 * goal stack (in reverse order, so that they are processed intuitively left-to-right when popped off again).
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Unify the current goal against the head of the current clause.
 *     <td> {@link ResolutionState}, {@link PrologUnifier}
 * <tr><td> Create any new goals resulting from the RHS of the unified against clause.
 *     <td> {@link ResolutionState}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DefaultBuiltIn extends BaseBuiltIn
{
    /** Used for providing user readable execution traces. */
    /* private static final Logger trace = Logger.getLogger("TRACE.Prolog." + DefaultBuiltIn.class.getSimpleName()); */

    /** Flag used as a quick check to see if trace is enabled. */
    private static final boolean TRACE = false; //log.isLoggable(Level.FINE)

    /**
     * Creates a default built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built in.
     */
    public DefaultBuiltIn(Functor functor)
    {
        super(functor);
    }

    /** {@inheritDoc} */
    public boolean proofStep(ResolutionState state)
    {
        Functor goalTerm = state.getGoalStack().peek().getFunctor();
        Functor matchTerm = state.getCurrentClause().getHead();

        // This is used to record variables bound on the domain side of the unificiation. This information seems
        // like is does not need to be kept because usually all of these bindings are in the stack frame.
        // However, this is not always the case as unification can capture a variable on the domain side.
        // These variables need to be unbound on backtracking too.
        List<Variable> domainVariables = new LinkedList<Variable>();

        // This is used to record variables bound on the query goal side of the unification. This information
        // must be kept so that the undo operation can unbind these variables before placing the goal back
        // onto the stack when backtracking.
        List<Variable> boundVariables = new LinkedList<Variable>();

        // Unify the current query goal with the possibly matching clause, creating variable bindings.
        boolean matched = state.getUnifier().unifyInternal(goalTerm, matchTerm, boundVariables, domainVariables);

        // Even if unification fails, any partial bindings created are remembered, to ensure that they are cleaned
        // up when this proof steps state is undone.
        for (Variable binding : boundVariables)
        {
            state.getVariableBindings().offer(binding);
        }

        for (Variable binding : domainVariables)
        {
            state.getVariableBindings().offer(binding);
        }

        // If the unification succeeded, establish a new state with the unified query removed from the goal stack, the
        // body of the unified with clause added to it for resolution, and the variable binding trail extended with
        // any additional bindings resulting from the unification.
        if (matched)
        {
            if (TRACE)
            {
                /*trace.fine(state.getTraceIndenter().generateTraceIndent() + "Unify " +
                    goalTerm.toString(state.getInterner(), true, true) + " against " +
                    matchTerm.toString(state.getInterner(), true, true) + ", ok.");*/
            }

            // Consume the successfully unified goal from the goal stack.
            state.getGoalStack().poll();

            // Add all functors on the body side of the unified clause onto the goal stack for resolution.
            Functor[] body = state.getCurrentClause().getBody();

            if ((body != null) && (body.length != 0))
            {
                // The new goals are placed onto the goal stack backwards. It is a stack, hence they get
                // explored first, depth first, but their insertion order is reversed for an intuitive
                // left-to-right evaluation order.
                for (int i = body.length - 1; i >= 0; i--)
                {
                    BuiltInFunctor newGoal = state.getBuiltInTransform().apply(body[i]);
                    newGoal.setParentChoicePointState(state.getLastChoicePoint());
                    state.getGoalStack().offer(newGoal);
                }
            }

            return true;
        }
        else
        {
            if (TRACE)
            {
                /*trace.fine(state.getTraceIndenter().generateTraceIndent() + "Failed to unify " +
                    goalTerm.toString(state.getInterner(), true, true) + " against " +
                    matchTerm.toString(state.getInterner(), true, true) + ".");*/
            }

            return false;
        }
    }
}
