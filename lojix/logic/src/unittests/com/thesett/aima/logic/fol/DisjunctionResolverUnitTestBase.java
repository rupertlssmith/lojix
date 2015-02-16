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
 * Applies resolution problems to a resolver implementation, in order to verify that it can handle disjunctions
 * correctly.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that resolution on the first of two possible matching functors succeeds.
 * <tr><td> Check that resolution on the second of two possible matching functors succeeds.
 * <tr><td> Check that resolution against two possible matching functors fails when none are matched.
 * <tr><td> Checks that a pair of disjunct functors explores both solutions.
 * <tr><td> Checks that a sequence of disjunct functors explores all solutions.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DisjunctionResolverUnitTestBase<S extends Clause, T, Q> extends BasicResolverUnitTestBase<S, T, Q>
{
    /**
     * Creates a arithmetic resolution test for the specified resolver, using the specified compiler.
     *
     * @param name   The name of the test.
     * @param engine The resolution engine to test.
     */
    public DisjunctionResolverUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name, engine);
    }

    /** Check that resolution on the first of two possible matching functors succeeds. */
    public void testResolvesOnFirstMatchingPossibleFunctor() throws Exception
    {
        resolveAndAssertSolutions("[[f(x), f(y)], (?- f(x)), [[]]]");
    }

    /** Check that resolution on the second of two possible matching functors succeeds. */
    public void testResolvesOnSecondMatchingPossibleFunctor() throws Exception
    {
        resolveAndAssertSolutions("[[f(x), f(y)], (?- f(y)), [[]]]");
    }

    /** Check that resolution on multiple matching clauses, takes a binding from the argument of the matched clause. */
    public void testArgBindsOnAllMatchingClauses() throws Exception
    {
        resolveAndAssertSolutions("[[f(x), f(y), f(z)], (?- f(X)), [[X <-- x], [X <-- y], [X <-- z]]]");
    }

    /** Check that resolution on multiple matching clauses, takes a binding from the first bodies. */
    public void testFirstBodyBindsOnAllMatchingClauses() throws Exception
    {
        resolveAndAssertSolutions("[[(f(X) :- X = x), (f(X) :- X = y), (f(X) :- X = z)], (?- f(X)), " +
            "[[X <-- x], [X <-- y], [X <-- z]]]");
    }

    /** Check that resolution on multiple matching clauses, takes a binding from the first item in longer bodies. */
    public void testFirstOfLongerBodyBindsOnAllMatchingClauses() throws Exception
    {
        resolveAndAssertSolutions("[[(f(X) :- X = x, true), (f(X) :- X = y, true), (f(X) :- X = z, true)], " +
            "(?- f(X)), [[X <-- x], [X <-- y], [X <-- z]]]");
    }

    /** Check that resolution against two possible matching functors fails when none are matched. */
    public void testFailsOnNoMatchingOutOfSeveralPossibleFunctors() throws Exception
    {
        resolveAndAssertFailure(new String[] { "f(x)", "f(y)" }, "?- f(z)");
    }

    /** Checks that a pair of disjunct functors explores both solutions. */
    public void testVariableTakesBindingsFromTwoDisjunctionPaths() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X = a; X = b), [[X <-- a], [X <-- b]]]");
    }

    /** Checks that a sequence of disjunct functors explores all solutions. */
    public void testVariableTakesBindingsFromManyDisjunctionPaths() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X = a; X = b; X = c; X = d; X = e), " +
            "[[X <-- a], [X <-- b], [X <-- c], [X <-- d], [X <-- e]]]");
    }

    /** Checks that a conjunction and disjunction bracketed so the conjunction fails, will fail. */
    public void testJunctionBracketingFalse() throws Exception
    {
        resolveAndAssertFailure(new String[] {}, "?- X = a, (X = b; X = c)");
    }

    /** Checks that a conjunction and disjunction bracketed so the disjunction succeeds, find a solution. */
    public void testJunctionBracketingAllowsDisjunction() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- (X = a, X = b); X = c), [[X <-- c]]]");
    }
}
