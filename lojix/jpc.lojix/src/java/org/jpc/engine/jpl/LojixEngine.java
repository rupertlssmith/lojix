package org.jpc.engine.jpl;

import org.jpc.Jpc;
import org.jpc.engine.prolog.AbstractPrologEngine;
import org.jpc.engine.prolog.ThreadModel;
import org.jpc.query.Query;
import org.jpc.term.Term;

public class LojixEngine extends AbstractPrologEngine
{
    public void close()
    {
    }

    public boolean isCloseable()
    {
        return false;
    }

    public ThreadModel threadModel()
    {
        return null;
    }

    public Query basicQuery(Term term, boolean errorHandledQuery, Jpc context)
    {
        return null;
    }

    public Term asTerm(String termString, Jpc context)
    {
        return null;
    }
}
