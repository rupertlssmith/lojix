package org.jpc.salt.jpl;

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
