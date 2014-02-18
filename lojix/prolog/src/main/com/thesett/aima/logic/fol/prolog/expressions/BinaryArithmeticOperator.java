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
package com.thesett.aima.logic.fol.prolog.expressions;

import com.thesett.aima.logic.fol.NumericType;
import com.thesett.aima.logic.fol.Term;

/**
 * BinaryArithmeticOperator provides a base implementation for all two-place arithmetic operators on numeric arguments.
 * The {@link #getValue()} method invokes {@link #getValue()} on its arguments to recursively evaluate them, as they may
 * also be arithmetic operators or other {@link BuiltInExpressionOperator}s. It checks that the resulting arguments are
 * fully instantiated and numeric prior to calling the operators implementing {#evaluate} method to compute the result.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Ensure that both arguments evaluate to fully instantiated numbers. <td> {@link Term}, {@link NumericType}.
 * <tr><td> Evaluate the arithmetic operator.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BinaryArithmeticOperator extends BuiltInExpressionOperator
{
    /**
     * Creates a binary arithmetic operator on two arguments.
     *
     * @param name      The interned name of the operator.
     * @param arguments The operators arguments. There must be exactly two.
     */
    protected BinaryArithmeticOperator(int name, Term[] arguments)
    {
        super(name, arguments);

        // Ensure that there are exactly two argument.
        if (arguments.length != 2)
        {
            throw new IllegalArgumentException("BinaryArithmeticOperators must take exactly two arguments.");
        }
    }

    /**
     * Gets the actual value of a term, which is a numeric type equal in value to the arithmetic operator applied to its
     * arguments. This method checks that both arguments produce values which are fully instantiated and numeric when
     * their {@link Term#getValue()} methods are invoked.
     *
     * @return A numeric type equal in value to the the arithmetic operator applied to its arguments.
     */
    public NumericType getValue()
    {
        Term firstArgValue = arguments[0].getValue();
        Term secondArgValue = arguments[1].getValue();

        // Check that the arguments to operate on are both numeric values.
        if (!firstArgValue.isNumber())
        {
            throw new RuntimeException(
                "instantiation_error, 'arithmetic/2' expects numeric arguments, but the first argument is non-numeric.");
        }

        if (!secondArgValue.isNumber())
        {
            throw new RuntimeException(
                "instantiation_error, 'arithmetic/2' expects numeric arguments, but the second argument is non-numeric.");
        }

        return evaluate((NumericType) firstArgValue, (NumericType) secondArgValue);
    }

    /**
     * Evaluates the arithmetic operator on its two numeric arguments.
     *
     * @param  firstNumber  The first argument.
     * @param  secondNumber The second argument.
     *
     * @return The result of performing the arithmetic operator on its arguments.
     */
    protected abstract NumericType evaluate(NumericType firstNumber, NumericType secondNumber);
}
