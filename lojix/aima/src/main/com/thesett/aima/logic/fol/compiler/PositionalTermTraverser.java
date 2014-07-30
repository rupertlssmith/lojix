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
package com.thesett.aima.logic.fol.compiler;

import com.thesett.aima.logic.fol.ClauseTraverser;
import com.thesett.aima.logic.fol.FunctorTraverser;
import com.thesett.aima.logic.fol.TermVisitor;

/**
 * A PositionalTermTraverser provides contextual positional information during a traversal of a predicate, clause or
 * functor. It provides a set of flags and properties as a term tree is walked over, to indicate some positional
 * properties of the current term within the tree.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Report whether the current term is a functor at the top-level within a clause.
 * <tr><td> Report whether the current term is within a clause head or body.
 * <tr><td> Report whether a top-level functor in a clause body is the last one in the body.
 * <tr><td> Accept a term visitor to call on entering/leaving the context of any term.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface PositionalTermTraverser extends ClauseTraverser, FunctorTraverser, PositionalContext
{
    /**
     * Indicates that a call is being made to a term visitor because its context is being established.
     *
     * @return <tt>true</tt> if a call is being made to a term visitor because its context is being established.
     */
    boolean isEnteringContext();

    /**
     * Indicates that a call is being made to a term visitor because its context is being left.
     *
     * @return <tt>true</tt> if a call is being made to a term visitor because its context is being left.
     */
    boolean isLeavingContext();

    /**
     * Indicates that a call is being made to a term visitor because its context is being changed.
     *
     * @return <tt>true</tt> if a call is being made to a term visitor because its context is being changed.
     */
    boolean isContextChange();

    /**
     * Allows a visitor to notify on context changes to be set.
     *
     * @param contextChangeVisitor The visitor to notify on context changes.
     */
    void setContextChangeVisitor(TermVisitor contextChangeVisitor);
}
