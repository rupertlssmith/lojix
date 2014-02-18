/*
 * Copyright The Sett Ltd, 2005 to 2009.
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
package com.thesett.common.error;

/**
 * A base class for defining all application exceptions from. Provides no extra features over Exception except that it
 * allows all application exceptions to be caught from a single catch statement by virtue of the fact that they all
 * extend a common base class.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Act as a root for application exceptions.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BaseApplicationException extends Exception
{
    /**
     * Creates a new BaseApplicationException object.
     *
     * @param message The exception message.
     * @param cause   The underlying throwable cause. This may be null.
     */
    public BaseApplicationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
