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
package com.thesett.aima.learning;

/**
 * LearningFailureException represents the failure of a learning algorithm to run. It does not and should not be used to
 * represent the failure of a learning algorithm to learn a representation when the data and the algorithm mean that
 * this is the correct thing to happen. This exception class should only be used in situations where the algorithm must
 * terminate prematurely becuase it is unable to work as intended.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Represent failure of a learning algorithm to run.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class LearningFailureException extends Exception
{
    /**
     * Creates a new LearningFailureException object.
     *
     * @param message A message explaining the reason for this exception.
     * @param cause   The exception which is the underlying cause, or null if there was none.
     */
    public LearningFailureException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
