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
package com.thesett.aima.logic.fol;

import java.util.Iterator;
import java.util.LinkedList;

import com.thesett.aima.search.Operator;
import com.thesett.common.util.StackQueue;

/**
 * A predicate is a collection of clauses, that describe the logical truth of the predicate.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Capture a disjunction of clauses into a predicate.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Predicate<T extends Clause> extends BaseTerm implements Term
{
    /** The clauses that make up this predicate. */
    protected T[] body;

    /**
     * Creates a predicate formed from a set of clauses.
     *
     * @param body The clauses that make up the body of the predicate.
     */
    public Predicate(T[] body)
    {
        this.body = body;
    }

    /** {@inheritDoc} */
    public Term getValue()
    {
        return this;
    }

    /**
     * Gets all of the clauses that make up the body of the predicate.
     *
     * @return All of the clauses that make up the body of the predicate.
     */
    public T[] getBody()
    {
        return body;
    }

    /** {@inheritDoc} */
    public void free()
    {
    }

    /** {@inheritDoc} */
    public Iterator<Operator<Term>> getChildren(boolean reverse)
    {
        if ((traverser != null) && (traverser instanceof PredicateTraverser))
        {
            return ((PredicateTraverser) traverser).traverse(this, reverse);
        }
        else
        {
            LinkedList<Operator<Term>> resultList = null;

            if (!reverse)
            {
                resultList = new LinkedList<Operator<Term>>();
            }
            else
            {
                resultList = new StackQueue<Operator<Term>>();
            }

            if (body != null)
            {
                for (Term bodyTerm : body)
                {
                    resultList.add(bodyTerm);
                }
            }

            return resultList.iterator();
        }
    }

    /** {@inheritDoc} */
    public void accept(TermVisitor visitor)
    {
        if (visitor instanceof PredicateVisitor)
        {
            ((PredicateVisitor) visitor).visit(this);
        }
        else
        {
            super.accept(visitor);
        }
    }

    /** {@inheritDoc} */
    public String toString(VariableAndFunctorInterner interner, boolean printVarName, boolean printBindings)
    {
        String result = "";

        if (body != null)
        {
            for (int i = 0; i < body.length; i++)
            {
                result +=
                    body[i].toString(interner, printVarName, printBindings) + ((i < (body.length - 1)) ? "\n" : "");
            }
        }

        return result;
    }

    /** {@inheritDoc} */
    public String toString()
    {
        String bodyString = "[";

        if (body != null)
        {
            for (int i = 0; i < body.length; i++)
            {
                bodyString += body[i].toString() + ((i < (body.length - 1)) ? ", " : "");
            }
        }

        bodyString += "]";

        return "Clause: [ body = " + bodyString + " ]";
    }
}
