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
 * A base class for defining all runtime application exceptions from. Provides no extra features over RuntimeException
 * except that it allows all application exceptions to be caught from a single catch statement by virtue of the fact
 * that they all extend a common base class.
 *
 * <p>Consider making all application exceptions checked by default. Only once it becomes clear that an exception cannot
 * usually be handled and does not arise from incorrect application logic but as a result of the runtime system being
 * broken in some way should it be changed to a runtime exception.
 *
 * <p>For example if a program tries to create a user account but cannot because one with that user name already exists
 * then this is certainly a checked exception; the executing code has tried to place the system into an inconsistent
 * state, although the call was legal and reasonable. On the other hand, if the system fails to call a remote service
 * due to a remote exception because the remote end of the service is down this is an abnormal situation with the
 * runtime system; normally the system is expected to be up and running perfectly. The program may reasonably re-throw
 * such an exception as a runtime exception if it cannot be recovered from (by retrying for example).
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Act as a root for runtime application exceptions.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BaseRuntimeApplicationException extends RuntimeException
{
    /**
     * Creates a new BaseRuntimeApplicationException object.
     *
     * @param message The exception message.
     * @param cause   The underlying throwable cause. This may be null.
     */
    public BaseRuntimeApplicationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
