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
package com.thesett.aima.logic.fol.compiler;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface PositionalContext
{
    /**
     * Indicates whether the current term is a top-level functor in a clause head or body.
     *
     * @return <tt>true</tt> if the current term is a top-level functor in a clause head or body.
     */
    boolean isTopLevel();

    /**
     * Indicates whether the current term is in a clause head.
     *
     * @return <tt>true</tt> if the current term is in a clause head.
     */
    boolean isInHead();

    /**
     * Indicates whether the current term is the last functor in a clause body.
     *
     * @return <tt>true</tt> if the current term is the last functor in a clause body.
     */
    boolean isLastBodyFunctor();

    /**
     * Gets the positional context of the parent position to this one.
     *
     * @return The positional context of the parent position to this one, or <tt>null</tt> if there is no parent.
     */
    PositionalContext getParentContext();
}
