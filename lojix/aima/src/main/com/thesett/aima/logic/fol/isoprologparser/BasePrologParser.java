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
package com.thesett.aima.logic.fol.isoprologparser;

import com.thesett.aima.logic.fol.OpSymbol;
import com.thesett.aima.logic.fol.Parser;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.common.util.Source;

/**
 * BasePrologParser provides a base implementations for writing parsers over Prolog syntax.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Allow the token source to be set.
 * <tr><td> Determine the parsers interner for functor and variable names.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BasePrologParser<T extends Term> implements Parser<T, Token>
{
    /** Holds the underlying parser implementation. */
    protected PrologParser parser;

    /**
     * Creates a prolog parser using the specified interner.
     *
     * @param interner The functor and variable name interner.
     */
    public BasePrologParser(VariableAndFunctorInterner interner)
    {
        parser = new PrologParser(null, interner);
    }

    /**
     * Establishes the token source to parse from.
     *
     * @param source The token source to parse from.
     */
    public void setTokenSource(Source<Token> source)
    {
        parser.setTokenSource(source);
    }

    /**
     * Sets up a custom operator symbol on the parser.
     *
     * @param operatorName  The name of the operator to create.
     * @param priority      The priority of the operator, zero unsets it.
     * @param associativity The operators associativity.
     */
    public void setOperator(String operatorName, int priority, OpSymbol.Associativity associativity)
    {
        parser.internOperator(operatorName, priority, associativity);
    }

    /** {@inheritDoc} */
    public boolean peekAndConsumeMore()
    {
        return parser.peekAndConsumeMore();
    }
}
