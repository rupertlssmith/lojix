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
package com.thesett.common.webapp.actions;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.thesett.common.error.NotImplementedException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * ActionBase is an abstract class which provides some useful com.jpmorgan.grc behaviour for Struts action classes to
 * use. It provides an error handling mechanism for all actions to use. This takes an exception as its input and
 * generates an appropriate error depending on the type of exception.
 *
 * <p/>The user readable error message is interpreted as being a key for an error message in the properties file.
 *
 * <p/>The error handling code has been moved to {@link ErrorHandler}. This class simply defines a useful method that
 * action classes can call locally.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide default error handler for all Java Throwables
 * <tr><td> Translate exception into struts ActionErrors <td> {@link ErrorHandler}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class BaseAction extends Action
{
    /** Used for logging. */
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(BaseAction.class.getName());

    /**
     * Processes the action providing default error handling. Implementation should override this method to provide
     * their own error handling if the default is not to be used.
     *
     * @param     mapping  The ActionMapping used to select this instance
     * @param     form     The optional ActionForm bean for this request (if any)
     * @param     request  The HTTP request.
     * @param     response The HTTP response.
     *
     * @return    A struts forward to tell struts where to go after this action.
     *
     * @exception IOException      If an input/output error occurs.
     * @exception ServletException If a servlet exception occurs.
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws IOException, ServletException
    {
        log.fine("ActionForward perform(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse): called");

        // Build an ActionErrors object to hold any errors that occurr
        ActionErrors errors = new ActionErrors();

        // Create reference to the session
        HttpSession session = request.getSession();

        // Use a try block to catch any errors that may occur
        try
        {
            return executeWithErrorHandling(mapping, form, request, response, errors);
        }

        // Catch all exceptions here. This will forward to the error page in the event of
        // any exception that falls through to this top level handler.
        // Don't catch Throwable here as Errors should fall through to the JVM top level and will result in
        // termination of the application.
        catch (Exception t)
        {
            log.log(Level.WARNING, "Caught a Throwable", t);

            // Don't Forward the error to the error handler to interpret it as a Struts error as the exception will
            // automatically be translated by the error page.

            // @todo Could add code here to check if there is a 'error' forward page defined. If there is then call
            // the error handler to translate the throwable into Struts errors and then forward to the 'error' page.
            // This would mean that the error page defined in web.xml would be the default unless an action explicitly
            // defined an alternative 'error' forward.
            // handleErrors(t, errors);

            // Save all the error messages in the request so that they will be displayed
            // request.setAttribute(Action.ERROR_KEY, errors);

            // Rethrow the error as a ServletException here to cause forwarding to error page defined in web.xml
            throw new WrappedStrutsServletException(t);
        }
    }

    /**
     * This perform method is the same as the normal perform method but it is called from inside an error wrapper that
     * catches all throwables. The wrapper automatically generates Struts error messages for any Java throwable that may
     * fall through this message.
     *
     * <p/>Implementations should override this method to use the default error handling.
     *
     * @param  mapping  The ActionMapping used to select this instance
     * @param  form     The optional ActionForm bean for this request (if any)
     * @param  request  The HTTP request.
     * @param  response The HTTP response.
     * @param  errors   A struts errors object to write any error messages to.
     *
     * @return A struts forward to tell struts where to go after this action.
     *
     * @throws Exception Any exceptions are allowed to fall through to the top level handler.
     */
    public ActionForward executeWithErrorHandling(ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response, ActionErrors errors) throws Exception
    {
        // Dummy implementation, does nothing useful and should never be called.
        throw new NotImplementedException();
    }

    /**
     * Handles any exceptions that fell though to the top level. This is delegated to the error handler to translate
     * those exceptions into struts action errors.
     *
     * @param exception The exception that fell through.
     * @param errors    A struts action errors obejct to write the error messages into.
     */
    public void handleErrors(Throwable exception, ActionErrors errors)
    {
        // Delegate to the error handler defined in ErrorHandler
        ErrorHandler.handleErrors(exception, errors);
    }
}
