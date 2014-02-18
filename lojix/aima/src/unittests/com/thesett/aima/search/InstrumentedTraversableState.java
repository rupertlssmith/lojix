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
package com.thesett.aima.search;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.thesett.aima.search.util.backtracking.ReTraversable;
import com.thesett.common.util.Filterator;
import com.thesett.common.util.Function;
import com.thesett.common.util.StackQueue;

/**
 * InstrumentedTraversableState is a wrapper class that wraps a TraversableState so as to intercept its method calls in
 * order to gather test statistics.
 *
 * <p>Some stats are gathered in statics to make gathering stats accross many states easier.Use the {@link #resetStats}
 * method to clear them out.
 */
public class InstrumentedTraversableState<O> extends TraversableState<O> implements GoalState, ReTraversable<O>
{
    /** Used to hold a reference to a collection to put all goal tested states in for test routines to examine. */
    public static Collection<InstrumentedTraversableState> examinedStates =
        new LinkedList<InstrumentedTraversableState>();

    /** Used to hold a stack of states for checking back-tracking searches back-track in reverse order. */
    private static Queue<Traversable> backtrackStack = new StackQueue<Traversable>();

    /** Holds a reference to the underlying wrapped TraversableState object. */
    Traversable<O> testState;

    /** Used in order to record that the correct node was visited upon backtracking. */
    public boolean correctNodeVisitedOnBacktracking = false;

    /** Used in order to record that the apply operator method was called. */
    public boolean operatorApplied = false;

    /**
     * Used to mark this state as having its successors nodes expanded. Set to true when the {@link #successors} method
     * has been called.
     */
    public boolean expanded = false;

    /** Wraps a TraversableState in this test rig to gather stats on the test calls made to it. */
    public InstrumentedTraversableState(Traversable<O> testState)
    {
        this.testState = testState;
    }

    /** Resets the static test stats. */
    public static void resetStats()
    {
        examinedStates.clear();
        backtrackStack.clear();
    }

    /** Returns whether this state is a goal node. */
    public boolean isGoal()
    {
        // Keep track of the fact that this state was goal tested.
        examinedStates.add(this);

        return ((GoalState) testState).isGoal();
    }

    /**
     * A helper method to perform the goal test without modifiying the examined states collection or else a concurrent
     * modification exception will arise.
     */
    public boolean isGoalHelper()
    {
        return ((GoalState) testState).isGoal();
    }

    /**
     * Returns an enumeration of successors of the state.
     *
     * @param reverse Use a reverse ordering.
     */
    public Iterator<Successor<O>> successors(boolean reverse)
    {
        // Keep track of the fact that this state had its successors expanded.
        expanded = true;

        return new Filterator<Successor<O>, Successor<O>>(testState.successors(reverse),
            new Function<Successor<O>, Successor<O>>()
            {
                public Successor<O> apply(Successor<O> successor)
                {
                    return new Successor<O>(new InstrumentedTraversableState<O>(successor.getState()),
                        successor.getOperator(), successor.getCost());
                }
            });
    }

    /**
     * Returns the state obtained by applying the specified operation. If the operation is not valid then this should
     * return null.
     */
    public Traversable<O> getChildStateForOperator(Operator op)
    {
        return new InstrumentedTraversableState<O>(testState.getChildStateForOperator(op));
    }

    /** Returns the cost of applying the specified operations. */
    public float costOf(Operator op)
    {
        return testState.costOf(op);
    }

    /**
     * Gets all operators valid from this state.
     *
     * @param reverse Don't care about reverse orderings. Ignored.
     */
    public Iterator<Operator<O>> validOperators(boolean reverse)
    {
        return testState.validOperators(reverse);
    }

    /** Apply any globally visible state changes required by the operator that generated this state. */
    public void applyOperator()
    {
        if (testState instanceof ReTraversable)
        {
            ((ReTraversable) testState).applyOperator();

            backtrackStack.offer(testState);

            operatorApplied = true;
        }
        else
        {
            throw new RuntimeException("'testState' is not an instance of ReTraversable.");
        }
    }

    /** Undo any globally visible state changes made by applying an operator to this states parent state. */
    public void undoOperator()
    {
        if (testState instanceof ReTraversable)
        {
            ((ReTraversable) testState).undoOperator();

            Traversable<O> comparator = backtrackStack.poll();

            correctNodeVisitedOnBacktracking = (testState.equals(comparator));
        }
        else
        {
            throw new RuntimeException("'testState' is not an instance of ReTraversable.");
        }
    }

    public String toString()
    {
        return testState.toString();
    }
}
