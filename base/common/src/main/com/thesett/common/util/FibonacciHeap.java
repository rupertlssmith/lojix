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

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;

import com.thesett.common.error.NotImplementedException;

/**
 * A Fibonacci heap (F-heap) is a collection of heap-ordered trees. F-heaps are a type of data structure in which the
 * work that must be done to reorder the structure is postponed until the very last possible moment. F-heaps are useful
 * for algorithms involving graph data structures, such as those used for computing shortest paths in computer networks.
 * The common operations supported by F-heaps are insert, find minimum, extract minimum and merge. These operations are
 * described below:
 *
 * <ul>
 * <li>Insert. A new node is inserted into the heap by simply adding the node to the root list and updating the pointer
 * to the minimum node if necessary.</li>
 * <li>Find minimum. This operation simply returns the node in the heap with the minimum key. This node is constantly
 * referenced, so no real work must be done here.</li>
 * <li>Merge. Two Fibonacci heaps are joined into one by simply combining the two circular root lists into one larger,
 * circular root list. The mimimum node is set to the smaller of the minimums of the two heaps.</li>
 * <li>Extract minimum. First, each of the minimum node's children is added to the root list and the minimum node itself
 * is deleted from the list. Finally, roots of equal degree in the root list are combined until at most one root of each
 * degree remains.</li>
 * </ul>
 *
 * <p>Operations on F-heaps can be classified into two categories, those that involve deleting an element and those that
 * do not; with the former class of operations running in amortized O(log(n)) time and the latter in O(1) time.
 * Although, F-heaps are relatively complex to code and do not support quick searching, they have advantages over other
 * types of heaps because they are the only known data structure that can perform the decrease-key operation in constant
 * amortized time.
 *
 * <p>The kth order binomial tree, Bk, is defined recursilvely. B0 is a single node. For all k > 0, Bk consists of two
 * copies of Bk-1 that have been linked together, meaning that the root of one Bk-1 has become a new child of the other
 * root. Binomial trees have the following useful properties:
 *
 * <ul>
 * <li>The root of Bk has degree k.</li>
 * <li>The children of the root of Bk are the roots of B0, B1, ..., Bk-1.</li>
 * <li>Bk has height k.</li>
 * <li>Bk has 2^k nodes.</li>
 * <li>Bk can be obtained from Bk-1 by adding a new child to every node.</li>
 * <li>Bk has k-choose-d nodes at depth d, for all 0 <= d <= k.</li>
 * <li>Bk has 2^(k-h-1) nodes with height h, for all 0 <= h <= k, and one node (the root) with height k.</li>
 * </ul>
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Insert elements onto the heap.
 * <tr><td>Report the minimum element very efficiently.
 * <tr><td>Combine two heaps into one.
 * <tr><td>Remove the minimum element.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Add the decrease key and delete operations. The caller must correctly identify the node to be
 *         decreased/removed. Probably the best way to do this is to add another version of the offer method that return
 *         the node for new elements inserted. It is then up to the caller to keep track of this node for later
 *         decrease/remove operations if it needs to do them.
 */
public class FibonacciHeap<E> extends AbstractHeap<E>
{
    /** Node annotations. See {@link Node} for details. */
    private enum Annotation
    {
        /** Used to mark a node in the heap. */
        Marked,

        /** Used to decrease a node to minus infinity in the heap prior to deleting it. */
        MinusInfinity
    }

    /** Holds a reference to the minimum node on the heap. This is always maintained for fast access. */
    private Node minNode = null;

    /** Builds a heap that is ordered on the natural ordering of its elements. */
    public FibonacciHeap()
    {
        super(null);
    }

    /**
     * Builds a heap that is ordered by the specified comparator.
     *
     * @param comparator The comparator to use to order the heap elements.
     */
    public FibonacciHeap(Comparator<? super E> comparator)
    {
        super(comparator);
    }

    /**
     * Returns an iterator over the elements in this heap.
     *
     * @return An iterator over the elements in this heap.
     */
    public Iterator<E> iterator()
    {
        throw new NotImplementedException();
    }

