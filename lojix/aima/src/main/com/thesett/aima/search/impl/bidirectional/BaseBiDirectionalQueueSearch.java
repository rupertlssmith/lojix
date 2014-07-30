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
package com.thesett.aima.search.impl.bidirectional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.thesett.aima.search.Operator;
import com.thesett.aima.search.QueueBasedSearchMethod;
import com.thesett.aima.search.RepeatedStateFilter;
import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.Successor;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.spi.PathJoinAlgorithm;
import com.thesett.common.error.NotImplementedException;
import com.thesett.common.util.logic.UnaryPredicate;

/**
 * BaseBiDirectionalQueueSearch provides a base implementation for deriving new bidirectional searches from by providing
 * different queue implementations for the forward and reverse portions of the search. For example using a LIFO queue
 * generates depth first search and a FIFO queue generates breadth first. Heuristic searches can easily be implemented
 * by using priority queues with ordering predicates based on a heuristic evaluation.
 *
 * <p>The search proceeds by taking the next element of the forward search fringe and testing to see if it matches one
 * element of the reverse fringe. It then expands the forward fringe with the successor nodes if no match is found. Then
 * it takes the next element of the reverse fringe and does the same matching and expanding process against the forward
 * fringe. The states need to be hashable in order to make the fringe matching efficient.
 *
 * <p>Once a match between the forward and reverse fringes has been found it then walks backwards along the node found
 * on the reverse fringe adding all its states and operations to the forward search path. This class implements a
 * standard algorithm for doing this. It assumes that the state space being searched by both halfs of the search is over
 * the same states. It also assumes that the applied operations in the reverse search node have already been reverse.
 * For example, if the search space was the eight tiles game then the moves stored in the reverse half of the search
 * would have to be stored in the reverse direction to that in which they are applied when really playing the game
 * backwards. This means that two implementations of {@link com.thesett.aima.search.TraversableState} are needed, one
 * that gives its operators reversed.
 *
 * <p>It is possible to supply an alternative path joining algorithm that does know how to reverse the operators. Set
 * this algorithm by calling the {@link #setPathJoinAlgorithm} method. This is an alternative to writing two
 * implementatios of {@link com.thesett.aima.search.TraversableState}
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Bidirectional search state space using specified queue implementations <td> {@link SearchNode}
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   At least one of the directions ought to be a breadth first search. Provide some explanation of this from the
 *         literature as well as some other saliant information about bi-directional searches.
 */
public class BaseBiDirectionalQueueSearch<O, T extends Traversable<O>> implements QueueBasedSearchMethod<O, T>
{
    /** The queue used to control the forward search order. */
    protected Queue<SearchNode<O, T>> forwardQueue;

    /** The queue used to control the reverse search order. */
    protected Queue<SearchNode<O, T>> reverseQueue;

    /** Used to maintain a map of states to search nodes to those states on the forward fringe. */
    protected Map<T, SearchNode<O, T>> forwardFringe;

    /** Used to maintain a map of states to search nodes to those states on the reverse fringe. */
    protected Map<T, SearchNode<O, T>> reverseFringe;

    /** Holds a reference to the start node. */
    protected SearchNode<O, T> startNode;

    /** Holds a reference to the goal node. */
    protected SearchNode<O, T> goalNode;

    /** Holads the goal predicate. */
    protected UnaryPredicate<T> goalPredicate;

    /** Used to hold a reference to an alternative path join algorithm. */
    PathJoinAlgorithm<O, T> pathJoiner;

    /**
     * Constructor that takes an initial search node, a goal node, a forward and a reverse search queue implementation.
     * Different kinds of searches can be instantiated by passing different queue and search nodeimplementations to this
     * constructor.
     *
     * @param startNode    The search start point as a search node.
     * @param goalNode     The search end point as a search node.
     * @param forwardQueue The queue implementation for the forward search.
     * @param reverseQueue The queue implmenetation for the reverse search.
     */
    public BaseBiDirectionalQueueSearch(SearchNode<O, T> startNode, SearchNode<O, T> goalNode,
        Queue<SearchNode<O, T>> forwardQueue, Queue<SearchNode<O, T>> reverseQueue)
    {
        this.forwardQueue = forwardQueue;
        this.reverseQueue = reverseQueue;
        this.startNode = startNode;
        this.goalNode = goalNode;

        // Clear both fringe sets
        forwardFringe = new HashMap<T, SearchNode<O, T>>();
        reverseFringe = new HashMap<T, SearchNode<O, T>>();

        // Add just the start node to the forward queue and fringe set to begin with
        forwardQueue.add(startNode);
        forwardFringe.put(startNode.getState(), startNode);

        // Add just the goal node to the reverse queue and fringe set to begin with
        reverseQueue.add(goalNode);
        reverseFringe.put(goalNode.getState(), goalNode);
    }

    /**
     * Attaches the specified repeated state filtering strategy to the search algorithm.
     *
     * @param filter The repeated state filter to prune the search space with.
     */
    public void setRepeatedStateFilter(RepeatedStateFilter filter)
    {
        throw new NotImplementedException();
    }

