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
package com.thesett.aima.logic.fol.wam;

import com.thesett.aima.logic.fol.AllTermsVisitor;
import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.IntegerType;
import com.thesett.aima.logic.fol.LiteralType;
import com.thesett.aima.logic.fol.Predicate;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.compiler.PositionalContext;
import com.thesett.aima.logic.fol.compiler.PositionalTermTraverser;
import com.thesett.common.util.doublemaps.SymbolTable;

/**
 * BasePositionalVisitor is an {@link AllTermsVisitor} that is being driven by a {@link PositionalTermTraverser}. It is
 * used as a base class for implementing visitors that need to understand the positional context during visitation.
 *
 * <p/>It uses positional context information from a {@link PositionalTermTraverser}, to determine whether terms are
 * being entered or left, and splits these down into calls on appropriate enter/leave methods. Default no-op
 * implementations of these methods are supplied by this base class and may be extended by specific implementations as
 * needed to figure out the positional context during visitation.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide default enter/leave methods for every part of a term.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BasePositionalVisitor implements AllTermsVisitor
{
    /** The name interner. */
    protected VariableAndFunctorInterner interner;

    /** The symbol table. */
    protected SymbolTable<Integer, String, Object> symbolTable;

    /** The positional context. */
    protected PositionalTermTraverser traverser;

    /**
     * Creates a positional visitor.
     *
     * @param interner    The name interner.
     * @param symbolTable The compiler symbol table.
     * @param traverser   The ppositional context traverser.
     */
    public BasePositionalVisitor(VariableAndFunctorInterner interner, SymbolTable<Integer, String, Object> symbolTable,
        PositionalTermTraverser traverser)
    {
        this.interner = interner;
        this.symbolTable = symbolTable;
        this.traverser = traverser;
    }

    /** {@inheritDoc} */
    public void visit(Term term)
    {
        if (traverser.isEnteringContext())
        {
            enterTerm(term);
        }
        else if (traverser.isLeavingContext())
        {
            leaveTerm(term);
            term.setTermTraverser(null);
        }
    }

    /** {@inheritDoc} */
    public void visit(Functor functor)
    {
        if (traverser.isEnteringContext())
        {
            enterFunctor(functor);
        }
        else if (traverser.isLeavingContext())
        {
            leaveFunctor(functor);
            functor.setTermTraverser(null);
        }
    }

    /** {@inheritDoc} */
    public void visit(Variable variable)
    {
        if (traverser.isEnteringContext())
        {
            enterVariable(variable);
        }
        else if (traverser.isLeavingContext())
        {
            leaveVariable(variable);
            variable.setTermTraverser(null);
        }
    }

    /** {@inheritDoc} */
    public void visit(Predicate predicate)
    {
        if (traverser.isEnteringContext())
        {
            enterPredicate(predicate);
        }
        else if (traverser.isLeavingContext())
        {
            leavePredicate(predicate);
            predicate.setTermTraverser(null);
        }
    }

    /** {@inheritDoc} */
    public void visit(Clause clause)
    {
        if (traverser.isEnteringContext())
        {
            enterClause(clause);
        }
        else if (traverser.isLeavingContext())
        {
            leaveClause(clause);
            clause.setTermTraverser(null);
        }
    }

    /** {@inheritDoc} */
    public void visit(IntegerType literal)
    {
        if (traverser.isEnteringContext())
        {
            enterIntLiteral(literal);
        }
        else if (traverser.isLeavingContext())
        {
            leaveIntLiteral(literal);
            literal.setTermTraverser(null);
        }
    }

    /** {@inheritDoc} */
    public void visit(LiteralType literal)
    {
        if (traverser.isEnteringContext())
        {
            enterLiteral(literal);
        }
        else if (traverser.isLeavingContext())
        {
            leaveLiteral(literal);
            literal.setTermTraverser(null);
        }
    }

    /**
     * Called when a term is entered during the visitation.
     *
     * @param term The term being entered.
     */
    protected void enterTerm(Term term)
    {
    }

    /**
     * Called when a term is being left during the visitation.
     *
     * @param term The term being left.
     */
    protected void leaveTerm(Term term)
    {
    }

    /**
     * Called when a functor is entered during the visitation.
     *
     * @param functor The functor being entered.
     */
    protected void enterFunctor(Functor functor)
    {
    }

    /**
     * Called when a functor is being left during the visitation.
     *
     * @param functor The functor being left.
     */
    protected void leaveFunctor(Functor functor)
    {
    }

    /**
     * Called when a variable is entered during the visitation.
     *
     * @param variable The variable being entered.
     */
    protected void enterVariable(Variable variable)
    {
    }

    /**
     * Called when a variable is being left during the visitation.
     *
     * @param variable The variable being left.
     */
    protected void leaveVariable(Variable variable)
    {
    }

    /**
     * Called when a predicate is entered during the visitation.
     *
     * @param predicate The predicate being entered.
     */
    protected void enterPredicate(Predicate predicate)
    {
    }

    /**
     * Called when a predicate is being left during the visitation.
     *
     * @param predicate The predicate being left.
     */
    protected void leavePredicate(Predicate predicate)
    {
    }

    /**
     * Called when a clause is entered during the visitation.
     *
     * @param clause The clause being entered.
     */
    protected void enterClause(Clause clause)
    {
    }

    /**
     * Called when a clause is being left during the visitation.
     *
     * @param clause The clause being left.
     */
    protected void leaveClause(Clause clause)
    {
    }

    /**
     * Called when a integer literal is entered during the visitation.
     *
     * @param literal The integer literal being entered.
     */
    protected void enterIntLiteral(IntegerType literal)
    {
    }

    /**
     * Called when a integer literal is being left during the visitation.
     *
     * @param literal The integer literal being left.
     */
    protected void leaveIntLiteral(IntegerType literal)
    {
    }

    /**
     * Called when a literal is entered during the visitation.
     *
     * @param literal The literal being entered.
     */
    protected void enterLiteral(LiteralType literal)
    {
    }

    /**
     * Called when a literal is being left during the visitation.
     *
     * @param literal The literal being left.
     */
    protected void leaveLiteral(LiteralType literal)
    {
    }
}
