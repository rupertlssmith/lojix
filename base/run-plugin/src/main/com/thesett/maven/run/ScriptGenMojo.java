package com.thesett.maven.run;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author                       Rupert Smith
 * @goal                         script
 * @phase                        package
 * @requiresDependencyResolution test
 */
public class ScriptGenMojo extends AbstractMojo
{
    protected static final String UNIX_SCRIPT_LANGUAGE = "#!/bin/bash\n\n";

    protected static final String UNIX_OPT_PARSER =
        "# Parse arguements taking all - prefixed args as JAVA_OPTS\n" + "for arg in \"$@\"; do\n" +
        "    if [[ $arg == -java#* ]]; then\n" + "        JAVA_OPTS=\"${JAVA_OPTS}-`echo $arg|cut -d '#' -f 2`  \"\n" +
        "    else\n" + "        ARGS=\"${ARGS}$arg \"\n" + "    fi\n" + "done\n\n";

    /** Used for debugging purposes. */
    protected final Log log = getLog();

    /**
     * Where to write out the scripts.
     *
     * @parameter
     */
    protected String scriptOutDirectory;

    /**
     * The build artifact as a .jar.
     *
     * @parameter property="project.build.finalName"
     */
    protected String outputJar;

    /**
     * The system properties to pass to java runtime.
     *
     * @parameter
     */
    protected Properties systemproperties;

    /**
     * The classpath elements of the project being tested.
     *
     * @parameter property="project.testClasspathElements"
     * @readonly
     * @required
     */
    protected List classpathElements;

    /**
     * The TKTest runner command lines. There are passed directly to the TKTestRunner main method.
     *
     * @parameter
     */
    protected final Map<String, String> commands = new LinkedHashMap<String, String>();

    /**
     * Use 'start' to launch under windows, allowing .bat file to return immediately.
     *
     * @parameter
     */
    protected boolean winStart;

    /**
     * Use 'javaw' to launch under windows, hiding java conole output.
     *
     * @parameter
     */
    protected boolean winJavaw;

    /**
     * Implementation of the script goal.
     *
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        //log.debug("public void execute() throws MojoExecutionException: called");

        // Turn each of the test runner command lines into a script.
        for (String commandName : commands.keySet())
        {
            if (scriptOutDirectory != null)
            {
                writeUnixScript(commandName, scriptOutDirectory);
                writeWindowsScript(commandName, scriptOutDirectory);
            }
        }
    }

    /**
     * Appends the classpath onto the command line.
     *
     * @param  commandLine The command line to append to.
     * @param  unix        <tt>true</tt> to use unix paths, <tt>false</tt> to use windows.
     *
     * @return The appended to command line.
     */
    protected String appendClasspath(String commandLine, boolean unix)
    {
        String pathSeperator;
        String seperator;

        if (unix)
        {
            pathSeperator = "/";
            seperator = ":";
        }
        else
        {
            pathSeperator = "\\";
            seperator = ";";
        }

        for (Iterator i = classpathElements.iterator(); i.hasNext();)
        {
            String cpPath = (String) i.next();

            cpPath = cpPath.replace("/", pathSeperator);

            commandLine += cpPath + (i.hasNext() ? seperator : "");
        }

        return commandLine;
    }

    protected void writeWindowsScript(String commandName, String outputDirectory)
    {
        String testOptions = commands.get(commandName);
        String commandLine = new String();

        if (winStart)
        {
            commandLine += "start ";
        }

        if (winJavaw)
        {
            commandLine += "javaw ";
        }
        else
        {
            commandLine += "java ";
        }

        String logdir = null;

        if (systemproperties != null)
        {
            for (Object key : systemproperties.keySet())
            {
                String keyString = (String) key;
                String value = systemproperties.getProperty(keyString);

                if (keyString.equals("logdir"))
                {
                    logdir = value;
                }
                else
                {
                    if (keyString.startsWith("-X"))
                    {
                        commandLine += keyString + value + " ";
                    }
                    else
                    {
                        commandLine += "-D" + keyString + "=" + value + " ";
                    }
                }
            }
        }

        commandLine += "-cp ";
        commandLine = appendClasspath(commandLine, false);
        commandLine += " " + ((testOptions != null) ? testOptions : "") + " %*";

        log.info("Generating Script for command: " + commandName);
        // log.debug(commandLine);

        // Ensure that the output directory for the script exists.
        boolean madeDirectory = new File(outputDirectory).mkdir();

        String fileName = outputDirectory + "/" + commandName + ".bat";

        try
        {
            File scriptFile = new File(fileName);
            Writer scriptWriter = new FileWriter(scriptFile);

            if (logdir != null)
            {
                scriptWriter.write("mkdir -p " + logdir + "\n");
            }

            scriptWriter.write(commandLine);
            scriptWriter.flush();
            scriptWriter.close();
        }
        catch (IOException e)
        {
            getLog().error("Failed to write: " + fileName);
        }
    }

    protected void writeUnixScript(String commandName, String outputDirectory)
    {
        String testOptions = commands.get(commandName);
        String commandLine = "java ";

        String logdir = null;

        if (systemproperties != null)
        {
            for (Object key : systemproperties.keySet())
            {
                String keyString = (String) key;
                String value = systemproperties.getProperty(keyString);

                if (keyString.equals("logdir"))
                {
                    logdir = value;
                }
                else
                {
                    if (keyString.startsWith("-X"))
                    {
                        commandLine += keyString + value + " ";
                    }
                    else
                    {
                        commandLine += "-D" + keyString + "=" + value + " ";
                    }
                }
            }
        }

        commandLine += "${JAVA_OPTS} -cp ";
        commandLine = appendClasspath(commandLine, true);
        commandLine += " " + ((testOptions != null) ? testOptions : "") + " ${ARGS}";

        log.info("Generating Script for command: " + commandName);
        // log.debug(commandLine);

        // Ensure that the output directory for the script exists.
        boolean madeDirectory = new File(outputDirectory).mkdir();

        String fileName = outputDirectory + "/" + commandName;

        try
        {
            File scriptFile = new File(fileName);
            Writer scriptWriter = new FileWriter(scriptFile);
            scriptWriter.write(UNIX_SCRIPT_LANGUAGE);
            scriptWriter.write(UNIX_OPT_PARSER);

            if (logdir != null)
            {
                scriptWriter.write("mkdir -p " + logdir + "\n");
            }

            scriptWriter.write(commandLine);
            scriptWriter.flush();
            scriptWriter.close();
        }
        catch (IOException e)
        {
            getLog().error("Failed to write: " + fileName);
        }
    }
}