    /**
     * Inserts the specified element into this heap.
     *
     * @param  o The data element to add to the heap.
     *
     * @return True if it was succesfully added, false if not. (This implmentation always returns true).86
     */
    public boolean offer(E o)
    {
        // Make a new node out of the new data element.
        Node newNode = new Node(o);

        // Check if there is already a minimum element.
        if (minNode != null)
        {
            // There is already a minimum element, so add this new element to its right.
            newNode.next = minNode.next;
            newNode.prev = minNode;

            minNode.next.prev = newNode;
            minNode.next = newNode;

            // Compare the new element with the minimum and update the minimum if neccessary.
            updateMinimum(newNode);
        }

        // There is not already a minimum element.
        else
        {
            // Update the new element previous and next references to refer to itself so that it forms a doubly linked
            // list with only one element. This leaves the data structure in a suitable condition for adding more
            // elements.
            newNode.next = newNode;
            newNode.prev = newNode;

            // Set the minimum element to be the new data element.
            minNode = newNode;
        }

        // Increment the count of data elements in this collection.
        size++;

        // Return true to indicate that the new data element was accepted into the heap.
        return true;
    }

    /**
     * Retrieves, but does not remove, the (head) minimum element of this heap, returning null if this heap is empty.
     *
     * @return The head element of the hea, or null if the heap is empty.
     */
    public E peek()
    {
        // Return the minimum element
        return minNode.element;
    }

    /**
     * Retrieves and removes the (head) minimum element of this heap, or null if this heap is empty.
     *
     * @return The head element of the hea, or null if the heap is empty.
     */
    public E poll()
    {
        // Check if there is only one element in the heap.
        if (size == 1)
        {
            E result = minNode.element;

            // Set the minimum to null.
            minNode = null;

            // Decrease the count of elements in the heap.
            size--;

            return result;
        }

        // Check that there is a minimum element to return.
        else if (minNode != null)
        {
            // Get the minimum value that is to be returned.
            E result = minNode.element;

            // Promote the minimum nodes children into the root list but don't update their parent references. Updating
            // their parent references will be done later.
            if (minNode.degree > 0)
            {
                insertNodes(minNode, minNode.child);
            }

            // Cut the minimum node out of the root list and update the minimum to be old minimums next node. This
            // node may not really be the minimum but it is taken to be the initial candidate for a new minimum. The
            // real minimum will be scanned for later.
            minNode.next.prev = minNode.prev;
            minNode.prev.next = minNode.next;

            minNode = minNode.next;

            // The consolidation process will merge all the binomial trees of the same order until there are none
            // of duplicate size left. As each binomial tree of order k, Bk, holds 2^k nodes the biggest tree
            // possible for a heap of n nodes will be log to the base 2 of the next biggest power of 2 above or
            // equal to n. The next section of code creates an array with this many elements, indexed by the order
            // of the binomial tree, that is used to keep track of what tree orders currently exist during the
            // consolidation process.
            Node[] tree = (Node[]) Array.newInstance(minNode.getClass(), ceilingLog2(size - 1) + 1);

            // Loop through the root list, setting parent references to null and consolidating the remainder of the heap
            // into binomial trees. The loop begins at the next node after the min node and finishes on the min node
            // itself. This means that the min node is always the very last in the root list to be examined and
            // consolidated ensuring that it cannot have been removed from the root list before the loop gets to it. The
            // terminal min node is explicitly referenced because the consolidation process will update the min node
            // during the loop.
            Node nextNode = null;
            Node nextNextNode = minNode.next;
            Node terminalNode = minNode;

            do
            {
                // Move on to the next node.
                nextNode = nextNextNode;

                // Work out what the next next node will be at the start of the loop as manipulations durings
                // the loop may override the next node reference of the current next node.
                nextNextNode = nextNode.next;

                // Update parent references to null.
                nextNode.parent = null;

                // Update the minimum if the current root list element is smaller than the current best candidate.
                updateMinimum(nextNode);

                // Consolidate the remainder of the heap into binomial trees by merging duplicate trees of equal size.
                // Loop until no tree with the same size as the current one exists.
                int degree = nextNode.degree;
                Node parentNode = nextNode;

                while (tree[degree] != null)
                {
                    // Clear the binomial tree of this size from the tree array as it is about to be merged with the
                    // current node and a new tree of twice the size created.
                    Node mergeNode = tree[degree];

                    tree[degree] = null;

                    // Compare the roots of the two trees to be merged to decide which is the smaller and should form
                    // the single root of the merged tree.
                    if (compare(mergeNode, parentNode) < 0)
                    {
                        // Swap the two nodes.
                        Node temp = mergeNode;

                        mergeNode = parentNode;
                        parentNode = temp;
                    }

                    // Cut the tree rooted by the larger root node from the root list.
                    mergeNode.next.prev = mergeNode.prev;
                    mergeNode.prev.next = mergeNode.next;
                    mergeNode.next = mergeNode;
                    mergeNode.prev = mergeNode;

                    // Paste the tree rooted by the larger root node into the tree with the smaller root node.
                    mergeNode.parent = parentNode;

                    // Check if the smaller node (the parent) already has some children.
                    if (parentNode.child != null)
                    {
                        // Stitch the larger node into the circular doubly linked list of children.
                        insertNodes(parentNode.child, mergeNode);

                        // Update all the parent references of the newly added children.
                    }

                    // The smaller node does not already have children.
                    else
                    {
                        // Set the larger node (of degree 1) as its first child.
                        parentNode.child = mergeNode;

                        // Make sure the child node forms a doubly linked list with itself.
                        mergeNode.next = mergeNode;
                        mergeNode.prev = mergeNode;
                    }

                    // Bump up by one the order of the smaller root node to which the other tree of equal size was
                    // added.
                    parentNode.degree++;

                    // Continue the scan for duplicate trees on the next larger degree.
                    degree++;
                }

                tree[degree] = parentNode;
            }
            while (nextNode != terminalNode);

            /*
             * String out = "At End: ["; for (int i = 0; i < tree.length; i++) out += (i == tree.length - 1) ? tree[i] :
             * tree[i] + ", "; out += "]"; log.info(out);
             */

            // Decrease the count of elements in the heap.
            size--;

            // Return the minimum element.
            return result;
        }

        // There is no minimum element so return null.
        else
        {
            return null;
        }
    }

