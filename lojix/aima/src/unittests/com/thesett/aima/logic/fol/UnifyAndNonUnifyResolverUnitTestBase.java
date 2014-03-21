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
 * UnifyAndNonUnifyResolverUnitTestBase overrides the unification test methods in {@link UnifierUnitTestBase} to use the
 * resolution solution test method {@link BasicResolverUnitTestBase#resolveAndAssertSolutions(String)}, by rewriting the
 * unification success or failure tests to use the '=' and '\=' operators as resolution queries. This allows all the
 * existing unification tests to be run through a resolver and re-used.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Run all the unification tests through a resolver.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class UnifyAndNonUnifyResolverUnitTestBase<S extends Clause, T, Q> extends UnifierUnitTestBase
{
    /**
     * Holds a basic resolver that supplies the resolve and check bindings test method over a resolver, parser,
     * compiler.
     */
    BasicResolverUnitTestBase<S, T, Q> basicResolver;

    public UnifyAndNonUnifyResolverUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name);

        this.compiler = engine;
        this.parser = engine;
        this.interner = engine;

        // Create the basic resolver to supply the resolve and check bindings test. */
        basicResolver = new BasicResolverUnitTestBase<S, T, Q>(name, engine);
    }

    /**
     * Helper method for simple unifications that produce a known number of variable bindings, this method performs the
     * unification and asserts that it succeeds and produces the expected number of bindings.
     *
     * @param statement The first term to unify.
     * @param query     The second term to unify.
     * @param num       The number of bindings expected.
     */
    public void unifyAndAssertNumBindings(String statement, String query, int num) throws Exception
    {
        basicResolver.resolveAndAssertSolutions("[[], (?- " + query + " = " + statement + "), [[]]]");
    }

    /**
     * Helper method to check that a unification fails. This method performs the unification and asserts that it fails.
     *
     * @param statement The first term to unify.
     * @param query     The second term to unify.
     */
    public void unifyAndAssertFailure(String statement, String query) throws Exception
    {
        basicResolver.resolveAndAssertSolutions("[[], (?- " + query + " \\= " + statement + "), [[]]]");
    }
}
