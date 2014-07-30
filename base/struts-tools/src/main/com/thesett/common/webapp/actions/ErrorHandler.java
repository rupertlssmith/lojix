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

import java.io.FilterWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

import com.thesett.common.error.UserReadableError;

/**
 * ErrorHandler is a top-level error handler for struts based web applications. It defines a single static method for
 * handling exceptions, logging them as errors and translating them into Struts ActionErrors. This is defined here
 * rather than in {@link BaseAction} because the error handling code may also be called directly from a JSP page and not
 * just from Struts actions.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Log all top-level exceptions as errors.
 * <tr><td> Translate exceptions into errors.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ErrorHandler
{
    /** Used for logging. */
    private static final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(ErrorHandler.class.getName());

    /**
     * Converts an exception into struts action errors. The exception stack trace is stored under the 'exception'
     * message key. The message resource 'error.internalerror' is stored under the message key 'generalerror'. The stack
     * trace is pretty printed in HTML.
     *
     * @param exception The exception to be converted into struts action errors.
     * @param errors    The struts action errors object into which the action errors should be placed.
     *
     * @todo  This method can be modified to check if the exception is a user readable exception and to insert the user
     *        readable message under the 'generalerror' message key. Currently it does not handle user readable errors
     *        like this.
     */
    public static void handleErrors(Throwable exception, ActionErrors errors)
    {
        // Log the error.
        log.log(Level.SEVERE, exception.getMessage(), exception);

        if (exception.getCause() == null)
        {
            log.fine("Exception.getCause() is null");
        }

        // Unwrap the exception if it is a WrappedStrutsServletException, which is a place holder for returning
        // other throwables from struts actions.
        // See BaseAction and WrappedStrutsServletException for more information.
        if ((exception instanceof WrappedStrutsServletException) && (exception.getCause() != null))
        {
            exception = exception.getCause();
            log.fine("Unwrapped WrappedStrutsServletException");
        }

        // Create an error called 'exception' in the Struts errors for debugging purposes
        // Debugging code can print this piece of html containing the exception stack trace at the bottom
        // of the page for convenience.
        Writer stackTrace = new StringWriter();

        exception.printStackTrace(new PrintWriter(new HTMLFilter(stackTrace)));
        errors.add("exception", new ActionError("error.general", stackTrace));

        // Check if the exception is a user readable exception
        if (exception instanceof UserReadableError)
        {
            UserReadableError userError = (UserReadableError) exception;

            // Check that it contains a user readable message
            if (userError.isUserReadable())
            {
                // Check if there is an error message key to use
                if (userError.getUserMessageKey() != null)
                {
                    errors.add("generalerror",
                        new ActionError(userError.getUserMessageKey(), userError.getUserMessageKey()));
                }

                // There is no error message key to use so default to error.general and pass the error message as an
                // argument so that it will be displayed
                else
                {
                    errors.add("generalerror", new ActionError("error.general", userError.getUserMessage()));
                }

                return;
            }
        }

        // Not a user reable exception so print a standard error message
        errors.add("generalerror", new ActionError("error.internalerror"));
    }
}

/**
 * Filter writer that converts from text to HTML. This filter replaces newline characters '\n' with html line breaks.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Filter new lines into HTML line breaks.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Add more HTML filtering to this.
 */
class HTMLFilter extends FilterWriter
{
    /**
     * Creates a new HTMLFilter object.
     *
     * @param out The writer to filter output to.
     */
    public HTMLFilter(Writer out)
    {
        super(out);
    }

    /**
     * Writed a single character to the filtered writer. No filtering is done for this method.
     *
     * @param  c The character to write.
     *
     * @throws IOException If the writer won't accept the character.
     */
    public void write(int c) throws IOException
    {
        out.write(c);
    }

    /**
     * Writes an array of characters to the filtered writer. No filtering is done for this method.
     *
     * @param  cbuf The character array to write.
     * @param  off  The offset into the array to begin writing from.
     * @param  len  The number of characters to write.
     *
     * @throws IOException If the writer won't accept the character.
     */
    public void write(char[] cbuf, int off, int len) throws IOException
    {
        out.write(cbuf, off, len);
    }

    /**
     * Writes a string of characters to the filtered writer. Any newline characters '\n' are replaced with an HTML break
     * tag "&lt;br&gt;".
     *
     * @param  str The character array to write.
     * @param  off The offset into the array to begin writing from.
     * @param  len The number of characters to write.
     *
     * @throws IOException If the writer won't accept the string.
     */
    public void write(String str, int off, int len) throws IOException
    {
        // Get just the portion of the input string to display
        String inputString = str.substring(off, off + len);

        StringBuffer outputString = new StringBuffer();

        // Build a string tokenizer that uses '\n' as its splitting character
        // Cycle through all tokens
        for (StringTokenizer tokenizer = new StringTokenizer(inputString, "\n", true); tokenizer.hasMoreTokens();)
        {
            // Replace '\n' token with a <br>
            String nextToken = tokenizer.nextToken();

            if ("\n".equals(nextToken))
            {
                outputString.append("<br>");
            }
            else
            {
                outputString.append(nextToken);
            }
        }

        // Write out the generated string
        out.write(outputString.toString());
    }
}
