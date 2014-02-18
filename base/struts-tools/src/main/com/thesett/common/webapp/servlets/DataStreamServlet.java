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
package com.thesett.common.webapp.servlets;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * DataStreamServlet is used to stream chunks of data in a response. The servlet generates its data from an input
 * stream, a content type attribute and a content disposition attribute (both optional) which are passed to this servlet
 * as attributes or parameters of the request.
 *
 * <p/>The names of the variables that are passed as attributes in the request are:
 *
 * <pre><table>
 * <tr><td> contentType <td> the content type of the data to be streamed
 * <tr><td> inputStream <td> the input stream where this servlet reads its data from
 * </table></pre>
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Stream a block of data to an HTTP client.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Add parameters to select byte ranges which is useful for streaming PDF files.
 */
public class DataStreamServlet extends HttpServlet
{
    /** Used for logging. */
    private static final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(DataStreamServlet.class.getName());

    /**
     * Processes the http request that is directed to this servlet.
     *
     * @param  request  The HTTP request.
     * @param  response The HTTP response.
     *
     * @throws IOException If the is an I/O error whilst writing data to the HTTP client through the remote output
     *                     stream.
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        log.fine("void service(HttpServletRequest, HttpServletResponse): called");

        // Read the parameters and attributes from the request
        String contentType = (String) request.getAttribute("contentType");
        String contentDisposition = (String) request.getAttribute("contentDisposition");
        InputStream inputStream = (InputStream) request.getAttribute("inputStream");

        // Build the response header
        // response.addHeader("Content-disposition", "attachment; filename=" + fileName);
        if (contentType != null)
        {
            response.setContentType(contentType);
        }

        if (contentDisposition != null)
        {
            response.addHeader("Content-disposition", contentDisposition);
        }

        // response.setContentLength((int)f.length());

        // Create a stream to write the data out to
        BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());

        // Read the entire input stream until no more bytes can be read and write the results into the response
        // This is done in chunks of 8k at a time.
        int length = -1;
        byte[] chunk = new byte[8192];

        while ((length = inputStream.read(chunk)) != -1)
        {
            outputStream.write(chunk, 0, length);
        }

        // Clear up any open stream and ensure that they are flushed
        outputStream.flush();
        inputStream.close();
    }
}
