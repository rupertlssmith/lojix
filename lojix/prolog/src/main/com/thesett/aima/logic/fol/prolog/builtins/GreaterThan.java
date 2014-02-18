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
 * GreaterThan compares two numbers.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Compare two numbers using integer or floating point arithemtic. <td> {@link NumericType}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class GreaterThan extends BinaryArithmeticComparison
{
    /**
     * Creates a greater-than built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built in.
     */
    public GreaterThan(Functor functor)
    {
        super(functor);
    }

    /**
     * Evaluates the arithmetic comparison on its two numeric arguments.
     *
     * @param  firstNumber  The first argument.
     * @param  secondNumber The second argument.
     *
     * @return The result of performing the arithmetic comparison on its arguments.
     */
    protected boolean evaluate(NumericType firstNumber, NumericType secondNumber)
    {
        // If either of the arguments is a real number, then use real number arithmetic, otherwise use integer arithmetic.
        if (firstNumber.isInteger() && secondNumber.isInteger())
        {
            return firstNumber.intValue() > secondNumber.intValue();
        }
        else
        {
            return firstNumber.doubleValue() > secondNumber.doubleValue();
        }
    }
}
