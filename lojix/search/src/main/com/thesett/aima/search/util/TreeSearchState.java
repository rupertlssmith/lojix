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
package com.thesett.aima.search.util;

import java.util.ArrayList;
import java.util.Iterator;

import com.thesett.aima.search.Operator;
import com.thesett.aima.search.TraversableState;
import com.thesett.common.util.Tree;

/**
 * TreeSearchState is a {@link TraversableState} that enables {@link com.thesett.aima.search.QueueBasedSearchMethod}s to
 * be applied to {@link com.thesett.common.util.Tree} s. It does not dictate the method of searching, or the order in
 * which the nodes and leafs of the tree are visited; a free choice of search algorithm is permitted. The constructor
 * accepts a predicate over the tree element type that is used to determine whether node or leaf points in the tree
 * correspond to goal states of the search.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Translate operations into new states.
 * <tr><td> Report the cost of an operation.
 * <tr><td> Enumerate the valid operations on a state.
 * <tr><td> Provide links to successor states.
 * <tr><td> Report goal state status.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TreeSearchState<E> extends TraversableState<Tree<E>>
{
    /* Used for logging. */
    /* private static final Logger log = Logger.getLogger(TreeSearchState.class.getName()); */

    /** Holds the tree to search. */
    private final Tree<E> tree;

    /**
     * Builds a tree search state over a specified tree.
     *
     * @param tree The tree to search.
     */
    public TreeSearchState(Tree<E> tree)
    {
        /*log.fine("public TreeSearchState(Tree<E> tree, UnaryPredicate<E> goalPredicate): called");*/

        // Keep the tree to search.
        this.tree = tree;
    }

    /**
     * Returns the data element in the tree associated with this search state.
     *
     * @return The data element in the tree associated with this search state.
     */
    public E getElement()
    {
        return tree.getElement();
    }

    /**
     * Returns the state obtained by applying the specified operation. If the operation is not valid then this should
     * return null.
     *
     * @param  op The operator to apply to the traversable state.
     *
     * @return The new traversable state generated by applying the specified operator.
     */
    public TreeSearchState<E> getChildStateForOperator(Operator<Tree<E>> op)
    {
        /*log.fine("public Traversable getChildStateForOperator(Operator op): called");*/

        // Extract the child tree from the operator and create a new tree search state from it.
        return new TreeSearchState<E>(op.getOp());
    }

    /**
     * Calculates the cost of applying the specified operations.
     *
     * @param  op The operator to apply.
     *
     * @return The cost of applying the specified operation.
     */
    public float costOf(Operator op)
    {
        return 1.0f;
    }

    /**
     * Gets all operators valid from this state. If the current tree to search has any children these are encoded as
     * operators to access those child trees as tree search states. If the current tree is a leaf then an empty iterator
     * is returned.
     *
     * @param  reverse <tt>true</tt> if the operators are to be presented in reverse order.
     *
     * @return An iterator over all the operators that may be applied to this state.
     */
    public Iterator<Operator<Tree<E>>> validOperators(boolean reverse)
    {
        /*log.fine("public Iterator<Operator> validOperators(): called");*/

        // Check if the tree is a leaf and return an empty iterator if so.
        if (tree.isLeaf())
        {
            /*log.fine("is leaf");*/

            return new ArrayList<Operator<Tree<E>>>().iterator();
        }

        // Generate an iterator over the child trees of the current node, encapsulating them as operators.
        else
        {
            /*log.fine("is node");*/

            Tree.Node<E> node = tree.getAsNode();

            return new TreeSearchOperatorIterator<E>(node.getChildIterator());
        }
    }

    /**
     * Translates an iterator over child trees into an iterator over operators that encapsulate those child trees. This
     * saves building an intermediate collection to hold the child trees.
     */
    public static class TreeSearchOperatorIterator<E> implements Iterator<Operator<Tree<E>>>
    {
        /** Holds the child iterator. */
        private final Iterator<Tree<E>> childIterator;

        /**
         * Creates an iterator that turns child trees into operations.
         *
         * @param childIterator The iterator over the child trees.
         */
        public TreeSearchOperatorIterator(Iterator<Tree<E>> childIterator)
        {
            this.childIterator = childIterator;
        }

        /**
         * Determines whether or not there is a next element. Delegates to the underlying child tree iterator.
         *
         * @return <tt>true</tt> if there are more elements.
         */
        public boolean hasNext()
        {
            return childIterator.hasNext();
        }

        /**
         * Gets the next child tree encapsulated as an operator on tree search states.
         *
         * @return The next child tree encapsulated as an operator on tree search states.
         */
        public Operator<Tree<E>> next()
        {
            return new OperatorImpl<Tree<E>>(childIterator.next());
        }

        /** This operation is not supported. */
        public void remove()
        {
            throw new UnsupportedOperationException("Operation 'remove' not supported.");
        }
    }
}
