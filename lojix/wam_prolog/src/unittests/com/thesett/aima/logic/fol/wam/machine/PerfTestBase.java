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
package com.thesett.aima.logic.fol.wam.machine;

import java.util.Set;

import com.thesett.aima.logic.fol.BasicResolverUnitTestBase;
import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.Sentence;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.interpreter.ResolutionEngine;
import com.thesett.aima.logic.fol.isoprologparser.TokenSource;
import com.thesett.common.parsing.SourceCodeException;

public class PerfTestBase<S extends Clause, T, Q> extends BasicResolverUnitTestBase<S, T, Q>
{
    public PerfTestBase(String name, ResolutionEngine<S, T, Q> engine)
    {
        super(name, engine);
    }

    public void testNRev() throws Exception
    {
        engine.reset();

        addClause("nrev([], [])");
        addClause("nrev([X|Rest], Ans) :- nrev(Rest, L), append(L, [X], Ans)");
        addClause("donrev :- " + "nrev([a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, " +
            "a16, a17, a18, a19, a20, a21, a22, a23, a24, a25, a26, a27, a28, a29, a30],_)");

        for (int j = 0; j < 100; j++)
        {
            long start = System.currentTimeMillis();

            for (int i = 0; i < 1000; i++)
            {
                engine.endScope();

                setQuery("?- donrev.");

                for (Set<Variable> solution : engine)
                {
                }
            }

            long end = System.currentTimeMillis();

            System.out.println("1000 iterations in " + (end - start) + " millis.");
        }
    }

    private void setQuery(String queryString)
    {
        engine.setTokenSource(TokenSource.getTokenSourceForString(queryString));

        try
        {
            engine.compile(engine.parse());
        }
        catch (SourceCodeException e)
        {
            // If the query fails to parse or link, then this is a non-recoverable bug, so is reported as a runtime
            // exception.
            throw new RuntimeException("The query, " + queryString + ", failed to compile.", e);
        }
    }

    private void addClause(String termText)
    {
        // Parse the instance clause into a prolog clause and add it to the list of clauses.
        parser.setTokenSource(TokenSource.getTokenSourceForString(termText));

        try
        {
            Sentence<S> sentence = parser.parse();
            engine.compile(sentence);
        }
        catch (SourceCodeException e)
        {
            throw new RuntimeException("Badly formed typedef conversion to logical term.", e);
        }
    }
}
