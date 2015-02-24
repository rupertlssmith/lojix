/*
 * Copyright The Sett Ltd, 2005 to 2014.
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
 * <tr><td> Check that calling an atom works.
 * <tr><td> Check that calling a functor with a variable produces a binding.
 * <tr><td> Check that a chained call to a functor with a variable produces a binding.
 * <tr><td> Check that not applied to a functor that fails resolution  succeeds.
 * <tr><td> Check that a calling a functor with a variable produces no binding when called through a double not.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class CallAndNotResolverUnitTestBase<S extends Clause, T, Q> extends BasicResolverUnitTestBase<S, T, Q>
{
    /**
     * Creates a call/not resolution test for the specified resolver, using the specified compiler.
     *
     * @param name   The name of the test.
     * @param engine The resolution engine to test.
     */
    public CallAndNotResolverUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name, engine);
    }

    /** Check that not fail succeeds. */
    public void testNotFailSucceeds() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- not(fail)), [[]]]");
    }

    /** Check that not true fails. */
    public void testNotTrueFails() throws Exception
    {
        resolveAndAssertFailure(new String[] {}, "?- not(true)");
    }

    /** Check that calling an atom works. */
    public void testSimpleCallOk() throws Exception
    {
        resolveAndAssertSolutions("[[f], (?- call(f)), [[]]]");
    }

    /** Check that calling an atom works when the atom is dynamically chosen. */
    public void testDynamicCallOk() throws Exception
    {
        resolveAndAssertSolutions("[[g, (f(X) :- call(X))], (?- f(g)), [[]]]");
    }

    /** Check that calling a functor with a variable produces a binding. */
    public void testCallFunctorWithArgumentBindsVariable() throws Exception
    {
        resolveAndAssertSolutions("[[f(x)], (?- call(f(X))), [[X <-- x]]]");
    }

    /** Check that calling an atom works when the atom is dynamically chosen. */
    public void testDynamicCallWithArgumentBindsVariable() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), (f(Y) :- call(Y))], (?- f(g(X))), [[X <-- x]]]");
    }

    /** Check that a chained call to a functor with a variable produces a binding. */
    public void testCallFunctorWithArgumentBindsVariableInChainedCall() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), (f(Y) :- call(g(Y)))], (?- call(f(X))), [[X <-- x]]]");
    }

    /** Check that not applied to a functor that fails resolution succeeds. */
    public void testNotFunctorWithArgumentOkWhenArgumentsDoNotMatch() throws Exception
    {
        resolveAndAssertSolutions("[[f(x)], (?- not(f(y))), [[]]]");
    }

    /** Check that a calling a functor with a variable produces no binding when called through a double not. */
    public void testNotFunctorWithArgumentDoesNotBindVariableInDoubleNegation() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), (f(Y) :- not(g(Y)))], (?- not(f(X))), [[]]]");
    }
}
