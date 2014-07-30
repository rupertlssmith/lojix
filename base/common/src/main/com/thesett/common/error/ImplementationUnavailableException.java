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
 * ImplementationUnavailableException is used to signal that an implementation of a class is unavailable for
 * instantiation, for any reason. This exception may be used where an implementation cannot be used because some of its
 * resources are missing, or because a native library is not present, for example. In the case of native libraries the
 * runtime will throw UnsatisfiedLinkError, but this checked exception may be substituted by the loading/instantiating
 * code instead, in situation where the caller would like to deal with this error condition, by instantiating an
 * alternative implementation instead.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Represent failure to instantiate a class.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ImplementationUnavailableException extends UserReadableException
{
    /**
     * Creates the ImplementationUnavailableException.
     *
     * @param message        the exception message
     * @param userMessageKey a key to look up user readable messages with
     * @param userMessage    the user readable message or data string
     * @param e              the wrapped exception underlying this one
     */
    public ImplementationUnavailableException(String message, Throwable e, String userMessageKey, String userMessage)
    {
        super(message, e, userMessageKey, userMessage);
    }
}
