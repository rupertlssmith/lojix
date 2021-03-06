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
package com.thesett.aima.logic.fol.jpc.engine;

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
