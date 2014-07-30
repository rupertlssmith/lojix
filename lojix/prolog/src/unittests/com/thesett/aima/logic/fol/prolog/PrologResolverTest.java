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
package com.thesett.aima.logic.fol.prolog;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

import com.thesett.aima.logic.fol.ArithmeticResolverUnitTestBase;
import com.thesett.aima.logic.fol.BacktrackingResolverUnitTestBase;
import com.thesett.aima.logic.fol.BasicResolverUnitTestBase;
import com.thesett.aima.logic.fol.CallAndNotResolverUnitTestBase;
import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.ConjunctionResolverUnitTestBase;
import com.thesett.aima.logic.fol.DisjunctionResolverUnitTestBase;
import com.thesett.aima.logic.fol.ListResolverUnitTestBase;
import com.thesett.aima.logic.fol.RuntimeTypeCheckUnitTestBase;
import com.thesett.aima.logic.fol.TrueAndFailResolverUnitTestBase;
import com.thesett.aima.logic.fol.UnifyAndNonUnifyResolverUnitTestBase;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.VariableAndFunctorInternerImpl;
import com.thesett.aima.logic.fol.isoprologparser.ClauseParser;

/**
 * PrologResolverTest tests resolution and unification over a range of terms in first order logic, in order to test all
 * success and failure paths, through an interpreted Prolog machine. The interpreted Prolog machine handles full
 * resolution with backtracking, in addition to unification.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Run all basic resolution tests. <td> {@link BasicResolverUnitTestBase}.
 * <tr><td> Run all back-tracking resolution tests. <td> {@link BacktrackingResolverUnitTestBase}.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PrologResolverTest extends TestCase
{
    /**
     * Creates a test with the specified name.
     *
     * @param name The name of the test.
     */
    public PrologResolverTest(String name)
    {
        super(name);
    }

    /**
     * Compile all the tests for the default tests for unifiers into a suite, plus the tests defined in this class.
     *
     * @return A test suite.
     */
    public static Test suite()
    {
        // Build a new test suite.
        TestSuite suite = new TestSuite("L2ResolvingJavaMachine Tests");

        VariableAndFunctorInterner interner =
            new VariableAndFunctorInternerImpl("Prolog_Variable_Namespace", "Prolog_Functor_Namespace");
        ClauseParser parser = new ClauseParser(interner);
        PrologCompiler compiler = new PrologCompiler(interner);
        PrologResolver resolver = new PrologResolver(interner);

        PrologEngine engine = new PrologEngine(parser, interner, compiler, resolver);

        // Add all tests defined in the BasicResolverUnitTestBase class.
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testAtomAsArgumentToFunctorResolves", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNonMatchingAtomAsArgumentToFunctorFailsResolution", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testVariableAsArgumentToFunctorResolvesToCorrectBinding", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testAtomAsArgumentToFunctorResolvesInChainedCall", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNonMatchingAtomAsArgumentToFunctorFailsResolutionInChainedCall", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testVariableAsArgumentToFunctorResolvesToCorrectBindingInChainedCall", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testAtomAsArgumentToFunctorResolvesInTwoChainedCalls", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNonMatchingAtomAsArgumentToFunctorFailsResolutionInTwoChainedCalls", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testVariableAsArgumentToFunctorResolvesToCorrectBindingInTwoChainedCalls", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testResolutionFailsWhenNoMatchingFunctor", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testResolutionFailsWhenNoMatchingFunctorInChainedCall", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFunctorAsArgumentToFunctorResolves", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNonMatchingFunctorAsArgumentToFunctorFailsResolution", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFunctorAsArgumentToFunctorResolvesInChainedCall", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNonMatchingFunctorAsArgumentToFunctorFailsResolutionInChainedCall", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testAnonymousVariableBindingNotReported", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testAnonymousIdentifiedVariableBindingNotReported", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testAnonymousIdentifiedVariableBindingPropagatedAccrossCall", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testAnonymousVariableBindingNotPropagatedAccrossCall", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testMultipleVariablesAreBoundOk", engine));
        suite.addTest(new BasicResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testVariablesUnboundOnBacktrackingMemberOk", engine));

        // Add all tests defined in the ConjunctionResolverUnitTestBase class.
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testConjunctionResolves", engine));
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testConjunctionFailsToResolveWhenFirstPathFails", engine));
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testConjunctionFailsToResolveWhenSecondPathFails", engine));
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testConjunctionResolvesWhenFirstPathRevisitsSecond", engine));
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testConjunctionResolvesWhenSecondPathRevisitsFirst", engine));
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testVariableBindingFromQueryPropagatesAccrossConjunction", engine));
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testVariableBindingFromFirstPathPropagatesAccrossConjunction", engine));
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testVariableBindingFromFirstPathPropagatesAccrossConjunctionAndFailsOnNonUnification", engine));
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testConjoinedVariablesPropagateAccrossConjunction", engine));
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testConjoinedVariablesPropagateAccrossConjunctionFailingOnNonUnification", engine));
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testVariableInClauseMayTakeMultipleSimultaneousBindings", engine));
        suite.addTest(new ConjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testSuccesiveConjunctiveTermsOk", engine));

        // Add all tests defined in the DisjunctionResolverUnitTestBase class.
        suite.addTest(new DisjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testResolvesOnFirstMatchingPossibleFunctor", engine));
        suite.addTest(new DisjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testResolvesOnSecondMatchingPossibleFunctor", engine));
        suite.addTest(new DisjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFailsOnNoMatchingOutOfSeveralPossibleFunctors", engine));
        suite.addTest(new DisjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testVariableTakesBindingsFromTwoDisjunctionPaths", engine));
        suite.addTest(new DisjunctionResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testVariableTakesBindingsFromManyDisjunctionPaths", engine));

        // Add all tests defined in the BacktrackingResolverUnitTestBase class.
        suite.addTest(new BacktrackingResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testMultipleFactsProduceMultipleSolutions", engine));
        suite.addTest(new BacktrackingResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testInstantiatingClausesSeveralTimesWithSameVariableDoesNotConflictVariableBindings", engine));
        suite.addTest(new BacktrackingResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testInstantiatingClausesSeveralTimesWithDifferentVariableAllowsIndependentBindings", engine));

        // Add all tests defined in the ListResolverUnitTestBase class.
        suite.addTest(new ListResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNilRecognized", engine));
        suite.addTest(new ListResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNonEmptyListRecognized", engine));
        suite.addTest(new ListResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testConsRecognized", engine));
        suite.addTest(new ListResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testListIterationTerminatesOnEmpty", engine));
        suite.addTest(new ListResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testListIterationTerminatesOnList", engine));

        // Add all tests defined in the ArithmeticResolverUnitTestBase class.
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testAddIntegerOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testSubtractIntegerOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testMultiplyIntegerOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testDivideIntegerOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testExponentialIntegerOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testAddRealOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testSubtractRealOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testMultiplyRealOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testDivideRealOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testExponentialRealOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testPrecedenceMulOverAdd", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testInstantiatedVariablesInArithmeticOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testLessThanOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testLessThanFails", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testLessThanOrEqualsOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testLessThanOrEqualsFails", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testGreaterThanOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testGreaterThanFails", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testGreaterThanOrEqualsOk", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testGreaterThanOrEqualsFails", engine));
        suite.addTest(new ArithmeticResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testComparisonOfInstantiatedVariablesOk", engine));

        // Add all tests defined in the RuntimeTypeCheckUnitTestBase class.
        suite.addTest(new RuntimeTypeCheckUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testIntegerOk", engine));
        suite.addTest(new RuntimeTypeCheckUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFloatOk", engine));

        // Add all tests defined in the CallAndNotResolverUnitTestBase class.
        suite.addTest(new CallAndNotResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testSimpleCallOk", engine));
        suite.addTest(new CallAndNotResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testCallFunctorWithArgumentBindsVariable", engine));
        suite.addTest(new CallAndNotResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testCallFunctorWithArgumentBindsVariableInChainedCall", engine));
        suite.addTest(new CallAndNotResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNotFunctorWithArgumentOkWhenArgumentsDoNotMatch", engine));
        suite.addTest(new CallAndNotResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNotFunctorWithArgumentDoesNotBindVariableInDoubleNegation", engine));
        suite.addTest(new CallAndNotResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNotFailSucceeds", engine));
        suite.addTest(new CallAndNotResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNotTrueFails", engine));

        // Add all tests defined in the TrueAndFailResolverUnitTestBase class.
        suite.addTest(new TrueAndFailResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testTrueSucceeds", engine));
        suite.addTest(new TrueAndFailResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFailFails", engine));
        suite.addTest(new TrueAndFailResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testDisjunctionOfTrueAndFailSucceeds", engine));
        suite.addTest(new TrueAndFailResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testConjunctionOfTrueAndFailFails", engine));
        suite.addTest(new TrueAndFailResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testConjunctionOfTruesSucceeds", engine));

        // Add all the tests defined in the UnifyAndNonUnifyResolverUnitTestBase class.
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testAtomsUnifyOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNonMatchingAtomsFailUnify", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFreeLeftVarUnifiesAtomOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFreeLeftVarUnifiesFunctorOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFreeRightVarUnifiesAtomOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFreeRightVarUnifiesFunctorOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFreeVarUnifiesWithSameNameOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFreeVarUnifiesWithDifferentNameOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testQueryAtomDoesNotUnifyWithProgFunctorSameName", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testProgAtomDoesNotUnifyWithQueryFunctorSameName", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testBoundVarUnifiesWithDifferentEqualBoundVarOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testBoundVarToFunctorUnifiesWithEqualBoundVarOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testBoundVarFailsToUnifyWithDifferentBinding", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testBoundVarToFunctorFailsToUnifyWithDifferentFunctorBinding", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testProgBoundVarUnifiesWithDifferentEqualBoundVarOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testProgBoundVarToFunctorUnifiesWithEqualBoundVarOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testProgBoundVarFailsToUnifyWithDifferentBinding", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testProgBoundVarToFunctorFailsToUnifyWithDifferentFunctorBinding", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testBoundVarInQueryUnifiesAgainstVarInProg", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testBoundVarFailsToUnifyWithDifferentlyBoundVar", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testBoundVarPropagatesIntoFunctors", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testBoundVarUnifiesToSameVar", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testBoundProgVarUnifiesToDifferentQueryVar", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testBoundQueryVarUnifiesToDifferentProgVar", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFunctorsSameArityUnify", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFunctorsDifferentArityFailToUnify", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFunctorsSameArityDifferentArgsFailToUnify", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testFunctorsDifferentNameSameArgsDoNotUnify", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testEqualNumbersUnifyOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNonEqualNumbersFailToUnify", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testEqualStringsUnifyOk", engine));
        suite.addTest(new UnifyAndNonUnifyResolverUnitTestBase<Clause, PrologCompiledClause, PrologCompiledClause>(
                "testNonEqualStringsFailToUnify", engine));

        return suite;
    }

    protected void setUp()
    {
        NDC.push(getName());
    }

    protected void tearDown()
    {
        NDC.pop();
    }
}
