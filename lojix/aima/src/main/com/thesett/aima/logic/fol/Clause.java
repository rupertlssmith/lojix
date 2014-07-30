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
 * A clause is a composition of functors in first order logic. A clause consists of a head, which is a single functor,
 * and a body, which is a conjunction of functors. If the body is true (provable), then so is the head.
 *
 * <p/>A query is also modelled as a clause, but one with no head. If the body of a query is true, then the query is
 * true. The proof procedure will produce a list of bindings of variables in the queries body, and these bindings
 * provide a set of conditions under which the query is true.
 *
 * <p/>Arbitrary disjunctions, negations and conjunctions of functors in first order logic, may be normalized into 'Horn
 * clauses' and this Clause implementation is a Horn clause. A Horn clause is a logical implication with a single
 * implied 'fact' and a body consisting of a conjunction of conditions that prove the fact.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Capture an optional head and optioanl body sequence of functors as a Horn clause.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Clause<T extends Functor> extends BaseTerm implements Term
{
    /** Holds the conjuctive list of functors that make up the query or program body. */
    protected T[] body;

    /** Holds the functor that makes up the matching head of a program. */
    protected T head;

    /**
     * Creates a program sentence in L2.
     *
     * @param head The head of the program.
     * @param body The functors that make up the query body of the program, if any. May be <tt>null</tt>
     */
    public Clause(T head, T[] body)
    {
        this.head = head;
        this.body = body;
    }

    /**
     * Gets all of the functors that make up the query body of the program.
     *
     * @return All of the functors that make up the query body of the program.
     */
    public T[] getBody()
    {
        return body;
    }

    /**
     * Gets the functor that forms the programs head.
     *
     * @return The functor that forms the programs head.
     */
    public T getHead()
    {
        return head;
    }

    /**
     * Reports whether or not this sentence is a query. A sentence is a query if it has no head.
     *
     * @return <tt>true</tt> if this sentence is a query.
     */
    public boolean isQuery()
    {
        return head == null;
    }

    /**
     * Reports whether or not this term is compound (a functor of arity one or more).
     *
     * @return <tt>true</tt> if this term is compound, <tt>fals</tt> otherwise.
     */
    public boolean isCompound()
    {
        return true;
    }

    /**
     * Gets the actual value of a term, which is either the term itself, or in the case of variables, the value that is
     * currently assigned to the variable.
     *
     * @return The term itself, or the assigned value for variables.
     */
    public Term getValue()
    {
        return this;
    }

    /** Frees all assigned variables in the term, leaving them unnassigned. */
    public void free()
    {
    }

    /**
     * Provides an iterator over the child terms, if there are any. Only functors and clauses are compound, and build
     * across a list of child arguments.
     *
     * @param  reverse Set, if the children should be presented in reverse order.
     *
     * @return The sub-terms of a compound term.
     */
    public Iterator<Operator<Term>> getChildren(boolean reverse)
    {
        if ((traverser != null) && (traverser instanceof ClauseTraverser))
        {
            return ((ClauseTraverser) traverser).traverse(this, reverse);
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

            if (head != null)
            {
                resultList.add(head);
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

    /**
     * Makes a clone of the term, converting its variables to refer directly to their storage cells.
     *
     * @return A copy of this term, with entirely independent variables to the term it was copied from.
     */
    public Clause queryConversion()
    {
        Clause copy = (Clause) super.queryConversion();

        if (head != null)
        {
            copy.head = head.queryConversion();
        }

        if (body != null)
        {
            copy.body = new Functor[body.length];

            for (int i = 0; i < body.length; i++)
            {
                copy.body[i] = body[i].queryConversion();
            }
        }

        return copy;
    }

    /** {@inheritDoc} */
    public void accept(TermVisitor visitor)
    {
        if (visitor instanceof ClauseVisitor)
        {
            ((ClauseVisitor) visitor).visit(this);
        }
        else
        {
            super.accept(visitor);
        }
    }

    /** {@inheritDoc} */
    public Clause acceptTransformer(TermTransformer transformer)
    {
        Clause result;

        if (transformer instanceof ClauseTransformer)
        {
            result = ((ClauseTransformer) transformer).transform(this);
        }
        else
        {
            result = (Clause) super.acceptTransformer(transformer);
        }

        if (head != null)
        {
            result.head = (Functor) head.acceptTransformer(transformer);
        }

        if (body != null)
        {
            for (int i = 0; i < body.length; i++)
            {
                result.body[i] = (Functor) body[i].acceptTransformer(transformer);
            }
        }

        return result;
    }

    /**
     * Outputs this sentence as a string, mainly for debugging purposes.
     *
     * @return This sentence as a string, mainly for debugging purposes.
     */
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

        return "Clause: [ head = " + head + ", body = " + bodyString + " ]";
    }

    /** {@inheritDoc} */
    public String toString(VariableAndFunctorInterner interner, boolean printVarName, boolean printBindings)
    {
        String result = "";

        if (head != null)
        {
            result += head.toString(interner, printVarName, printBindings);
        }

        if (body != null)
        {
            result += isQuery() ? "?- " : " :- ";

            for (int i = 0; i < body.length; i++)
            {
                result +=
                    body[i].toString(interner, printVarName, printBindings) + ((i < (body.length - 1)) ? ", " : "");
            }
        }

        return result;
    }
}