    /**
     * Calculates the smallest integer value, m, such that m^2 >= n. The ceiling log2 of n.
     *
     * @param  n The value to calulate ceiling log 2 of.
     *
     * @return The smallest integer value, m, such that m^2 >= n.
     *
     * @todo   This can possibly be done more neatly. Consult Hackers Delight or aggregate.org for the following
     *         algorithm: y = (x & (x - 1)); y |= -y; y >>= (WORDBITS - 1); x |= (x >> 1); x |= (x >> 2); x |= (x >> 4);
     *         x |= (x >> 8); x |= (x >> 16); #ifdef LOG0UNDEFINED return(ones(x) - 1 - y); #else return(ones(x >> 1) -
     *         y); #endif
     */
    private static int ceilingLog2(int n)
    {
        int oa;
        int i;
        int b;

        oa = n;
        b = 32 / 2;
        i = 0;

        while (b != 0)
        {
            i = (i << 1);

            if (n >= (1 << b))
            {
                n /= (1 << b);
                i = i | 1;
            }
            else
            {
                n &= (1 << b) - 1;
            }

            b /= 2;
        }

        if ((1 << i) == oa)
        {
            return i;
        }
        else
        {
            return i + 1;
        }
    }

    /**
     * Compares the specified node with the minimum and updates the minimum if neccessary. If a comparator was used to
     * create this heap then this comparator is used to perform the comparison. If no comparator was set then the
     * natural ordering of the element type is used. The element must implement the Comparable interface to support a
     * natural ordering. If it does not there will be a class cast exception thrown.
     *
     * @param  node the node to compare with the current minimum.
     *
     * @throws ClassCastException if the element in the specified node cannot be compared with elements currently in the
     *                            heap according to the heap's ordering.
     *
     * @todo   Use the compare method.
     */
    private void updateMinimum(Node node)
    {
        // Check if a comparator was set.
        if (entryComparator != null)
        {
            // Use the comparator to compare the candidate new minimum with the current one and check if the new one
            // should be set.
            if (entryComparator.compare(node.element, minNode.element) < 0)
            {
                // Update the minimum node.
                minNode = node;
            }
        }

        // No comparator was set so use the natural ordering.
        else
        {
            // Cast the candidate new minimum element into a Comparable and compare it with the existing minimum
            // to check if the new one should be set.
            if (((Comparable) node.element).compareTo(minNode.element) < 0)
            {
                // Update the minimum node.
                minNode = node;
            }
        }
    }

