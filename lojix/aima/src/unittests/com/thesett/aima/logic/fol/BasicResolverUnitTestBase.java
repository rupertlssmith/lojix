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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.NDC;

import com.thesett.aima.logic.fol.interpreter.ResolutionEngine;
import com.thesett.aima.logic.fol.isoprologparser.Token;
import com.thesett.aima.logic.fol.isoprologparser.TokenSource;
import com.thesett.common.parsing.SourceCodeException;

/**
 * Applies basic resolution problems to a resolver implementation, in order to verify that it functions correctly. All
 * of the resolution problems that this test class uses, are fully deterministic (that is, they either have only one
 * solution, or none) and do not require any backtracking (that is, unbinding of variables bound by following false
 * resolution paths does not need to be implemented). The queries are also all fairly simple, consisting of a single
 * term with no conjunction, disjunction or use of built-in operators.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that resolution of an atom as argument to a functor succeeds.
 * <tr><td> Check that resolution of an non matching atom as argument to a functor fails.
 * <tr><td> Check that resolution of a variable as argument to a functor succeeds with a binding.
 * <tr><td> Check that resolution of an atom as argument to a functor succeeds through a chained call.
 * <tr><td> Check that resolution of an non matching atom as argument to a functor fails through a chained call.
 * <tr><td> Check that resolution of a variable as argument to a functor succeeds with a binding through a chained call.
 * <tr><td> Check that resolution of an atom as argument to a functor succeeds through a chained call.
 * <tr><td> Check that resolution of an non matching atom as argument to a functor fails through a chained call.
 * <tr><td> Check that resolution of a variable as argument to a functor succeeds with a binding through a chained call.
 * <tr><td> Check that resolution of a functor with no match in the knowledge base fails.
 * <tr><td> Check that resolution of a functor with no match in the knowledge base fails through a chained call.
 * <tr><td> Check that a conjunction resolves when both of its paths do.
 * <tr><td> Check that a conjunction fails to resolve when its first path fails.
 * <tr><td> Check that a conjunction fails to resolve when its second path fails.
 * <tr><td> Check that a conjunction resolves when its first path revisits items resolved on the second.
 * <tr><td> Check that a conjunction resolves when its second path revisits items resolved on the first.
 * <tr><td> Check that a variable binding from a query is propagates across a conjunction.
 * <tr><td> Check that a variable binding from a resolution path propagates across a conjunction.
 * <tr><td> Check that a variable binding from a resolution path propagates across a conjunction and can fail the
 *          resolution on non-unification on a later path.
 * <tr><td> Check that a two arg functor call where both args are bound together by the calling query propagates the
 *          binding.
 * <tr><td> Check that a two arg functor call where both args are bound together by the calling query propagates the
 *          binding to produce a failure when the variables are unified differently.
 * <tr><td> Check that resolution of a functor as argument to a functor succeeds.
 * <tr><td> Check that resolution of a non matching functor as argument to a functor fails.
 * <tr><td> Check that resolution of a functor as argument to a functor succeeds through a chained call.
 * <tr><td> Check that resolution of a non matching functor as argument to a functor fails through a chained call.
 * <tr><td> Check that anonymous variable bindings are not reported.
 * <tr><td> Check that anonymous but identified variable bindings are not reported.
 * <tr><td> Check that anonymous but identified variable bindings are propagated across calls.
 * <tr><td> Check that anonymous variable bindings are not propagated across calls.
 * <tr><td> Check that variables in clauses hold bindings local to a clause instantiation only.
 * <tr><td> Check that multiple variables are bound in a simple call.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BasicResolverUnitTestBase<S extends Clause, T, Q> extends TestCase
{
    /** Used for debugging purposes. */
    public static final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(BasicResolverUnitTestBase.class.getName());

    /** Holds the parser to parse the test examples with. */
    protected Parser<S, Token> parser;

    /** Holds the compiler to compile test terms for unification with. */
    protected LogicCompiler<S, T, Q> compiler;

    /** Holds the observer for compiler outputs. */
    //protected CompilerOutputObserver compilerObserver;

    /** Holds the unifier to test. */
    protected Resolver<T, Q> resolver;

    /** Holds the interner for functor and variable names. */
    VariableAndFunctorInterner interner;

    /** Holds the resolution engine to test. */
    protected ResolutionEngine<S, T, Q> engine;

    /** Flag indicating that extra solutions should be checked for and failed against. <tt>true</tt> by default. */
    private boolean checkExtraSolutions = true;

    /**
     * Creates a simple resolution test for the specified resolver, using the specified compiler.
     *
     * @param name   The name of the test.
     * @param engine The resolution engine to test.
     */
    public BasicResolverUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name);
        this.resolver = engine;
        this.compiler = engine;
        this.parser = engine;
        this.interner = engine;
        this.engine = engine;

        // Set up the compiler output observer.
        //compilerObserver = new CompilerOutputObserver();
        //compiler.setCompilerObserver(compilerObserver);

        // Set up the custom binding operator on the parser.
        parser.setOperator("<--", 20, OpSymbol.Associativity.XFX);
    }

    /** Check that resolution of an atom as argument to a functor succeeds. */
    public void testAtomAsArgumentToFunctorResolves() throws Exception
    {
        resolveAndAssertSolutions("[[f(x)], (?- f(x)), [[]]]");
    }

    /** Check that resolution of an non matching atom as argument to a functor fails. */
    public void testNonMatchingAtomAsArgumentToFunctorFailsResolution() throws Exception
    {
        resolveAndAssertFailure(new String[] { "f(x)" }, "?- f(y)");
    }

    /** Check that resolution of a variable as argument to a functor succeeds with a binding. */
    public void testVariableAsArgumentToFunctorResolvesToCorrectBinding() throws Exception
    {
        resolveAndAssertSolutions("[[f(x)], (?- f(X)), [[X <-- x]]]");
    }

    /** Check that resolution of an atom as argument to a functor succeeds through a chained call. */
    public void testAtomAsArgumentToFunctorResolvesInChainedCall() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), (f(X) :- g(X))], (?- f(x)), [[]]]");
    }

    /** Check that resolution of an non matching atom as argument to a functor fails through a chained call. */
    public void testNonMatchingAtomAsArgumentToFunctorFailsResolutionInChainedCall() throws Exception
    {
        resolveAndAssertFailure(new String[] { "g(x)", "f(X) :- g(X)" }, "?- f(y)");
    }

    /** Check that resolution of a variable as argument to a functor succeeds with a binding through a chained call. */
    public void testVariableAsArgumentToFunctorResolvesToCorrectBindingInChainedCall() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), (f(X) :- g(X))], (?- f(Y)), [[Y <-- x]]]");
    }

    /** Check that resolution of an atom as argument to a functor succeeds through a chained call. */
    public void testAtomAsArgumentToFunctorResolvesInTwoChainedCalls() throws Exception
    {
        resolveAndAssertSolutions("[[h(x), (g(X) :- h(X)), (f(Y) :- g(Y))], (?- f(x)), [[]]]");
    }

    /** Check that resolution of an non matching atom as argument to a functor fails through a chained call. */
    public void testNonMatchingAtomAsArgumentToFunctorFailsResolutionInTwoChainedCalls() throws Exception
    {
        resolveAndAssertFailure(new String[] { "h(x)", "g(X) :- h(X)", "f(Y) :- g(Y)" }, "?- f(y)");
    }

    /** Check that resolution of a variable as argument to a functor succeeds with a binding through a chained call. */
    public void testVariableAsArgumentToFunctorResolvesToCorrectBindingInTwoChainedCalls() throws Exception
    {
        resolveAndAssertSolutions("[[h(x), (g(X) :- h(X)), (f(Y) :- g(Y))], (?- f(Z)), [[Z <-- x]]]");
    }

    /** Check that resolution of a functor with no match in the knowledge base fails. */
    public void testResolutionFailsWhenNoMatchingFunctor() throws Exception
    {
        resolveAndAssertFailure(new String[] { "g(x)" }, "?- f(x)");
    }

    /** Check that resolution of a functor with no match in the knowledge base fails through a chained call. */
    public void testResolutionFailsWhenNoMatchingFunctorInChainedCall() throws Exception
    {
        resolveAndAssertFailure(new String[] { "f(X) :- g(X)" }, "?- f(x)");
    }

    /** Check that resolution of a functor as argument to a functor succeeds. */
    public void testFunctorAsArgumentToFunctorResolves() throws Exception
    {
        resolveAndAssertSolutions("[[f(g(x))], (?- f(g(x))), [[]]]");
    }

    /** Check that resolution of a non matching functor as argument to a functor fails. */
    public void testNonMatchingFunctorAsArgumentToFunctorFailsResolution() throws Exception
    {
        resolveAndAssertFailure(new String[] { "f(g(x))" }, "?- f(g(y))");
    }

    /** Check that resolution of a functor as argument to a functor succeeds through a chained call. */
    public void testFunctorAsArgumentToFunctorResolvesInChainedCall() throws Exception
    {
        resolveAndAssertSolutions("[[g(h(x)), (f(X) :- g(X))], (?- f(h(x))), [[]]]");
    }

    /** Check that resolution of a non matching functor as argument to a functor fails through a chained call. */
    public void testNonMatchingFunctorAsArgumentToFunctorFailsResolutionInChainedCall() throws Exception
    {
        resolveAndAssertFailure(new String[] { "g(h(x))", "f(X) :- g(X)" }, "?- f(h(y))");
    }

    /** Check that anonymous variable bindings are not reported. */
    public void testAnonymousVariableBindingNotReported() throws Exception
    {
        resolveAndAssertSolutions("[[f(x)], (?- f(_)), [[]]]");
    }

    /** Check that anonymous but identified variable bindings are not reported. */
    public void testAnonymousIdentifiedVariableBindingNotReported() throws Exception
    {
        resolveAndAssertSolutions("[[f(x)], (?- f(_X)), [[]]]");
    }

    /** Check that anonymous but identified variable bindings are propagated accross calls. */
    public void testAnonymousIdentifiedVariableBindingPropagatedAccrossCall() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), (f(_X) :- g(_X))], (?- f(x)), [[]]]");
    }

    /** Check that anonymous variable bindings are not propagated accross calls. */
    public void testAnonymousVariableBindingNotPropagatedAccrossCall() throws Exception
    {
        resolveAndAssertSolutions("[[g(x), (f(_) :- g(_))], (?- f(y)), [[]]]");
    }

    /** Check that multiple variables are bound in a simple call. */
    public void testMultipleVariablesAreBoundOk() throws Exception
    {
        resolveAndAssertSolutions("[[f(x, y)], (?- f(X, Y)), [[X <-- x, Y <-- y]]]");
    }

    /** Check anonymous variables work ok. */
    public void testAnonymousProgramAndQuery() throws Exception
    {
        resolveAndAssertSolutions("[[f(_, _, _)], (?- f(_, _, _)), [[]]]");
    }

    /** Check anonymous variables nested within functors work ok. */
    public void testAnonymousNestedProgramAndQuery() throws Exception
    {
        resolveAndAssertSolutions("[[f(g(_, _, _))], (?- f(g(_, _, _))), [[]]]");
    }

    /** Check that bound variables are unbound when backtracking, this error case was found. */
    public void testVariablesUnboundOnBacktrackingMemberOk() throws Exception
    {
        resolveAndAssertSolutions("[[test([labels([first])]), test([labels([second])])], " +
            "(?- test(_PS), member(labels(L), _PS)), " + "[[L <-- [first]], [L <-- [second]]]]");
    }

    /** Check that a variable argument can bind to a functor in a clause head. */
    public void testVariableBindsToFunctorInHead() throws Exception
    {
        resolveAndAssertSolutions("[[b(f(x))], (?- b(X)), [[X <-- f(x)]]]");
    }

    /** Check that a variable argument can bind to a functor argument in a clause head. */
    public void testVariableBindsToFunctorArgInHead() throws Exception
    {
        resolveAndAssertSolutions("[[b(f(x))], (?- b(f(X))), [[X <-- x]]]");
    }

    /** Check that a variable argument can bind to a functor in a body. */
    public void testVariableBindsToFunctorInBody() throws Exception
    {
        resolveAndAssertSolutions("[[b(Y, Y)], (?- b(f(x), X)), [[X <-- f(x)]]]");
    }

    /** Check that a variable argument can bind to a functor argument in a body. */
    public void testVariableBindsToFunctorArgInBody() throws Exception
    {
        resolveAndAssertSolutions("[[b(f(Y), Y)], (?- b(f(x), X)), [[X <-- x]]]");
    }

    /** Check that a variable argument can bind to a functor in a clause head through a call. */
    public void testVariableBindsToFunctorInHeadThroughCall() throws Exception
    {
        resolveAndAssertSolutions("[[b(f(x)), (a(f(Y)) :- b(f(Y)))], (?- a(X)), [[X <-- f(x)]]]");
    }

    /** Check that a variable argument can bind to a functor argument in a clause head through a call. */
    public void testVariableBindsToFunctorArgInHeadThroughCall() throws Exception
    {
        resolveAndAssertSolutions("[[b(f(x)), (a(Y) :- b(f(Y)))], (?- a(X)), [[X <-- x]]]");
    }

    /** Check that variables assigned to temporary registers, are not overwritten by argument registers. */
    public void testTemporaryRegistersNotOverwritten() throws Exception
    {
        resolveAndAssertSolutions("[[b(x, y, z), (a(Y) :- b(x, y, Y))], (?- a(X)), [[X <-- z]]]");
    }

    /** Check that a variable created within a clause body can be bound correctly. */
    public void testBodyVariableBindsOk() throws Exception
    {
        resolveAndAssertSolutions("[[h(X,X), g(x), (f(Y) :- h(Y,Z), g(Z))], (?- f(W)), [[W <-- x]]]");
    }

    /**
     * Sets the state of the extra solutions check.
     *
     * @param checkExtraSolutions The state of the extra solutions check.
     */
    public BasicResolverUnitTestBase withCheckExtraSolutions(boolean checkExtraSolutions)
    {
        this.checkExtraSolutions = checkExtraSolutions;

        return this;
    }

    /**
     * Helper method for resolutions that produce a known set of variable binding solutions, this method performs the
     * resolution and asserts that it succeeds and produces the expected bindings. Every expected binding must
     * correspond to a free variable in the query. There can be free variables in the query, for which no expected
     * binding is specified, in which case the test does not care what value that variable takes.
     *
     * <p/>The entire test specification is passed to this method as a string, to be parsed into a list. The reason for
     * this is that variables are scoped to a single sentence in first order logic, and in order to specify that a
     * particular variable in a query must take a particular binding the expected binding is easiest to specify as part
     * of the same sentence as the test itself. Otherwise variables in the test need to be matched up with variables in
     * the expected results by name.
     *
     * <p/>The format of the test specification string is as follows:
     *
     * <pre>"[[ domain clause, ... ], query expression to test, [[ variable &lt;-- expected binding, ... ], ...]]".</pre>
     *
     * <p/>A simple example that specifies that 'X' must take the binding 'x' is:
     *
     * <pre>"[[f(x)], (?- f(X)), [[ X &lt;-- x]]]"</pre>
     *
     * <p/>An example specifying several solutions upon backtracking is:
     *
     * <pre>"[[f(x), f(y)], (?- f(X)), [[ X &lt;-- x], [ X &lt;-- y]]"</pre>
     *
     * @param test The test specification as detailed above.
     */
    protected void resolveAndAssertSolutions(String test) throws Exception
    {
        /*log.fine("protected void resolveAndAssertSolutions(String test): called");*/

        String errorMessages = "";

        // Parse the entire test specification as a term.
        parser.setTokenSource(TokenSource.getTokenSourceForString(test));

        // Used to count the number of solutions found.
        int numSolutions = 0;

        Sentence<S> testSentence = parser.parse();

        // Split the parsed test specification up as a list of three elements. The first element contains a list
        // of domain clauses, the second contains the test query, the third contains a list of solutions as lists of
        // expected variable bindings.
        S testListingClause = testSentence.getT();
        RecursiveList testListing = (RecursiveList) testListingClause.getHead();
        Iterator<Term> testListingIterator = testListing.iterator();

        RecursiveList domainListing = (RecursiveList) testListingIterator.next();
        Functor queryTerm = (Functor) testListingIterator.next();
        Clause query = TermUtils.convertToClause(queryTerm, interner);
        RecursiveList expectedSolutionsListing = (RecursiveList) testListingIterator.next();

        /*log.fine("domainListing = " + domainListing.toString(interner, true, false));*/
        /*log.fine("query = " + query.toString(interner, true, false));*/
        /*log.fine("expectedSolutionsListing = " + expectedSolutionsListing.toString(interner, true, false));*/

        // Clear the resolvers domain on every test.
        engine.reset();

        // Compile the domain clauses and insert them into the resolver.
        for (Term aDomainListing : domainListing)
        {
            Functor domainClauseTerm = (Functor) aDomainListing;
            Clause domainClause = TermUtils.convertToClause(domainClauseTerm, interner);

            /*log.fine("domainClause = " + domainClause.toString(interner, true, false));*/

            compiler.compile(new SentenceImpl<S>((S) domainClause));
        }

        compiler.endScope();

        // Compile the query and insert it into the resolver.
        compiler.compile(new SentenceImpl<S>((S) query));

        // Get the free variables in the query.
        Set<Variable> freeVarsInQuery = TermUtils.findFreeNonAnonymousVariables(query);

        // Create an iterator over all solutions.
        Iterator<Map<String, Variable>> solutions = engine.expandResultSetToMap(resolver.iterator()).iterator();

        // Attempt to generate as many resolutions as there are solutions specified in the test.
        for (Term anExpectedSolutionsListing : expectedSolutionsListing)
        {
            RecursiveList expectedBindingsListing = (RecursiveList) anExpectedSolutionsListing;

            // Turn the array listing of expected bindings into a set of expected bindings, ensuring that there are no
            // duplicates specified.
            Map<Variable, Term> expectedBindingsMap = new HashMap<Variable, Term>();

            for (Term bindingTerm : expectedBindingsListing)
            {
                // Check that the binding is specified using an infix binding operator.
                if (!(bindingTerm instanceof OpSymbol))
                {
                    fail("Bindings must be specified using the infix '<--' symbol.");
                }

                OpSymbol bindingOperator = (OpSymbol) bindingTerm;

                // Check that the binding is specified as a pair.
                assertEquals("Expected bindings must be specified as pairs.", 2, bindingOperator.getArity());

                Variable var = (Variable) bindingOperator.getArgument(0);
                Term term = bindingOperator.getArgument(1);

                /*log.fine("Got expected binding, " + var.toString(interner, true, false) + " <-- " +
                    term.toString(interner, true, false));*/

                // Check that there is no duplicate already in the expected binding set for the new expected binding.
                if (expectedBindingsMap.containsKey(var))
                {
                    fail("Expected bindings must be unique but got a duplicate for the variable " +
                        var.toString(interner, true, false));
                }

                expectedBindingsMap.put(var, term);
            }

            // Check that the expected bindings do not contain any variables not free in the query.
            for (Variable expectedVar : expectedBindingsMap.keySet())
            {
                if (!freeVarsInQuery.contains(expectedVar))
                {
                    fail("There is an expected binding for the variable " +
                        interner.getVariableName(expectedVar.getName()) + " that is not a free variable in the query.");
                }
            }

            // Perform query resolution to obtain the next solution and check that the resolution did indeed produce
            // an expected solution.
            Map<String, Variable> solutionBindings = null;

            try
            {
                numSolutions++;
                solutionBindings = solutions.next();
            }
            catch (NoSuchElementException e)
            {
                fail("Resolution failed to find solution " + numSolutions + ".");
            }

            // For each bound variable perform a structural equality check against the expected binding, reporting any
            // mismatches.

            for (Map.Entry<Variable, Term> expectedBinding : expectedBindingsMap.entrySet())
            {
                Variable var = expectedBinding.getKey();
                Term term = expectedBinding.getValue();

                Variable varInSolution = solutionBindings.get(engine.getVariableName(var.getName()));

                if (varInSolution == null)
                {
                    errorMessages +=
                        "The expected binding variable " + interner.getVariableName(var.getName()) +
                        " is not bound in the solution.\n";
                }
                else if (!varInSolution.structuralEquals(term))
                {
                    errorMessages +=
                        "The expected binding variable " + interner.getVariableName(var.getName()) +
                        " is not structurally equal to its expected binding, " + term.toString(interner, true, false) +
                        ", instead its value is " + varInSolution.toString(interner, true, true) + ".\n";
                }
            }
        }

        // Check if more solutions than were specified in the test can be found.
        if (checkExtraSolutions)
        {
            int extraSolutions = 0;

            while (solutions.hasNext() && (extraSolutions < 100))
            {
                solutions.next();
                extraSolutions++;
            }

            if (extraSolutions >= 100)
            {
                errorMessages += "100+ extra solutions were found...";
            }
            else if (extraSolutions > 0)
            {
                errorMessages += extraSolutions + " extra solutions were found.";
            }
        }

        assertTrue(errorMessages, "".equals(errorMessages));
    }

    /**
     * Helper method for simple resolutions that should fail. This method performs the resolution and asserts that it
     * fails.
     *
     * @param  domain The domain clauses to query over.
     * @param  query  The query to run.
     *
     * @throws SourceCodeException If the test code fails to parse, compile or link.
     */
    protected void resolveAndAssertFailure(String[] domain, String query) throws SourceCodeException
    {
        // Clear the resolvers domain on every test.
        engine.reset();

        // Compile the domain clauses and insert them into the resolver.
        for (String predicate : domain)
        {
            compileDomainClause(predicate);
        }

        compiler.endScope();

        // Compile the query and insert it into the resolver.
        compileQuery(query);

        // Perform query resolution.
        Set<Variable> bindings = resolver.resolve();

        // Check that the resolution succeeded.
        assertNull("Resolution was expected to fail but did not.", bindings);
    }

    /** Creates a NDC to track test methods against log statements. */
    protected void setUp()
    {
        NDC.push(getName());
    }

    /** Ensures that the NDC is cleaned up before the next test. */
    protected void tearDown()
    {
        NDC.pop();
    }

    /**
     * Parses and compiles the given text as a statement in fol to be unified against.
     *
     * @param  s The statement to compile.
     *
     * @return The parsed and compiled statement.
     */
    protected void compileDomainClause(String s) throws SourceCodeException
    {
        parser.setTokenSource(TokenSource.getTokenSourceForString(s));

        Sentence<S> p1 = parser.parse();

        compiler.compile(p1);
    }

    /**
     * Parses and compiles the given text as a query in fol to unify with.
     *
     * @param  s The statement to compile.
     *
     * @return The parsed and compiled statement.
     */
    protected void compileQuery(String s) throws SourceCodeException
    {
        parser.setTokenSource(TokenSource.getTokenSourceForString(s));

        Sentence<S> q1 = parser.parse();

        compiler.compile(q1);
    }
}
