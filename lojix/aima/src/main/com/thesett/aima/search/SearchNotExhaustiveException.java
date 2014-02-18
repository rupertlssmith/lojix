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
 * SearchNotExhaustiveException represents the failure of a search algorithm to find a goal or to exhaust the available
 * search space. It does not and should not be used to represent general failures of a search algorithm, for example,
 * resulting from other exceptions encountered during a search. It means that the search has terminated without
 * exhausting the available search space, within the parameters of the search.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Represent failure of a search algorithm to find a goal or exhaust the search space.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SearchNotExhaustiveException extends Exception
{
    /**
     * Creates a new SearchNotExhaustiveException object.
     *
     * @param message A message explaining the reason for the premature termination of the search.
     * @param cause   The underlying exception that caused the termination, or null if there is none.
     */
    public SearchNotExhaustiveException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
