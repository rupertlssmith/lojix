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
package com.thesett.aima.logic.fol;

import com.thesett.common.parsing.SourceCodeException;
import com.thesett.common.util.Source;

/**
 * A parser translates a token source for some first order logic langauge derivative (prolog, for example), and returns
 * valid sentences in that language over some refinement of the basic {@link Term} syntax tree.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Transform tokens into syntax trees for sentences.
 * </table></pre>
 *
 * @param  <S> The term type that this parser produces.
 * @param  <T> The type of token that this parser parses.
 *
 * @author Rupert Smith
 */
public interface Parser<S extends Term, T>
{
    /**
     * Establishes the token source to parse from.
     *
     * @param source The token source to parse from.
     */
    void setTokenSource(Source<T> source);

    /**
     * Parses the next sentence from the current token source.
     *
     * @return The fully parsed syntax tree for the next sentence.
     *
     * @throws SourceCodeException If the source being parsed does not match the grammar.
     */
    Sentence<S> parse() throws SourceCodeException;

    /**
     * Sets up a custom operator symbol on the parser.
     *
     * @param operatorName  The name of the operator to create.
     * @param priority      The priority of the operator, zero unsets it.
     * @param associativity The operators associativity.
     */
    void setOperator(String operatorName, int priority, OpSymbol.Associativity associativity);
}
