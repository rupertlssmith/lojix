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

/**
 * SentenceImpl provides a simple implementation of {@link Sentence} for packaging logic terms as sentences in some
 * logical language.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Capture some sub-type of Term as a sentence. <td> {@link Term}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SentenceImpl<T extends Term> implements Sentence<T>
{
    /** The term to present as a sentence. */
    T term;

    /**
     * Creates a new sentence from a term.
     *
     * @param term The term to capture as a sentence.
     */
    public SentenceImpl(T term)
    {
        this.term = term;
    }

    /**
     * Provides the term captured by this sentence.
     *
     * @return The term captured by this sentence.
     */
    public T getT()
    {
        return term;
    }
}
