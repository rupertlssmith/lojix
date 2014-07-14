package org.jpc.engine.jpl;

import org.jpc.Jpc;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.query.PrologQuery;
import org.jpc.query.Solution;
import org.jpc.term.Term;

import java.util.function.Consumer;

public class SingleThreadedLojixQuery extends PrologQuery
{
    public SingleThreadedLojixQuery(PrologEngine prologEngine, Term goal, boolean errorHandledQuery, Jpc context)
    {
        super(prologEngine, goal, errorHandledQuery, context);
    }

    public boolean isAbortable()
    {
        return false;
    }

    protected void basicAbort()
    {
    }

    protected void basicClose()
    {
    }

    protected Solution basicNext()
    {
        return null;
    }

    public void forEachRemaining(Consumer<? super Solution> action)
    {
    }
}