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
package com.thesett.aima.logic.fol.jpc.salt.jpl;

import org.jpc.salt.TermBuilder;
import org.jpc.salt.TermContentHandler;
import org.jpc.salt.TermWriter;

public class LojixTermWriter extends TermWriter<com.thesett.aima.logic.fol.Term>
{
    public TermContentHandler startIntegerTerm(long value)
    {
        return this;
    }

    public TermContentHandler startFloatTerm(double value)
    {
        return this;
    }

    public TermContentHandler startVariable(String name)
    {
        return this;
    }

    public TermContentHandler startAtom(String name)
    {
        return this;
    }

    public TermContentHandler startJRef(Object ref)
    {
        throw new UnsupportedOperationException();
    }

    protected TermBuilder<com.thesett.aima.logic.fol.Term> createCompoundBuilder()
    {
        return null;
    }
}