    /**
     * Sets the maximum number of search steps that a search method may take. If it fails to find a solution before this
     * number of steps has been reached its {@link #findGoalPath} method should fail and return null. What exactly
     * constitutes a single step, and the granularity of the step size, is open to different interpretation by different
     * search algorithms. The guideline is that this is the maximum number of states on which the
     * {@link com.thesett.aima.search.GoalState#isGoal} test should be performed.
     *
     * @param max the maximum number of states to goal test. If this is zero or less then the maximum number of steps
     *            will not be checked for.
     */
    public void setMaxSteps(int max)
    {
        throw new NotImplementedException();
    }

    /**
     * Adds a start state to the search. Many search algorithms will only accept a single start state in which case the
     * most recent call to this method is taken as the definition of the single start state to use. Some search
     * algorithms may take multiple start states in which case successive calls to this method should be used to
     * establish all the start states.
     *
     * @param startState The start state to add to the collection of start states to begin the search with.
     */
    public void addStartState(T startState)
    {
        throw new NotImplementedException();
    }

    /**
     * Some search algorithms may accept definitions of the goal state in which case the most recent call to this method
     * is taken as the definition of the goal state. In some searches the goal state is not known in advance, in others
     * the purpose of the search is to find a path from the start to the goal. If the search algorithm does not accept a
     * goal state it should throw an exception indicating that this method is not supported by that algorithm.
     *
     * <p/>
     * <p>Note that the {@link com.thesett.aima.search.GoalState} interface itself defines the method
     * {@link com.thesett.aima.search.GoalState#isGoal} which tests if a state is a goal state. It is therefore not
     * necessary to call this method to set goal states where the goal definition is defined already in the states.
     * Setting goal states through this method, when it is supported by an algorithm overrides the goal definition in
     * the states themselves.
     *
     * @param  unaryPredicate The goal predicate.
     *
     * @throws UnsupportedOperationException if the search algorithm does not accept goal states.
     */
    public void setGoalPredicate(UnaryPredicate<T> unaryPredicate) throws UnsupportedOperationException
    {
        this.goalPredicate = unaryPredicate;
    }

    /** Resets the search, clearing out the queue and setting it to contain just the start state nodes. */
    public void reset()
    {
        throw new NotImplementedException();
    }

    /**
     * Implementation of the general bi-dircetional search. The search alternated between taking a forward and a reverse
     * step.
     *
     * @return The first {@link SearchNode} corresponding to a goal state that is found or null if none can be found
     *         after exhaustive searching.
     *
     * @throws SearchNotExhaustiveException If the search stops before finding a solution or exhausting the search
     *                                      space. Different search algorithms may fail for different reasons but in
     *                                      particular it it is worth noting that if the maximum number of steps is
     *                                      reached without exhausting the search space this will cause an exception to
     *                                      be thrown rather than for this method to return null.
     */
    public SearchNode<O, T> findGoalPath() throws SearchNotExhaustiveException
    {
        // Keep running until the queue becomes empty or a goal state is found
        while (!forwardQueue.isEmpty() || !reverseQueue.isEmpty())
        {
            // Only run the forward step of the search if the forward queue is not empty
            if (!forwardQueue.isEmpty())
            {
                // Extract the next node from the forward queue
                SearchNode<O, T> currentForwardNode = forwardQueue.remove();

                // Remove this node from the forward fringe map as it will soon be replaced by more fringe members
                forwardFringe.remove(currentForwardNode.getState());

                // Check the reverse fringe against the next forward node for a match.
                if (reverseFringe.containsKey(currentForwardNode.getState()))
                {
                    // A path from start to the goal has been found. Walk backwards along the reverse path adding all
                    // nodes encountered to the forward path until the goal is reached.
                    return joinBothPaths(currentForwardNode, reverseFringe.get(currentForwardNode.getState()));
                }

                // There was no match so a path to the goal has not been found
                else
                {
                    // Get all of the successor states to the current node
                    Queue<SearchNode<O, T>> newStates = new LinkedList<SearchNode<O, T>>();

                    currentForwardNode.expandSuccessors(newStates, false);

                    // Expand all the successors to the current forward node into the buffer to be searched.
                    forwardQueue.addAll(newStates);

                    // Also add all the successors to the current forward fringe map.
                    for (SearchNode<O, T> nextSearchNode : newStates)
                    {
                        forwardFringe.put(nextSearchNode.getState(), nextSearchNode);
                    }
                }
            }

            // Only run the reverse step of the search if the reverse queue is not empty
            if (!reverseQueue.isEmpty())
            {
                // Extract the next node from the reverse queue
                SearchNode<O, T> currentReverseNode = reverseQueue.remove();

                // Remove this node from the reverse fringe set as it will soon be replaced by more fringe members
                reverseFringe.remove(currentReverseNode.getState());

                // Check the forward fringe against the next reverse node for a match.
                if (forwardFringe.containsKey(currentReverseNode.getState()))
                {
                    // A path from start to goal has been found.
                    // Walk backwards along the reverse path adding all nodes encountered to the foward path until the
                    // goal is reached.
                    return joinBothPaths(forwardFringe.get(currentReverseNode.getState()), currentReverseNode);
                }

                // There was no match so a path to the goal has not been found
                else
                {
                    // Get all of the successor states to the current node (really predecessor state)
                    Queue<SearchNode<O, T>> newStates = new LinkedList<SearchNode<O, T>>();

                    currentReverseNode.expandSuccessors(newStates, false);

                    // Expand all the successors to the current reverse node into the reverse buffer to be searched.
                    reverseQueue.addAll(newStates);

                    // Add all the successors to the current reverse fringe set
                    for (SearchNode<O, T> nextSearchNode : newStates)
                    {
                        reverseFringe.put(nextSearchNode.getState(), nextSearchNode);
                    }
                }
            }
        }

        // No goal state was found so return null
        return null;
    }

