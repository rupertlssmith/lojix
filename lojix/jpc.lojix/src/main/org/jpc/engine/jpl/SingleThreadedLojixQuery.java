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
package org.jpc.engine.jpl;

import java.util.function.Consumer;

import org.jpc.Jpc;
import org.jpc.engine.prolog.PrologEngine;
import org.jpc.query.PrologQuery;
import org.jpc.query.Solution;
import org.jpc.term.Term;

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

    public void forEachRemaining(Consumer<? super Solution> action)
    {
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
}