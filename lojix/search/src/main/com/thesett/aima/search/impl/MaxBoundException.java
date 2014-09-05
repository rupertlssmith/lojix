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

import com.thesett.aima.search.SearchNotExhaustiveException;

/**
 * MaxBoundException represents the failue of a search algorithm because it has reached a maximum boundary but the
 * search space is not exhausted; there are successor nodes known to exist beyond the fringe of the boundary.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Represent premature termination of a bounded search algorithm because it has reached its maximum bound.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class MaxBoundException extends SearchNotExhaustiveException
{
    /**
     * Creates a new MaxBoundException object.
     *
     * @param message A message explaining that the maximum bound of a search has been reached.
     * @param cause   The underlying exception causing this one. Almost certainly null.
     */
    public MaxBoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
