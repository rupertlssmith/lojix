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
 * A UserReadableError is an error that is built with a message that is intended to be read by an application end users.
 * Such an error message should be carefully written to be helpful and meaningful to a user.
 *
 * <p>UserReadableError is an interface eather than an extension of {@link java.lang.Exception} because an exception may
 * need to be written that extends another exception class, {java.lang.RuntimeException} for example. Such an exception
 * could not extend both that class and this one so this is defined as an interface. Also a user readable error may not
 * necessarily be an exception either.
 *
 * <p>A UserReadableError may contain a user readable error but this is only optional. There is a flag method to
 * indicate whether or not the error is actually user readable.
 *
 * <p>User error message may either be specified as a String containing the actual message or as a String that contains
 * a key to look up the actual message in a resource file with.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Represent user readable errors.
 * <tr><td>Maintain a key to lookup the error in a properties file.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface UserReadableError
{
    /**
     * Gets the user readable message.
     *
     * @return The user readable message.
     */
    public String getUserMessage();

    /**
     * Gets the user readable message key to look up the user message in a resource file by.
     *
     * @return The user readable message key.
     */
    public String getUserMessageKey();

    /**
     * Reports whether or not this object is a user readable error.
     *
     * @return True if this is user readable, false if not.
     */
    public boolean isUserReadable();
}
