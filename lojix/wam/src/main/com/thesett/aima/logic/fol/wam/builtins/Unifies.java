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
import com.thesett.aima.logic.fol.wam.compiler.WAMInstruction;
import com.thesett.common.util.SizeableLinkedList;

/**
 * Unifies is the ISO Prolog built in operator '='/2. It performs a standard unification (no occurs check) on its left
 * and right arguments, possibly binding variables as a result of the unification, and succeeds iff the unification
 * succeeds.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Unify the left and right arguments of the unify operator.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Unifies extends BaseBuiltIn
{
    /**
     * Creates a cut built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built-in.
     */
    public Unifies(Functor functor)
    {
        super(functor);
    }

    /** {@inheritDoc} */
    public SizeableLinkedList<WAMInstruction> compileBody(Functor expression, boolean isFirstBody)
    {
        return new SizeableLinkedList<WAMInstruction>();
    }

    /** {@inheritDoc} */
    public SizeableLinkedList<WAMInstruction> compileBodyCall(Functor expression, boolean lastBody, boolean chainRule,
        int permVarsRemaining)
    {
        return new SizeableLinkedList<WAMInstruction>();
    }
}
