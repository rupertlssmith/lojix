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

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.NDC;

import com.thesett.aima.logic.fol.isoprologparser.Token;
import com.thesett.aima.logic.fol.isoprologparser.TokenSource;
import com.thesett.common.parsing.SourceCodeException;

/**
 * Performs some basic unification tests over standard equality in first order logic enhanced with arithmetic. Terms to
 * be unified are constructed using prolog syntax and a parser to parse them into an abstract syntax tree. The unifier
 * to test must be an implementation of {@link Unifier}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Check that matching atoms unify.
 * <tr><td> Check that non-matching atoms do not unify.
 * <tr><td> Check that equal numbers unify.
 * <tr><td> Check that non-equal numbers do not unify.
 * <tr><td> Check that free left variable unifies with an atom.
 * <tr><td> Check that free left variable unifies with a functor.
 * <tr><td> Check that free right variable unifies with an atom.
 * <tr><td> Check that free right variable unifies with a functor.
 * <tr><td> Check that a variable unifies with a different variable with the same name, with a substitution.
 * <tr><td> Check that a variable unifies with a different variable with a different name, with a substitution.
 * <tr><td> Check that a query atom does not unify with a program functor of the same name.
 * <tr><td> Check that a program atom does not unify with a query functor of the same name.
 * <tr><td> Check that bound variables, with the same bindings unify.
 * <tr><td> Check that bound variables, with the same functor bindings unify.
 * <tr><td> Check that a variable, cannot be bound to two different things.
 * <tr><td> Check that bound variables, with the same functor bindings unify.
 * <tr><td> Check that bound variables, with the same bindings unify.
 * <tr><td> Check that bound variables, with the same functor bindings unify.
 * <tr><td> Check that a variable, cannot be bound to two different things.
 * <tr><td> Check that bound variables, with the same functor bindings unify.
 * <tr><td> Check that a variable in a query and one in a program with equal bindings unify with each other.
 * <tr><td> Check that bound variables, with different bindings do not unify.
 * <tr><td> Check that a bound var, propagates its binding into functors.
 * <tr><td> Check that a variable bound to a variable, unifies with that variable.
 * <tr><td> Check that a program variable bound to a query variable, unifies with another variable.
 * <tr><td> Check that a program variable bound to a query variable, unifies with another variable.
 * <tr><td> Check that compound terms with same name and arity and arguments unify.
 * <tr><td> Check that compound terms with same name but different arity do not unify.
 * <tr><td> Check that compound terms with same name and arity but non-unifiying arguments do not unify.
 * <tr><td> Check that compound terms with different names do not unify.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Add capability to translate/compile the term trees prior to the unification.
 */
public class UnifierUnitTestBase<S extends Term, T extends Term> extends TestCase
{
    /** Holds the parser to parse the test examples with. */
    protected Parser<S, Token> parser;

    /** Holds the query parser to parse the test examples with. */
    protected Parser<S, Token> qparser;

    /** Holds the compiler to compile test terms for unification with. */
    protected LogicCompiler<S, T, T> compiler;

    /** Holds the compiler output observer. */
    protected CompilerOutputObserver compilerObserver;

    /** Holds the unifier to test. */
    protected Unifier<T> unifier;

    /** Holds the interner for functor and variable names. */
    protected VariableAndFunctorInterner interner;

    /**
     * Creates a simple unification test for the specified unifier, using the specified compiler.
     *
     * @param name     The name of the test.
     * @param unifier  The unifier to test.
     * @param compiler The compiler to prepare terms for unification with.
     * @param parser   The parser for program terms.
     * @param qparser  The parser for query terms.
     * @param interner The functor and variable interner.
     */
    public UnifierUnitTestBase(String name, Unifier<T> unifier, LogicCompiler<S, T, T> compiler,
        Parser<S, Token> parser, Parser<S, Token> qparser, VariableAndFunctorInterner interner)
    {
        super(name);
        this.unifier = unifier;
        this.compiler = compiler;
        this.parser = parser;
        this.qparser = qparser;
        this.interner = interner;

    }

    /**
     * Constructs a test case with the given name. This constructor is protected and is intended for sub-classing these
     * tests to provide different implementations of the unify and assert methods.
     *
     * @param name The name of the test.
     */
    protected UnifierUnitTestBase(String name)
    {
        super(name);
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
        T t2 = compileStatement(statement);
        T t1 = compileQuery(query);

        // Unify them.
        List<Variable> bindings = unifier.unify(t1, t2);

        // Check that the unification succeeded.
        assertNotNull("Unification failed.", bindings);

        // Check that no bindings were created, because the unification does not need any.
        assertEquals("Unification did not result in the expected number of bindings.", num, bindings.size());
    }

