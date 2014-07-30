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
 * UMinus implements the unary minus operator '-' in Prolog.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Negate a number.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class UMinus extends UnaryArithmeticOperator
{
    /**
     * Creates a new '-' expression operator.
     *
     * @param name      The interned name of the operator.
     * @param arguments The arguments, of which there must be two.
     */
    public UMinus(int name, Term[] arguments)
    {
        super(name, arguments);
    }

    /**
     * Evaluates the arithmetic operator on its numeric argument.
     *
     * @param  firstNumber The first argument.
     *
     * @return The result of performing the arithmetic operator on its argument.
     */
    protected NumericType evaluate(NumericType firstNumber)
    {
        // If the argument is a real number, then use real number arithmetic, otherwise use integer arithmetic.
        if (firstNumber.isInteger())
        {
            return new IntLiteral(-firstNumber.intValue());
        }
        else
        {
            return new DoubleLiteral(-firstNumber.doubleValue());
        }
    }
}
