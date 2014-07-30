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

import java.util.List;

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.TermUtils;

/**
 * Disjunction implements the Prolog disjunction operator ';' that sets up multiple choice points potentially leading to
 * multiple solutions.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Implement the disjunction operator.
 *     <td> {@link Functor}, {@link TermUtils}, {@link ResolutionState}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Disjunction extends BuiltInFunctor
{
    /** Holds the interned name of the operator matching this built in. */
    private int name;

    /** Holds the disjunctiv choice points that lead to states following this proof step. */
    private List<Functor> disjuncts;

    /**
     * Creates a disjunction built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built-in.
     */
    public Disjunction(Functor functor)
    {
        super(functor);
    }

    /** {@inheritDoc} */
    public boolean proofStep(ResolutionState state)
    {
        // Flatten the head goal on the disjunction operator to produce a series of choice points.
        Functor goalTerm = state.getGoalStack().poll().getFunctor();

        disjuncts = TermUtils.flattenTerm(goalTerm, Functor.class, state.getInterner().internFunctorName(";", 2));

        return true;
    }

    /** {@inheritDoc} */
    public void createContinuationStates(final ResolutionState state)
    {
        for (Functor disjunct : disjuncts)
        {
            BuiltInFunctor newGoal = state.getBuiltInTransform().apply(disjunct);
            newGoal.setParentChoicePointState(state);
            state.createContinuationStatesForGoal(newGoal);
        }
    }
}