    /**
     * Perform the search. This can be called multiple times to get successive results where more than one goal can be
     * found, if the algorithm supports this. In this case it should return null once no more goals can be found.
     *
     * @return The first state corresponding to a goal state that is found or null if none can be found after exhaustive
     *         searching. A return value of null should really mean that exhaustive searching has been completed rather
     *         than some premature termination condition being reached. See the comment below about the
     *         SearchNotExhaustiveException.
     *
     * @throws SearchNotExhaustiveException If the search stops before finding a solution or exhausting the search
     *                                      space. Different search algorithms may fail for different reasons but in
     *                                      particular it it is worth noting that if the maximum number of steps is
     *                                      reached without exhausting the search space this will cause an exception to
     *                                      be thrown rather than for this method to return null.
     */
    public T search() throws SearchNotExhaustiveException
    {
        SearchNode<O, T> path = findGoalPath();

        if (path != null)
        {
            return path.getState();
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets an alternative path joining algorithm to use when a match is found between the forward and reverse portions
     * of the search.
     *
     * @param joiner The path join algorithm to use to combine the forward and reverse paths.
     */
    public void setPathJoinAlgorithm(PathJoinAlgorithm<O, T> joiner)
    {
        // Keep the path joining algorithm
        pathJoiner = joiner;
    }

    /**
     * Once a match has been found between the forward and reverse fringes of the search a path is known to exist from
     * the start to the goal. The path is not complete at this stage because it remains to reverse all of the steps in
     * the backward half of the path and add them to the forward half of the path to produce the complete forward path
     * from start to the goal.
     *
     * <p>This method implements a standard algorithm for doing this. It assumes that the state space being searched by
     * both halfs of the search is over the same states. It also assumes that the applied operations in the reverse
     * search node have already been reverse. For example, if the search space was the eight tiles game then the moves
     * stored in the reverse half of the search would have to be stored in the reverse direction to that in which they
     * are applied when really playing the game backwards. This means that two implementations of
     * {@link com.thesett.aima.search.TraversableState} are needed, one that gives its operators reversed.
     *
     * <p>It is possible to supply an alternative path joining algorithm that does know how to reverse the operators.
     * Set this in this search algorithm by calling the {@link #setPathJoinAlgorithm} method. This is an alternative to
     * writing two implementatios of {@link com.thesett.aima.search.TraversableState}
     *
     * @param  forwardPath a search node for the forward portion of the goal path
     * @param  reversePath a search node for the reverse portion of the goal path
     *
     * @return a search node corresponding to the path right from start to goal
     *
     * @throws SearchNotExhaustiveException If the node implementation does not allow new nodes to be created from old.
     *                                      See the {@link SearchNode#makeNode} method for more information.
     */
    private SearchNode<O, T> joinBothPaths(SearchNode<O, T> forwardPath, SearchNode<O, T> reversePath)
        throws SearchNotExhaustiveException
    {
        // Check if an alternative path join algorithm has been set and delegate to it if so
        if (pathJoiner != null)
        {
            return pathJoiner.joinBothPaths(forwardPath, reversePath);
        }

        // No alternative path join algorithm has been supplied so use this default one
        else
        {
            // Used to hold the current position along the reverse path of search nodes
            SearchNode<O, T> currentReverseNode = reversePath;

            // Used to hold the current position along the forward path of search nodes
            SearchNode<O, T> currentForwardNode = forwardPath;

            // Loop over all nodes in the reverse path checking if the current reverse node is the
            // goal state to terminate on.
            while (!goalPredicate.evaluate(currentReverseNode.getState()))
            {
                // Create a new forward node from the parent state of the current reverse node, the current reverse
                // nodes applied operation and cost, and an increment of one to the path depth
                SearchNode<O, T> reverseParentNode = currentReverseNode.getParent();
                T state = currentReverseNode.getParent().getState();
                Operator<O> operation = currentReverseNode.getAppliedOp();
                float cost = currentReverseNode.getPathCost() - reverseParentNode.getPathCost();

                currentForwardNode = currentForwardNode.makeNode(new Successor<O>(state, operation, cost));

                // Move one step up the reverse path
                currentReverseNode = reverseParentNode;
            }

            // Return the last forward search node found
            return currentForwardNode;
        }
    }
}
