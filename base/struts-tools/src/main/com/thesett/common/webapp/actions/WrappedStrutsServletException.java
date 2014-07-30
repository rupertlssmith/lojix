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
package com.thesett.common.webapp.actions;

import javax.servlet.ServletException;

/**
 * A wrapped exception that appears to be a ServletException. Used to pass throwables back from struts actions (which
 * can only legitimately pass IOException or ServletException). The {@link ErrorHandler} knows how to unwrap these
 * exceptions.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class WrappedStrutsServletException extends ServletException
{
    /** The underlying exception cause that is wrapped by this exception. */
    Throwable cause;

    /**
     * Creates a wrapped exception that is also a servlet exception.
     *
     * @param t The underlying throwable to be wrapped.
     */
    public WrappedStrutsServletException(Throwable t)
    {
        // Build the exception object with the message
        super("WrappedStrutsServletException", t);

        this.cause = t;
    }

    /**
     * Gets the underlying exception cause.
     *
     * @return The underlying exception cause.
     */
    public Throwable getCause()
    {
        return cause;
    }
}
