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
package com.thesett.aima.logic.fol.wam.debugger;

import java.io.IOException;

import com.thesett.aima.logic.fol.BasicUnificationUnitTestBase;
import com.thesett.aima.logic.fol.ConjunctionResolverUnitTestBase;
import com.thesett.aima.logic.fol.DisjunctionResolverUnitTestBase;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.NDC;

import com.thesett.aima.logic.fol.CallAndNotResolverUnitTestBase;
import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.LogicCompiler;
import com.thesett.aima.logic.fol.Parser;
import com.thesett.aima.logic.fol.interpreter.ResolutionEngine;
import com.thesett.aima.logic.fol.isoprologparser.ClauseParser;
import com.thesett.aima.logic.fol.isoprologparser.Token;
import com.thesett.aima.logic.fol.wam.compiler.WAMCompiledPredicate;
import com.thesett.aima.logic.fol.wam.compiler.WAMCompiledQuery;
import com.thesett.aima.logic.fol.wam.compiler.WAMCompiler;
import com.thesett.aima.logic.fol.wam.debugger.controller.TopLevelStandaloneController;
import com.thesett.aima.logic.fol.wam.debugger.monitor.MachineMonitor;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactoryBuilder;
import com.thesett.aima.logic.fol.wam.machine.WAMEngine;
import com.thesett.aima.logic.fol.wam.machine.WAMResolvingJavaMachine;
import com.thesett.aima.logic.fol.wam.machine.WAMResolvingMachine;
import com.thesett.common.util.doublemaps.SymbolTableImpl;

public class DebuggerTest extends TestCase
{
    /** Holds the WAM machine to run the tests through. */
    private static WAMResolvingMachine machine;

    /**
     * Creates a test with the specified name.
     *
     * @param name The name of the test.
     */
    public DebuggerTest(String name)
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
        // Create a machine to debug.
        SymbolTableImpl<Integer, String, Object> symbolTable = new SymbolTableImpl<Integer, String, Object>();
        machine = new WAMResolvingJavaMachine(symbolTable);

        LogicCompiler<Clause, WAMCompiledPredicate, WAMCompiledQuery> compiler = new WAMCompiler(symbolTable, machine);
        Parser<Clause, Token> parser = new ClauseParser(machine);

        ResolutionEngine<Clause, WAMCompiledPredicate, WAMCompiledQuery> engine =
            new WAMEngine(parser, machine, compiler, machine);

        // Attach the debugger to the machine.
        final TopLevelStandaloneController controller =
            new TopLevelStandaloneController(ComponentFactoryBuilder.createComponentFactory(
                    ComponentFactoryBuilder.SWING_FACTORY));
        controller.open();

        MachineMonitor monitor = controller.getMachineMonitor();

        machine.attachMonitor(monitor);

        // Register a shutdown hook to keep the window open at the end of the tests.
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            System.in.read();
                        }
                        catch (IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                }));

        // Build a new test suite.
        TestSuite suite = new TestSuite("WAMResolvingJavaMachine Tests");

        /*suite.addTest(new DisjunctionResolverUnitTestBase<Clause, WAMCompiledPredicate, WAMCompiledQuery>(
                "testJunctionBracketingFalse", engine));*/
        /*suite.addTest(new DisjunctionResolverUnitTestBase<Clause, WAMCompiledPredicate, WAMCompiledQuery>(
                "testJunctionBracketingAllowsDisjunction", engine));*/
        /*suite.addTest(new ConjunctionResolverUnitTestBase<Clause, WAMCompiledPredicate, WAMCompiledQuery>(
                "testConjunctionInQueryVarUnifyNotLastFailsWhenBindingsDoNotMatch", engine));*/
        suite.addTest(new BasicUnificationUnitTestBase<Clause, WAMCompiledPredicate, WAMCompiledQuery>(
                "testFreeLeftVarUnifiesFunctorOk", engine));

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
