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
package com.thesett.aima.learning;

/**
 * ClassifyingFailureException represents the failure of a classifying machine to run. This exception class should only
 * be used in situations where the classifying machine is unable to run. For example, if the data it is classifying is
 * of the wrong type.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Represent failure of a classifying machine to run.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ClassifyingFailureException extends Exception
{
    /**
     * Creates a new ClassifyingFailureException object.
     *
     * @param message A message explaining the reason for this exception.
     * @param cause   The exception which is the underlying cause, or null if there was none.
     */
    public ClassifyingFailureException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
