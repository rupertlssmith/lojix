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
package com.thesett.aima.logic.fol.vam;

import java.io.PrintStream;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.interpreter.ResolutionInterpreter;
import com.thesett.aima.logic.fol.vam.vam2p.VAM2PCompiledClause;

/**
 * VAMInterpreter builds an interactive resolving interpreter using the compiled Prolog resolution engine
 * {@link VAMEngine}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Create a compiler and interpreter for Prolog. <td> {@link VAMEngine}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAMInterpreter
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(VAMInterpreter.class.getName()); */

    /**
     * Creates the interpreter and launches its top-level run loop.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args)
    {
        try
        {
            VAMEngine engine = new VAMEngine();
            engine.reset();

            ResolutionInterpreter<Clause, VAM2PCompiledClause, VAM2PCompiledClause> interpreter =
                new ResolutionInterpreter<Clause, VAM2PCompiledClause, VAM2PCompiledClause>(engine);

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
