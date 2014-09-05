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
package com.thesett.aima.search;

/**
 * A dummy test repeated state filter that lets everything through, and is instrumented for checking that the reset
 * method is called.
 */
public class BlankFilter<O, T extends Traversable<O>> implements RepeatedStateFilter<O, T>
{
    /** Used to check whether reset has been called. */
    public boolean resetCalled = false;

    /**
     * Always returns true.
     *
     * @param  state      The seach state to filter. Ignored.
     * @param  parentNode The parent node to filter. Ignored.
     *
     * @return Alayws <tt>true</tt>.
     */
    public boolean evaluate(T state, SearchNode<O, T> parentNode)
    {
        return true;
    }

    /* Sets the test instrumenting reset flag. */
    public void reset()
    {
        resetCalled = true;
    }
}
