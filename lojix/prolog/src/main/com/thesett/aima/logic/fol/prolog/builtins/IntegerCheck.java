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
import com.thesett.aima.logic.fol.NumericType;
import com.thesett.aima.logic.fol.Term;

/**
 * Implements the integer/1 runtime type check, that checks its argument is instantiated to an integer.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Checks that a term is instantiated to an integer value. <td> {@link ResolutionState}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class IntegerCheck extends BaseBuiltIn implements BuiltIn
{
    /**
     * Creates an integer built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built in.
     */
    public IntegerCheck(Functor functor)
    {
        super(functor);
    }

    /** {@inheritDoc} */
    public boolean proofStep(ResolutionState state)
    {
        Functor goalTerm = state.getGoalStack().poll().getFunctor();
        Term argument = goalTerm.getArgument(0).getValue();

        // Check that the argument is not a free variable.
        return argument.isNumber() && ((NumericType) argument.getValue()).isInteger();
    }
}
