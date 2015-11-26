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
package com.thesett.common.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * SimpleTree provides an implementation of the {@link Tree} interface.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide access to a tree as a node or a leaf.
 * <tr><td>Iterate over all data elements in a tree.
 * <tr><td>Provide access to the elements of a tree.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SimpleTree<E> implements Tree<E>, Tree.Node<E>, Tree.Leaf<E>, Serializable
{
    /** There are two type of tree, nodes and leafs. These enums are used to define which this is. */
    private enum Type
    {
        /** Used to indicate that a point in the tree is a node. */
        Node,

        /** Used to indicate that a point in the tree is a leaf. */
        Leaf
    }

    /** Used to indicate whether this is a node or a leaf. */
    private Type nodeOrLeaf;

    /** Used to hold the data element at this point in the tree. */
    private E element;

    /** Used to hold the parent of this point in the tree. Null if this is a root. */
    private Tree.Node<E> parent = null;

    /** Used to hold the child trees. */
    private List<Tree<E>> children = null;

    /** Builds an empty leaf. */
    public SimpleTree()
    {
        // Mark this tree as a leaf.
        nodeOrLeaf = Type.Leaf;

        // Set the data element to null.
        this.element = null;
    }

    /**
     * Builds a leaf.
     *
     * @param element The data element to store at the leaf.
     */
    public SimpleTree(E element)
    {
        // Mark this tree as a leaf.
        nodeOrLeaf = Type.Leaf;

        // Keep the data element.
        this.element = element;
    }

    /**
     * Builds a node.
     *
     * @param element  The data element to store at the node.
     * @param children A collection of sub-trees to place below the node.
     */
    public SimpleTree(E element, Collection<? extends Tree<E>> children)
    {
        // Check if children is empty in which case this should be a leaf and not a node.
        if (children.isEmpty())
        {
            nodeOrLeaf = Type.Leaf;
        }
        else
        {
            // Mark this tree as a node.
            nodeOrLeaf = Type.Node;
        }

        // Keep the data element.
        this.element = element;

        // Keep all the children.
        this.children = new ArrayList<Tree<E>>(children);
        // this.children = children;
    }

    /**
     * Returns an iterator over all the children of this node. This is different to the {@link #iterator} method which
     * returns an iterator over the whole tree from this point downwards. This iterator is only over the child nodes and
     * does not recurse over their children.
     *
     * @return An iterator over the children of this node, if it is a node and has children.
     */
    public Iterator<Tree<E>> getChildIterator()
    {
        initChildren();

        return new ChildIterator();
    }

    /**
     * Gets all the child nodes of this one.
     *
     * @return All the child nodes as a collection.
     */
    public Collection<Tree<E>> getChildren()
    {
        initChildren();

        return children;
    }

    /**
     * Gets the ith child of this node.
     *
     * @param  i The index of the child tree to get.
     *
     * @return The ith child of this node.
     */
    public Tree<E> getChild(int i)
    {
        initChildren();

        return children.get(i);
    }

    /**
     * Reports the number of children that a node has.
     *
     * @return The number of children that a node has.
     */
    public int numChildren()
    {
        initChildren();

        return children.size();
    }

    /**
     * Returns true if this tree has no children and is therefore a leaf in the tree.
     *
     * @return True if this node has no children, false if it does.
     */
    public boolean isLeaf()
    {
        return nodeOrLeaf.equals(Type.Leaf);
    }

    /**
     * Returns the tree which is the parent of this tree. If this tree's root is the real root node of the whole tree
     * then this returns null.
     *
     * @return The tree which this tree is a sub-tree of. If this tree if a root tree then this returns null.
     */
    public Node<E> getParent()
    {
        return parent;
    }

    /**
     * Set the node which is the parent of this tree.
     *
     * @param parent The parent tree which this tree is to be added as a sub-tree of.
     */
    public void setParent(Node<E> parent)
    {
        this.parent = parent;
    }

    /**
     * If this is a leaf, then returns the tree as a Leaf. If it is not a leaf then this method should return null.
     *
     * @return This point in the tree as a leaf, or null if this is a node.
     */
    public Leaf<E> getAsLeaf()
    {
        return nodeOrLeaf.equals(Type.Leaf) ? this : null;
    }

    /**
     * If this is a node, then returns the tree as a Node. If it is not a node then this method should return null.
     *
     * @return This point in the tree as a node, or null if this is a leaf.
     */
    public Node<E> getAsNode()
    {
        return nodeOrLeaf.equals(Type.Node) ? this : null;
    }

    /**
     * Returns the data element stored at the current point in this tree.
     *
     * @return The data element stored at this point in the tree.
     */
    public E getElement()
    {
        return element;
    }

    /**
     * Sets the data element stored at the current point in this tree.
     *
     * @param newElement The data element to store in the tree at this point.
     */
    public void setElement(E newElement)
    {
        this.element = newElement;
    }

    /**
     * Adds a child tree to the children of this point in the tree. If this is already a node then it remains as a node.
     * If this is a leaf then adding a child to it must promote it to become a node. This implementation supports
     * turning leaves into nodes.
     *
     * @param child A child tree to add below this point in the tree.
     */
    public void addChild(Tree<E> child)
    {
        initChildren();

        // Add the new child to the collection of children.
        children.add(child);

        // Set the type of this point in the tree to a node as it now has children.
        nodeOrLeaf = Type.Node;

        // Set the new childs parent to this.
        child.setParent(this);
    }

    /**
     * Clears all the children of this point in the tree. If this point is a leaf it will have no children so this
     * operation does nothing. If this point is a node it will be reduced to a leaf by this operation. This
     * implementation supports turning nodes into leaves.
     */
    public void clearChildren()
    {
        // Check that their are children to clear.
        if (children != null)
        {
            // Loop over all the children setting their parent to null.
            for (Tree<E> child : children)
            {
                child.setParent(null);
            }

            // Clear out the children collection.
            children.clear();

            // Mark this as a leaf node.
            nodeOrLeaf = Type.Leaf;
        }
    }

    /**
     * This iterates over the elements stored in this Tree (including all its subtrees). The iteration order must be
     * specified as a parameter to this method.
     *
     * @param  order The iteration order. See {@link Tree} for more information about possible iteration orders.
     *
     * @return An iterator over the tree.
     */
    public Iterator<SimpleTree<E>> iterator(IterationOrder order)
    {
        return new SimpleTreeIterator(order, this);
    }

    /**
     * Returns the entire tree as a string for debugging purposes.
     *
     * @return The entire tree as a string for debugging purposes.
     */
    public String toString()
    {
        String result = "[ ";

        // Loop over the tree preorder. Root first, then children.

        // Print the root.
        E rootElement = getElement();

        result += rootElement + (isLeaf() ? "" : " : ");

        // Iterator<SimpleTree<E>> i = iterator(IterationOrder.PreOrder);
        if (!isLeaf())
        {
            // Print the children.
            for (Iterator<Tree<E>> i = getChildIterator(); i.hasNext();)
            {
                Tree<E> child = i.next();
                result += child.toString() + (i.hasNext() ? ", " : "");
            }
        }

        result += " ]";

        return result;
    }

    /** Ensures that the child list is initialized. */
    private void initChildren()
    {
        // If the children collection is empty then create a new array list to hold the children in.
        if (children == null)
        {
            children = new ArrayList<Tree<E>>();
        }
    }

    /**
     * An iterator that explicitly manages its own queue of elements and tree fragments for later expansion rather than
     * using a recursive scheme which would be too costly on stack space and may cause a StackOverflowError for large
     * trees.
     */
    private class SimpleTreeIterator<E> implements Iterator<SimpleTree<E>>
    {
        /** Used to hold the iteration order. */
        IterationOrder order;

        /** Used to queue the trees for this iterator. */
        Queue<QueueElement<E>> elementVisitationQueue = new StackQueue<QueueElement<E>>();

        /**
         * Creates a new SimpleTreeIterator object.
         *
         * @param order The order in which this iterator is to traverse the tree. See {@link Tree} for more information
         *              about the possible traversal orders.
         * @param tree  The tree to iterate over.
         */
        public SimpleTreeIterator(IterationOrder order, Tree<E> tree)
        {
            // Keep the iteration order.
            this.order = order;

            // Enqueue the initial tree.
            enqueueTree(tree);
        }

        /**
         * Tests if this iterator has more elements.
         *
         * @return True if there are more elements to iterate over, false otherwise.
         */
        public boolean hasNext()
        {
            // Check that the queue is not empty, in which case there are more elements.
            return !elementVisitationQueue.isEmpty();
        }

        /**
         * Extracts the next element from this iterator.
         *
         * @return The next sub-tree in the iteration.
         */
        public SimpleTree<E> next()
        {
            // Throw an exception if the queue is empty.
            if (elementVisitationQueue.isEmpty())
            {
                throw new NoSuchElementException();
            }

            // Loop until the head element of the queue is a simple element.
            while (!elementVisitationQueue.peek().holdsSimpleElement)
            {
                // Take the head element off the queue and expand its tree fragment.
                QueueElement<E> nextElement = elementVisitationQueue.remove();

                enqueueTree(nextElement.treeFragment);
            }

            // Extract the head element of the queue and return its simple element.
            return (SimpleTree<E>) elementVisitationQueue.remove().treeFragment;
        }

        /** Remove is not supported by this iterator. */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        /**
         * Expands a tree fragment onto the element visitation queue.
         *
         * @param tree The sub-tree to be expanded onto the iteration queue.
         */
        private void enqueueTree(Tree<E> tree)
        {
            // Get the root element from the tree and build a queue element to contain it.
            QueueElement<E> rootElement = new QueueElement<E>();

            rootElement.treeFragment = tree;
            rootElement.holdsSimpleElement = true;

            // Check if the tree has children and if so then cast it to a node type and get an iterator over its
            // children.
            Tree.Node<E> treeNode = null;
            Iterator<Tree<E>> i = null;

            if (!tree.isLeaf())
            {
                treeNode = tree.getAsNode();
                i = treeNode.getChildIterator();
            }

            // Enqueue the root and its children in an order depending on the type of iteration order requested.
            switch (order)
            {
            case PreOrder:
            {
                // Enqueue all its children and then the root element.
                if (!tree.isLeaf())
                {
                    while (i.hasNext())
                    {
                        QueueElement<E> childElement = new QueueElement<E>();

                        childElement.treeFragment = i.next();
                        childElement.holdsSimpleElement = false;
                        elementVisitationQueue.offer(childElement);
                    }
                }

                elementVisitationQueue.offer(rootElement);

                break;
            }

            case InOrder:
            {
                // Enqueue the rest of the children, the root element and then the first child.
                QueueElement<E> firstChildElement = null;

                if (!tree.isLeaf() && i.hasNext())
                {
                    firstChildElement = new QueueElement<E>();
                    firstChildElement.treeFragment = i.next();
                    firstChildElement.holdsSimpleElement = false;

                    while (i.hasNext())
                    {
                        QueueElement<E> childElement = new QueueElement<E>();

                        childElement.treeFragment = i.next();
                        childElement.holdsSimpleElement = false;
                        elementVisitationQueue.offer(childElement);
                    }
                }

                elementVisitationQueue.offer(rootElement);

                if (!tree.isLeaf())
                {
                    elementVisitationQueue.offer(firstChildElement);
                }

                break;
            }

            case PostOrder:
            {
                // Enqueue the root element and then all its children.
                elementVisitationQueue.offer(rootElement);

                if (!tree.isLeaf())
                {
                    while (i.hasNext())
                    {
                        QueueElement<E> childElement = new QueueElement<E>();

                        childElement.treeFragment = i.next();
                        childElement.holdsSimpleElement = false;
                        elementVisitationQueue.offer(childElement);
                    }
                }

                break;
            }

            default:
            {
                throw new IllegalStateException("Unsupported tree traversal ordering.");
            }
            }
        }

        /**
         * This data structure is used to keep track of trees enqueued for examination by this iterator. Either a tree
         * is enqueued for later expansion, or its element in enqueued for immediate retrieval and removal from the
         * queue.
         */
        private class QueueElement<E>
        {
            /** Used to indicate that this queue element holds a simple element and not a tree fragment. */
            public boolean holdsSimpleElement = true;

            /** Used to hold a tree fragment for later expansion. Null if this is a simple element. */
            public Tree<E> treeFragment = null;
        }
    }

    /**
     * ChildIterator is an iterator over the child trees/leafs of a node which are held in a list referenced by the
     * node. This iterator simply keeps track of the index of the current position within that list that it is iterating
     * over. As new children are added to the node, they are placed on the end of that list. This iterator will not
     * throw ConcurrentModificationExceptions when the underlying list is modified whilst it is iterating. However, it
     * is not synchronized, so only a single thread should be modifying the list or reading from the iterator where
     * overlapping could occurr.
     */
    private class ChildIterator implements Iterator<Tree<E>>
    {
        /** Holds the current offset within the child list. */
        private int index = 0;

        /**
         * Determines whether this iterator has more elements, depending on the current size of the child list.
         *
         * @return <tt>true</tt> if this iterator has more elements, <tt>false</tt> if not.
         */
        public boolean hasNext()
        {
            return (children.size() > index);
        }

        /**
         * Gets the next element in the child list.
         *
         * @return The next element in the child list.
         */
        public Tree<E> next()
        {
            return (children.get(index++));
        }

        /** Remove is not supported by this iterator. */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
