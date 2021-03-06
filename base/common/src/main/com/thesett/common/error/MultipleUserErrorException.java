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
package com.thesett.common.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Wraps multiple user readable errors in a single exception.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class MultipleUserErrorException extends UserReadableException
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(MultipleUserErrorException.class.getName()); */

    /** Holds the user readable error messages. */
    List<UserReadableError> errors = new ArrayList<UserReadableError>();

    /**
     * Builds an exception with a message and a user message and a message key. To create an exception with no key and
     * just a user message or with no user message at all pass in null arguments for the key or user message.
     *
     * @param message     The exception message.
     * @param cause       The wrapped exception underlying this one.
     * @param key         A key to look up user readable messages with.
     * @param userMessage The user readable message or data string.
     */
    public MultipleUserErrorException(String message, Throwable cause, String key, String userMessage)
    {
        super(message, cause, key, userMessage);

        errors.add(this);
    }

    /**
     * Builds an exception with a message and a user message and a message key. To create an exception with no key and
     * just a user message or with no user message at all pass in null arguments for the key or user message.
     *
     * @param message The exception message.
     * @param cause   The wrapped exception underlying this one.
     * @param errors  A list of many user readable errors.
     */
    public MultipleUserErrorException(String message, Throwable cause, Collection<UserReadableError> errors)
    {
        super(message, cause, null, null);

        /*log.fine(
            " public MultipleUserErrorException(String message, Throwable cause, List<UserReadableError> errors): called");*/

        for (UserReadableError error : errors)
        {
            /*log.fine("next error = " + error.getUserMessageKey());*/
        }

        errors.addAll(errors);
    }

    /**
     * Adds another user readable error message to this exception.
     *
     * @param key         A key to look up user readable messages with.
     * @param userMessage The user readable message or data string.
     */
    public void addErrorMessage(String key, String userMessage)
    {
        /*log.fine("addErrorMessage(String key, String userMessage): called");*/
        /*log.fine("userMessage = " + userMessage);*/

        errors.add(new UserReadableErrorImpl(key, userMessage));
    }

    /**
     * Gets all the user readable error messages.
     *
     * @return All the user readable error messages.
     */
    public List<UserReadableError> getErrors()
    {
        /*log.fine("public List<UserReadableError> getErrors(): called");*/

        return errors;
    }
}
