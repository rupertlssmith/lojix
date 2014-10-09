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
 * Applies resolution problems to a resolver implementation, in order to verify that it can handle arithmetic correctly.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check simple addition of integers.
 * <tr><td> Check simple subtraction of integers.
 * <tr><td> Check simple multiplication of integers.
 * <tr><td> Check simple division of integers.
 * <tr><td> Check simple exponentiation of integers.
 * <tr><td> Check simple addition of reals.
 * <tr><td> Check simple subtraction of reals.
 * <tr><td> Check simple multiplication of reals.
 * <tr><td> Check simple division of reals.
 * <tr><td> Check simple exponentiation of reals.
 * <tr><td> Check precedence of multiplication over addition.
 * <tr><td> Check that instantiated variables can be used in arithmetic.
 * <tr><td> Check that less than evaluates to true correctly.
 * <tr><td> Check that less than fails correctly.
 * <tr><td> Check that less than or equals evaluates to true correctly.
 * <tr><td> Check that less than or equals fails correctly.
 * <tr><td> Check that greater than evaluates to true correctly.
 * <tr><td> Check that greater than fails correctly.
 * <tr><td> Check that greater than or equals evaluates to true correctly.
 * <tr><td> Check that greater than or equals fails correctly.
 * <tr><td> Check that numerically instantiated variables can be compared.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ArithmeticResolverUnitTestBase<S extends Clause, T /*extends Clause<? extends Functor>*/, Q>
    extends BasicResolverUnitTestBase<S, T, Q>
{
    /**
     * Creates a arithmetic resolution test for the specified resolver, using the specified compiler.
     *
     * @param name   The name of the test.
     * @param engine The resolution engine to test.
     */
    public ArithmeticResolverUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name, engine);
    }

    /** Check simple addition of integers. */
    public void testAddIntegerOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X is 2 + 3), [[X <-- 5]]]");
    }

    /** Check simple subtraction of integers. */
    public void testSubtractIntegerOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X is 20 - 8), [[X <-- 12]]]");
    }

    /** Check simple multiplication of integers. */
    public void testMultiplyIntegerOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X is 20 * 5), [[X <-- 100]]]");
    }

    /** Check simple division of integers. */
    public void testDivideIntegerOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X is 20 / 5), [[X <-- 4]]]");
    }

    /** Check simple exponentiation of integers. */
    public void testExponentialIntegerOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X is 2 ** 4), [[X <-- 16]]]");
    }

    /** Check simple addition of reals. */
    public void testAddRealOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X is 2.0 + 4.0), [[X <-- 6.0]]]");
    }

    /** Check simple subtraction of reals. */
    public void testSubtractRealOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X is 8.1 - 0.6), [[X <-- 7.5]]]");
    }

    /** Check simple multiplication of reals. */
    public void testMultiplyRealOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X is 1.5 * 2.5), [[X <-- 3.75]]]");
    }

    /** Check simple division of reals. */
    public void testDivideRealOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X is 4.5 / 1.5), [[X <-- 3.0]]]");
    }

    /** Check simple exponentiation of reals. */
    public void testExponentialRealOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X is 4.5 ** 2.0), [[X <-- 20.25]]]");
    }

    /** Check precedence of multiplication over addition. */
    public void testPrecedenceMulOverAdd() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- X is 2 * 3 + 1), [[X <-- 7]]]");
        resolveAndAssertSolutions("[[], (?- X is 1 + 2 * 3), [[X <-- 7]]]");

        resolveAndAssertSolutions("[[], (?- X is 2 * (3 + 1)), [[X <-- 8]]]");
        resolveAndAssertSolutions("[[], (?- X is (1 + 2) * 3), [[X <-- 9]]]");
    }

    /** Check that instantiated variables can be used in arithmetic. */
    public void testInstantiatedVariablesInArithmeticOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- Y = 4, Z = 5, X is Y + Z), [[X <-- 9, Y <-- 4, Z <-- 5]]]");
    }

    /** Check that less than evaluates to true correctly. */
    public void testLessThanOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- 2 < 3), [[]]]");
    }

    /** Check that less than fails correctly. */
    public void testLessThanFails() throws Exception
    {
        resolveAndAssertFailure(new String[] {}, "?- 3 < 2");
    }

    /** Check that less than or equals evaluates to true correctly. */
    public void testLessThanOrEqualsOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- 3 =< 3), [[]]]");
    }

    /** Check that less than or equals fails correctly. */
    public void testLessThanOrEqualsFails() throws Exception
    {
        resolveAndAssertFailure(new String[] {}, "?- 3 =< 2");
    }

    /** Check that greater than evaluates to true correctly. */
    public void testGreaterThanOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- 20 > 10), [[]]]");
    }

    /** Check that greater than fails correctly. */
    public void testGreaterThanFails() throws Exception
    {
        resolveAndAssertFailure(new String[] {}, "?- 10 > 20");
    }

    /** Check that greater than or equals evaluates to true correctly. */
    public void testGreaterThanOrEqualsOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- 200 >= 200), [[]]]");
    }

    /** Check that greater than or equals fails correctly. */
    public void testGreaterThanOrEqualsFails() throws Exception
    {
        resolveAndAssertFailure(new String[] {}, "?- 5 >= 50");
    }

    /** Check that numerically instantiated variables can be compared. */
    public void testComparisonOfInstantiatedVariablesOk() throws Exception
    {
        resolveAndAssertSolutions("[[], (?- Y = 4, Z = 5, Y < Z), [[Y <-- 4, Z <-- 5]]]");
        resolveAndAssertSolutions("[[], (?- Y = 4, Z = 5, Y =< Z), [[Y <-- 4, Z <-- 5]]]");
        resolveAndAssertSolutions("[[], (?- Y = 4, Z = 5, Z > Y), [[Y <-- 4, Z <-- 5]]]");
        resolveAndAssertSolutions("[[], (?- Y = 4, Z = 5, Z >= Y), [[Y <-- 4, Z <-- 5]]]");
    }

    /** Check that arithmetic operators (-) can be used to build structures too. */
    public void testNonArithOperatorNotMistaken() throws Exception{
        resolveAndAssertSolutions("[[f(x-y), (g(X-Y) :- f(X-Y))], (?- g(X-Y)), [[X <-- x, Y <-- y]]]");
    }
}
