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
package com.thesett.aima.logic.fol.interpreter;

import java.util.Set;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.Parser;
import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.isoprologparser.Token;
import com.thesett.common.parsing.SourceCodeException;

/**
 * ResolutionInterpreter implements an interactive Prolog like interpreter, built on top of a {@link ResolutionEngine}.
 * It implements a top-level interpreter loop where queries or domain clauses may be entered. Queries are resolved
 * against the current domain using the resolver, after they have been compiled.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Parse text into first order logic clauses. <td> {@link com.thesett.aima.logic.fol.Parser}.
 * <tr><td> Compile clauses down to their compiled form. <td> {@link Compiler}.
 * <tr><td> Add facts to the current knowledge base. <td> {@link com.thesett.aima.logic.fol.Resolver}.
 * <tr><td> Resolve queries against the current knowledge base. <td> {@link com.thesett.aima.logic.fol.Resolver}.
 * <tr><td> Print the variable bindings resulting from resolution.
 *     <td> {@link com.thesett.aima.logic.fol.VariableAndFunctorInterner}.
 * </table></pre>
 *
 * @param  <S> The source clause type that the parser produces.
 * @param  <T> The compiled clause type that the compiler produces.
 *
 * @author Rupert Smith
 */
public class ResolutionInterpreter<T, Q>
{
    /** Used for debugging purposes. */
    private static final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(ResolutionInterpreter.class.getName());

    /** Holds the resolution engine that the interpreter loop runs on. */
    ResolutionEngine<Clause, T, Q> engine;

    /** Holds the interacive parser that the interpreter loop runs on. */
    private final InteractiveParser parser;

    /**
     * Builds an interactive logical resolution interpreter from a parser, interner, compiler and resolver, encapsulated
     * as a resolution engine.
     *
     * @param engine The resolution engine. This must be using an {@link InteractiveParser}.
     */
    public ResolutionInterpreter(ResolutionEngine<Clause, T, Q> engine)
    {
        this.engine = engine;

        Parser<Clause, Token> parser = engine.getParser();

        if (!(parser instanceof InteractiveParser))
        {
            throw new IllegalArgumentException("'engine' must be built on an InteractiveParser.");
        }

        this.parser = (InteractiveParser) parser;
    }

    /**
     * Implements the top-level interpreter loop. This will parse and evaluate sentences until it encounters an EOF at
     * which point the interpreter will terminate.
     *
     * @throws SourceCodeException If malformed code is encountered.
     */
    public void interpreterLoop() throws SourceCodeException
    {
        while (true)
        {
            // Parse the next clause.
            Sentence<Clause> nextParsing = parser.parse();
            log.fine(nextParsing.toString());

            if (nextParsing == null)
            {
                break;
            }

            // Evaluate it in Prolog.
            evaluate(nextParsing);
        }
    }

    /**
     * Evaluates a query against the resolver or adds a clause to the resolvers domain. In the case of queries, the
     * specified interner is used to recover textual names for the resulting variable bindings. The user is queried
     * through the parser to if more than one solution is required.
     *
     * @param  sentence The clausal sentence to run as a query or as a clause to add to the domain.
     *
     * @throws SourceCodeException If the query or domain clause fails to compile or link into the resolver.
     */
    private void evaluate(Sentence<Clause> sentence) throws SourceCodeException
    {
        engine.compile(sentence);

        if (sentence.getT().isQuery())
        {
            boolean foundAtLeastOneSolution = false;

            // Create an iterator to generate all solutions on demand with. Iteration will stop if the request to
            // the parser for the more ';' token fails.
            for (Set<Variable> solution : engine)
            {
                foundAtLeastOneSolution = true;

                for (Variable nextVar : solution)
                {
                    String varName = engine.getVariableName(nextVar.getName());

                    System.out.println(varName + " = " + nextVar.getValue().toString(engine, true, false));
                }

                // Check if the user wants more solutions.
                if (!engine.peekAndConsumeMore())
                {
                    break;
                }
            }

            // Print yes or no depending on whether or not there were some solutions.
            if (foundAtLeastOneSolution)
            {
                System.out.println("Yes.");
            }
            else
            {
                System.out.println("No.");
            }
        }
    }
}
