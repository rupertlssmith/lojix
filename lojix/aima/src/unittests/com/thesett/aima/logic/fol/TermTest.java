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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;



import com.thesett.aima.search.QueueBasedSearchMethod;
import com.thesett.aima.search.util.Searches;
import com.thesett.aima.search.util.uninformed.BreadthFirstSearch;
import com.thesett.aima.search.util.uninformed.DepthFirstSearch;
import com.thesett.aima.search.util.uninformed.PostFixSearch;
import com.thesett.common.util.Comparisons;
import com.thesett.common.util.Function;

/**
 * TermTest provides test for {@link Term} and its sub-classes defined in the fol package.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that abstract syntax tree can be walked over correctly using search methods.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TermTest extends TestCase
{
    /** Used for debugging. */
    java.util.logging.Logger log = java.util.logging.Logger.getLogger(TermTest.class.getName());

    public TermTest(String name)
    {
        super(name);
    }

    /** Check that abstract syntax tree can be walked correctly using depth first search methods. */
    public void testSyntaxTreeDepthWalkOk()
    {
        String errorMessage = "";

        // Build a term to walk over. (f(g(x),y))
        Variable x = new Variable(1, null, false);
        Variable y = new Variable(2, null, false);
        Functor g = new Functor(3, new Term[] { x });
        Functor f = new Functor(4, new Term[] { g, y });

        // Build up the expected iteration order.
        List<Term> expectedOrder = new LinkedList<Term>();
        expectedOrder.add(f);
        expectedOrder.add(g);
        expectedOrder.add(x);
        expectedOrder.add(y);

        Iterator<Term> expectedIterator = expectedOrder.iterator();

        // Create a depth first search to walk over the term.
        QueueBasedSearchMethod<Term, Term> search = new DepthFirstSearch<Term, Term>();

        // Check the ordering matches the expected order.
        errorMessage += checkOrderUnderSearch(f, search, expectedIterator);

        // Fail on any errors.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that abstract syntax tree can be walked correctly using breadth first search methods. */
    public void testSyntaxTreeBreadthWalkOk()
    {
        String errorMessage = "";

        // Build a term to walk over. (f(g(x),y))
        Variable x = new Variable(1, null, false);
        Variable y = new Variable(2, null, false);
        Functor g = new Functor(3, new Term[] { x });
        Functor f = new Functor(4, new Term[] { g, y });

        // Build up the expected iteration order.
        List<Term> expectedOrder = new LinkedList<Term>();
        expectedOrder.add(f);
        expectedOrder.add(g);
        expectedOrder.add(y);
        expectedOrder.add(x);

        Iterator<Term> expectedIterator = expectedOrder.iterator();

        // Create a depth first search to walk over the term.
        QueueBasedSearchMethod<Term, Term> search = new BreadthFirstSearch<Term, Term>();

        // Check the ordering matches the expected order.
        errorMessage += checkOrderUnderSearch(f, search, expectedIterator);

        // Fail on any errors.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /** Check that abstract syntax tree can be walked correctly using postfix ordered search methods. */
    public void testSyntaxTreePostfixWalkOk()
    {
        String errorMessage = "";

        // Build a term to walk over. (f(g(x),y))
        Variable x = new Variable(1, null, false);
        Variable y = new Variable(2, null, false);
        Functor g = new Functor(3, new Term[] { x });
        Functor f = new Functor(4, new Term[] { g, y });

        // Build up the expected iteration order.
        List<Term> expectedOrder = new LinkedList<Term>();
        expectedOrder.add(x);
        expectedOrder.add(g);
        expectedOrder.add(y);
        expectedOrder.add(f);

        Iterator<Term> expectedIterator = expectedOrder.iterator();

        // Create a depth first search to walk over the term.
        QueueBasedSearchMethod<Term, Term> search = new PostFixSearch<Term, Term>();

        // Check the ordering matches the expected order.
        errorMessage += checkOrderUnderSearch(f, search, expectedIterator);

        // Fail on any errors.
        assertTrue(errorMessage, "".equals(errorMessage));
    }

    /**
     * Checks that a search method, starting from a particular start state, produces an expected ordering.
     *
     * @param  startState       The startState to being the search from.
     * @param  search           The search method to walk over it with.
     * @param  expectedIterator The expected order of the search.
     *
     * @return The empty string if there are no errors, or error messages if there are.
     */
    private String checkOrderUnderSearch(Term startState, QueueBasedSearchMethod<Term, Term> search,
        Iterator<Term> expectedIterator)
    {
        search.reset();
        search.addStartState(startState);

        // Check that it is correctly navigated by the search ordering.
        Iterator<Term> treeWalker = Searches.allSolutions(search);

        // Compare the iterators for the search and the expected ordering.
        return Comparisons.compareIterators(treeWalker, expectedIterator, new Function<Term, Term>()
            {
                public Term apply(Term term)
                {
                    return term;
                }
            });
    }
}
