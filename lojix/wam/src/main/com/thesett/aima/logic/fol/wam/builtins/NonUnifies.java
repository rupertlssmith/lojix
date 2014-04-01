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
 * NonUnifies is the ISO Prolog built in operator '\='/2. It performs a standard unification (no occurrs check) on its
 * left and right arguments, possibly binding variables as a result of the unification, and fails iff the unification
 * succeeds. As failure will cause this proof step to be undone, any variable bindings resulting from the unification
 * will immediately be discarded.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check if unification of the left and right arguments of the non-unify operator fails.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class NonUnifies extends BaseBuiltIn
{
    /**
     * Creates a cut built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built-in.
     */
    public NonUnifies(Functor functor)
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