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
 * UserReadableErrorImpl provides a simple implementation of the {@link UserReadableError} interface. It captures an
 * error key and a data message.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Encapsulate error key and data message.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class UserReadableErrorImpl implements UserReadableError
{
    /** Holds the message key. */
    private final String userMessageKey;

    /** Holds the message data string. */
    private final String userMessage;

    /**
     * Create a user readable error message.
     *
     * @param key     A key to look up user readable messages with.
     * @param message The user readable message or data string.
     */
    public UserReadableErrorImpl(String key, String message)
    {
        this.userMessageKey = key;
        this.userMessage = message;
    }

    /**
     * Gets the user readable message.
     *
     * @return The user readable message.
     */
    public String getUserMessage()
    {
        return userMessage;
    }

    /**
     * Gets the user readable message key to look up the user message in a resource file by.
     *
     * @return The user readable message key.
     */
    public String getUserMessageKey()
    {
        return userMessageKey;
    }

    /**
     * Reports whether or not this object is a user readable error.
     *
     * @return True if this is user readable, false if not.
     */
    public boolean isUserReadable()
    {
        return true;
    }

    /**
     * Prints as a string for debugging purposes.
     *
     * @return As a string for debugging purposes.
     */
    public String toString()
    {
        return "key = " + userMessageKey + ", message = " + userMessage;
    }
}
