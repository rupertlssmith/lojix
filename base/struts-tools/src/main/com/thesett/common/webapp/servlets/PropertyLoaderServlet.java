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
package com.thesett.common.webapp.servlets;

import java.util.Properties;

import javax.servlet.http.HttpServlet;

import com.thesett.common.properties.DefaultPropertyReader;

/**
 * PropertyLoaderServlet is a servlet that uses its config method to load a properties file into the application scope.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Load properties and store them in the application scope
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Deprecate this functionality to a property config bean to be run by the config bean manager once it has been
 *         written.
 * @todo   Could make this servlet return some status information about the config beans if its service method is
 *         called.
 */
public class PropertyLoaderServlet extends HttpServlet
{
    /** Used for logging. */
    private static final java.util.logging.Logger log =
        java.util.logging.Logger.getLogger(PropertyLoaderServlet.class.getName());

    /** Holds the name of the initialization parameter for the property resource name. */
    private static final String PROPERTY_RESOURCE = "property_resource";

    /** Holds the name of the initialization parameter for the application scope variable name. */
    private static final String APP_VAR_NAME = "app_var_name";

    /**
     * Loads a properties file and stores it in the application context. The property resource name and the application
     * scope variable name are passed as initialization parameters in the servlet config in the web.xml.
     */
    public void init()
    {
        log.fine("public void init(): called");

        // Get the name of the property file resource to load and the application variable name to store it under
        String propertyResource = getInitParameter(PROPERTY_RESOURCE);
        String varName = getInitParameter(APP_VAR_NAME);

        log.fine("varName = " + varName);

        // Use the default property reader to load the resource
        Properties properties = DefaultPropertyReader.getProperties(propertyResource);

        log.fine("properties = " + properties);

        // Store the properties under the specified variable name in the application scope
        getServletContext().setAttribute(varName, properties);
    }
}
