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
package com.thesett.aima.search.util.backtracking;

import java.util.Collection;
import java.util.Queue;

import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.impl.BaseQueueSearchAlgorithm;
import com.thesett.aima.search.spi.QueueSearchState;
import com.thesett.common.util.logic.UnaryPredicate;

/**
 * Implements a back-tracking search algorithm. This algorithm is mentioned but not explained on page 84 of "Artificial
 * Intelligence, A Modern Approach".
 *
 * <p/>There are a number of different approaches to implementing back-tracking of which this is only one. All searches
 * are back-tracking in the sense that they explore a search space, retreating from fruitless paths and trying to direct
 * effort towards potential solution paths. This back-tracking algorithm is more explicit in that it walks over search
 * nodes in a forward direction, calling the {@link ReTraversable#applyOperator()} method on them as it advances, then
 * when retreating from exhausted search paths, back-tracks over search nodes in the reverse order that it moved
 * forwards over them, calling the {@link ReTraversable#undoOperator()} method as it goes.
 *
 * <p/>For example, in the constraint search described on page 84, as the search moves forward it binds variables, as it
 * back-tracks it unbinds them. The same effect can be achieved without using an explicitly back-tracking search by
 * passing a fresh copy of the variable state to each child search state created. When the search changes direction to a
 * different area of the search space, the abandoned states can simply be forgotten (garbage collected) without being
 * undone. This has the disadvantage of having to replicate a potentially large amount of shared data, but the advantage
 * that the search ordering can be more free without coming up with a way of sharing state.
 *
 * <p/>When the search order is depth first, operators on shared state can be applied cumulatively to the shared state
 * built up by their predecessors. For example, in the constraint search, each new variable binding builds on those
 * already set up. Each undone operator undoes a single variable binding to revert to the binding set of a predecessor.
 * When using a depth first ordering only the states between the root of the search and the state being currently
 * examined will be 'active' in the sense that they have had their operator applied and not undone. That is to say that,
 * under depth first orderings only one search path is active at any one time.
 *
 * <p/>This search algorithm can be used usefully with orderings other that depth first. Consider a search where each
 * child is given a state composed of the parent state plus one more element. The parent state is shared and not
 * replicated. For example, its a linked list, with the child nodes extra element linked into the end in such a way that
 * it is only visible to that child node and not other nodes sharing earlier parts of the same list. The search is now
 * free to move in whatever order it likes. Upon backtracking over exhausted areas of the search child nodes clean up
 * their local piece of the state, so it can be garbage collected. In the constraint search example, each new state can
 * add its variable binding onto the linked list in such a way that it is only visible on search paths from that state
 * onwards. If the search order moves to a completely different search path, the bindings will not be visible there, and
 * the same variable could be bound to a different value on another path. Some degree of indirection in looking up the
 * value of a variable must be introduced to achieve this. With general search orderings multiple search paths may be
 * active at once.
 *
 * <p/>To summarize, a backtracking search with depth first ordering should be used when shared state can be built
 * cummulatively, the space or time requirements of replicating shared state is prohibitive, where the complexity of
 * representing state with path-local visibility is not needed, or where the search ordering of depth first is not an
 * inconvenience. A backtracking search with other orderings can be used where path-local visibility of state can be
 * achieved.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Search based on the queue ordering until a goal node is found or the maximum allowed number of steps is
 *          reached.
 * <tr><td> Apply operators to establish states when searching forwards.
 * <tr><td> Undo operators on states in exact reverse order when back-tracking over states.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   This can also be used with depth bounded and iterative deepening. Can those algorithms be generified to work
 *         over this and max steps interchangeably? They may even work unchanged.
 */
public class BacktrackingAlgorithm<O, T extends ReTraversable<O>> extends BaseQueueSearchAlgorithm<O, T>
{
    /**
     * Holds the most recently discovered goal state, in order that subsequent searches remember to backtrack over it.
     */
    protected SearchNode<O, T> mostRecentGoalNode;

