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
package org.jpc.salt.jpl;

import org.jpc.salt.TermContentHandler;
import org.jpc.salt.TermReader;

public class LojixTermReader extends TermReader
{
    private com.thesett.aima.logic.fol.Term jplTerm;

    public LojixTermReader(com.thesett.aima.logic.fol.Term jplTerm, TermContentHandler contentHandler)
    {
        super(contentHandler);
        this.jplTerm = jplTerm;
    }

    public void read()
    {
        read(jplTerm);
    }

    private void read(com.thesett.aima.logic.fol.Term term)
    {
        /*if (term.isInteger()) {
            com.thesett.aima.logic.fol.Integer jplInteger = (com.thesett.aima.logic.fol.Integer) term;
            getContentHandler().startIntegerTerm(jplInteger.longValue());
        } else if (term.isFloat()) {
            com.thesett.aima.logic.fol.Float jplFloat = (com.thesett.aima.logic.fol.Float) term;
            getContentHandler().startFloatTerm(jplFloat.doubleValue());
        } else if (term.isVar()) {
            getContentHandler().startVariable(term.name());
        } else if (term.isAtom()) {
            getContentHandler().startAtom(term.name());
        } else if (term.isCompound()) {
            getContentHandler().startCompound();
            getContentHandler().startAtom(term.name());
            for (com.thesett.aima.logic.fol.Term child : term.args()) {
                read(child);
            }
            getContentHandler().endCompound();
        } else
            throw new RuntimeException("Unrecognized JPL term: " + term);*/
    }
}
