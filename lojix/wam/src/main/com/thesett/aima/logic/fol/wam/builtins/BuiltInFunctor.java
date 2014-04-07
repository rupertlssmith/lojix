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
package com.thesett.aima.logic.fol.wam.builtins;

import com.thesett.aima.logic.fol.Functor;

/**
 * BuiltInFunctor provides a mapping from a functor onto its built-in implementation, in the case of built-in predicates
 * in Prolog.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide a mapping from a functor to its built-in implementation. <td> {@link BuiltIn}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BuiltInFunctor extends Functor implements BuiltIn
{
    /** Holds the functor that this is a built in for. */
    protected Functor functor;

    /** Flag to indicate if this functor is top-level within a clause body. */
    protected final boolean isTopLevel;

    /**
     * Creates a built-in for the specified functor.
     *
     * @param functor The functor to create a built-in for.
     */
    public BuiltInFunctor(Functor functor)
    {
        super(functor.getName(), functor.getArguments());
        this.functor = functor;
        this.isTopLevel = false;
    }

    /**
     * Creates a built-in for the specified functor.
     *
     * @param functor    The functor to create a built-in for.
     * @param isTopLevel <tt>true</tt> iff this is a top-level functor within a clause.
     */
    public BuiltInFunctor(Functor functor, boolean isTopLevel)
    {
        super(functor.getName(), functor.getArguments());
        this.functor = functor;
        this.isTopLevel = isTopLevel;
    }

    /**
     * Provides the functor that this is a built-in for.
     *
     * @return The functor that this is a built-in for.
     */
    public Functor getFunctor()
    {
        return functor;
    }
}
