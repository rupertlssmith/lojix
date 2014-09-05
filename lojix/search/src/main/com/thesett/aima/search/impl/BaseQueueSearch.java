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
package com.thesett.aima.search.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;

import com.thesett.aima.search.QueueBasedSearchMethod;
import com.thesett.aima.search.RepeatedStateFilter;
import com.thesett.aima.search.SearchNode;
import com.thesett.aima.search.SearchNotExhaustiveException;
import com.thesett.aima.search.Traversable;
import com.thesett.aima.search.spi.QueueSearchAlgorithm;
import com.thesett.aima.search.spi.QueueSearchState;
import com.thesett.common.util.logic.UnaryPredicate;

/**
 * BaseQueueSearch provides a base implementation for deriving new searches from by providing different queue
 * implementations. For example using a LIFO queue generates depth first search and a FIFO queue generates breadth
 * first. Heuristic searches can easily be implemented by using priority queues with ordering based on a heuristic
 * evaluation. Uniform cost search can be implement by using a priority queue with an ordering based on the path cost.
 *
 * <p>The actual search algorithm is factored out of this class and provided by classes that implement the
 * {@link QueueSearchAlgorithm} interface. Roughly speaking all queue search algorithms are quite similar; they procede
 * by extracting the head item from the queue buffer and testing to see if it is a goal state, if it is then the search
 * terminates on that {@link SearchNode}, otherwise the accessible states from the present search node are expanded into
 * the queue and the search continues with the next element. See the search algorithm implementations for the finer
 * details of each. This class uses the {@link MaxStepsAlgorithm} by default.
 *
 * <p>Depending on the queue implementation the search nodes will be extracted from it in different orders and this is
 * what determines the order in which elements are examined by the search algorithms. The queue implementation must be
 * provided by concrete sub-classes of this base class.
 *
 * <p>Different kinds of search node can be used to capture different information about the states being searched over
 * and this in turn can be used by the queue implementations and ordering functions to control the way in which the
 * search proceeds. The most obvious example is to extend the basic search node with a heuristic one that computes
 * heuristics for the states and then to provide an ordering over heuristics (Greedy, or A-Star for example) to control
 * the search.
 *
 * <p>As specified in the {@link QueueBasedSearchMethod} interface this class provides a way to plug in a repeated state
 * filter.
 *
 * <p>These four aspects of the search routine, the basic algorithm, the search node type, the queue and the rejected
 * state pool can all be combined independently of one another to provide an array of different queue based search
 * techniques in a very flexible manner. It is necessary though to understand each of the components separately in order
 * to be able to combine them together successfully. Implementations of many standard search routines are provided in
 * this package by the combination of plug-in elements, consult the implementations for the details.
 *
 * <p>Generally speaking if the queue should become empty without a goal state being found then the search algorithm
 * should return null and terminate. However, it should only do this if the search space is known to have been searched
 * completely. Searches that terminate prematurely and know that there are still unsearched states to work with should
 * terminate by throwing a {@link SearchNotExhaustiveException} or a specific sub-class of it. Returning with null must
 * be reserved for the case where it is known that no solutions exist so there is no point in searching any further.
 *
 * <p>This class implements {@link QueueSearchState} interface. This is a call-back interface that provides a limited
 * view onto this class to the {@link QueueSearchAlgorithm} implementations so that they can call its
 * {@link #enqueueStartStates} method to initialize the queue ready for searching. This was done for the sake of hiding
 * the full implementation of this class from the search algorithms.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Combine implementations of queue, search node, repeated state filter and basic algorithm into a search.
 *     <td> {@link SearchNode}, {@link RepeatedStateFilter}, {@link QueueSearchAlgorithm}, {@link QueueSearchState}
 * <tr><td> Set up the start states for a search.
 * <tr><td> Set a maximum number of search steps allowable.
 * <tr><td> Reset a search algorithm.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BaseQueueSearch<O, T extends Traversable<O>> implements QueueBasedSearchMethod<O, T>,
    QueueSearchState<O, T>
{
    /** The queue used to control the search order. */
    protected Queue<SearchNode<O, T>> queue;

    /** Used to hold the start states. */
    protected Collection<T> startStates;

    /** Holds a reference to the repeated state filtering strategy. */
    protected RepeatedStateFilter<O, T> repeatedStateFilter;

    /** Holds the goal predicate for evaluating potential goal states with. */
    protected UnaryPredicate<T> goalPredicate;

    /**
     * Used to hold the maximum number of search steps to perform before quitting with no solution. If this is zero or
     * less then this is taken to mean that no maximum limit is in effect.
     */
    protected int maxSteps;

    /** Used to hold the number of search steps taken so far in a search. */
    protected int searchSteps;

    /** Holds the queue search algorithm to run. */
    protected QueueSearchAlgorithm<O, T> searchAlgorithm;

    /** Flag used to monitor whether or not the start states have already been enqueued. */
    protected boolean enqueuedOnce;

    /**
     * Constructor takes an initial search node, and a search queue. Specific searches can be instantiated by choosing
     * specific queues in the constructor.
     */
    public BaseQueueSearch()
    {
        // Create an empty collection of start states.
        startStates = new ArrayList<T>();

        // Set up the max steps algorithm as the default.
        searchAlgorithm = new MaxStepsAlgorithm<O, T>();
    }

    /**
     * This abstract method should be overriden to turn states into search nodes. Different implementation of queue
     * based algorithms can control the way they search by using different search node implementations. This method
     * allows the type of search node to be abstracted out of this search method to be supplied by concrete
     * implementations.
     *
     * @param  state The state to create a {@link SearchNode} from.
     *
     * @return The {@link SearchNode} for the specified state.
     */
    public abstract SearchNode<O, T> createSearchNode(T state);

    /**
     * This abstract method should be overriden to return an empty queue of search nodes. Different implementations of
     * queue search algorithms can control the way the search proceeds by using different queue implementations. This
     * method allows the type of queue to be abstracted out of this search method to be supplied by concrete
     * implementations.
     *
     * @return An empty queue of search nodes.
     */
    public abstract Queue<SearchNode<O, T>> createQueue();

    /**
     * Attaches the specified repeated state filtering strategy to the search algorithm. This should be called prior to
     * starting a search for the filter to take effect.
     *
     * @param filter The repeated state filter to prune the search space with.
     */
    public void setRepeatedStateFilter(RepeatedStateFilter filter)
    {
        // Keep the reference to the filter.
        this.repeatedStateFilter = filter;
    }

    /**
     * Sets the maximum number of goal tests that this search will perform before quiting without a solution. If the max
     * is 0 or negative then the maximum step limit is lifted.
     *
     * @param max The number of search states to examine before stopping the search without a solution.
     */
    public void setMaxSteps(int max)
    {
        maxSteps = max;
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
        // Add the new start state to the collection of start states
        startStates.add(startState);
    }

    /**
     * Some search algorithms may accept definitions of the goal state in which case the most recent call to this method
     * is taken as the definition of the goal state. In some searches the goal state is not known in advance, in others
     * the purpose of the search is to find a path from the start to the goal. If the search algorithm does not accept a
     * goal state it should throw an exception indicating that this method is not supported by that algorithm.
     *
     * <p/>Note that the {@link com.thesett.aima.search.GoalState} interface itself defines the method
     * {@link com.thesett.aima.search.GoalState#isGoal} which tests if a state is a goal state. It is therefore not
     * necessary to call this method to set goal states where the goal definition is defined already in the states.
     * Setting goal states through this method, when it is supported by an algorithm overrides the goal definition in
     * the states themselves.
     *
     * @param  goalPredicate The predicate that evaluates search states to check if they are goals.
     *
     * @throws UnsupportedOperationException If the search algorithm does not accept goal predicates.
     */
    public void setGoalPredicate(UnaryPredicate<T> goalPredicate) throws UnsupportedOperationException
    {
        this.goalPredicate = goalPredicate;
    }

    /**
     * Provides the goal predicate for the search.
     *
     * @return The goal predicate for the search.
     */
    public UnaryPredicate<T> getGoalPredicate()
    {
        return goalPredicate;
    }

    /** Resets the search, clearing out the queue and setting it to contain just the start state node. */
    public void reset()
    {
        // Clear out the start states.
        startStates.clear();
        enqueuedOnce = false;

        // Reset the queue to a fresh empty queue.
        queue = createQueue();

        // Clear the goal predicate.
        goalPredicate = null;

        // Reset the maximum steps limit
        maxSteps = 0;

        // Reset the number of steps taken
        searchSteps = 0;

        // Reset the repeated state filter if there is one.
        if (repeatedStateFilter != null)
        {
            repeatedStateFilter.reset();
        }

        // Reset the search alogorithm if it requires resettting.
        searchAlgorithm.reset();
    }

    /**
     * This logic enqueues all the start states, calling the {@link #createSearchNode} method that concrete sub-classes
     * implement to specify the search node type. If there is a repeated state filter set up then this is attached to
     * the search nodes. This is called at the start of the search method to set up the queue into a state which is
     * ready for the search algorithm.
     *
     * @param  startStates The set of start states to being the search at.
     *
     * @return The queue of search nodes for each of the start states.
     *
     * @throws IllegalStateException if no start states have been set for the search.
     */
    public Queue<SearchNode<O, T>> enqueueStartStates(Collection<T> startStates)
    {
        // Check that there are some start states
        if (!startStates.isEmpty())
        {
            // Only enqueue the start states if they have not already been enqueued.
            if (!enqueuedOnce)
            {
                // Enqueue all the start states
                for (T nextStartState : startStates)
                {
                    // If the goal predicate has not been defined, try to set up the default from the initial search
                    // states.
                    if (goalPredicate == null)
                    {
                        goalPredicate = nextStartState.getDefaultGoalPredicate();
                    }

                    // Make search nodes out of the start states using the abstract search node creation method,
                    // subclasses override this to create different kinds of search node.
                    SearchNode newStartNode = createSearchNode(nextStartState);

                    // Check if a repeated state filter has been applied and attach it to the start node if so.
                    if (repeatedStateFilter != null)
                    {
                        // Attach it to the start node.
                        newStartNode.setRepeatedStateFilter(repeatedStateFilter);
                    }

                    // Insert the new start search node onto the queue.
                    queue.offer(newStartNode);
                }
            }
        }
        else
        {
            // There are no start states so raise an exception because the search cannot be run without a start state.
            throw new IllegalStateException("Cannot start the search because there are no start states defined. " +
                "Queue searches require some start states.", null);
        }

        // Check that a goal predicate is set up.
        if (goalPredicate == null)
        {
            throw new IllegalStateException("Cannot start the search because there is no goal predicate.", null);
        }

        // Set the enqueuedOnce flag to indicate that the start states have already been enqueued.
        enqueuedOnce = true;

        return queue;
    }

    /**
     * Indicates whether or not the start states have already been enqueued.
     *
     * @return <tt>true</tt> if the start states have already been enqueued, <tt>false</tt> otherwise.
     */
    public boolean isEnqueuedOnce()
    {
        return enqueuedOnce;
    }

    /**
     * Resets the flag that indicates whether or not start states have already been enqueued. Iterative algorithms will
     * use this, for example, to allow their search to be reset to the begining before searching to a higher search
     * bound.
     */
    public void resetEnqueuedOnceFlag()
    {
        enqueuedOnce = false;
    }

    /**
     * This provides a call back that search algorithms use to update the number of search steps taken. They can do this
     * at the end of the search, so that it is only updated once to save on the number of calls, or as the search is
     * running, which will be slower but provide runtime statistics.
     *
     * @param steps The number of search states seen so far.
     */
    public void setStepsTaken(int steps)
    {
        searchSteps = steps;
    }

    /**
     * This provides a call back that search algorithms use to get the number of search steps taken so far.
     *
     * @return The number of search states seen so far.
     */
    public int getStepsTaken()
    {
        return searchSteps;
    }

    /**
     * Implementation of the general queue search. The basic algorithm is simple: take the next element from the queue
     * and goal test it. If it is a goal then return it and stop the search. If it is not a goal then expand its
     * successor states and enqueue them and then repeat the procedure.
     *
     * @return The first {@link SearchNode} corresponding to a goal state that is found or null if none can be found
     *         after exhaustive searching. A return value of null should really mean that exhaustive searching has been
     *         completed rather than some premature termination condition being reached. See the comment below about the
     *         SearchNotExhaustiveException.
     *
     * @throws SearchNotExhaustiveException If the search stops before finding a solution or exhausting the search
     *                                      space. Different search algorithms may fail for different reasons but in
     *                                      particular it it is worth noting that if the maximum number of steps is
     *                                      reached without exhausting the search space this will cause an exception to
     *                                      be thrown rather than for this method to return null.
     */
    public SearchNode<O, T> findGoalPath() throws SearchNotExhaustiveException
    {
        // Delegate the search to the pluggable search algorithm.
        return searchAlgorithm.search(this, startStates, maxSteps, searchSteps);
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
     * Allows different queue search algorithms to replace the default one.
     *
     * @param algorithm The search algorithm to use.
     */
    protected void setQueueSearchAlgorithm(QueueSearchAlgorithm<O, T> algorithm)
    {
        this.searchAlgorithm = algorithm;
    }
}
