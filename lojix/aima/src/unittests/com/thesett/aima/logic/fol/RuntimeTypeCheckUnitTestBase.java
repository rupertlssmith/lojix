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
 * Applies resolution problems to a resolver implementation, in order to verify that its runtime type checking
 * predicates function correctly. The runtime type checking predicates are ones like float/1, integer/1, var/1 and so
 * on, that check the meta-logical properties of their argument at run time.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that an integer type checks as an integer.
 * <tr><td> Check that a float type checks as a float.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class RuntimeTypeCheckUnitTestBase<S extends Clause, T, Q> extends BasicResolverUnitTestBase<S, T, Q>
{
    /**
     * Creates a backtracking resolution test for the specified resolver, using the specified compiler.
     *
     * @param name   The name of the test.
     * @param engine The resolution engine to test.
     */
    public RuntimeTypeCheckUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name, engine);
    }

    /** Check that an integer type checks as an integer. */
    public void testIntegerOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- integer(1)), [[]]]");
    }

    /** Check that a float type checks as a float. */
    public void testFloatOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- float(1.0)), [[]]]");
    }
}
