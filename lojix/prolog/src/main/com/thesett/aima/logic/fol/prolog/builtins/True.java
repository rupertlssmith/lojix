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
 * Implements the true atom. This always succeeds without generating any choice points.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Always succeed, consuming the 'true' goal.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class True extends BaseBuiltIn
{
    /** Used for providing user readable execution traces. */
    /* private static final Logger trace = Logger.getLogger("TRACE.Prolog." + True.class.getSimpleName()); */

    /** Flag used as a quick check to see if trace is enabled. */
    private static final boolean TRACE = false; //log.isLoggable(Level.FINE)

    /**
     * Creates a true built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built in.
     */
    public True(Functor functor)
    {
        super(functor);
    }

    /** {@inheritDoc} */
    public boolean proofStep(ResolutionState state)
    {
        if (TRACE)
        {
            /*trace.fine(state.getTraceIndenter().generateTraceIndent() + "True.");*/
        }

        state.getGoalStack().poll();

        return true;
    }
}
