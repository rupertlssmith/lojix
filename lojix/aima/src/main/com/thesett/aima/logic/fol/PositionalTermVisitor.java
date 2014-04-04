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
package com.thesett.aima.logic.fol;

import com.thesett.aima.logic.fol.compiler.PositionalTermTraverser;

/**
 * PositionalTermVisitor is a {@link TermVisitor} that accepts a {@link PositionalTermTraverser}. The
 * PositionalTermTraverser provides additional information during a visit of a term tree, about the positional context
 * in which sub-terms are visited.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Accept a positional term traverser.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface PositionalTermVisitor extends TermVisitor
{
    /**
     * Sets up the positional term traverser used to traverse the term being visited, providing a positional context as
     * it does so.
     *
     * @param traverser The positional term traverser used to traverse the term being visited.
     */
    void setPositionalTraverser(PositionalTermTraverser traverser);
}
