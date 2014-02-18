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
import com.thesett.common.parsing.SourceCodeException;

/**
 * Applies resolution problems to a resolver implementation, in order to verify that it can implements the 'true' and
 * 'fail' predicates.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that true succeeds.
 * <tr><td> Check that fail fails.
 * <tr><td> Check that disjunction of true and fail succeeds.
 * <tr><td> Check that conjunction of true and fail fails.
 * <tr><td> Check that a conjunction of trues succeeds.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TrueAndFailResolverUnitTestBase<S extends Clause, T, Q> extends BasicResolverUnitTestBase<S, T, Q>
{
    /**
     * Creates a true/fail resolution test for the specified resolver, using the specified compiler.
     *
     * @param name   The name of the test.
     * @param engine The resolution engine to test.
     */
    public TrueAndFailResolverUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name, engine);
    }

    /** Check that true succeeds. */
    public void testTrueSucceeds() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- true), [[]]]");
    }

    /** Check that fail fails. */
    public void testFailFails() throws Exception
    {
        resolveAndAssertFailure(new String[] {}, "?- fail");
    }

    /** Check that disjunction of true and fail succeeds. */
    public void testDisjunctionOfTrueAndFailSucceeds() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- true ; fail ; true ; fail), [[]]]");
    }

    /** Check that conjunction of true and fail fails. */
    public void testConjunctionOfTrueAndFailFails() throws SourceCodeException
    {
        resolveAndAssertFailure(new String[] {}, "?- true, fail, true, fail");
    }

    /** Check that a conjunction of trues succeeds. */
    public void testConjunctionOfTruesSucceeds() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- true , true, true), [[]]]");
    }
}
