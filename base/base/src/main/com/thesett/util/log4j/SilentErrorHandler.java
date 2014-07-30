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
