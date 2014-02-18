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

import com.thesett.aima.logic.fol.NumericType;
import com.thesett.aima.logic.fol.Term;

/**
 * BuiltInUtils provides some helper methods for evaluating built-in operators and predicates.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Evaluate terms and check that they are valid numbers.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BuiltInUtils
{
    /**
     * Evaluates a term by invoking its {@link Term#getValue()} method (which may cause a recursive evaluation, for
     * example, in the case of arithmetic expressions), and checks that the result is a fully instantiated numeric
     * value.
     *
     * @param  numeric The term to evaluate as a number.
     *
     * @return The term as a number.
     */
    static NumericType evaluateAsNumeric(Term numeric)
    {
        // Evaluate the expression.
        Term expressionValue = numeric.getValue();

        // Ensure that the result of evaluating the expression is a number.
        if (expressionValue.isVar())
        {
            throw new RuntimeException("instantiation_error, 'is' expects a fully instantiated term to unify against.");
        }

        if (!expressionValue.isNumber())
        {
            throw new RuntimeException("arithmetic_error, 'is' expectes a numeric expression to unify against.");
        }

        return (NumericType) expressionValue;
    }
}
