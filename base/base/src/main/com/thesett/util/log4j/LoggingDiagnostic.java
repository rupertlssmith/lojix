package com.thesett.util.log4j;

import java.io.File;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * LoggingDiagnostic provides a diagnostic string, containing information about the java.util.logging configuration.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide a java.util.logging diagnostic string.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class LoggingDiagnostic
{
    /**
     * Provides a string containing information about the configured logging set up.
     *
     * @return A string containing information about the configured logging set up.
     */
    public static String currentConfiguration()
    {
        StringBuffer rtn = new StringBuffer(1024);

        String loggingConfigClass = System.getProperty("java.util.logging.config.class");
        String loggingConfigFile = System.getProperty("java.util.logging.config.file");
        boolean configClassOK = false;

        if (loggingConfigClass == null)
        {
            rtn.append("No java.util.logging.config.class class is set.\n");
        }
        else
        {
            rtn.append("java.util.logging.config.class is set to '").append(loggingConfigClass).append("'\n");

            try
            {
                Class c = Class.forName(loggingConfigClass);
                c.newInstance();
                rtn.append("This class was loaded and a new instance was sucessfully created.\n");
                configClassOK = true;
            }
            catch (ClassNotFoundException e)
            {
                e = null;
                rtn.append(loggingConfigClass).append(" could not be found.");
            }
            catch (InstantiationException e)
            {
                e = null;
                rtn.append(loggingConfigClass).append(" could not be instantiated.");
            }
            catch (IllegalAccessException e)
            {
                e = null;
                rtn.append(loggingConfigClass).append(" could not be accessed.");
            }
        }

        if (loggingConfigFile == null)
        {
            rtn.append("No java.util.logging.config.file file is set.\n");
        }
        else
        {
            rtn.append("java.util.logging.config.file is set to '").append(loggingConfigFile).append("'\n");

            File loggingFile = new File(loggingConfigFile);
            rtn.append(loggingFile.getAbsolutePath()).append("\n");

            if (!loggingFile.exists() || !loggingFile.isFile())
            {
                rtn.append("This file does NOT EXIST.\n");
            }

            if (loggingConfigClass != null)
            {
                if (configClassOK)
                {
                    rtn.append("This file is ignored because java.util.logging.config.class is set.\n");
                }
            }
        }

        Handler[] handlers = Logger.getLogger("").getHandlers();
        listHandlers(handlers, rtn);

        return rtn.toString();
    }

    /**
     * Lists information about logging handlers.
     *
     * @param  handlers The handlers.
     * @param  buffer   A string buffer to build up the listing in.
     *
     * @return The string buffer to build up the listing in.
     */
    private static StringBuffer listHandlers(Handler[] handlers, StringBuffer buffer)
    {
        for (Handler handler : handlers)
        {
            Class<? extends Handler> handlerClass = handler.getClass();
            Formatter formatter = handler.getFormatter();

            buffer.append("Handler:").append(handlerClass.getName()).append("\n");
            buffer.append("Level:").append(handler.getLevel().toString()).append("\n");

            if (formatter != null)
            {
                buffer.append("Formatter:").append(formatter.getClass().getName()).append("\n");
            }
        }

        return buffer;
    }
}
