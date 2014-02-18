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

/**
 * InstrumentedSearchNode is a wrapper class that wraps a SearchNode so as to intercept its method calls in order to
 * gather test statistics about its usage under testing.
 *
 * <p>Some stats are gathered in statics to make gathering stats accross many states easier.Use the {@link #resetStats}
 * method to clear them out.
 */
public class InstrumentedSearchNode<O, T extends Traversable<O>> extends SearchNode<O, T>
{
    /** Used to hold a reference to a collection to put all nodes created with the make node method in. */
    public static Collection<InstrumentedSearchNode> createdNodes = new ArrayList<InstrumentedSearchNode>();

    /** Used to indicate that a repeated state filter was attached to the node. */
    public boolean filterAttached = false;

    /**
     * No-argument constructor needed for Class.newInstance() call in the {@link SearchNode#makeNode} method to work.
     */
    public InstrumentedSearchNode()
    {
    }

    /** Wraps a SearchNode to gather test stats. */
    public InstrumentedSearchNode(T startState)
    {
        super(startState);
    }

    /** Resets the static test stats. */
    public static void resetStats()
    {
        createdNodes = new ArrayList<InstrumentedSearchNode>();
    }

    /**
     * Attaches the specified repeated state filtering strategy to the search node. This strategy is propagated into all
     * successor nodes generated from this one.
     */
    public void setRepeatedStateFilter(RepeatedStateFilter filter)
    {
        // Keep track of the fact that a repeated state filter was attached.
        filterAttached = true;

        super.setRepeatedStateFilter(filter);
    }

    /**
     * Makes a new node of the same type as this one from a Successor state.
     *
     * @todo Consider letting the exceptions fall through as some kind of search failure exception.
     */
    public SearchNode<O, T> makeNode(Successor successor) throws SearchNotExhaustiveException
    {
        InstrumentedSearchNode<O, T> node = (InstrumentedSearchNode<O, T>) super.makeNode(successor);

        // Keep track of the newly created node for test stats.
        createdNodes.add(node);

        return node;
    }
}
