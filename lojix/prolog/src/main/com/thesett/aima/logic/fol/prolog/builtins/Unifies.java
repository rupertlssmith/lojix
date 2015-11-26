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
 * Unifies is the ISO Prolog built in operator '='/2. It performs a standard unification (no occurrs check) on its left
 * and right arguments, possibly binding variables as a result of the unification, and succeeds iff the unification
 * succeeds.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Unify the left and right arguments of the unify operator.
 *     <td> {@link PrologUnifier}.
 * <tr><td> Add unified variables to the resolution state. <td> {@link ResolutionState}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Unifies extends BaseBuiltIn
{
    /** Used for providing user readable execution traces. */
    /* private static final Logger trace = Logger.getLogger("TRACE.Prolog." + Unifies.class.getSimpleName()); */

    /** Flag used as a quick check to see if trace is enabled. */
    private static final boolean TRACE = false; //log.isLoggable(Level.FINE)

    /**
     * Creates a unifies built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built in.
     */
    public Unifies(Functor functor)
    {
        super(functor);
    }

    /** {@inheritDoc} */
    public boolean proofStep(ResolutionState state)
    {
        Functor goalTerm = state.getGoalStack().poll().getFunctor();

        Term leftArg = goalTerm.getArgument(0);
        Term rightArg = goalTerm.getArgument(1);

        // This is used to record variables bound during the unification, so that they may be undone if the resolution
        // state is backtracked over.
        List<Variable> boundVariables = new LinkedList<Variable>();

        // Unify the current query goal with the possibly matching clause, creating variable bindings.
        boolean matched = state.getUnifier().unifyInternal(leftArg, rightArg, boundVariables, boundVariables);

        if (matched)
        {
            if (TRACE)
            {
                /*trace.fine(state.getTraceIndenter().generateTraceIndent() + "Unify " +
                    leftArg.toString(state.getInterner(), true, true) + " against " +
                    rightArg.toString(state.getInterner(), true, true) + ", ok.");*/
            }

            for (Variable binding : boundVariables)
            {
                state.getVariableBindings().offer(binding);
            }
        }

        return matched;
    }
}
