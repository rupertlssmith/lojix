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
package com.thesett.aima.logic.fol.jpc.salt;

import com.thesett.aima.logic.fol.FloatLiteral;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.IntLiteral;
import com.thesett.aima.logic.fol.NumericType;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;

import org.jpc.salt.TermContentHandler;
import org.jpc.salt.TermReader;

public class LojixTermReader extends TermReader
{
    /** The interner used to de-intern all names. */
    private final VariableAndFunctorInterner interner;

    /** The Lojix term to be translated to a JPC term. */
    private Term lojixTerm;

    public LojixTermReader(Term jplTerm, TermContentHandler contentHandler, VariableAndFunctorInterner interner)
    {
        super(contentHandler);
        this.lojixTerm = jplTerm;
        this.interner = interner;
    }

    public void read()
    {
        read(lojixTerm);
    }

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
            throw new RuntimeException("Unrecognized Lojix term: " + term);
        }
    }
}
