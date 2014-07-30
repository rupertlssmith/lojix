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

import java.io.PrintStream;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.Parser;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.VariableAndFunctorInternerImpl;
import com.thesett.aima.logic.fol.interpreter.InteractiveParser;
import com.thesett.aima.logic.fol.interpreter.ResolutionInterpreter;
import com.thesett.aima.logic.fol.isoprologparser.Token;

/**
 * PrologInterpreter builds an interactive resolving interpreter using the interpreted Prolog resolution engine
 * {@link PrologEngine}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Create an interpreter for Prolog. <td> {@link PrologEngine}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PrologInterpreter
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(PrologInterpreter.class.getName()); */

    /**
     * Creates the interpreter and launches its top-level run loop.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args)
    {
        try
        {
            VariableAndFunctorInterner interner =
                new VariableAndFunctorInternerImpl("Prolog_Variable_Namespace", "Prolog_Functor_Namespace");
            PrologCompiler compiler = new PrologCompiler(interner);
            PrologResolver resolver = new PrologResolver(interner);
            Parser<Clause, Token> parser = new InteractiveParser(interner);

            PrologEngine engine = new PrologEngine(parser, interner, compiler, resolver);
            engine.reset();

            ResolutionInterpreter<PrologCompiledClause, PrologCompiledClause> interpreter =
                new ResolutionInterpreter<PrologCompiledClause, PrologCompiledClause>(engine);

            interpreter.interpreterLoop();
        }
        catch (Exception e)
        {
            /*log.log(Level.SEVERE, e.getMessage(), e);*/
            e.printStackTrace(new PrintStream(System.err));
            System.exit(-1);
        }
    }
}
