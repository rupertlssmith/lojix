/*
 * Copyright The Sett Ltd, 2005 to 2009.
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
package com.thesett.common.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Tree defines the interface for an abstract tree data type. The trees it describes are capable of storing data
 * elements in their nodes and in their leaves. The type of data element held in the nodes is constrained to be the same
 * as the element type in the leaves.
 *
 * <p>Trees are defined as recursive data types. There are two types of tree: those that are nodes and have child trees,
 * those that are leaves and have no children. The leaves and nodes themselves are also trees and extend the tree
 * interface.
 *
 * <p>The branching factor is arbitrary. A node may have any number of children.
 *
 * <p>There is a method to generate an iterator over a tree. The iterator may choose one of several different orderings
 * to traverse the tree; pre-order, in-order and post-order:
 *
 * <ol>
 * <li>The preorder listing of tree T is the root of T, followed by the nodes of children T1 to Tk in preorder.</li>
 * <li>The inorder listing of tree T is the nodes of child T1 in inorder, followed by the root r, followed by the nodes
 * of T2 to Tk in inorder.</li>
 * <li>The postorder listing of T is the nodes of T1 to Tk in postorder, all followed by the root r.</li>
 * </ol>
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Provide access to a tree as a node or a leaf.
 * <tr><td>Iterate over all data elements in a tree.
 * <tr><td>Provide access to the elements of a tree.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface Tree<E>
{
    /** Defines the different types of iteration order over the elements of trees. */
    public enum IterationOrder
    {
        /** Used to request pre-order iteration. */
        PreOrder,

        /** Used to request in-order iteration. */
        InOrder,

        /** Used to request post-order iteration. */
        PostOrder
    }

    /**
     * Returns true if this tree has no children and is therefore a leaf in the tree.
     *
     * @return True if this tree is a leaf and false if it is a node.
     */
    public boolean isLeaf();

    /**
     * Returns the tree which is the parent of this tree. If this tree's root is the real root node of the whole tree
     * then this should return null.
     *
     * @return The tree which this tree is a sub-tree of. If this tree if a root tree then this returns null.
     */
    public Node<E> getParent();

    /**
     * Set the node which is the parent of this tree.
     *
     * @param parent The parent tree which this tree is to be added as a sub-tree of.
     */
    public void setParent(Node<E> parent);

    /**
     * If this is a leaf, then returns the tree as a Leaf. If it is not a leaf then this method should return null.
     *
     * @return This point in the tree as a leaf, or null if this is a node.
     */
    public Leaf<E> getAsLeaf();

    /**
     * If this is a node, then returns the tree as a Node. If it is not a node then this method should return null.
     *
     * @return This point in the tree as a node, or null if this is a leaf.
     */
    public Node<E> getAsNode();

    /**
     * Returns the data element stored at the current point in this tree.
     *
     * @return The data element stored at this point in the tree.
     */
    public E getElement();

    /**
     * Sets the data element stored at the current point in this tree.
     *
     * @param newElement The data element to store in the tree at this point.
     */
    public void setElement(E newElement);

    /**
     * Adds a child tree to the children of this point in the tree. If this is already a node then it remains as a node.
     * If this is a leaf then adding a child to it must promote it to become a node. Some implementation of leaf may not
     * support this in which case they should throw an UnsupportedOperationException.
     *
     * @param  child A child tree to add below this point in the tree.
     *
     * @throws UnsupportedOperationException if this operation is not supported (because the tree in not modifiable, or
     *                                       because this point is a leaf that cannot be promoted to a node).
     */
    public void addChild(Tree<E> child);

    /**
     * Clears all the children of this point in the tree. If this point is a leaf it will have no children so this
     * operation does nothing. If this point is a node it must be reduced to a leaf by this operation.
     */
    public void clearChildren();

    /**
     * Returns an iterator over all the children of this node. This is different to the {@link #iterator} method which
     * returns an iterator over the whole tree from this point downwards. This iterator is only over the child nodes and
     * does not recurse over their children.
     *
     * @return An iterator over the children of this node, if it is a node and has children.
     */
    public Iterator<Tree<E>> getChildIterator();

    /**
     * This iterates over the sub-trees stored in this Tree at or below this point (that is, this point an all its
     * subtrees). The iteration order must be specified as a parameter to this method.
     *
     * @param  order The iteration order. See {@link Tree} for more information about possible iteration orders.
     *
     * @return An iterator over the tree.
     */
    public Iterator<? extends Tree<E>> iterator(IterationOrder order);

    /**
     * A Node is a Tree that has children.
     */
    public static interface Node<E> extends Tree<E>
    {
        /**
         * Gets an iterator over the children of this node. This is different to the {@link #iterator} method which
         * returns an iterator over the whole tree from this point downwards. This iterator is only over the child nodes
         * and does not recurse over their children.
         *
         * @return An iterator over all the children of this node.
         */
        Iterator<Tree<E>> getChildIterator();

        /**
         * Gets all the child nodes of this one.
         *
         * @return The children of this node as a collection.
         */
        Collection<Tree<E>> getChildren();
    }

    /**
     * A Leaf is a Tree that does not have children.
     */
    public static interface Leaf<E> extends Tree<E>
    {
    }
}
