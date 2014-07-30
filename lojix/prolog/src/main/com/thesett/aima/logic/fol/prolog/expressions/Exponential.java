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

import com.thesett.aima.logic.fol.DoubleLiteral;
import com.thesett.aima.logic.fol.IntLiteral;
import com.thesett.aima.logic.fol.NumericType;
import com.thesett.aima.logic.fol.Term;

/**
 * Exponential implements the multiplication operator '**' in Prolog.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Raise one numeric value to the power of the other argument using integer or floating point arithmetic.
 *     <td> {@link NumericType}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Exponential extends BinaryArithmeticOperator
{
    /**
     * Creates a new '**' expression operator.
     *
     * @param name      The interned name of the operator.
     * @param arguments The arguments, of which there must be two.
     */
    public Exponential(int name, Term[] arguments)
    {
        super(name, arguments);
    }

    /**
     * Evaluates the arithmetic operator on its two numeric arguments.
     *
     * @param  firstNumber  The first argument.
     * @param  secondNumber The second argument.
     *
     * @return The result of performing the arithmetic operator on its arguments.
     */
    protected NumericType evaluate(NumericType firstNumber, NumericType secondNumber)
    {
        // If either of the arguments is a real number, then use real number arithmetic, otherwise use integer arithmetic.
        if (firstNumber.isInteger() && secondNumber.isInteger())
        {
            int n1 = firstNumber.intValue();
            int n2 = secondNumber.intValue();

            int result = 1;

            for (int i = 0; i < n2; i++)
            {
                result *= n1;
            }

            return new IntLiteral(result);
        }
        else
        {
            return new DoubleLiteral(Math.pow(firstNumber.doubleValue(), secondNumber.doubleValue()));
        }
    }
}
