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

/**
 * A simple sub-class of SearchNode to use for the make node test that checks that the right sub-class is created by the
 * {@link SearchNode#makeNode} method.
 */
public class SearchNodeSubClassPrivateConstructor extends SearchNode
{
    /** Creates a new SearchNodeSubClassPrivateConstructor object. */
    private SearchNodeSubClassPrivateConstructor()
    {
        super();
    }

    /** @return */
    public static SearchNode getInstance()
    {
        return new SearchNodeSubClassPrivateConstructor();
    }
}
