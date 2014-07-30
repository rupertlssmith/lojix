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

import java.util.Queue;

import com.thesett.aima.logic.fol.Functor;

/**
 * Cut implements the prolog '!' operator, that prevents back-tracking within a functor. '!' is true; that is it does
 * not ever fail. All choice points between the cut and the parent goal are removed. The effect is to commit to use both
 * the current clause and the substitutions found at the point of the cut.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Implement the cut operator.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Cut extends BaseBuiltIn
{
    /** Used for providing user readable execution traces. */
    /* private static final Logger trace = Logger.getLogger("TRACE.Prolog." + Cut.class.getSimpleName()); */

    /** Flag used as a quick check to see if trace is enabled. */
    private static final boolean TRACE = false; //log.isLoggable(Level.FINE)

    /**
     * Creates a cut built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built-in.
     */
    public Cut(Functor functor)
    {
        super(functor);
    }

    /** {@inheritDoc} */
    public boolean proofStep(ResolutionState state)
    {
        if (TRACE)
        {
            /*trace.fine(state.getTraceIndenter().generateTraceIndent() + "Cutting choice points...");*/
        }

        Queue<ResolutionState> choicePointStates = parentChoicePointState.getChoicePoints();

        if (choicePointStates != null)
        {
            for (ResolutionState choicePointState : choicePointStates)
            {
                choicePointState.cut();
            }
        }

        state.getGoalStack().poll();

        return true;
    }
}