    /**
     * Helper method to check that a unification fails. This method performs the unification and asserts that it fails.
     *
     * @param statement The first term to unify.
     * @param query     The second term to unify.
     */
    public void unifyAndAssertFailure(String statement, String query) throws Exception
    {
        T t2 = compileStatement(statement);
        T t1 = compileQuery(query);

        // Unify them.
        List<Variable> bindings = unifier.unify(t1, t2);

        // Check that the unification failed.
        assertNull("Unification succeeded but it should have failed.", bindings);
    }

    /** Check that matching atoms unify. */
    public void testAtomsUnifyOk() throws Exception
    {
        unifyAndAssertNumBindings("x", "x", 0);
    }

    /** Check that non-matching atoms do not unify. */
    public void testNonMatchingAtomsFailUnify() throws Exception
    {
        unifyAndAssertFailure("y", "x");
    }

    /** Check that equal numbers unify. */
    public void testEqualNumbersUnifyOk() throws Exception
    {
        unifyAndAssertNumBindings("1", "1", 0);
        unifyAndAssertNumBindings("" + Math.E, "" + Math.E, 0);
    }

    /** Check that non-equal numbers do not unify. */
    public void testNonEqualNumbersFailToUnify() throws Exception
    {
        unifyAndAssertFailure("2", "1");
        unifyAndAssertFailure("" + Math.PI, "" + Math.E);
    }

    /** Check that equal string literals unify. */
    public void testEqualStringsUnifyOk() throws Exception
    {
        unifyAndAssertNumBindings("\"test\"", "\"test\"", 0);
    }

    /** Check that non-equal string literals fail to unify. */
    public void testNonEqualStringsFailToUnify() throws Exception
    {
        unifyAndAssertFailure("\"test\"", "\"notmatch\"");
    }

    /** Check that free left variable unifies with an atom. */
    public void testFreeLeftVarUnifiesAtomOk() throws Exception
    {
        unifyAndAssertNumBindings("f(x)", "f(X)", 1);
    }

    /** Check that free left variable unifies with a functor. */
    public void testFreeLeftVarUnifiesFunctorOk() throws Exception
    {
        unifyAndAssertNumBindings("f(g(x))", "f(X)", 1);
    }

    /** Check that free right variable unifies with an atom. */
    public void testFreeRightVarUnifiesAtomOk() throws Exception
    {
        unifyAndAssertNumBindings("f(X)", "f(x)", 0);
    }

    /** Check that free right variable unifies with a functor. */
    public void testFreeRightVarUnifiesFunctorOk() throws Exception
    {
        unifyAndAssertNumBindings("f(X)", "f(g(x))", 0);
    }

    /** Check that a variable unifies with a different variable with the same name, with a substitution. */
    public void testFreeVarUnifiesWithSameNameOk() throws Exception
    {
        unifyAndAssertNumBindings("f(X)", "f(X)", 1);
    }

    /** Check that a variable unifies with a different variable with a different name, with a substitution. */
    public void testFreeVarUnifiesWithDifferentNameOk() throws Exception
    {
        unifyAndAssertNumBindings("f(Y)", "f(X)", 1);
    }

    /** Check that a query atom does not unify with a program functor of the same name. */
    public void testQueryAtomDoesNotUnifyWithProgFunctorSameName() throws Exception
    {
        unifyAndAssertFailure("f(a(b))", "f(a)");
    }

    /** Check that a program atom does not unify with a query functor of the same name. */
    public void testProgAtomDoesNotUnifyWithQueryFunctorSameName() throws Exception
    {
        unifyAndAssertFailure("f(a)", "f(a(b))");
    }

    /** Check that bound variables, with the same bindings unify. */
    public void testBoundVarUnifiesWithDifferentEqualBoundVarOk() throws Exception
    {
        unifyAndAssertNumBindings("f(x,x)", "f(X,X)", 1);
    }

    /** Check that bound variables, with the same functor bindings unify. */
    public void testBoundVarToFunctorUnifiesWithEqualBoundVarOk() throws Exception
    {
        unifyAndAssertNumBindings("f(g(x),g(x))", "f(X,X)", 1);
    }

    /** Check that a variable, cannot be bound to two different things. */
    public void testBoundVarFailsToUnifyWithDifferentBinding() throws Exception
    {
        unifyAndAssertFailure("f(x,y)", "f(X,X)");
    }

    /** Check that bound variables, with the same functor bindings unify. */
    public void testBoundVarToFunctorFailsToUnifyWithDifferentFunctorBinding() throws Exception
    {
        unifyAndAssertFailure("f(g(x),g(y))", "f(X,X)");
    }

    /** Check that bound variables, with the same bindings unify. */
    public void testProgBoundVarUnifiesWithDifferentEqualBoundVarOk() throws Exception
    {
        unifyAndAssertNumBindings("f(X,X)", "f(x,x)", 0);
    }

