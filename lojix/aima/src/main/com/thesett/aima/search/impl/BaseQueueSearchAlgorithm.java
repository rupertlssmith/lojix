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
package com.thesett.aima.search.impl;

import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.spi.QueueSearchAlgorithm;

/**
 * BaseQueueSearchAlgorithm provides a base for implementing queue based search algorithms. This base class provides a
 * 'peekAtHeadFlag' that ensures that a nodes successors are expanded before it is removed form the queue, allowing its
 * ordering to bo compared with its successors for searches where the search path from root to current node may be
 * discontinuous. It provides a 'reverseEnqueue' flag for stack based searches to ensure child nodes are expanded
 * backwards for intuitive left-to-right search ordering of child nodes.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Maintain the peek at head flag.
 * <tr><td> Maintain the reverse enqueue flag.
 * <tr><td> Define the default reset behaviour as doing nothing.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BaseQueueSearchAlgorithm<O, T extends Traversable<O>> implements QueueSearchAlgorithm<O, T>
{
    /** The peek at head flag indicates that the head is only peeked at before expanding successors. */
    protected boolean peekAtHead = false;

    /** The reverse enqueue order flag, indicates that successors should be expanded in reverse order. */
    protected boolean reverseEnqueue = false;

    /**
     * Sets the peek at head flag. When this is set, the head node of the queue to be examined is not removed from the
     * queue prior to its successors being expanded. The node to goal check is removed after this, which means that the
     * head node is ordered with its successors, rather than always being taken ahead of them. Depending on the queue
     * type, the node to be examined next may not be the same node as was at the head of the queue prior to expanding
     * successors.
     *
     * @param flag The value of the peek at head flag to use.
     */
    public void setPeekAtHead(boolean flag)
    {
        peekAtHead = flag;
    }

    /**
     * Sets the value of the reverse queue flag. This is used to allow FIFO based queues to traverse successors left to
     * right.
     *
     * @param flag The value of the reverse enqueue order flag.
     */
    public void setReverseEnqueueOrder(boolean flag)
    {
        reverseEnqueue = flag;
    }

    /**
     * Resests the state of this algorithm. Some search algorithms may preserve state between successive invocations.
     * This method is intended to be called when the entire search is reset (see
     * {@link com.thesett.aima.search.QueueBasedSearchMethod#reset}), to clear any such state.
     *
     * <p/>Does nothing by default as most algorithms do not have any state to clear. Override when state is required to
     * be cleared.
     */
    public void reset()
    {
    }
}
