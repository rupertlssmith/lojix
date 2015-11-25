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

import com.thesett.aima.search.GoalState;
import com.thesett.aima.search.Operator;
import com.thesett.aima.search.TraversableState;
import com.thesett.aima.search.util.backtracking.Reversable;
import com.thesett.common.parsing.SourceCodePosition;
import com.thesett.common.util.doublemaps.SymbolKey;

/**
 * BaseTerm provides an abstract base implementation of {@link Term}. In particular it provides methods to make an
 * abstract syntax tree expressable as a state space with operations to navigate over it.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Express navigation over the term syntax tree as operators.
 * <tr><td> Provide default search goal that matches all nodes in the tree.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BaseTerm extends TraversableState<Term> implements Term, GoalState, Cloneable
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(BaseTerm.class.getName()); */

    /**
     * Used to hold the terms allocation cell reference, can be usefull during compilation. This is initialized to -1 to
     * indicate an unnasigned status.
     */
    protected int alloc = -1;

    /** Holds the source code position that this term was parsed from. */
    protected SourceCodePosition sourcePosition;

    /** Holds the bracketing flag for this term. */
    protected boolean bracketed = false;

    /** Holds a traverser to supply search operators over terms. When <tt>null</tt> this is used as the operator. */
    protected TermTraverser traverser;

    /** Holds this terms unique symbol key. */
    protected SymbolKey symbolKey;

    /** Holds a reversable operator to establish and restore state when traversing this term. */
    private Reversable reversable;

    /** {@inheritDoc} */
    public boolean isGoal()
    {
        return true;
    }

    /** {@inheritDoc} */
    public Term getChildStateForOperator(Operator<Term> op)
    {
        return op.getOp();
    }

    /** {@inheritDoc} */
    public float costOf(Operator op)
    {
        return 0;
    }

    /** {@inheritDoc} */
    public void setReversable(Reversable reversable)
    {
        this.reversable = reversable;
    }

    /** {@inheritDoc} If a {@link Reversable} has been set on this term it is applied, otherwise nothing is done. */
    public void applyOperator()
    {
        if (reversable != null)
        {
            reversable.applyOperator();
        }
    }

    /** {@inheritDoc} If a {@link Reversable} has been set on this term it is undone, otherwise nothing is done. */
    public void undoOperator()
    {
        if (reversable != null)
        {
            reversable.undoOperator();
        }
    }

    /** {@inheritDoc} */
    public void setTermTraverser(TermTraverser traverser)
    {
        this.traverser = traverser;
    }

    /** {@inheritDoc} */
    public Iterator<Operator<Term>> validOperators(boolean reverse)
    {
        return getChildren(reverse);
    }

    /** {@inheritDoc} */
    public Iterator<Operator<Term>> getChildren(boolean reverse)
    {
        // Return an empty iterator by default.
        return new LinkedList<Operator<Term>>().iterator();
    }

    /** {@inheritDoc} */
    public Term getOp()
    {
        return this;
    }

    /** {@inheritDoc} */
    public void setSymbolKey(SymbolKey key)
    {
        symbolKey = key;
    }

    /** {@inheritDoc} */
    public SymbolKey getSymbolKey()
    {
        return symbolKey;
    }

    /** {@inheritDoc} */
    public int getAllocation()
    {
        return alloc;
    }

    /** {@inheritDoc} */
    public void setAllocation(int alloc)
    {
        this.alloc = alloc;
    }

    /** {@inheritDoc} */
    public SourceCodePosition getSourceCodePosition()
    {
        return sourcePosition;
    }

    /** {@inheritDoc} */
    public void setSourceCodePosition(SourceCodePosition sourceCodePosition)
    {
        this.sourcePosition = sourceCodePosition;
    }

    /** {@inheritDoc} */
    public boolean isBracketed()
    {
        return bracketed;
    }

    /** {@inheritDoc} */
    public void setBracketed(boolean bracketed)
    {
        this.bracketed = bracketed;
    }

    /** {@inheritDoc} */
    public Term queryConversion()
    {
        try
        {
            return (Term) clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new IllegalStateException("Got CloneNotSupportedException but clone should be implemented on Terms.", e);
        }
    }

    /** {@inheritDoc} */
    public void accept(TermVisitor visitor)
    {
        visitor.visit(this);
    }

    /** {@inheritDoc} */
    public Term acceptTransformer(TermTransformer transformer)
    {
        return transformer.transform(this);
    }

    /** {@inheritDoc} */
    public String toString(VariableAndFunctorInterner interner, boolean printVarName, boolean printBindings)
    {
        return toString();
    }

    /** {@inheritDoc} */
    public boolean structuralEquals(Term term)
    {
        return this.equals(term);
    }

    /** {@inheritDoc} */
    public boolean isNumber()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isFunctor()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isVar()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isConstant()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isCompound()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isAtom()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isGround()
    {
        return false;
    }

    /** {@inheritDoc} */
    protected Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