    /** Check that bound variables, with the same functor bindings unify. */
    public void testProgBoundVarToFunctorUnifiesWithEqualBoundVarOk() throws Exception
    {
        unifyAndAssertNumBindings("f(X,X)", "f(g(x),g(x))", 0);
    }

    /** Check that a variable, cannot be bound to two different things. */
    public void testProgBoundVarFailsToUnifyWithDifferentBinding() throws Exception
    {
        unifyAndAssertFailure("f(X,X)", "f(x,y)");
    }

    /** Check that bound variables, with the same functor bindings unify. */
    public void testProgBoundVarToFunctorFailsToUnifyWithDifferentFunctorBinding() throws Exception
    {
        unifyAndAssertFailure("f(X,X)", "f(g(x),g(y))");
    }

    /** Check that a variable in a query and one in a program with equal bindings unify with each other. */
    public void testBoundVarInQueryUnifiesAgainstVarInProg() throws Exception
    {
        unifyAndAssertNumBindings("f(X,X,g(x))", "f(g(x),Y,Y)", 1);
    }

    /** Check that bound variables, with different bindings do not unify. */
    public void testBoundVarFailsToUnifyWithDifferentlyBoundVar() throws Exception
    {
        unifyAndAssertFailure("f(y,Y,Y)", "f(X,X,x)");
    }

    /** Check that a bound var, propagates its binding into functors. */
    public void testBoundVarPropagatesIntoFunctors() throws Exception
    {
        unifyAndAssertNumBindings("f(g(h(X)),X)", "f(Y,x)", 1);
    }

    /** Check that a variable bound to a variable, unifies with that variable. */
    public void testBoundVarUnifiesToSameVar() throws Exception
    {
        unifyAndAssertNumBindings("f(X,X)", "f(Y,Y)", 1);
    }

    /** Check that a program variable bound to a query variable, unifies with another variable. */
    public void testBoundProgVarUnifiesToDifferentQueryVar() throws Exception
    {
        unifyAndAssertNumBindings("f(X,X)", "f(Y,Z)", 2);
    }

    /** Check that a program variable bound to a query variable, unifies with another variable. */
    public void testBoundQueryVarUnifiesToDifferentProgVar() throws Exception
    {
        unifyAndAssertNumBindings("f(Y,Z)", "f(X,X)", 1);
    }

    /** Check that compound terms with same name and arity and arguments unify. */
    public void testFunctorsSameArityUnify() throws Exception
    {
        unifyAndAssertNumBindings("f(x,x,x)", "f(x,x,x)", 0);
    }

    /** Check that compound terms with same name but different arity do not unify. */
    public void testFunctorsDifferentArityFailToUnify() throws Exception
    {
        unifyAndAssertFailure("f(x,x,x,x)", "f(x,x,x)");
    }

    /** Check that compound terms with same name and arity but non-unifiying arguments do not unify. */
    public void testFunctorsSameArityDifferentArgsFailToUnify() throws Exception
    {
        unifyAndAssertFailure("f(x,x,x)", "f(y,y,y)");
    }

    /** Check that compound terms with different names do not unify. */
    public void testFunctorsDifferentNameSameArgsDoNotUnify() throws Exception
    {
        unifyAndAssertFailure("g(x,x,x)", "f(y,y,y)");
    }

    protected void setUp()
    {
        NDC.push(getName());
    }

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
    private T compileStatement(String s) throws SourceCodeException
    {
        parser.setTokenSource(TokenSource.getTokenSourceForString(s));

        Sentence<S> p1 = parser.parse();

        compilerObserver = new CompilerOutputObserver();
        this.compiler.setCompilerObserver(compilerObserver);
        compiler.compile(p1);

        return compilerObserver.getLatest();
    }

    /**
     * Parses and compiled the given text as a query in fol to unify with.
     *
     * @param  s The statement to compile.
     *
     * @return The parsed and compiled statement.
     */
    private T compileQuery(String s) throws SourceCodeException
    {
        qparser.setTokenSource(TokenSource.getTokenSourceForString(s));

        Sentence<S> q1 = qparser.parse();

        compilerObserver = new CompilerOutputObserver();
        this.compiler.setCompilerObserver(compilerObserver);
        compiler.compile(q1);

        return compilerObserver.getLatest();
    }

    /**
     * Holds the most recent compiler output statement or query.
     */
    private class CompilerOutputObserver implements LogicCompilerObserver<T, T>
    {
        private T latest;

        /** {@inheritDoc} */
        public void onCompilation(Sentence<T> sentence) throws SourceCodeException
        {
            latest = sentence.getT();
        }

        /** {@inheritDoc} */
        public void onQueryCompilation(Sentence<T> sentence) throws SourceCodeException
        {
            latest = sentence.getT();
        }

        /**
         * Provides the most recent compiled statement.
         *
         * @return The most recent compiled statement.
         */
        public T getLatest()
        {
            return latest;
        }
    }
}
