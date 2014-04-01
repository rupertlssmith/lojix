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
package com.thesett.aima.logic.fol.wam.machine;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.LogicCompiler;
import com.thesett.aima.logic.fol.Parser;
import com.thesett.aima.logic.fol.Resolver;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.interpreter.ResolutionEngine;
import com.thesett.aima.logic.fol.isoprologparser.Token;
import com.thesett.aima.logic.fol.wam.compiler.WAMCompiledPredicate;
import com.thesett.aima.logic.fol.wam.compiler.WAMCompiledQuery;

/**
 * WAMEngine implements a {@link ResolutionEngine} for an WAM-based Prolog with built-ins. This engine loads its
 * standard library of built-ins from a resource on the classpath.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Load the Prolog built-in library from a resource on the classpath when the engine is reset.
 * </table></pre>
 *
 * @author Rupert Smith
 */
class WAMEngine extends ResolutionEngine<Clause, WAMCompiledPredicate, WAMCompiledQuery>
{
    /**
     * Builds an logical resolution engine from a parser, interner, compiler and resolver.
     *
     * @param parser   The parser.
     * @param interner The interner.
     * @param compiler The compiler.
     * @param resolver The resolver.
     */
    public WAMEngine(Parser<Clause, Token> parser, VariableAndFunctorInterner interner,
        LogicCompiler<Clause, WAMCompiledPredicate, WAMCompiledQuery> compiler,
        Resolver<WAMCompiledPredicate, WAMCompiledQuery> resolver)
    {
        super(parser, interner, compiler, resolver);
    }

    /** {@inheritDoc} */
    public void reset()
    {
        resolver.reset();
    }
}
