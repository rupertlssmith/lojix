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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.thesett.aima.search.util.OperatorImpl;
import com.thesett.aima.search.util.backtracking.ReTraversable;

/**
 * This is a test state class used to exercise the classes in the com.thesett.aima.search package.
 *
 * <p/>It represents a tree search space. The depth of the tree and branching factor are set as parameters to the
 * constructor. It provides operations to walk all over the generated tree, right down to the leaves.
 *
 * <p/>The states within the tree are encoded as "seq0;...;seqn". For example the node at depth 3, with parent number 1
 * (at depth 2), the parent of which is number 5 (at depth 1) and sequence number 3 is encoded as "0;5;1;3". Sequence
 * numbers are assigned sequentially to children as they are generated, starting at 0. The root node is 0.
 *
 * <p/>There are no repeated states and no operations that can form a loop through repeated states.
 *
 * <p/>By default there are no goal states in the tree. Goal states may be added by calling the addGoalSequence method
 * with the encoding of a state to mark as a goal. The goal states will be propagated to the child nodes so it is a good
 * idea to set them in the root node before any children are expanded.
 */
public class TestTraversableState extends TraversableState<String> implements GoalState, ReTraversable<String>
{
    /** Sets a default branching factor to be used when the default constructor is called. */
    private static final int DEFAULT_BRANCHING_FACTOR = 3;

    /** Sets a default depth to be used when the default constructor is called. */
    private static final int DEFAULT_DEPTH = 1;

    /** Used to hold the states encoding. */
    public String encoding;

    /** Used to hold the states depth. */
    private int depth;

    /** Used to hold the states sequence number. */
    private int sequence;

    /** Holds the branching factor. */
    private int branchingFactor;

    /** Holds the max depth. */
    private int maxDepth;

    /** Used to hold the goal states. */
    public Set<String> goalStates = new HashSet<String>();

    /** Creates a new TestTraversableState object. */
    public TestTraversableState()
    {
        this.branchingFactor = DEFAULT_BRANCHING_FACTOR;
        this.maxDepth = DEFAULT_DEPTH;

        depth = 0;
        encoding = "0";
        sequence = 0;
    }

    /**
     * Creates a new TestTraversableState object.
     *
     * @param branchingFactor
     * @param maxDepth
     */
    public TestTraversableState(int branchingFactor, int maxDepth)
    {
        this.branchingFactor = branchingFactor;
        this.maxDepth = maxDepth;

        depth = 0;
        encoding = "0";
        sequence = 0;
    }

    /**
     * Creates a new TestTraversableState object.
     *
     * @param branchingFactor
     * @param maxDepth
     * @param depth
     * @param encoding
     * @param sequence
     * @param goalStates
     */
    public TestTraversableState(int branchingFactor, int maxDepth, int depth, String encoding, int sequence,
        Set<String> goalStates)
    {
        this.branchingFactor = branchingFactor;
        this.maxDepth = maxDepth;

        this.depth = depth;
        this.encoding = encoding;
        this.sequence = sequence;
        this.goalStates = goalStates;
    }

    public boolean isGoal()
    {
        return goalStates.contains(encoding);
    }

    public int getDepth()
    {
        return depth;
    }

    public Traversable<String> getChildStateForOperator(Operator op)
    {
        // Parse the operator as an integer
        int child = Integer.valueOf((String) op.getOp());

        // Generate the child state with that sequence number so long as its less than the branching factor
        if (child < branchingFactor)
        {
            return new TestTraversableState(branchingFactor, maxDepth, depth + 1, encoding + ";" + child, child,
                goalStates);
        }
        else
        {
            return null;
        }
    }

    public float costOf(Operator op)
    {
        return 1.0f;
    }

    /**
     * @param  reverse Ignored.
     *
     * @return
     */
    public Iterator<Operator<String>> validOperators(boolean reverse)
    {
        // There should be no operators if they will take the state below the max depth
        if (depth >= maxDepth)
        {
            Collection<Operator<String>> noOps = new ArrayList<Operator<String>>(0);

            return noOps.iterator();
        }

        // Valid operators are 0 to branching factor - 1
        Collection<Operator<String>> ops = new ArrayList<Operator<String>>(branchingFactor);

        for (int i = 0; i < branchingFactor; i++)
        {
            ops.add(new OperatorImpl<String>(Integer.toString(i)));
        }

        return ops.iterator();
    }

    /**
     * The super class method successors is called to ensure that the TraversableState class implementation of this
     * method is being tested. This method is here just to record the fact that the successors were expanded.
     *
     * @param reverse Set if a reverse ordering is to be used.
     */
    public Iterator<Successor<String>> successors(boolean reverse)
    {
        return super.successors(reverse);
    }

    /** Apply any globally visible state changes required by the operator that generated this state. */
    public void applyOperator()
    {
    }

    /** Undo any globally visible state changes made by applying an operator to this states parent state. */
    public void undoOperator()
    {
    }

    public void addGoalSequence(String sequence)
    {
        goalStates.add(sequence);
    }

    public boolean equals(Object o)
    {
        return encoding.equals(((TestTraversableState) o).encoding);
    }

    public int hashCode()
    {
        return encoding.hashCode();
    }

    public String toString()
    {
        return encoding;
    }
}
