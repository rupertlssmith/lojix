package com.thesett.util.log4j;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A silent error handler for log4j.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Ignore all errors.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SilentErrorHandler implements ErrorHandler
{
    /**
     * Does not do anything.
     *
     * @param logger Ingored.
     */
    public void setLogger(Logger logger)
    {
    }

    /** No options to activate. */
    public void activateOptions()
    {
    }

    /**
     * Does not do anything.
     *
     * @param message   Ignored.
     * @param e         Ignored.
     * @param errorCode Ignored.
     */
    public void error(String message, Exception e, int errorCode)
    {
    }

    /**
     * Does not do anything.
     *
     * @param message   Ignored.
     * @param e         Ignored.
     * @param errorCode Ignored.
     * @param event     Ignored.
     */
    public void error(String message, Exception e, int errorCode, LoggingEvent event)
    {
    }

    /**
     * Does not do anything.
     *
     * @param message Ignored.
     */
    public void error(String message)
    {
    }

    /**
     * Does not do anything.
     *
     * @param appender Ignored.
     */
    public void setAppender(Appender appender)
    {
    }

    /**
     * Does not do anything.
     *
     * @param appender Ignored.
     */
    public void setBackupAppender(Appender appender)
    {
    }
}
