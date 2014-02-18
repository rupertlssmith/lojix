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
package com.thesett.common.parsing;

import com.thesett.common.error.UserReadableException;

/**
 * SourceCodeException represents an error condition that relates to a location with a text source file, and it is also
 * a user readable exception, as typically such exceptions will be reported back to the user to correct.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Identify error location within a source file.
 * <tr><td>Represent user readable errors.
 * <tr><td>Maintain a key to lookup the error in a properties file.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SourceCodeException extends UserReadableException
{
    /** Holds the error position within a source file that this error relates to. */
    private SourceCodePosition sourceCodePosition;

    /**
     * Builds an exception with a message and a user message and a message key. To create an exception with no key and
     * just a user message or with no user message at all pass in null arguments for the key or user message.
     *
     * @param message     The exception message.
     * @param cause       The wrapped exception underlying this one.
     * @param key         A key to look up user readable messages with.
     * @param userMessage The user readable message or data string.
     * @param position    The position of the error.
     */
    public SourceCodeException(String message, Throwable cause, String key, String userMessage,
        SourceCodePosition position)
    {
        super(message, cause, key, userMessage);

        this.sourceCodePosition = position;
    }

    /**
     * Provides the source code position that this error relates to.
     *
     * @return The source code position that this error relates to.
     */
    public SourceCodePosition getSourceCodePosition()
    {
        return sourceCodePosition;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return The detail message string of this <tt>Throwable</tt> instance.
     */
    public String getMessage()
    {
        return super.getMessage() + ((sourceCodePosition != null) ? (" " + sourceCodePosition) : "");
    }
}
