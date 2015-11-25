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
package com.thesett.aima.logic.fol.isoprologparser;

import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.SentenceImpl;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.common.parsing.SourceCodeException;

/**
 * Parses first order logic terms from strings using Prolog syntax. The term parsed may be any valid first order logic
 * term; variables, atoms, functors, literals, clauses all included. The term will be parsed as a 'sentence' which means
 * that it will be parsed in a fresh variable context, so that variables are scoped over the term only.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Parse a string as a logical term relative to a variable and functor interner.
 *     <td> {@link Term}, {@link VariableAndFunctorInterner}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TermParser extends BasePrologParser<Term>
{
    /**
     * Creates a term parser over an interner.
     *
     * @param interner The interner to use to intern all functor and variable names.
     */
    public TermParser(VariableAndFunctorInterner interner)
    {
        super(interner);
    }

    /**
     * Parses the next sentence from the current token source.
     *
     * @return The fully parsed syntax tree for the next sentence.
     *
     * @throws SourceCodeException If the source being parsed does not match the grammar.
     */
    public Sentence<Term> parse() throws SourceCodeException
    {
        try
        {
            return new SentenceImpl<Term>(parser.termSentence());
        }
        catch (SourceCodeException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
