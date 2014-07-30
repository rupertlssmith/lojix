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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import com.thesett.aima.logic.fol.interpreter.ResolutionEngine;
import com.thesett.common.parsing.SourceCodeException;

/**
 * Applies resolution problems to a resolver implementation, in order to verify that it can backtrack correctly. The
 * resolution problems that this test class uses, are typically non-deterministic or require that variable bindings made
 * in pursuit of one possible resolution path need to be undone in order to follow other resolution paths.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Checks that multiple simple facts queried to bind a variable, produce all possible bindings.
 * <tr><td> Check that instantiating the same clause several times with the same variable does not produce conflicting
 *          bindings for the variable.
 * <tr><td> Check that instantiating the same clause several times with different variable allows the variables to be
 *          bound independently.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BacktrackingResolverUnitTestBase<S extends Clause, T, Q> extends BasicResolverUnitTestBase<S, T, Q>
{
    /**
     * Creates a backtracking resolution test for the specified resolver, using the specified compiler.
     *
     * @param name   The name of the test.
     * @param engine The resolution engine to test.
     */
    public BacktrackingResolverUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name, engine);
    }

    /** Checks that multiple simple facts queried to bind a variable, produce all possible bindings. */
    public void testMultipleFactsProduceMultipleSolutions() throws Exception
    {
        resolveAndAssertSolutions("[[f(x), f(y)], (?- f(X)), [[X <-- x], [X <-- y]]]");
    }

    /** Checks that multiple simple facts queried to bind a variable, produce all possible bindings. */
    public void testThreeFactsProduceThreeSolutions() throws Exception
    {
        resolveAndAssertSolutions("[[f(x), f(y), f(z)], (?- f(X)), [[X <-- x], [X <-- y], [X <-- z]]]");
    }

    /**
     * Check that instantiating the same clause several times with the same variable does not produce conflicting
     * bindings for the variable.
     */
    public void testInstantiatingClausesSeveralTimesWithSameVariableDoesNotConflictVariableBindings() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), g(y), (f(X) :- g(X))], (?- f(X), f(X)), [[X <-- x], [X <-- y]]]");
    }

    /**
     * Check that instantiating the same clause several times with different variable allows the variables to be bound
     * independently.
     */
    public void testInstantiatingClausesSeveralTimesWithDifferentVariableAllowsIndependentBindings() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), g(y), (f(X) :- g(X))], (?- f(X), f(Y)), " +
            "[[X <-- x, Y <-- x], [X <-- x, Y <-- y], [X <-- y, Y <-- x], [X <-- y, Y <-- y]]]");
    }

    /**
     * Helper method for resolutions that produce a known number of solutions, this method performs all possible
     * resolutions and asserts that the expected number are produced.
     *
     * @param  domain The domain clauses to query over.
     * @param  query  The query to run.
     * @param  num    The number of solutions expected.
     *
     * @throws SourceCodeException If the test code fails to parse, compile or link.
     */
    protected void resolveAndAssertNumSolutions(String[] domain, String query, int num) throws SourceCodeException
    {
        // Clear the resolvers domain on every test.
        resolver.reset();

        // Compile the domain clauses and insert them into the resolver.
        for (String predicate : domain)
        {
            /*T clause = */ compileDomainClause(predicate);
            //resolver.addToDomain(clause);
        }

        // Compile the query and insert it into the resolver.
        /*Q q = */ compileQuery(query);
        //resolver.setQuery(q);

        // Perform all possible resolutions.
        //Collection<P> solutions = Searches.bagOf(resolver);
        Collection<Set<Variable>> solutions = new LinkedList<Set<Variable>>();

        for (Set<Variable> solution : resolver)
        {
            solutions.add(solution);
        }

        // Check that the resolution succeeded.
        assertNotNull("Resolution failed.", solutions);

        // Check that no expected number of bindings were created.
        assertEquals("Resolution did not result in the expected number of solutions.", num, solutions.size());
    }
}
