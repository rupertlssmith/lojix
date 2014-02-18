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

/**
 * BinaryArithmeticComparison provides a base class for implementing two-place arithmetic comparisons on numeric
 * arguments. The arguments are evaluated in the same way as the argument to the 'is' operator is evaluated, that is,
 * any arithmetic operators are evaluated and the result is checked to ensure that it is numeric and fully instantiated.
 * After this, the {@link #evaluate} method is invoked to determine the outcome of the arithmetic comparison.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Ensure that both arguments evaluate to fully instantiated numbers. <td> {@link NumericType}.
 * <tr><td> Evaluate the arithmetic comparison.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BinaryArithmeticComparison extends BaseBuiltIn
{
    /**
     * Creates a binary arithmetic built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built-in.
     */
    protected BinaryArithmeticComparison(Functor functor)
    {
        super(functor);
    }

    /** {@inheritDoc} */
    public boolean proofStep(ResolutionState state)
    {
        Functor isOp = state.getGoalStack().poll().getFunctor();

        // Evaluate both sides of the comparison, checking that they are fully instantiated numbers.
        NumericType n1 = BuiltInUtils.evaluateAsNumeric(isOp.getArgument(0));
        NumericType n2 = BuiltInUtils.evaluateAsNumeric(isOp.getArgument(1));

        // Evaluate the comparison operator.
        return evaluate(n1, n2);
    }

    /**
     * Evaluates the arithmetic comparison on its two numeric arguments.
     *
     * @param  firstNumber  The first argument.
     * @param  secondNumber The second argument.
     *
     * @return The result of performing the arithmetic comparison on its arguments.
     */
    protected abstract boolean evaluate(NumericType firstNumber, NumericType secondNumber);
}
