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
package com.thesett.common.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * DefaultPropertyReader is a factory class that creates a property reader from a named resource. The name is used to
 * locate the properties in the same way as described in the {@link PropertyReaderBase} class comment.
 *
 * <p/>DefaultPropertyReader will create only one property reader per resource name. If the same name is requested more
 * than once then a previous singleton instance will be returned.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Create a property reader from a resource name <td> {@link PropertyReaderBase}
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Merge the functionality of the com.thesett.common.properties.ContextualProperties class in the junitppmod
 *         project into this class and then delete that one. It provides more sophisticated property handling than this
 *         class that allows properties to be defined on a per environment/class/method level with default values. This
 *         provides look up on the classpath, by location named by system property and in current working directory.
 *         Combined together they provide a very powerful way of looking up properties in a prioritsed and hierarchical
 *         way that is easy to use.
 */
public class DefaultPropertyReader extends PropertyReaderBase
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(DefaultPropertyReader.class.getName()); */

    /** Used to hold a map from property resource names to singleton property reader instances. */
    private static Map propertyReaders = new HashMap();

    /** Used to hold the name of the property resource. */
    private String resourceName;

    /**
     * Private constructor that builds a property reader for a named resource. This is private to ensure that only
     * singleton instances of property readers can be created for each named resource.
     *
     * @param resourceName The name of the property file resource.
     */
    private DefaultPropertyReader(String resourceName)
    {
        /*log.fine("private DefaultPropertyReader(String resourceName): called");*/
        /*log.fine("resourceName = " + resourceName);*/

        // Keep the name of the properties resource
        this.resourceName = resourceName;

        // Load the properties resource
        findProperties();
    }

    /**
     * Static factory method that locates an existing instance or creates a new property reader for a named resource.
     *
     * @param  resourceName The name of the property file resource.
     *
     * @return The properties found under that resource name.
     */
    public static synchronized Properties getProperties(String resourceName)
    {
        /*log.fine("public static synchronized Properties getProperties(String resourceName): called");*/
        /*log.fine("resourceName = " + resourceName);*/

        // Try to find an already created singleton property reader for the resource
        PropertyReaderBase propertyReader = (PropertyReaderBase) propertyReaders.get(resourceName);

        if (propertyReader != null)
        {
            /*log.fine("found property reader in the cache for resource: " + resourceName);*/

            return propertyReader.getProperties();
        }

        /*log.fine("did not find property reader in the cache for resource: " + resourceName);*/

        // There is not already a singleton for the named resource so create a new one
        propertyReader = new DefaultPropertyReader(resourceName);

        // Keep the newly created singleton for next time
        propertyReaders.put(resourceName, propertyReader);

        return propertyReader.getProperties();
    }

    /**
     * This should return the name of the resource, file or system property to use to locate the properties file to be
     * read.
     *
     * @return The name of the property file resource.
     */
    public String getPropertiesResourceName()
    {
        /*log.fine("public String getPropertiesResourceName(): called");*/
        /*log.fine("resourceName = " + resourceName);*/

        return resourceName;
    }
}
