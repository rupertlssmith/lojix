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
package com.thesett.aima.logic.fol.prolog;

import com.thesett.aima.logic.fol.LogicCompiler;
import com.thesett.aima.logic.fol.LogicCompilerObserver;
import com.thesett.aima.logic.fol.Parser;
import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Unifier;
import com.thesett.aima.logic.fol.UnifierUnitTestBase;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.VariableAndFunctorInternerImpl;
import com.thesett.aima.logic.fol.isoprologparser.TermParser;
import com.thesett.aima.logic.fol.isoprologparser.Token;
import com.thesett.aima.logic.fol.prolog.builtins.PrologUnifier;
import com.thesett.common.parsing.SourceCodeException;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the {@link PrologUnifier} class.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PrologUnifierTest extends UnifierUnitTestBase<Term, Term>
{
    /** Used for debugging. */
    java.util.logging.Logger log = java.util.logging.Logger.getLogger(PrologUnifierTest.class.getName());

    public PrologUnifierTest(String name, Unifier unifier, LogicCompiler compiler, Parser parser, Parser qparser,
        VariableAndFunctorInterner interner)
    {
        super(name, unifier, compiler, parser, qparser, interner);
    }

    /** Compile all the tests for the default test for unifiers into a suite, plus the tests defined in this class. */
    public static Test suite()
    {
        // Build a new test suite
        TestSuite suite = new TestSuite("PrologUnifier Tests");

        // Create a no-op compiler.
        LogicCompiler<Term, Term, Term> compiler =
            new LogicCompiler<Term, Term, Term>()
            {
                /** Holds the compiler output observer. */
                LogicCompilerObserver<Term, Term> observer;

                /** {@inheritDoc} */
                public void compile(Sentence<Term> sentence) throws SourceCodeException
                {
                    observer.onCompilation(sentence);
                }

                /** {@inheritDoc} */
                public void setCompilerObserver(LogicCompilerObserver<Term, Term> observer)
                {
                    this.observer = observer;
                }

                /** {@inheritDoc} */
                public void endScope()
                {
                }
            };

        VariableAndFunctorInternerImpl interner =
            new VariableAndFunctorInternerImpl("PrologUnifierTest_Variable_Namespace",
                "PrologUnifierTest_Functor_Namespace");

        Parser<Term, Token> parser = new TermParser(interner);

        // Add all tests defined in the ClassifyingMachineUnitTestBase class
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testAtomsUnifyOk", new PrologUnifier(), compiler, parser,
                parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testNonMatchingAtomsFailUnify", new PrologUnifier(),
                compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testFreeLeftVarUnifiesAtomOk", new PrologUnifier(), compiler,
                parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testFreeLeftVarUnifiesFunctorOk", new PrologUnifier(),
                compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testFreeRightVarUnifiesAtomOk", new PrologUnifier(),
                compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testFreeRightVarUnifiesFunctorOk", new PrologUnifier(),
                compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testFreeVarUnifiesWithSameNameOk", new PrologUnifier(),
                compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testFreeVarUnifiesWithDifferentNameOk", new PrologUnifier(),
                compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testQueryAtomDoesNotUnifyWithProgFunctorSameName",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testProgAtomDoesNotUnifyWithQueryFunctorSameName",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testBoundVarUnifiesWithDifferentEqualBoundVarOk",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testBoundVarToFunctorUnifiesWithEqualBoundVarOk",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testBoundVarFailsToUnifyWithDifferentBinding",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>(
                "testBoundVarToFunctorFailsToUnifyWithDifferentFunctorBinding", new PrologUnifier(), compiler, parser,
                parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testProgBoundVarUnifiesWithDifferentEqualBoundVarOk",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testProgBoundVarToFunctorUnifiesWithEqualBoundVarOk",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testProgBoundVarFailsToUnifyWithDifferentBinding",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>(
                "testProgBoundVarToFunctorFailsToUnifyWithDifferentFunctorBinding", new PrologUnifier(), compiler,
                parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testBoundVarInQueryUnifiesAgainstVarInProg",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testBoundVarFailsToUnifyWithDifferentlyBoundVar",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testBoundVarPropagatesIntoFunctors", new PrologUnifier(),
                compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testBoundVarUnifiesToSameVar", new PrologUnifier(), compiler,
                parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testBoundProgVarUnifiesToDifferentQueryVar",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testBoundQueryVarUnifiesToDifferentProgVar",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testFunctorsSameArityUnify", new PrologUnifier(), compiler,
                parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testFunctorsDifferentArityFailToUnify", new PrologUnifier(),
                compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testFunctorsSameArityDifferentArgsFailToUnify",
                new PrologUnifier(), compiler, parser, parser, interner));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testFunctorsDifferentNameSameArgsDoNotUnify",
                new PrologUnifier(), compiler, parser, parser, interner));

        /*suite.addTest(new UnifierUnitTestBase<Term, Term>("testEqualNumbersUnifyOk", new PrologUnifier(), compiler,
                                                          parser, parser));
        suite.addTest(new UnifierUnitTestBase<Term, Term>("testNonEqualNumbersFailToUnify", new PrologUnifier(),
                                                          compiler, parser, parser));*/

        // Add all the tests defined in this class.
        suite.addTest(new PrologUnifierTest("testEqualNumbersUnify", new PrologUnifier(), compiler, parser, parser,
                interner));
        suite.addTest(new PrologUnifierTest("testNonEqualNumbersDoNotUnify", new PrologUnifier(), compiler, parser,
                parser, interner));
        suite.addTest(new PrologUnifierTest("testNumbersDoNotUnifyWithFunctors", new PrologUnifier(), compiler, parser,
                parser, interner));

        return suite;
    }

    /** Check that equal numbers unify. */
    public void testEqualNumbersUnify() throws Exception
    {
        unifyAndAssertNumBindings("1", "1", 0);
    }

    /** Check that non-equal numbers do not unify. */
    public void testNonEqualNumbersDoNotUnify() throws Exception
    {
        unifyAndAssertFailure("2", "1");
    }

    /** Check that numbers do not unify with functors. */
    public void testNumbersDoNotUnifyWithFunctors() throws Exception
    {
        unifyAndAssertFailure("f", "1");
    }

    protected void setUp()
    {
        unifier = new PrologUnifier();
    }
}
