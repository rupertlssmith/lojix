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

/**
 * UserReadableRuntimeException is a runtime exception class that implements the {@link UserReadableError} interface. It
 * provides a useful base from which to derive runtime application exceptions.
 *
 * <p>{@link UserReadableException} provides a useful base exception class for checked application exceptions.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Represent user readable errors.
 * <tr><td>Maintain a key to lookup the error in a properties file.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class UserReadableRuntimeException extends BaseRuntimeApplicationException implements UserReadableError
{
    /** Holds the user readable error message. */
    private String userMessage;

    /** Holds the user error message key. */
    private String userMessageKey;

    /**
     * Builds an exception with a message and a user message and a message key. To create an exception with no key and
     * just a user message or with no user message at all pass in null arguments for the key or user message.
     *
     * @param message     The exception message.
     * @param cause       The wrapped exception underlying this one.
     * @param key         A key to look up user readable messages with.
     * @param userMessage The user readable message or data string.
     */
    public UserReadableRuntimeException(String message, Throwable cause, String key, String userMessage)
    {
        // Build the exception object with the message
        super(message, cause);

        // Store the user readable message
        setUserMessage(userMessage);

        // Store the user message key
        setUserMessageKey(key);
    }

    /**
     * Sets the user readable message.
     *
     * @param userMessage The user readable message.
     */
    public void setUserMessage(String userMessage)
    {
        this.userMessage = userMessage;
    }

    /**
     * Gets the user readable message.
     *
     * @return The user readable message.
     */
    public String getUserMessage()
    {
        return this.userMessage;
    }

    /**
     * Sets the user readable message key to look up the user message in a resource file by.
     *
     * @param userMessageKey the user readable message key to look up the user message in a resource file by.
     */
    public void setUserMessageKey(String userMessageKey)
    {
        this.userMessageKey = userMessageKey;
    }

    /**
     * Gets the user readable message key to look up the user message in a resource file by.
     *
     * @return The user readable message key.
     */
    public String getUserMessageKey()
    {
        return this.userMessageKey;
    }

    /**
     * Reports whether or not this object is a user readable error.
     *
     * @return True if this is user readable, false if not.
     */
    public boolean isUserReadable()
    {
        return ((userMessage != null) || (userMessageKey != null));
    }
}