    /**
     * Compares two heap nodes. The comparison performed is dependant on whether a comparator has been set or the
     * natural ordering is to be used.
     *
     * @param  node1 The first node to compare.
     * @param  node2 The second node to compare.
     *
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object.
     */
    private int compare(Node node1, Node node2)
    {
        // Check if a comparator was set.
        if (entryComparator != null)
        {
            // Use the comparator to compare.
            return entryComparator.compare(node1.element, node2.element);
        }

        // No comparator was set so use the natural ordering.
        else
        {
            // Cast one of the elements into a Comparable and compare it with the other.
            return ((Comparable) node1.element).compareTo(node2.element);
        }
    }

    /**
     * Inserts a single node or a circular doubly linked list of nodes into a list next to the specified node. I does
     * not matter if the specified nodes are singletons or part of a chain as they will be correctly linked in in either
     * case so long as their prev and next references form a loop with themselves.
     *
     * @param node    the node or node within a chain that the new node chain is to be inserted next to.
     * @param newNode a single node or a node within a chain that is to be linked into the root list next to the minimum
     *                node.
     */
    private void insertNodes(Node node, Node newNode)
    {
        // Keep a reference to the next node in the node's chain as this will be overwritten when attaching the node
        // or chain into the root list.
        Node oldNodeNext = newNode.next;

        // Break open the node's chain and attach it into the root list.
        newNode.next.prev = node;
        newNode.next = node.next;

        // Break open the root list chain and attach it to the new node or chain.
        node.next.prev = newNode;
        node.next = oldNodeNext;
    }

    /**
     * Every heap node maintains a reference to (up to) four other elements: its parent, one of its children, the next
     * node and the previous node. The root node has no parent and leaf nodes have no children. The next and previous
     * nodes are nodes at the same level in the heap or one of its binomial trees. All such nodes are kept in a circular
     * doubly linked list.
     *
     * <p>Nodes may be specially annotated when keys are decreased or deleted. Deletion is equivalent to decreasing the
     * key to minus infinity and then extracting the minimum. An annotation is used to tag a key as being minus
     * infinity. When a key is decreased its parent must be marked to indicate that one of its children has been
     * promoted, or if it is already marked then it must also be promoted and the mark removed.
     */
    private class Node
    {
        /** The data element. */
        E element;

        /**
         * The degree of this node. This is the number of children it has but not including grand-children or below.
         * This is also equal to the order of the binomial tree that this node head forms the root of. See the class
         * level comment for {@link FibonacciHeap} for a reminder of the properties of binomial trees.
         */
        int degree = 0;

        /** Used to hold the parent of this node. */
        Node parent = null;

        /** Used to hold the first child of this node. */
        Node child = null;

        /** Used to hold the previous node in the circular list at the same level as this node. */
        Node prev = null;

        /** Used to hold the next node in the circular list at the same level as this node. */
        Node next = null;

        /** Used to indicate special annotations on this node. */
        Annotation annotation = null;

        /**
         * Creates a new Node object for a given data element.
         *
         * @param element The data element to create a node for.
         */
        Node(E element)
        {
            this.element = element;
        }

        // public String toString() { return "" + degree; }// + " " + element; }
    }
}
