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

import com.thesett.aima.logic.fol.VariableAndFunctorInterner;

import org.jpc.salt.TermBuilder;
import org.jpc.salt.TermContentHandler;
import org.jpc.salt.TermWriter;

public class LojixTermWriter extends TermWriter<com.thesett.aima.logic.fol.Term>
{
    /** The interner used to intern all names. */
    private final VariableAndFunctorInterner interner;

    /** A term builder to assist term construction. */
    private final com.thesett.aima.logic.fol.builder.TermBuilder tb;

    public LojixTermWriter(VariableAndFunctorInterner interner)
    {
        this.interner = interner;
        tb = new com.thesett.aima.logic.fol.builder.TermBuilder(interner);

    }

    /** {@inheritDoc} */
    public TermContentHandler startIntegerTerm(long value)
    {
        process(tb.integer((int) value));

        return this;
    }

    /** {@inheritDoc} */
    public TermContentHandler startFloatTerm(double value)
    {
        process(tb.real((float) value));

        return this;
    }

    /** {@inheritDoc} */
    public TermContentHandler startVariable(String name)
    {
        process(tb.var(name));

        return this;
    }

    /** {@inheritDoc} */
    public TermContentHandler startAtom(String name)
    {
        process(tb.atom(name));

        return this;
    }

    /** {@inheritDoc} */
    public TermContentHandler startJRef(Object ref)
    {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    protected TermBuilder<com.thesett.aima.logic.fol.Term> createCompoundBuilder()
    {
        return new LojixTermBuilder(interner);
    }
}
