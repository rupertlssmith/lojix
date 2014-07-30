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
import com.thesett.aima.logic.fol.isoprologparser.Token;
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
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BasicUnificationUnitTestBase<S extends Clause, T, Q> extends BasicResolverUnitTestBase<S, T, Q>
{
    /** Used for debugging purposes. */
    public static final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(BasicUnificationUnitTestBase.class.getName());

    /** Holds the parser to parse the test examples with. */
    protected Parser<S, Token> parser;

    /** Holds the compiler to compile test terms for unification with. */
    protected LogicCompiler<S, T, T> compiler;

    /** Holds the unifier to test. */
    protected Resolver<T, T> resolver;

    /** Holds the interner for functor and variable names. */
    VariableAndFunctorInterner interner;

    /** Holds the resolution engine to test. */
    protected ResolutionEngine<S, T, T> engine;

    /**
     * Creates a simple resolution test for the specified resolver, using the specified compiler.
     *
     * @param name   The name of the test.
     * @param engine The resolution engine to test.
     */
    public BasicUnificationUnitTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name, engine);
    }

    public void unifyAndAssertNumBindings(String statement, String query, int num) throws Exception
    {
        resolveAndAssertSolutions("[[" + statement + "], (?- " + query + "), [[]]]");
    }

    public void unifyAndAssertFailure(String statement, String query) throws SourceCodeException
    {
        resolveAndAssertFailure(new String[] { statement }, "?- " + query);
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

    /** Check that free right variable unifies with an atom. */
    public void testFreeRightVarUnifiesAtomOk() throws Exception
    {
        unifyAndAssertNumBindings("f(X)", "f(x)", 0);
    }

    /** Check that free left variable unifies with a functor. */
    public void testFreeLeftVarUnifiesFunctorOk() throws Exception
    {
        unifyAndAssertNumBindings("f(g(x))", "f(X)", 1);
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

    /** Check that a query atom does not unify with a program functor of the same name. */
    public void testQueryAtomDoesNotUnifyWithProgFunctorArgSameName() throws Exception
    {
        unifyAndAssertFailure("f(a(b))", "f(b)");
    }

    /** Check that a program atom does not unify with a query functor of the same name. */
    public void testProgAtomDoesNotUnifyWithQueryFunctorArgSameName() throws Exception
    {
        unifyAndAssertFailure("f(b)", "f(a(b))");
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

    /** Check that deeper bound variables, with the same functor bindings unify. */
    public void testDeeperBoundVarToFunctorUnifiesWithEqualBoundVarOk() throws Exception
    {
        unifyAndAssertNumBindings("f(g(h(x)),g(h(x)))", "f(g(h(X)),g(h(X)))", 1);
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

    /** Check that multiple nested constants unify. */
    public void testFunctorsMultipleInnerConstantsUnify() throws Exception
    {
        unifyAndAssertNumBindings("f(g(x,x,x))", "f(g(x,x,x))", 0);
    }

    /** Check that multiple nested constants unify with a variable. */
    public void testFunctorsMultipleInnerConstantsUnifyWithVar() throws Exception
    {
        unifyAndAssertNumBindings("f(g(x,x,x))", "f(g(X, X, X))", 1);
    }

    /** Check that a variable unifies with multiple nested constants. */
    public void testFunctorsVarUnifiesWithMultipleInnerConstants() throws Exception
    {
        unifyAndAssertNumBindings("f(g(X, X, X))", "f(g(x, x, x))", 1);
    }

    /** Check that multiple different nested constants unify. */
    public void testFunctorsMultipleDifferentInnerConstantsUnify() throws Exception
    {
        unifyAndAssertNumBindings("f(g(x,y,z))", "f(g(x,y,z))", 0);
    }

    /** Check that nested mixed variables and constants unify. */
    public void testFunctorsMultipleNestedMixedVarsAndConstantsUnify() throws Exception
    {
        unifyAndAssertNumBindings("f(g(w,X,y,Z))", "f(g(W,x,Y,z))", 4);
    }

    /** Check that nested constants intermixed with variables unify. */
    public void testFunctorsNestedConstantsMixedWithVarsUnify() throws Exception
    {
        unifyAndAssertNumBindings("f(g(w),h(x),i(y),j(z))", "f(g(W),X,i(Y),Z)", 4);
    }

    /** A unification example from the WAM book. */
    public void testWamBook2_9() throws Exception
    {
        unifyAndAssertNumBindings("p(f(X),h(Y,f(a)),Y)", "p(Z,h(Z,W),f(W))", 2);
    }

    /** A unification example from the WAM book, but with the statement and query swapped around. */
    public void testWamBook2_9OtherWay() throws Exception
    {
        unifyAndAssertNumBindings("p(Z,h(Z,W),f(W))", "p(f(X),h(Y,f(a)),Y)", 2);
    }
}
