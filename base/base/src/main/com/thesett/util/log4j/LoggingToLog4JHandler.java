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
package com.thesett.util.log4j;

import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * LoggingToLog4JHandler is a java.util.logging handler that redirects all of its output onto Log4J.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Publish a java.util.logging message to Log4J.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class LoggingToLog4JHandler extends Handler
{
    /**
     * Provides the target Log4J logger for a class, by name.
     *
     * @param  clazz The class to get the logger for.
     *
     * @return The target Log4J logger.
     */
    public static Logger getTargetLogger(Class clazz)
    {
        return getTargetLogger(clazz.getName());
    }

    /**
     * Provides the target Log4J logger.
     *
     * @param  loggerName The name of the logger.
     *
     * @return The target Log4J logger.
     */
    public static Logger getTargetLogger(String loggerName)
    {
        return Logger.getLogger(loggerName);
    }

    /** {@inheritDoc} */
    public void publish(LogRecord record)
    {
        org.apache.log4j.Logger log4j = getTargetLogger(record.getLoggerName());
        Priority priority = toLog4j(record.getLevel());

        if (!priority.equals(org.apache.log4j.Level.OFF))
        {
            log4j.log(priority, toLog4jMessage(record), record.getThrown());
        }
    }

    /** {@inheritDoc} */
    public void flush()
    {
    }

    /** {@inheritDoc} */
    public void close()
    {
    }

    /**
     * Transforms a java.util.logging.LogRecord to a message printable on Log4J.
     *
     * @param  record The log record.
     *
     * @return The log4j message.
     */
    private String toLog4jMessage(LogRecord record)
    {
        String message = record.getMessage();

        // Format message
        Object[] parameters = record.getParameters();

        if ((parameters != null) && (parameters.length != 0))
        {
            // Check for the first few parameters ?
            if ((message.indexOf("{0}") >= 0) || (message.indexOf("{1}") >= 0) || (message.indexOf("{2}") >= 0) ||
                    (message.indexOf("{3}") >= 0))
            {
                message = MessageFormat.format(message, parameters);
            }
        }

        return message;
    }

    /**
     * Converts java.util.logging levels to Log4J logging levels.
     *
     * @param  level The java.util.logging level to convert.
     *
     * @return The corresponding Log4J level.
     */
    private org.apache.log4j.Level toLog4j(Level level)
    {
        if (Level.SEVERE == level)
        {
            return org.apache.log4j.Level.ERROR;
        }
        else if (Level.WARNING == level)
        {
            return org.apache.log4j.Level.WARN;
        }
        else if (Level.INFO == level)
        {
            return org.apache.log4j.Level.INFO;
        }
        else if (Level.FINE == level)
        {
            return org.apache.log4j.Level.DEBUG;
        }
        else if (Level.FINER == level)
        {
            return org.apache.log4j.Level.TRACE;
        }
        else if (Level.OFF == level)
        {
            return org.apache.log4j.Level.OFF;
        }

        return org.apache.log4j.Level.OFF;
    }
}
