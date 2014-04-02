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

import com.thesett.aima.logic.fol.interpreter.ResolutionEngine;

/**
 * Applies resolution problems to a resolver implementation, in order to verify that it can handle the call/1 and not/1
 * built in predicates. Call evaluates its argument, which may be a variable instantiated to a functor. Not does the
 * same as call but fails when its argument succeeds and the other way around.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that a neck cut prevents back-tracking.
 * <tr><td> Check that a deep cut prevents back-tracking.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class CutResolverUnitTestBase<S extends Clause, T, Q> extends BasicResolverUnitTestBase<S, T, Q>
{
    /**
     * Creates a cut resolution test for the specified resolver, using the specified compiler.
     *
     * @param name   The name of the test.
     * @param engine The resolution engine to test.
     */
    public CutResolverUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name, engine);
    }

    public void testNeckCutPreventsBacktrackingOk() throws Exception
    {
        resolveAndAssertSolutions("[[(f(x) :- !, true), f(y)], (?- f(X)), [[X <-- x]]]");
    }

    public void testDeepCutPreventsBacktrackingOk() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), (f(X) :- g(X), !, true), f(y)], (?- f(X)), [[X <-- x]]]");
    }
}
