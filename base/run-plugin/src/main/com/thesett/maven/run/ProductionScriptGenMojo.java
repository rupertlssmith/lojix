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
