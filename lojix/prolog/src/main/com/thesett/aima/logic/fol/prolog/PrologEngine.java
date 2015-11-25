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

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.LogicCompiler;
import com.thesett.aima.logic.fol.Parser;
import com.thesett.aima.logic.fol.Resolver;
import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.VariableAndFunctorInternerImpl;
import com.thesett.aima.logic.fol.interpreter.ResolutionEngine;
import com.thesett.aima.logic.fol.isoprologparser.SentenceParser;
import com.thesett.aima.logic.fol.isoprologparser.Token;
import com.thesett.aima.logic.fol.isoprologparser.TokenSource;
import com.thesett.common.parsing.SourceCodeException;
import com.thesett.common.util.Source;

/**
 * PrologEngine implements a {@link ResolutionEngine} for an interpreted prolog with built-ins. This engine loads its
 * built-ins from a resource on the classpath.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Load the prolog built-in library from a resource on the classpath when the engine is reset.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PrologEngine extends ResolutionEngine<Clause, PrologCompiledClause, PrologCompiledClause>
{
    /** Holds the name of the resource on the classpath that contains the built-in library. */
    private static final String BUILT_IN_LIB = "builtins.pl";

    /**
     * Creates a resolution engine for the prolog interpreter, allowing the default components to be overriden with a
     * specified set of components.
     *
     * @param parser   The parser to use.
     * @param interner The interner to use.
     * @param compiler The compiler to use.
     * @param resolver The resolver to use.
     */
    public PrologEngine(Parser<Clause, Token> parser, VariableAndFunctorInterner interner,
        LogicCompiler<Clause, PrologCompiledClause, PrologCompiledClause> compiler,
        Resolver<PrologCompiledClause, PrologCompiledClause> resolver)
    {
        super(parser, interner, compiler, resolver);
        parser.setTokenSource(TokenSource.getTokenSourceForInputStream(System.in));
    }

    /** Creates a resolution engine for the prolog interpreter. */
    public PrologEngine()
    {
        interner = new VariableAndFunctorInternerImpl("Prolog_Variable_Namespace", "Prolog_Functor_Namespace");
        parser = new SentenceParser(interner);
        compiler = new PrologCompiler(interner);
        resolver = new PrologResolver(interner);
        parser.setTokenSource(TokenSource.getTokenSourceForInputStream(System.in));
        compiler.setCompilerObserver(chainedObserver);
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Loads the built-in library resource specified by {@link #BUILT_IN_LIB}.
     */
    public void reset()
    {
        // Reset the resolver to completely clear out its domain.
        resolver.reset();

        // Create a token source to load the model rules from.
        Source<Token> tokenSource =
            TokenSource.getTokenSourceForInputStream(PrologEngine.class.getClassLoader().getResourceAsStream(
                    BUILT_IN_LIB));

        // Set up a parser on the token source.
        Parser<Clause, Token> libParser = new SentenceParser(interner);
        libParser.setTokenSource(tokenSource);

        // Load the built-ins into the domain.
        try
        {
            while (true)
            {
                Sentence<Clause> sentence = libParser.parse();

                if (sentence == null)
                {
                    break;
                }

                compiler.compile(sentence);
            }
        }
        catch (SourceCodeException e)
        {
            // There should not be any errors in the built in library, if there are then the prolog engine just
            // isn't going to work, so report this as a bug.
            throw new IllegalStateException("Got an exception whilst loading the built-in library.", e);
        }
    }
}
