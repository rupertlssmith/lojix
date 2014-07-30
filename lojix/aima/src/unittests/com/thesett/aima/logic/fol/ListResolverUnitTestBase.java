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
 * Applies resolution problems to a resolver implementation, in order to verify that it can handle lists correctly.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that the special list symbol 'nil' is recognized.
 * <tr><td> Check that non empty lists are recognized.
 * <tr><td> Check that the special list symbol 'cons' is recognized.
 * <tr><td> Check that a list recursion pattern termintes on the empty list.
 * <tr><td> Check that a list recursion pattern termintes on a non empty list.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ListResolverUnitTestBase<S extends Clause, T, Q> extends BasicResolverUnitTestBase<S, T, Q>
{
    /**
     * Creates a backtracking resolution test for the specified resolver, using the specified compiler.
     *
     * @param name   The name of the test.
     * @param engine The resolution engine to test.
     */
    public ListResolverUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name, engine);
    }

    /** Check that the special list symbol 'nil' is recognized. */
    public void testNilRecognized() throws Exception
    {
        resolveAndAssertSolutions("[[f([])], (?- f(X)), [[X <-- []]]]");
    }

    /** Check that non empty lists are recognized. */
    public void testNonEmptyListRecognized() throws Exception
    {
        resolveAndAssertSolutions("[[f([a])], (?- f(X)), [[X <-- [a]]]]");
    }

    /** Check that the special list symbol 'cons' is recognized. */
    public void testConsRecognized() throws Exception
    {
        resolveAndAssertSolutions("[[f([a|[b,c]])], (?- f(X)), [[X <-- [a, b, c]]]]");
    }

    /** Check that a list recursion pattern termintes on the empty list. */
    public void testListIterationTerminatesOnEmpty() throws Exception
    {
        resolveAndAssertSolutions("[[f([]), (f([_|XS]) :- f(XS))], (?- f([])), [[]]]");
    }

    /** Check that a list recursion pattern terminates on a non empty list. */
    public void testListIterationTerminatesOnList() throws Exception
    {
        resolveAndAssertSolutions("[[f([]), (f([_|XS]) :- f(XS))], (?- f([a, b, c])), [[]]]");
    }

    /** Check that a list recursion pattern terminates when the termination case is a one element list. */
    public void testListIterationTerminatesOnNonEmptyFinalCase() throws Exception
    {
        resolveAndAssertSolutions("[[f([X]), (f([_|XS]) :- f(XS))], (?- f([a, b, c])), [[]]]");
    }

    /** Checks that a list recursion backtracks to find all solutions. */
    public void testListIterationBacktracks() throws Exception
    {
        resolveAndAssertSolutions(
            "[[m(Y, [Y|_]), (m(X, [_|XS]) :- m(X, XS))], (?- m(X, [a, b, c])), [[X <-- a], [X <-- b], [X <-- c]]]");
    }
}
