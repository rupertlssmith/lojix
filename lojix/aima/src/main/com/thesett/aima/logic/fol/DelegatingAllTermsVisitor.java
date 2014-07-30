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

/**
 * DelegatingAllTermsVisitor is an {@link AllTermsVisitor} that accepts an optional delegate. All visit operations by
 * default defer onto the delegate.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Visit a term.
 * <tr><td> Visit a clause.
 * <tr><td> Visit a functor.
 * <tr><td> Visit a variable.
 * <tr><td> Visit a literal.
 * <tr><td> Visit an integer literal.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DelegatingAllTermsVisitor implements AllTermsVisitor
{
    /** The optional delegate. */
    protected AllTermsVisitor delegate;

    /**
     * Creates an AllTermsVisitor that by default delegates all visit operations to the specified delegate.
     *
     * @param delegate The delegate, may be <tt>null</tt> if none is to be used.
     */
    public DelegatingAllTermsVisitor(AllTermsVisitor delegate)
    {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    public void visit(Predicate predicate)
    {
        if (delegate != null)
        {
            delegate.visit(predicate);
        }
    }

    /** {@inheritDoc} */
    public void visit(Term term)
    {
        if (delegate != null)
        {
            //term.accept(delegate);
            delegate.visit(term);
        }
    }

    /** {@inheritDoc} */
    public void visit(Clause clause)
    {
        if (delegate != null)
        {
            //clause.accept(delegate);
            delegate.visit(clause);
        }
    }

    /** {@inheritDoc} */
    public void visit(Functor functor)
    {
        if (delegate != null)
        {
            //functor.accept(delegate);
            delegate.visit(functor);
        }
    }

    /** {@inheritDoc} */
    public void visit(Variable variable)
    {
        if (delegate != null)
        {
            //variable.accept(delegate);
            delegate.visit(variable);
        }
    }

    /** {@inheritDoc} */
    public void visit(IntegerType literal)
    {
        if (delegate != null)
        {
            //literal.accept(delegate);
            delegate.visit(literal);
        }
    }

    /** {@inheritDoc} */
    public void visit(LiteralType literal)
    {
        if (delegate != null)
        {
            //literal.accept(delegate);
            delegate.visit(literal);
        }
    }
}
