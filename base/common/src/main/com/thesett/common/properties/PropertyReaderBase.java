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
package com.thesett.common.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.thesett.common.util.PropertiesHelper;

/**
 * PropertyReaderBase is a property file reader that uses a variety of methods to locate and load property files. The
 * {@link #findProperties} method attempts to load the properties from a file referenced by the system property with the
 * same name as the properties resource name, from a resource on the classpath with the same name as the properties
 * resource name or from a properties file name relative to the current working directory. It tries these methods
 * sequentially one after the other until one succeeds.
 *
 * <p/>This class is intended to be extended by class representations of properties files so it contains a reference to
 * a {@link java.util.Properties} object. Extending classes must implement an abstract method to specify the name of the
 * properties resource file that contains the properties that the implementing class stands for. Normally, extending
 * classes will define constants for easy access to the properties that they are representing.
 *
 * <p/>This class is a singleton.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Search for properties resources in file or URL defined in system properties <td> {@link com.thesett.common.util.PropertiesHelper}
 * <tr><td>Search for properties resources in file in classpath <td> {@link com.thesett.common.util.PropertiesHelper}
 * <tr><td>Search for properties resources in file in current working directory <td> {@link com.thesett.common.util.PropertiesHelper}
 * <tr><td>Specify the name of the resource to get properties from
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class PropertyReaderBase
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(PropertyReaderBase.class.getName()); */

    /** Holds a reference to the read properties. */
    protected Properties properties;

    /** Default constructor. Loads the properties. This is a private constructor as this class is abstract. */
    protected PropertyReaderBase()
    {
        /*log.fine("protected PropertyReaderBase(): called");*/

        // Load the properties.
        // Don't load the properties until the sub-class explicitly triggers the find as the sub-class will
        // probably need to set up the resource name in its constructor first.
        // findProperties();
    }

    /**
     * Returns the read properties from the properties file.
     *
     * @return The properties for the specified resource name.
     */
    public Properties getProperties()
    {
        return properties;
    }

    /**
     * This should return the name of the resource, file or system property to use to locate the properties file to be
     * read.
     *
     * @return The resource name of the properties file.
     */
    protected abstract String getPropertiesResourceName();

    /**
     * This methods attempts to load the properties from a file or URL referenced by the system property with the same
     * name as the properties resource name, from a resource on the classpath with the same name as the properties
     * resource name or from a properties file name relative to the current working directory. It tries these methods
     * sequentially one after the other until one succeeds.
     */
    protected void findProperties()
    {
        /*log.fine("findProperties: called");*/

        // Try to load the properties from a file referenced by the system property matching
        // the properties file name.
        properties = getPropertiesUsingSystemProperty();

        if (properties != null)
        {
            /*log.fine("loaded properties using the system property");*/

            // The properties were succesfully located and loaded
            return;
        }

        /*log.fine("failed to get properties from the system properties");*/

        // Try to load the properties from a resource on the classpath using the current
        // class loader
        properties = getPropertiesUsingClasspath();

        if (properties != null)
        {
            /*log.fine("loaded properties from the class path");*/

            // The properties were succesfully located and loaded
            return;
        }

        /*log.fine("failed to get properties from the classpath");*/

        // Try to load the properties from a file relative to the current working directory
        properties = getPropertiesUsingCWD();

        if (properties != null)
        {
            /*log.fine("loaded properties from the current working directory");*/

            // The properties were succesfully located and loaded
            return;
        }

        /*log.fine("failed to get properties from the current working directory");*/
    }

    /**
     * Tries to load the properties from the file or URL named by the system property with name mathching the properties
     * resource name.
     *
     * @return The properties found under the file name or URL or null if none can be found there.
     */
    protected Properties getPropertiesUsingSystemProperty()
    {
        /*log.fine("getPropertiesUsingSystemProperty: called");*/

        // Get the path to the file from the system properties
        /*log.fine("getPropertiesResourceName() = " + getPropertiesResourceName());*/

        String path = System.getProperty(getPropertiesResourceName());

        /*log.fine("properties resource name = " + getPropertiesResourceName());*/
        /*log.fine("path = " + path);*/

        // Use PropertiesHelper to try to load the properties from the path
        try
        {
            return PropertiesHelper.getProperties(path);
        }
        catch (IOException e)
        {
            /*log.fine("Could not load properties from path " + path);*/

            // Failure of this method is noted, so exception is ignored.
            e = null;
        }

        return null;
    }

    /**
     * Tries to load the properties from the classpath using the classloader for this class.
     *
     * @return The properties loaded from the resource name under the classpath or null if none can be found there.
     */
    protected Properties getPropertiesUsingClasspath()
    {
        /*log.fine("getPropertiesUsingClasspath: called");*/

        // Try to open the properties resource name as an input stream from the classpath
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(getPropertiesResourceName());

        // Use PropertiesHelper to try to load the properties from the input stream if one was succesfully created
        if (is != null)
        {
            try
            {
                return PropertiesHelper.getProperties(is);
            }
            catch (IOException e)
            {
                /*log.fine("Could not load properties from classpath");*/

                // Failure of this method is noted, so exception is ignored.
                e = null;
            }
        }

        return null;
    }

    /**
     * Tries to load the properties as a file or URL matching the properties resource name. File names will be taken
     * relative to the current working directory.
     *
     * @return The properties found from the resource name as a file in the current working directory or null if none
     *         can be found there.
     */
    protected Properties getPropertiesUsingCWD()
    {
        /*log.fine("getPropertiesUsingCWD: called");*/

        // Use PropertiesHelper to try to load the properties from a file or URl
        try
        {
            return PropertiesHelper.getProperties(getPropertiesResourceName());
        }
        catch (IOException e)
        {
            /*log.fine("Could not load properties from file or URL " + getPropertiesResourceName());*/

            // Failure of this method is noted, so exception is ignored.
            e = null;
        }

        return null;
    }
}
