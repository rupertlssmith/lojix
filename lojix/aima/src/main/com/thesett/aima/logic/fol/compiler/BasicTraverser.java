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
package com.thesett.aima.logic.fol.compiler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.ClauseTraverser;
import com.thesett.aima.logic.fol.ClauseVisitor;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.FunctorTraverser;
import com.thesett.aima.logic.fol.Predicate;
import com.thesett.aima.logic.fol.PredicateTraverser;
import com.thesett.aima.logic.fol.PredicateVisitor;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.search.Operator;
import com.thesett.aima.search.util.backtracking.Reversable;
import com.thesett.common.util.StackQueue;

/**
 * BasicTraverser provides methods to produce reversible {@link Operator}s to transition over the structure of a
 * {@link Term}. Structurally terms are built up as clauses, and functors, and default methods are provided to iterate
 * over these constructions, with flags to set to determines whether heads or bodies of clauses are explored first, and
 * whether the arguments of functors are explored left-to-right or right-to-left.
 *
 * <p/>A BasicTraverser can be extended to form a concrete traverser over terms by providing implementations of the
 * methods to create operators for traversing into clause heads or bodies, and the arguments of functors. Any optional
 * visit method to visit the root of a clause being traversed may be also be implemented, usually to initialize the root
 * context of the traversal.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Establish an initial positional context upon visiting a clause.
 * <tr><td> Provide traversal operators over clauses.
 * <tr><td> Provide traversal operators over functors.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BasicTraverser implements PredicateTraverser, ClauseTraverser, FunctorTraverser, ClauseVisitor,
    PredicateVisitor
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(BasicTraverser.class.getName()); */

    /** Flag used to indicate that clause heads should come before bodies in the left-to-right ordering of clauses. */
    protected boolean clauseHeadFirst;

    /** Flag used to indicate that clause bodies should be taken in the intuitive left-to-right ordering. */
    protected boolean leftToRightClauseBodies;

    /** Flag used to indicate that predicate bodies should be taken in the intuitive left-to-right ordering. */
    protected boolean leftToRightPredicateBodies;

    /** Flag used to indicate that functor arguments should be taken in the intuitive left-to-right ordering. */
    protected boolean leftToRightFunctorArgs;

    /**
     * Creates a traverser that uses the normal intuitive left-to-right traversal orderings for clauses and functors.
     */
    public BasicTraverser()
    {
        clauseHeadFirst = true;
        leftToRightClauseBodies = true;
        leftToRightFunctorArgs = true;
        leftToRightPredicateBodies = true;
    }

    /**
     * Creates a traverser that uses the defined left-to-right traversal orderings for clauses and functors.
     *
     * @param clauseHeadFirst         <tt>true</tt> to use the normal ordering, <tt>false</tt> for the reverse.
     * @param leftToRightClauseBodies <tt>true</tt> to use the normal ordering, <tt>false</tt> for the reverse.
     * @param leftToRightFunctorArgs  <tt>true</tt> to use the normal ordering, <tt>false</tt> for the reverse.
     */
    public BasicTraverser(boolean clauseHeadFirst, boolean leftToRightClauseBodies, boolean leftToRightFunctorArgs)
    {
        this.clauseHeadFirst = clauseHeadFirst;
        this.leftToRightClauseBodies = leftToRightClauseBodies;
        this.leftToRightFunctorArgs = leftToRightFunctorArgs;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Can be used to visit a predicate, to set up an initial context for predicate traversals.
     */
    public abstract void visit(Predicate predicate);

    /**
     * {@inheritDoc}
     *
     * <p/>Can be used to visit a clause, to set up an initial context for clause traversals.
     */
    public abstract void visit(Clause clause);

    /** {@inheritDoc} */
    public Iterator<Operator<Term>> traverse(Predicate predicate, boolean reverse)
    {
        /*log.fine("Traversing predicate " + predicate.toString());*/

        Clause[] body = predicate.getBody();

        Queue<Operator<Term>> queue = (!reverse) ? new StackQueue<Operator<Term>>() : new LinkedList<Operator<Term>>();

        // For the predicate bodies.
        if (body != null)
        {
            for (int i = leftToRightPredicateBodies ? 0 : (body.length - 1);
                    leftToRightPredicateBodies ? (i < body.length) : (i >= 0);
                    i = i + (leftToRightPredicateBodies ? 1 : -1))
            {
                Clause bodyClause = body[i];

                bodyClause.setReversable(createClauseOperator(bodyClause, i, body, predicate));
                bodyClause.setTermTraverser(this);
                queue.offer(bodyClause);
            }
        }

        return queue.iterator();
    }

    /** {@inheritDoc} */
    public Iterator<Operator<Term>> traverse(Clause clause, boolean reverse)
    {
        /*log.fine("Traversing clause " + clause.toString());*/

        Functor head = clause.getHead();
        Functor[] body = clause.getBody();

        Queue<Operator<Term>> queue = (!reverse) ? new StackQueue<Operator<Term>>() : new LinkedList<Operator<Term>>();

        // For the head functor, set the top-level flag, set in head context.
        if (head != null)
        {
            head.setReversable(createHeadOperator(head, clause));
            head.setTermTraverser(this);
            queue.offer(head);
        }

        // For the body functors, set the top-level flag, clear in head context.
        if (body != null)
        {
            for (int i = leftToRightClauseBodies ? 0 : (body.length - 1);
                    leftToRightClauseBodies ? (i < body.length) : (i >= 0); i = i + (leftToRightClauseBodies ? 1 : -1))
            {
                Functor bodyFunctor = body[i];

                bodyFunctor.setReversable(createBodyOperator(bodyFunctor, i, body, clause));
                bodyFunctor.setTermTraverser(this);
                queue.offer(bodyFunctor);
            }
        }

        return queue.iterator();
    }

    /** {@inheritDoc} */
    public Iterator<Operator<Term>> traverse(Functor functor, boolean reverse)
    {
        /*log.fine("Traversing functor " + functor.toString());*/

        Queue<Operator<Term>> queue = (!reverse) ? new StackQueue<Operator<Term>>() : new LinkedList<Operator<Term>>();

        Term[] arguments = functor.getArguments();

        // For a top-level functor clear top-level flag, so that child functors are not taken as top-level.
        if (arguments != null)
        {
            for (int i = leftToRightFunctorArgs ? 0 : (arguments.length - 1);
                    leftToRightFunctorArgs ? (i < arguments.length) : (i >= 0);
                    i = i + (leftToRightFunctorArgs ? 1 : -1))
            {
                Term argument = arguments[i];
                argument.setReversable(createTermOperator(argument, i, functor));
                argument.setTermTraverser(this);
                queue.offer(argument);
            }
        }

        return queue.iterator();
    }

    /**
     * When traversing the head of a clause, creates a reversible operator to use to transition into the head.
     *
     * @param  head   The head to transition into.
     * @param  clause The containing clause.
     *
     * @return A reversable operator.
     */
    protected abstract StackableOperator createHeadOperator(Functor head, Clause clause);

    /**
     * When traversing the body functors of a clause, creates a reversible operator to use to transition into each body
     * functor.
     *
     * @param  bodyFunctor The body functor to transition into.
     * @param  pos         The position of the body functor within the body.
     * @param  body        The containing body.
     * @param  clause      The containing clause.
     *
     * @return A reversable operator.
     */
    protected abstract StackableOperator createBodyOperator(Functor bodyFunctor, int pos, Functor[] body,
        Clause clause);

    /**
     * When traversing the body clauses of a predicate, creates a reversible operator to use to transition into each
     * body clause.
     *
     * @param  bodyClause The body clause to transition into.
     * @param  pos        The position of the body clause within the body.
     * @param  body       The containing body.
     * @param  predicate  The containing predicate.
     *
     * @return A reversable operator.
     */
    protected abstract StackableOperator createClauseOperator(Clause bodyClause, int pos, Clause[] body,
        Predicate predicate);

    /**
     * When traversing the argument terms of a functor, creates a reversible operator to use to transition into each
     * argument term.
     *
     * @param  argument The argument term to transition into.
     * @param  pos      The position of the argument within the functor.
     * @param  functor  The containing functor.
     *
     * @return A reversable operator.
     */
    protected abstract StackableOperator createTermOperator(Term argument, int pos, Functor functor);

    /**
     * StackableOperator is a {@link Reversable} operator that can also delegates to another stackable operation by
     * default. This allows stack of operators to be built and applied or undone on every state transition.
     *
     * <pre><p/><table id="crc"><caption>CRC Card</caption>
     * <tr><th> Responsibilities <th> Collaborations
     * <tr><td> Delegate to a stackable operation.
     * </table></pre>
     */
    protected class StackableOperator implements Reversable
    {
        /** The optional stackable delegate operation. */
        StackableOperator delegate;

        /**
         * Creates an reversible operator with a delegate.
         *
         * @param delegate An optional delegate to chain onto, may be <tt>null</tt>.
         */
        public StackableOperator(StackableOperator delegate)
        {
            this.delegate = delegate;
        }

        /** {@inheritDoc} */
        public void applyOperator()
        {
            if (delegate != null)
            {
                delegate.applyOperator();
            }
        }

        /** {@inheritDoc} */
        public void undoOperator()
        {
            if (delegate != null)
            {
                delegate.undoOperator();
            }
        }
    }
}
