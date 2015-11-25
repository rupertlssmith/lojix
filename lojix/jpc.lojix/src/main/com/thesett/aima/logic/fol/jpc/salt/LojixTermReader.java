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
package com.thesett.aima.logic.fol.jpc.salt;

import org.jpc.salt.TermContentHandler;
import org.jpc.salt.TermReader;

import com.thesett.aima.logic.fol.FloatLiteral;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.IntLiteral;
import com.thesett.aima.logic.fol.NumericType;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;

/**
 * LojixTermReader is a {@link TermReader} for Lojix terms. It reads a Lojix term and describes its contents and
 * structure to a {@link TermContentHandler}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th><th> Collaborations </th>
 * <tr><td> Read Lojix terms and describe their structure/content to a content handler. </td>
 *     <td> {@link TermContentHandler} </td></tr>
 * </table></pre>
 */
public class LojixTermReader extends TermReader
{
    /** The interner used to de-intern all names. */
    private final VariableAndFunctorInterner interner;

    /** The Lojix term to be translated to a JPC term. */
    private Term readTerm;

    /**
     * Creates a Lojix term read.
     *
     * @param readTerm       The Lojix term to read.
     * @param contentHandler The content handler to visit the term with.
     * @param interner       The Lojix name interner to de-intern names with.
     */
    public LojixTermReader(Term readTerm, TermContentHandler contentHandler, VariableAndFunctorInterner interner)
    {
        super(contentHandler);
        this.readTerm = readTerm;
        this.interner = interner;
    }

    /** {@inheritDoc} */
    public void read()
    {
        read(readTerm);
    }

    /**
     * Reads a Lojix term and invoked appropriate methods on the content handler to describe its structure and contents
     * to it.
     *
     * @param term The Lojix term to read.
     */
    private void read(Term term)
    {
        if (term.isNumber())
        {
            NumericType numericType = (NumericType) term;

            if (numericType.isInteger())
            {
                IntLiteral jplInteger = (IntLiteral) term;
                getContentHandler().startIntegerTerm(jplInteger.longValue());
            }
            else if (numericType.isFloat())
            {
                FloatLiteral jplFloat = (FloatLiteral) term;
                getContentHandler().startFloatTerm(jplFloat.doubleValue());
            }
        }
        else if (term.isVar())
        {
            Variable var = (Variable) term;
            getContentHandler().startVariable(interner.getVariableName(var.getName()));
        }
        else if (term.isAtom())
        {
            Functor atom = (Functor) term;
            getContentHandler().startAtom(interner.getFunctorName(atom.getName()));
        }
        else if (term.isCompound())
        {
            Functor functor = (Functor) term;
            getContentHandler().startCompound();
            getContentHandler().startAtom(interner.getFunctorName(functor.getName()));

            for (com.thesett.aima.logic.fol.Term child : functor.getArguments())
            {
                read(child);
            }

            getContentHandler().endCompound();
        }
        else
        {
            throw new IllegalStateException("Unrecognized Lojix term: " + term);
        }
    }
}
