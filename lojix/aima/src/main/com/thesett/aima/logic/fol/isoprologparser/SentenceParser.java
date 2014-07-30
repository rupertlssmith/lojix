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

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.SentenceImpl;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.common.parsing.SourceCodeException;

/**
 * Parses first order logic Horn clauses from strings using Prolog syntax. The sentence should be a clause terminated by
 * a full stop.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Parse a string as a logical term relative to a variable and functor interner.
 *     <td> {@link Clause}, {@link VariableAndFunctorInterner}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SentenceParser extends BasePrologParser<Clause>
{
    /**
     * Creates a clause parser over an interner.
     *
     * @param interner The interner to use to intern all functor and variable names.
     */
    public SentenceParser(VariableAndFunctorInterner interner)
    {
        super(interner);
    }

    /**
     * Parses the next sentence from the current token source.
     *
     * @return The fully parsed syntax tree for the next sentence, or null if no more sentences are available, for
     *         example, because an end of file has been reached.
     *
     * @throws SourceCodeException If the source being parsed does not match the grammar.
     */
    public Sentence<Clause> parse() throws SourceCodeException
    {
        if (parser.peekAndConsumeEof())
        {
            return null;
        }
        else
        {
            return new SentenceImpl<Clause>(parser.sentence());
        }
    }
}
