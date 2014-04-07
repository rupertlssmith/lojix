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

import com.thesett.aima.logic.fol.Clause;
import com.thesett.aima.logic.fol.Functor;
import com.thesett.aima.logic.fol.Predicate;
import com.thesett.aima.logic.fol.Term;
import com.thesett.aima.logic.fol.TermVisitor;
import com.thesett.common.util.StackQueue;

/**
 * PositionalTermTraverserImpl provides contextual traversal of a term with additional information about the current
 * position of the traversal, as defined in {@link PositionalTermTraverser}; it provides a set of flags and properties
 * as a term tree is walked over, to indicate some positional properties of the current term within the tree.
 *
 * <p/>A {@link StackableOperator} is used which is reversible, and this is used to visit each sub-term encountered
 * twice,
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Establish an initial positional context upon visiting a clause.
 * <tr><td> Report whether the current term is a functor at the top-level within a clause.
 * <tr><td> Report whether the current term is within a clause head or body.
 * <tr><td> Report whether a top-level functor in a clause body is the last one in the body.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PositionalTermTraverserImpl extends BasicTraverser implements PositionalTermTraverser
{
    /** Flag used to indicate that a term context is being entered. */
    protected boolean enteringContext;

    /** Flag used to indicate that a term context is being left. */
    protected boolean leavingContext;

    /** Holds an optional visitor to notify on context changes. */
    protected TermVisitor contextChangeVisitor;

    /** Holds the context stack for the traversal. */
    protected StackQueue<PositionalContextOperator> contextStack = new StackQueue<PositionalContextOperator>();

    /** Inidicates that the initial context has been established at the start of a traversal. */
    private boolean initialContextCreated;

    /**
     * Creates a traverser that uses the normal intuitive left-to-right traversal orderings for clauses and functors.
     */
    public PositionalTermTraverserImpl()
    {
        clauseHeadFirst = true;
        leftToRightClauseBodies = true;
        leftToRightFunctorArgs = true;
    }

    /**
     * Creates a traverser that uses the defubes left-to-right traversal orderings for clauses and functors.
     *
     * @param clauseHeadFirst         <tt>true</tt> to use the normal ordering, <tt>false</tt> for the reverse.
     * @param leftToRightClauseBodies <tt>true</tt> to use the normal ordering, <tt>false</tt> for the reverse.
     * @param leftToRightFunctorArgs  <tt>true</tt> to use the normal ordering, <tt>false</tt> for the reverse.
     */
    public PositionalTermTraverserImpl(boolean clauseHeadFirst, boolean leftToRightClauseBodies,
        boolean leftToRightFunctorArgs)
    {
        this.clauseHeadFirst = clauseHeadFirst;
        this.leftToRightClauseBodies = leftToRightClauseBodies;
        this.leftToRightFunctorArgs = leftToRightFunctorArgs;
    }

    /** {@inheritDoc} Visits a predicate, to set up an initial context for clause traversals. */
    public void visit(Predicate predicate)
    {
        // Set up the initial context, if this is the top-level of the traversal.
        createInitialContext(predicate);
    }

    /** {@inheritDoc} Visits a clause, to set up an initial context for clause traversals. */
    public void visit(Clause clause)
    {
        // Set up the initial context, if this is the top-level of the traversal.
        createInitialContext(clause);
    }

    /** {@inheritDoc} */
    public void visit(Term term)
    {
    }

    /** {@inheritDoc} */
    public boolean isTopLevel()
    {
        PositionalTermTraverserImpl.PositionalContextOperator position = contextStack.peek();

        return (position != null) && position.isTopLevel();
    }

    /** {@inheritDoc} */
    public boolean isInHead()
    {
        PositionalTermTraverserImpl.PositionalContextOperator position = contextStack.peek();

        return (position != null) && position.isInHead();
    }

    /** {@inheritDoc} */
    public boolean isLastBodyFunctor()
    {
        PositionalTermTraverserImpl.PositionalContextOperator position = contextStack.peek();

        return (position != null) && position.isLastBodyFunctor();
    }

    /** {@inheritDoc} */
    public Term getTerm()
    {
        PositionalTermTraverserImpl.PositionalContextOperator position = contextStack.peek();

        return (position != null) ? position.getTerm() : null;
    }

    /** {@inheritDoc} */
    public int getPosition()
    {
        PositionalTermTraverserImpl.PositionalContextOperator position = contextStack.peek();

        return (position != null) ? position.getPosition() : -1;
    }

    /** {@inheritDoc} */
    public boolean isEnteringContext()
    {
        return enteringContext;
    }

    /** {@inheritDoc} */
    public boolean isLeavingContext()
    {
        return leavingContext;
    }

    /** {@inheritDoc} */
    public boolean isContextChange()
    {
        return enteringContext || leavingContext;
    }

    /** {@inheritDoc} */
    public PositionalContext getParentContext()
    {
        PositionalTermTraverserImpl.PositionalContextOperator position = contextStack.peek();

        return (position != null) ? position.getParentContext() : null;
    }

    /** {@inheritDoc} */
    public void setContextChangeVisitor(TermVisitor contextChangeVisitor)
    {
        this.contextChangeVisitor = contextChangeVisitor;
    }

    /**
     * Prints the position of this traverser, mainly for debugging purposes.
     *
     * @return The position of this traverser, mainly for debugging purposes.
     */
    public String toString()
    {
        return "BasicTraverser: [ topLevel = " + isTopLevel() + ", inHead = " + isInHead() + ", lastBodyfunctor = " +
            isLastBodyFunctor() + ", enteringContext = " + enteringContext + ", leavingContext = " + leavingContext +
            " ]";
    }

    /** {@inheritDoc} */
    protected StackableOperator createHeadOperator(Functor head, Clause clause)
    {
        return new PositionalContextOperator(head, -1, true, true, false, null);
    }

    /** {@inheritDoc} */
    protected StackableOperator createBodyOperator(Functor bodyFunctor, int pos, Functor[] body, Clause clause)
    {
        return new PositionalContextOperator(bodyFunctor, pos, true, false, pos == (body.length - 1), null);
    }

    /** {@inheritDoc} */
    protected StackableOperator createTermOperator(Term argument, int pos, Functor functor)
    {
        return new PositionalContextOperator(argument, pos, false, null, false, null);
    }

    /** {@inheritDoc} */
    protected StackableOperator createClauseOperator(Clause bodyClause, int pos, Clause[] body, Predicate predicate)
    {
        return new PositionalContextOperator(bodyClause, pos, false, false, false, null);
    }

    /**
     * Sets up the initial context once, at the start of a traversal.
     *
     * @param term The term to establish the initial positional traversal context for.
     */
    private void createInitialContext(Term term)
    {
        if (!initialContextCreated)
        {
            PositionalContextOperator initialContext =
                new PositionalContextOperator(term, -1, false, false, false, null);
            contextStack.offer(initialContext);
            term.setReversable(initialContext);
            initialContextCreated = true;
        }
    }

    /**
     * PositionalContextOperator is a {@link StackableOperator} that passes on state changes when the term it is
     * attached to is traversed into. Although it implements {@link StackableOperator} it does not undo state changes
     * when back-tracking over the term, as it is assumed that successive terms will overwrite state as they need to.
     *
     * <pre><p/><table id="crc"><caption>CRC Card</caption>
     * <tr><th> Responsibilities <th> Collaborations
     * <tr><td> Optionally set the top-level, in-head, or last functor flags on the traverser.
     *     <td> {@link PositionalTermTraverserImpl}.
     * </table></pre>
     */
    private class PositionalContextOperator extends StackableOperator implements PositionalContext
    {
        /** Holds the term that this is the context operator for. */
        Term term;

        /** Holds the 'position' within the parent term. */
        Integer position;

        /** The state of the top-level flag to establish. */
        Boolean topLevel;

        /** The state of the in-head flag to establish. */
        Boolean inHead;

        /** The state of the last functor flag to establish. */
        Boolean lastBodyFunctor;

        /**
         * Creates a context establishing operation.
         *
         * @param term            The term that this is the context operator for.
         * @param position        The 'position' within the parent term.
         * @param topLevel        <tt>true</tt> to set flag, <tt>false</tt> to clear, <tt>null</tt> to leave alone.
         * @param inHead          <tt>true</tt> to set flag, <tt>false</tt> to clear, <tt>null</tt> to leave alone.
         * @param lastBodyFunctor <tt>true</tt> to set flag, <tt>false</tt> to clear, <tt>null</tt> to leave alone.
         * @param delegate        A stackable operator to chain onto this one.
         */
        private PositionalContextOperator(Term term, Integer position, Boolean topLevel, Boolean inHead,
            Boolean lastBodyFunctor, StackableOperator delegate)
        {
            super(delegate);

            this.term = term;
            this.position = position;
            this.topLevel = topLevel;
            this.inHead = inHead;
            this.lastBodyFunctor = lastBodyFunctor;
        }

        /** {@inheritDoc} */
        public void applyOperator()
        {
            PositionalContextOperator previousContext = contextStack.peek();

            if (previousContext == null)
            {
                previousContext = new PositionalContextOperator(null, -1, false, false, false, null);
            }

            topLevel = (topLevel == null) ? previousContext.topLevel : topLevel;
            inHead = (inHead == null) ? previousContext.inHead : inHead;
            lastBodyFunctor = (lastBodyFunctor == null) ? previousContext.lastBodyFunctor : lastBodyFunctor;
            position = (position == null) ? previousContext.position : position;

            contextStack.offer(this);

            if (PositionalTermTraverserImpl.this.contextChangeVisitor != null)
            {
                PositionalTermTraverserImpl.this.enteringContext = true;
                term.accept(PositionalTermTraverserImpl.this.contextChangeVisitor);
                PositionalTermTraverserImpl.this.enteringContext = false;
            }

            super.applyOperator();
        }

        /** {@inheritDoc} */
        public void undoOperator()
        {
            super.undoOperator();

            if (PositionalTermTraverserImpl.this.contextChangeVisitor != null)
            {
                PositionalTermTraverserImpl.this.leavingContext = true;
                term.accept(PositionalTermTraverserImpl.this.contextChangeVisitor);
                PositionalTermTraverserImpl.this.leavingContext = false;
            }

            contextStack.poll();
        }

        /** {@inheritDoc} */
        public boolean isTopLevel()
        {
            return topLevel;
        }

        /** {@inheritDoc} */
        public boolean isInHead()
        {
            return inHead;
        }

        /** {@inheritDoc} */
        public boolean isLastBodyFunctor()
        {
            return lastBodyFunctor;
        }

        /** {@inheritDoc} */
        public Term getTerm()
        {
            return term;
        }

        /** {@inheritDoc} */
        public int getPosition()
        {
            return position;
        }

        /** {@inheritDoc} */
        public PositionalContext getParentContext()
        {
            if (contextStack.size() > 1)
            {
                return contextStack.get(1);
            }
            else
            {
                return null;
            }
        }
    }
}