    /** {@inheritDoc} */
    public SearchNode<O, T> search(QueueSearchState<O, T> initSearch, Collection<T> startStates, int maxSteps,
        int searchSteps) throws SearchNotExhaustiveException
    {
        // Initialize the queue with the start states set up in search nodes if this has not already been done.
        // This will only be done on the first call to this method, as enqueueStartStates sets a flag when it is
        // done. Subsequent searches continue from where the previous search left off. Have to call reset on
        // the search method to really start the search again from the start states.
        Queue<SearchNode<O, T>> queue = initSearch.enqueueStartStates(startStates);

        // Get the goal predicate configured as part of the enqueuing start states process.
        UnaryPredicate goalPredicate = initSearch.getGoalPredicate();

        // Backtrack the most recent goal state, if one has been established.
        if (mostRecentGoalNode != null)
        {
            backtrack(mostRecentGoalNode);

            // Clear the most recent goal, now that it has been backtracked if required.
            mostRecentGoalNode = null;
        }

        // Keep running until the queue becomes empty or a goal state is found.
        while (!queue.isEmpty())
        {
            // Extract or peek at the head element from the queue.
            SearchNode headNode = queue.remove();

            // Apply the current nodes operator to establish its shared state.
            ReTraversable reversableState = (ReTraversable) headNode.getState();
            reversableState.applyOperator();

            // Expand the successors into the queue whether the current node is a goal state or not.
            // This prepares the queue for subsequent searches, ensuring that goal states do not block
            // subsequent goal states that exist beyond them.
            headNode.unexaminedSuccessorCount = headNode.expandSuccessors(queue, reverseEnqueue);

            // As the head node is about to be goal tested, reduce the unexamined successor count of its predecessor.
            if (headNode.getParent() != null)
            {
                headNode.getParent().unexaminedSuccessorCount--;
            }

            // Check if the current node is a goal state.
            if (goalPredicate.evaluate(headNode.getState()))
            {
                // Remember this goal node so that subsequent searches remember to backtrack over it.
                mostRecentGoalNode = headNode;

                return headNode;
            }

            // Backtrack over all fully exhausted nodes from the current position, as required.
            backtrack(headNode);

            // Check if there is a maximum number of steps limit and increase the step count and check the limit if so.
            if (maxSteps > 0)
            {
                searchSteps++;

                // Update the search state with the number of steps taken so far.
                initSearch.setStepsTaken(searchSteps);

                if (searchSteps >= maxSteps)
                {
                    // The maximum number of steps has been reached, however if the queue is now empty then the search
                    // has just completed within the maximum. Check if the queue is empty and return null if so.
                    if (queue.isEmpty())
                    {
                        return null;
                    }

                    // Quit without a solution as the max number of steps has been reached but because there are still
                    // more states in the queue then raise a search failure exception.
                    else
                    {
                        throw new SearchNotExhaustiveException("Maximum number of steps reached.", null);
                    }
                }
            }
        }

        // No goal state was found so return null
        return null;
    }

    /** {@inheritDoc} */
    public void setPeekAtHead(boolean flag)
    {
        if (!flag)
        {
            throw new IllegalArgumentException("BacktrackingAlgorithm only supports false for the 'peekAtHead' flag.");
        }
    }

    /** Resests the state of this algorithm by clearing any saved goal node that is pending backtracking. */
    public void reset()
    {
        mostRecentGoalNode = null;
    }

    /**
     * Back-tracks from the specified node, moving succesively upwards through the chain of parent nodes, until a node
     * is encountered that has unexamined successors. This method implements the backtracking searches reverse
     * direction. By checking for the presence of unexamined successors, this method only backtracks where necessary.
     *
     * @param checkNode The search node to start back-tracking from.
     */
    protected void backtrack(SearchNode checkNode)
    {
        while ((checkNode != null) && (checkNode.unexaminedSuccessorCount == 0))
        {
            ReTraversable undoState = (ReTraversable) checkNode.getState();
            undoState.undoOperator();

            checkNode = checkNode.getParent();
        }
    }
}
