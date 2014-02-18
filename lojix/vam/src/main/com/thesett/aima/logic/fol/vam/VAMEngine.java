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

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.VariableAndFunctorInternerImpl;
import com.thesett.aima.logic.fol.interpreter.ResolutionEngine;
import com.thesett.aima.logic.fol.isoprologparser.SentenceParser;
import com.thesett.aima.logic.fol.isoprologparser.TokenSource;
import com.thesett.aima.logic.fol.vam.vam2p.VAM2PCompiledClause;
import com.thesett.aima.logic.fol.vam.vam2p.VAM2PResolver;
import com.thesett.common.util.doublemaps.SymbolTable;
import com.thesett.common.util.doublemaps.SymbolTableImpl;

/**
 * VAMEngine is a first-order logic engine, built on a VAM machine.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class VAMEngine extends ResolutionEngine<Clause, VAM2PCompiledClause, VAM2PCompiledClause>
{
    /** Holds the symbol table to share accross all compilers and resolution machines. */
    protected SymbolTable<Integer, String, Object> symbolTable;

    /** Creates a VAM execution engine. */
    public VAMEngine()
    {
        symbolTable = new SymbolTableImpl<Integer, String, Object>();

        interner = new VariableAndFunctorInternerImpl("Prolog_Variable_Namespace", "Prolog_Functor_Namespace");
        parser = new SentenceParser(interner);
        parser.setTokenSource(TokenSource.getTokenSourceForInputStream(System.in));

        compiler = new VAMCompiler(symbolTable, interner);

        // Dummy resolver so the compiler will run from the command line for now.
        resolver = new VAM2PResolver(symbolTable, interner);
    }

    /** {@inheritDoc} */
    public void reset()
    {
    }
}
