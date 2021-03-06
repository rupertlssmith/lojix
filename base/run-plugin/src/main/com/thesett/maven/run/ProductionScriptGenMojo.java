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
package com.thesett.maven.run;

import java.util.Iterator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author                       Rupert Smith
 * @goal                         prod-script
 * @phase                        package
 * @requiresDependencyResolution test
 */
public class ProductionScriptGenMojo extends ScriptGenMojo
{
    private static final String JAR_DIR_PREFIX_UNIX = "${assembly.jar.dir.unix}";
    private static final String JAR_DIR_PREFIX_WINDOWS = "${assembly.jar.dir.windows}";

    /**
     * Where to write out the production scripts.
     *
     * @parameter
     */
    protected String prodScriptOutDirectory;

    /**
     * Implementation of the prod-script goal.
     *
     * @throws org.apache.maven.plugin.MojoExecutionException
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        //log.debug("public void execute() throws MojoExecutionException: called");

        // Turn each of the test runner command lines into a script.
        for (String commandName : commands.keySet())
        {
            if (prodScriptOutDirectory != null)
            {
                writeUnixScript(commandName, prodScriptOutDirectory);
                writeWindowsScript(commandName, prodScriptOutDirectory);
            }
        }
    }

    /** {@inheritDoc} */
    protected String appendClasspath(String commandLine, boolean unix)
    {
        String pathSeperator;
        String seperator;
        String classpathDirPrefix;

        if (unix)
        {
            pathSeperator = "/";
            seperator = ":";
            classpathDirPrefix = JAR_DIR_PREFIX_UNIX;
        }
        else
        {
            pathSeperator = "\\";
            seperator = ";";
            classpathDirPrefix = JAR_DIR_PREFIX_WINDOWS;
        }

        for (Iterator i = classpathElements.iterator(); i.hasNext();)
        {
            String cpPath = (String) i.next();

            int lastSlash = cpPath.lastIndexOf("/");
            int lastBackslash = cpPath.lastIndexOf("\\");

            int lastPathSeperator = (lastSlash > lastBackslash) ? lastSlash : lastBackslash;

            if (lastPathSeperator != -1)
            {
                cpPath = cpPath.substring(lastPathSeperator + 1);
            }

            //cpPath = cpPath.replace("/", pathSeperator);

            if (cpPath.endsWith(".jar"))
            {
                commandLine += classpathDirPrefix + pathSeperator + cpPath + seperator;
            }
        }

        commandLine += classpathDirPrefix + pathSeperator + outputJar + ".jar";

        return commandLine;
    }
}
