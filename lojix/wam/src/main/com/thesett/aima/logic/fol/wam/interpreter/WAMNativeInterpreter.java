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
package com.thesett.aima.logic.fol.wam.interpreter;

import java.io.PrintStream;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.LogicCompiler;
import com.thesett.aima.logic.fol.Parser;
import com.thesett.aima.logic.fol.interpreter.ResolutionEngine;
import com.thesett.aima.logic.fol.interpreter.ResolutionInterpreter;
import com.thesett.aima.logic.fol.isoprologparser.SentenceParser;
import com.thesett.aima.logic.fol.isoprologparser.Token;
import com.thesett.aima.logic.fol.isoprologparser.TokenSource;
import com.thesett.aima.logic.fol.wam.WAMCompiledPredicate;
import com.thesett.aima.logic.fol.wam.WAMCompiledQuery;
import com.thesett.aima.logic.fol.wam.WAMCompiler;
import com.thesett.aima.logic.fol.wam.WAMResolvingMachine;
import com.thesett.aima.logic.fol.wam.nativemachine.WAMResolvingNativeMachine;
import com.thesett.common.util.doublemaps.SymbolTableImpl;

/**
 * PrologInterpreter builds an interactive resolving interpreter using the interpreted resolution engine
 * {@link WAMResolvingNativeMachine}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Create an interpreter for WAM.
 *     <td> {@link SentenceParser}, {@link WAMCompiler}, {@link WAMResolvingNativeMachine}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class WAMNativeInterpreter
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(WAMNativeInterpreter.class.getName()); */

    /**
     * Creates the interpreter and launches its top-level run loop.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args)
    {
        try
        {
            SymbolTableImpl<Integer, String, Object> symbolTable = new SymbolTableImpl<Integer, String, Object>();

            final WAMResolvingMachine machine = new WAMResolvingNativeMachine(symbolTable);

            Parser<Clause, Token> parser = new SentenceParser(machine);
            parser.setTokenSource(TokenSource.getTokenSourceForInputStream(System.in));

            LogicCompiler<Clause, WAMCompiledPredicate, WAMCompiledQuery> compiler =
                new WAMCompiler(symbolTable, machine);

            ResolutionEngine<Clause, WAMCompiledPredicate, WAMCompiledQuery> engine =
                new ResolutionEngine<Clause, WAMCompiledPredicate, WAMCompiledQuery>(parser, machine, compiler, machine)
                {
                    public void reset()
                    {
                        machine.reset();
                    }
                };

            ResolutionInterpreter<WAMCompiledPredicate, WAMCompiledQuery> interpreter =
                new ResolutionInterpreter<WAMCompiledPredicate, WAMCompiledQuery>(engine);

            interpreter.interpreterLoop();
        }
        catch (Exception e)
        {
            /*log.log(java.util.logging.Level.SEVERE, e.getMessage(), e);*/
            e.printStackTrace(new PrintStream(System.err));
            System.exit(-1);
        }
    }
}
