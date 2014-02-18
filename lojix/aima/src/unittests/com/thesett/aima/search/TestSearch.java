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

import java.util.Queue;

import com.thesett.aima.search.impl.BaseQueueSearch;
import com.thesett.common.util.StackQueue;

/**
 * Implements a test queue search built from any search node and queue implementation. Used for testing BaseQueueSearch.
 */
public class TestSearch<O, T extends Traversable<O>> extends BaseQueueSearch<O, T>
{
    /** Creates a new TestSearch object. */
    public TestSearch()
    {
    }

    /**
     * @param  state
     *
     * @return
     */
    public SearchNode<O, T> createSearchNode(T state)
    {
        return new InstrumentedSearchNode(state);
    }

    /** @return */
    public Queue<SearchNode<O, T>> createQueue()
    {
        return new StackQueue<SearchNode<O, T>>();
    }
}
