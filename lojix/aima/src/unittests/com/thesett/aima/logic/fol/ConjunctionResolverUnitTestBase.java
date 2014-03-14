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
 * Applies resolution problems to a resolver implementation, in order to verify that it can handle conjunctions
 * correctly.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Checks that a pair of disjunct functors explores both solutions.
 * <tr><td> Checks that a sequence of disjunct functors explores all solutions.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ConjunctionResolverUnitTestBase<S extends Clause, T, Q> extends BasicResolverUnitTestBase<S, T, Q>
{
    /**
     * Creates a arithmetic resolution test for the specified resolver, using the specified compiler.
     *
     * @param name   The name of the test.
     * @param engine The resolution engine to test.
     */
    public ConjunctionResolverUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name, engine);
    }

    /** Check that a conjuction resolves when both of its paths do. */
    public void testConjunctionResolves() throws Exception
    {
        resolveAndAssertSolutions("[[g, h, (f :- g, h)], (?- f), [[]]]");
    }

    /** Check that a conjunction fails to resolve when its first path fails. */
    public void testConjunctionFailsToResolveWhenFirstPathFails() throws Exception
    {
        resolveAndAssertFailure(new String[] { "h", "f :- g, h" }, "?- f");
    }

    /** Check that a conjunction fails to resolve when its second path fails. */
    public void testConjunctionFailsToResolveWhenSecondPathFails() throws Exception
    {
        resolveAndAssertFailure(new String[] { "g", "f :- g, h" }, "?- f");
    }

    /** Check that a conjunction resolves when its first path revisits items resolved on the second. */
    public void testConjunctionResolvesWhenFirstPathRevisitsSecond() throws Exception
    {
        resolveAndAssertSolutions("[[g, (h :- g), (f :- h, g)], (?- f), [[]]]");
    }

    /** Check that a conjunction resolves when its second path revisits items resolved on the first. */
    public void testConjunctionResolvesWhenSecondPathRevisitsFirst() throws Exception
    {
        resolveAndAssertSolutions("[[g, (h :- g), (f :- g, h)], (?- f), [[]]]");
    }

    /** Check that a variable binding from a query is propagates accross a conjunction. */
    public void testVariableBindingFromQueryPropagatesAccrossConjunction() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), h(x), (f(X) :- g(X), h(X))], (?- f(x)), [[]]]");
    }

    /** Check that a non-permanent variable resolved ok, but produces no output binding. */
    public void testNonPermanentVariableResolvedOkNoBinding() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), h(x), (f :- g(X), h(Y))], (?- f), [[]]]");
    }

    /** Check that a variable binding from a resolution path propagates accross a conjunction. */
    public void testVariableBindingFromFirstPathPropagatesAccrossConjunction() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), h(x), (f(X) :- g(X), h(X))], (?- f(Y)), [[Y <-- x]]]");
    }

    /**
     * Check that a variable binding from a resolution path propagates accross a conjunction and can fail the resolution
     * on non-unifiation on a later path.
     */
    public void testVariableBindingFromFirstPathPropagatesAccrossConjunctionAndFailsOnNonUnification() throws Exception
    {
        resolveAndAssertFailure(new String[] { "g(x)", "h(y)", "f(X) :- g(X), h(X)" }, "?- f(Y)");
    }

    /**
     * Check that a two arg functor call where both args are bound together by the calling query propagates the binding.
     */
    public void testConjoinedVariablesPropagateAccrossConjunction() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), h(x), (f(X, Y) :- g(X), h(Y))], (?- f(X, X)), [[X <-- x]]]");
    }

    /**
     * Check that a two arg functor call where both args are bound together by the calling query propagates the binding
     * to produce a failure when the variables are unified differently.
     */
    public void testConjoinedVariablesPropagateAccrossConjunctionFailingOnNonUnification() throws Exception
    {
        resolveAndAssertFailure(new String[] { "g(x)", "h(y)", "f(X, Y) :- g(X), h(Y)" }, "?- f(X, X)");
    }

    /** Check that variables in clauses hold bindings local to a clause instantiation only. */
    public void testVariableInClauseMayTakeMultipleSimultaneousBindings() throws Exception
    {
        resolveAndAssertSolutions("[[f(X)], (?- f(x), f(y)), [[]]]");
    }

    /** Check that succesive conjunctive terms all bind their variables. */
    public void testSuccesiveConjunctiveTermsOk() throws Exception
    {
        resolveAndAssertSolutions("[[f(a), g(b), h(c)], (?- f(X), g(Y), h(Z)), [[X <-- a, Y <-- b, Z <-- c]]]");
    }

    /**
     * Check that variable bindings that appear in multiple calls are handled correctly (permanent variables, and
     * environment trimming).
     */
    public void testManyVariablesAcrossCallsOk() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), (f(X, Y, Z) :- g(X), g(Y), g(Z))], (?- f(X, X, X)), [[X <-- x]]]");
    }
}
