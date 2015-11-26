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
package com.thesett.aima.attribute.time;

/**
 * TimeOfDay represents a time of the day, in hours, minutes, seconds and milliseconds. The 24 hour clock is always used
 * to report the hours component.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide a mutable time in 24 hour format, down to millisecond accuracy.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TimeOfDay
{
    /**
     * Provides the hours component. Hours are numbered 0 to 23.
     *
     * @return The hours component.
     */
    int getHours();

    /**
     * Accepts the hours component.
     *
     * @param hours The hours component. Hours are numbered 0 to 23.
     */
    void setHours(int hours);

    /**
     * Provides the minutes component. Minutes are numbered 0 to 59.
     *
     * @return The minutes component.
     */
    int getMinutes();

    /**
     * Accepts the minutes component.
     *
     * @param minutes The minutes component. Minutes are numbered 0 to 59.
     */
    void setMinutes(int minutes);

    /**
     * Provides the seconds component. Seconds are numbered 0 to 61 (60 and 61 may be used for leap seconds).
     *
     * @return The seconds component.
     */
    int getSeconds();

    /**
     * Accepts the seconds component.
     *
     * @param seconds The seconds component. Seconds are numbered 0 to 61 (60 and 61 may be used for leap seconds).
     */
    void setSeconds(int seconds);

    /**
     * Provides the milliseconds component. Milliseconds are numbered 0 to 999.
     *
     * @return The milliseconds component.
     */
    int getMilliseconds();

    /**
     * Accepts the milliseconds component.
     *
     * @param milliseconds The milliseconds component. Milliseconds are numbered 0 to 999.
     */
    void setMilliseconds(int milliseconds);
}
