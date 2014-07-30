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

import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.Term;

/**
 * BuiltInExpressionOperator is the root class for operators that appear in Prolog expressions. These operators are
 * distinct from so called top-level operators, and implement operators in expressions such as arithmetic and
 * comparisons. An expression operator does cannot form a sentence or a top-level functor in a clause in Prolog. For
 * example:
 *
 * <pre>
 * X = 2.
 * </pre>
 *
 * is allowed because '=' is a top-level built-in operator, but
 *
 * <pre>
 * X + 2.
 * </pre>
 *
 * is not allowed, because '+' is an built-in expression operator.
 *
 * <p/>The {@link #getValue()} method is used to evaluate an expression.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BuiltInExpressionOperator extends Functor
{
    /**
     * Creates a built-in expression operator as a functor.
     *
     * @param name      The interned name of the expression.
     * @param arguments The expressions arguments.
     */
    public BuiltInExpressionOperator(int name, Term[] arguments)
    {
        super(name, arguments);
    }
}
