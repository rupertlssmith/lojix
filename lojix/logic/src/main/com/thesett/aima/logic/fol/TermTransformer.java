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
 * TermVisitor provides the interface for a visitor/transformer over term trees.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Transform a term.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Visitor/transformer pattern over terms: Ability to replace child-terms in place with new terms. Ability to
 *         pass context down to child terms. Ability to iterate over child terms in any queue based search ordering.
 *         Looks like a traversable, operators capture and pass on state, goal checks do the visiting.
 */
public interface TermTransformer
{
    /**
     * Applies a transformation to the term.
     *
     * @param  term The term to transform.
     *
     * @return A term which is a transformation of the argument.
     */
    Term transform(Term term);
}
