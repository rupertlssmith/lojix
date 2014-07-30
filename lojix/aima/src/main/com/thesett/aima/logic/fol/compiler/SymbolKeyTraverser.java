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
package com.thesett.aima.logic.fol.compiler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.thesett.aima.logic.fol.AllTermsVisitor;
import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.IntegerType;
import com.thesett.aima.logic.fol.LiteralType;
import com.thesett.aima.logic.fol.Predicate;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.Variable;
import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.search.Operator;
import com.thesett.common.util.StackQueue;
import com.thesett.common.util.TraceIndenter;
import com.thesett.common.util.doublemaps.SymbolKey;
import com.thesett.common.util.doublemaps.SymbolTable;

/**
 * SymbolKeyTraverser is a {@link com.thesett.aima.logic.fol.ClauseTraverser} and
 * {@link com.thesett.aima.logic.fol.FunctorTraverser}, that creates entries within a {@link SymbolTable} for every
 * clause or functor it visits, and sets up {@link com.thesett.common.util.doublemaps.SymbolKey}s on those terms, so
 * that they can directly link to any fields held against them in the symbol table.
 *
 * <p/>SymbolKeyTraverser is an {@link AllTermsVisitor} and like a
 * {@link com.thesett.aima.logic.fol.DelegatingAllTermsVisitor} accepts an optional delegate. All visit operations by
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
public class SymbolKeyTraverser extends PositionalTermTraverserImpl implements PositionalTermTraverser, AllTermsVisitor
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(SymbolKeyTraverser.class.getName()); */

    /** Holds the symbol table field which holds the number of clauses in each predicate. */
    public static final String CLAUSE_NO_SYMBOL_FIELD = "clause_no";

    /** The symbol table scope under which clause heads are stored. */
    public static final int CLAUSE_HEAD_INDEX = 0;

    /** The symbol table scope under which clause bodies are stored. */
    public static final int CLAUSE_BODY_INDEX = 1;

    /** The symbol table scope under which free variables in a clause are stored. */
    public static final int CLAUSE_FREEVAR_INDEX = 2;

    /** The optional delegate. */
    protected AllTermsVisitor delegate;

    /** Holds the symbol name interner. */
    private VariableAndFunctorInterner interner;

    /** Holds the root symbol table. */
    protected SymbolTable<Integer, String, Object> rootSymbolTable;

    /** Holds the current symbol table for the position within the term. */
    protected SymbolTable<Integer, String, Object> currentSymbolTable;

    /** Holds the symbol table for the current clause. */
    private SymbolTable<Integer, String, Object> clauseScopedSymbolTable;

    /**
     * Holds the current positional context within a surrounding term that has child elements. The elements are always
     * numbered from zero.
     */
    protected int currentPosition;

    /** Used for trace indenting when log statements are enabled. */
    private TraceIndenter indenter = new TraceIndenter(true);

    /**
     * Creates a basic positional context traverser over terms, using the specified root symbol table as the starting
     * top-level for the term to be traversed.
     *
     * @param interner    The name interner.
     * @param symbolTable The root symbol table for the term to be traversed.
     * @param delegate    An optional AllTermsVisitor to delegate to.
     */
    public SymbolKeyTraverser(VariableAndFunctorInterner interner, SymbolTable<Integer, String, Object> symbolTable,
        AllTermsVisitor delegate)
    {
        this.delegate = delegate;
        this.rootSymbolTable = symbolTable;
        this.currentSymbolTable = symbolTable;
        this.interner = interner;
    }

    /** {@inheritDoc} */
    public Iterator<Operator<Term>> traverse(Clause clause, boolean reverse)
    {
        /*log.fine("Traversing clause " + clause.toString(interner, true, false));*/

        Functor head = clause.getHead();
        Functor[] body = clause.getBody();

        Queue<Operator<Term>> queue = (!reverse) ? new StackQueue<Operator<Term>>() : new LinkedList<Operator<Term>>();

        // Create a nested scope in the symbol table for the clause, under its functor name/arity.
        int predicateName;
        int clauseIndex;

        if (clause.isQuery())
        {
            predicateName = -1;
        }
        else
        {
            predicateName = head.getName();
        }

        Integer numberOfClauses = (Integer) rootSymbolTable.get(predicateName, CLAUSE_NO_SYMBOL_FIELD);

        if (numberOfClauses == null)
        {
            numberOfClauses = 0;
        }

        rootSymbolTable.put(predicateName, CLAUSE_NO_SYMBOL_FIELD, numberOfClauses + 1);

        clauseIndex = numberOfClauses;

        SymbolTable<Integer, String, Object> predicateScopedSymbolTable = rootSymbolTable.enterScope(predicateName);
        /*log.fine(indenter.generateTraceIndent(2) + "Enter predicate scope " + predicateName);*/

        clauseScopedSymbolTable = predicateScopedSymbolTable.enterScope(clauseIndex);
        /*log.fine(indenter.generateTraceIndent(2) + "Enter clause scope " + clauseIndex);*/

        // For the head functor, clear the top-level flag, set in head context.
        if (head != null)
        {
            head.setReversable(new ContextOperator(clauseScopedSymbolTable, 0, createHeadOperator(head, clause)));
            head.setTermTraverser(this);
            queue.offer(head);

            /*log.fine("Set SKT as traverser on " + head.toString(interner, true, false));*/
            /*log.fine("Created: " + ("head operator " + 0 + " on " + head.toString(interner, true, false)));*/
        }

        // For the body functors, set the top-level flag, clear in head context.
        if (body != null)
        {
            for (int i = 0; i < body.length; i++)
            {
                Functor bodyFunctor = body[i];

                bodyFunctor.setReversable(new ContextOperator(clauseScopedSymbolTable, i + 1,
                        createBodyOperator(bodyFunctor, i, body, clause)));
                bodyFunctor.setTermTraverser(this);
                queue.offer(bodyFunctor);

                /*log.fine("Set SKT as traverser on " + bodyFunctor.toString(interner, true, false));*/
                /*log.fine("Created: " +
                    ("body operator " + (i + 1) + " on " + bodyFunctor.toString(interner, true, false)));*/
            }
        }

        return queue.iterator();
    }

    /** {@inheritDoc} */
    public Iterator<Operator<Term>> traverse(Functor functor, boolean reverse)
    {
        /*log.fine("Traversing functor " + functor.toString(interner, true, false));*/

        Queue<Operator<Term>> queue = (!reverse) ? new StackQueue<Operator<Term>>() : new LinkedList<Operator<Term>>();

        Term[] arguments = functor.getArguments();

        // For a top-level functor clear top-level flag, so that child functors are not taken as top-level.
        if (arguments != null)
        {
            for (int i = 0; i < arguments.length; i++)
            {
                Term argument = arguments[i];

                // When navigating onto a variable, the variable is scoped within a clause, and may appear many times
                // within it. Therefore it always uses its unique id relative to the root symbol table for the
                // clause as its contextual position. Other terms use their position path and are relative to the
                // the current positions symbol table.
                SymbolTable<Integer, String, Object> contextSymbolTable;

                if (argument.isVar())
                {
                    contextSymbolTable = clauseScopedSymbolTable.enterScope(CLAUSE_FREEVAR_INDEX);
                    /*log.fine("Enter freevar scope");*/

                    argument.setReversable(new ContextOperator(contextSymbolTable,
                            createTermOperator(argument, i, functor)));
                    argument.setTermTraverser(this);
                    queue.offer(argument);

                    /*log.fine("Created: " +
                        ("var argument operator on " + argument.toString(interner, true, false)));*/
                }
                else
                {
                    contextSymbolTable = null;

                    argument.setReversable(new ContextOperator(contextSymbolTable, i,
                            createTermOperator(argument, i, functor)));
                    argument.setTermTraverser(this);
                    queue.offer(argument);

                    /*log.fine("Created: " +
                        ("argument operator " + i + " on " + argument.toString(interner, true, false)));*/
                }

            }
        }

        return queue.iterator();
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Assigns symbol keys to non-variable terms, based on the positional path to the term within its containing
     * clause.
     */
    public void visit(Term term)
    {
        if (isEnteringContext())
        {
            SymbolKey key = currentSymbolTable.getSymbolKey(currentPosition);
            term.setSymbolKey(key);
        }

        if (delegate != null)
        {
            delegate.visit(term);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Assigns symbol keys to variables, based on the variables id.
     */
    public void visit(Variable variable)
    {
        if (isEnteringContext())
        {
            SymbolKey key = currentSymbolTable.getSymbolKey(variable.getId());
            variable.setSymbolKey(key);

            /*log.fine(variable.toString(interner, true, false) + " assigned " + key);*/
        }
        else if (isLeavingContext())
        {
            variable.setTermTraverser(null);
        }

        if (delegate != null)
        {
            delegate.visit(variable);
        }
    }

    /** {@inheritDoc} */
    public void visit(Predicate predicate)
    {
        if (isEnteringContext())
        {
            super.visit(predicate);
        }
        else if (isLeavingContext())
        {
            predicate.setTermTraverser(null);
        }

        if (delegate != null)
        {
            delegate.visit(predicate);
        }
    }

    /** {@inheritDoc} */
    public void visit(Clause clause)
    {
        if (isEnteringContext())
        {
            super.visit(clause);
        }
        else if (isLeavingContext())
        {
            clause.setTermTraverser(null);
        }

        if (delegate != null)
        {
            delegate.visit(clause);
        }
    }

    /** {@inheritDoc} */
    public void visit(Functor functor)
    {
        if (isEnteringContext())
        {
            SymbolKey key = currentSymbolTable.getSymbolKey(currentPosition);
            functor.setSymbolKey(key);

            /*log.fine(functor.toString(interner, true, false) + " assigned " + key);*/
        }
        else if (isLeavingContext())
        {
            functor.setTermTraverser(null);
        }

        if (delegate != null)
        {
            delegate.visit(functor);
        }
    }

    /** {@inheritDoc} */
    public void visit(IntegerType literal)
    {
        if (isEnteringContext())
        {
            SymbolKey key = currentSymbolTable.getSymbolKey(currentPosition);
            literal.setSymbolKey(key);
        }
        else if (isLeavingContext())
        {
            literal.setTermTraverser(null);
        }

        if (delegate != null)
        {
            delegate.visit(literal);
        }
    }

    /** {@inheritDoc} */
    public void visit(LiteralType literal)
    {
        if (isEnteringContext())
        {
            SymbolKey key = currentSymbolTable.getSymbolKey(currentPosition);
            literal.setSymbolKey(key);
        }
        else if (isLeavingContext())
        {
            literal.setTermTraverser(null);
        }

        if (delegate != null)
        {
            delegate.visit(literal);
        }
    }

    /**
     * ContextOperator is a {@link StackableOperator} that passes on state changes when the term it is attached to is
     * traversed into. Although it implements {@link StackableOperator} it does not undo state changes when
     * back-tracking over the term, as it is assumed that successive terms will overwrite state as they need to.
     *
     * <pre><p/><table id="crc"><caption>CRC Card</caption>
     * <tr><th> Responsibilities <th> Collaborations
     * <tr><td> Optionally set the top-level, in-head, or last functor flags on the traverser.
     *     <td> {@link PositionalTermTraverserImpl}.
     * </table></pre>
     */
    private class ContextOperator extends StackableOperator
    {
        /** The root symbol table that contains the new context to be established. */
        private SymbolTable<Integer, String, Object> contextSymbolTable;

        /** Used to retain the previous symbol table if overwritten by a new one. */
        private SymbolTable<Integer, String, Object> previousSymbolTable;

        /** The new positional context to establish. */
        private int contextPosition;

        /** The previous positional context to restore. */
        private int previousPosition;

        /** Flag indicating whether or not this operator has a position. */
        private boolean hasPosition;

        /**
         * Creates a context establishing operation.
         *
         * @param symbolTable A new root symbol table to establish context within, or <tt>null</tt> if the current
         *                    symbol table should be used (default).
         * @param position    The new positional context to establish.
         * @param delegate    A stackable operator to chain onto this one.
         */
        private ContextOperator(SymbolTable<Integer, String, Object> symbolTable, int position,
            StackableOperator delegate)
        {
            super(delegate);

            contextSymbolTable = symbolTable;
            contextPosition = position;
            hasPosition = true;
        }

        /**
         * Creates a context establishing operation.
         *
         * @param symbolTable A new root symbol table to establish context within, or <tt>null</tt> if the current
         *                    symbol table should be used (default).
         * @param delegate    A stackable operator to chain onto this one.
         */
        private ContextOperator(SymbolTable<Integer, String, Object> symbolTable, StackableOperator delegate)
        {
            super(delegate);

            contextSymbolTable = symbolTable;
            contextPosition = -1;
            hasPosition = false;
        }

        /** {@inheritDoc} */
        public void applyOperator()
        {
            previousSymbolTable = currentSymbolTable;
            previousPosition = currentPosition;

            // If a starting context is given, move to it first.
            if (contextSymbolTable != null)
            {
                currentSymbolTable = contextSymbolTable;
            }

            if (hasPosition)
            {
                currentSymbolTable = (currentSymbolTable.enterScope(contextPosition));
                /*log.fine(indenter.generateTraceIndent(2) + "Enter scope " + contextPosition);*/
            }

            currentPosition = contextPosition;

            super.applyOperator();
        }

        /** {@inheritDoc} */
        public void undoOperator()
        {
            super.undoOperator();

            currentSymbolTable = previousSymbolTable;
            currentPosition = previousPosition;
        }
    }
}
