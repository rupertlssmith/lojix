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
 * Implements the fail atom. This always fails without generating any choice points.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Always fail, consuming the 'fail' goal.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Fail extends BuiltInFunctor
{
    /**
     * Creates a fail built-in to implement the specified functor.
     *
     * @param functor The functor to implement as a built-in.
     */
    public Fail(Functor functor)
    {
        super(functor);
    }
}
