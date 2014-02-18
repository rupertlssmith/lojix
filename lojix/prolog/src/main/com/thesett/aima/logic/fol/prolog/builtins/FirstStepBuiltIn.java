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

import com.thesett.aima.logic.fol.Clause;

/**
 * FirstStepBuiltIn takes a query (a headless clause) in first order logic, and creates continuation steps for each
 * conjunctive component of the query, as a proof step on a functor. The first step of a proof is different from the
 * other steps, because it operates on a query clause and not on a functor.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Turn a query into a conjunctive set of subsequent proof states.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class FirstStepBuiltIn extends BaseBuiltIn
{
    /**
     * Creates a first step built-in to implement the first step of turning a query clause into a conjunction of goals
     * to be proved.
     */
    public FirstStepBuiltIn()
    {
        super(null);
    }

    /** {@inheritDoc} */
    public boolean proofStep(ResolutionState state)
    {
        Clause query = state.getCurrentClause();

        // The query goals are placed onto the goal stack backwards so that their insertion order is reversed for an
        // intuitive left-to-right evaluation order.
        for (int i = query.getBody().length - 1; i >= 0; i--)
        {
            BuiltInFunctor newGoal = state.getBuiltInTransform().apply(query.getBody()[i]);
            state.getGoalStack().offer(newGoal);
        }

        return true;
    }
}
