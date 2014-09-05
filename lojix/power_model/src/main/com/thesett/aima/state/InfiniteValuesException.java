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
package com.thesett.aima.state;

/**
 * InfiniteValuesException is used to indicate that the set of values that a property of an object can take on, or that
 * an {@link OrdinalAttribute} can take on cannot be explicitly listed because there are not a finite number of them or
 * because they cannot be mapped onto the natural numbers as a generated sequence.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Indicate that a set of values cannot be listed because it is infinite.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class InfiniteValuesException extends RuntimeException
{
    /**
     * Creates a new InfiniteValuesException object.
     *
     * @param message A description of the reason for this exception being thrown.
     * @param cause   The underlying throwable cause of this exception if there is one. Set to null if there is no
     *                underlying throwable that caused this one.
     */
    public InfiniteValuesException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
