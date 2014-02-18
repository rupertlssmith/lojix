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
package com.thesett.aima.search.util.informed;

import com.thesett.aima.search.Traversable;

/**
 * A dummy heuristic implementation for use in tests. This heuristic always returns 0 so it provides no helpfull
 * information at all to the search.
 */
public class DummyHeuristic implements Heuristic
{
    /**
     * @param  state
     * @param  searchNode
     *
     * @return
     */
    public float computeH(Traversable state, HeuristicSearchNode searchNode)
    {
        return 0.0f;
    }
}
