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
 * A random heuristic to use with tests. Takes one of three values randomly.
 */
public class RandomHeuristic implements Heuristic
{
    /**
     * @param  state
     * @param  searchNode
     *
     * @return
     */
    public float computeH(Traversable state, HeuristicSearchNode searchNode)
    {
        float r = (float) Math.random();

        if (r < 0.333f)
        {
            return 0.0f;
        }

        if (r < 0.666f)
        {
            return 0.5f;
        }
        else
        {
            return 1.0f;
        }
    }
}
