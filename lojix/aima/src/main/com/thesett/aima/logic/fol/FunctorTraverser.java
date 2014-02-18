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

import java.util.Iterator;

import com.thesett.aima.search.Operator;

/**
 * FunctorTraverser provides a traversal pattern over functors.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide all reachable child terms of a functor.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface FunctorTraverser extends TermTraverser
{
    /**
     * Visits a functor.
     *
     * @param  functor The functor to visit.
     * @param  reverse <tt>true</tt> if the child operators should be presented in reverse order to what is deemed to be
     *                 a natural, left-to-right ordering.
     *
     * @return An iterator over operators producing the traveresed elements of the functor.
     */
    public Iterator<Operator<Term>> traverse(Functor functor, boolean reverse);
}
