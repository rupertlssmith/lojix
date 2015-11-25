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
package com.thesett.aima.logic.fol.prolog.expressions;

import com.thesett.aima.logic.fol.NumericType;
import com.thesett.aima.logic.fol.Term;

/**
 * BinaryArithmeticOperator provides a base implementation for all one-place arithmetic operators on a numeric argument.
 * The {@link #getValue()} method invokes {@link #getValue()} on its argument to recursively evaluate it, as it may also
 * be an arithmetic operator or other {@link BuiltInExpressionOperator}. It checks that the resulting argument is fully
 * instantiated and numeric prior to calling the operators implementing {#evaluate} method to compute the result.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Ensure that the argument evaluates to a fully instantiated number. <td> {@link Term}, {@link NumericType}.
 * <tr><td> Evaluate the arithmetic operator.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class UnaryArithmeticOperator extends BuiltInExpressionOperator
{
    /**
     * Creates a unary arithmetic operator on one argument.
     *
     * @param name      The interned name of the operator.
     * @param arguments The operators arguments. There must be exactly one.
     */
    protected UnaryArithmeticOperator(int name, Term[] arguments)
    {
        super(name, arguments);

        // Ensure that there are exactly two argument.
        if (arguments.length != 1)
        {
            throw new IllegalArgumentException("UnaryArithmeticOperators must take exactly one argument.");
        }
    }

    /**
     * Gets the actual value of a term, which is a numeric type equal in value to the arithmetic operator applied to its
     * argument. This method checks that the argument produces a value which is fully instantiated and numeric when its
     * {@link Term#getValue()} methods is invoked.
     *
     * @return A numeric type equal in value to the the arithmetic operator applied to its argument.
     */
    public NumericType getValue()
    {
        Term firstArgValue = arguments[0].getValue();

        // Check that the argument to operate on is a numeric values.
        if (!firstArgValue.isNumber())
        {
            throw new IllegalStateException(
                "instantiation_error, 'arithmetic/2' expects numeric arguments, but the first argument is non-numeric.");
        }

        return evaluate((NumericType) firstArgValue);
    }

    /**
     * Evaluates the arithmetic operator on its numeric argument.
     *
     * @param  firstNumber The first argument.
     *
     * @return The result of performing the arithmetic operator on its argument.
     */
    protected abstract NumericType evaluate(NumericType firstNumber);
}
