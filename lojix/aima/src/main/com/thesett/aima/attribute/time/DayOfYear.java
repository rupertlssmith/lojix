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
 * DayOfYear represents a pure date with no time component.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide a mutable date with no time component.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface DayOfYear
{
    /**
     * Provides the year component.
     *
     * @return The year component.
     */
    int getYear();

    /**
     * Accepts the year component.
     *
     * @param year The year component.
     */
    void setYear(int year);

    /**
     * Provides the month component. Months are numbered 1 to 12.
     *
     * @return The month component.
     */
    int getMonth();

    /**
     * Accepts the month component. Months are numbered 1 to 12.
     *
     * @param month The month component.
     */
    void setMonth(int month);

    /**
     * Provides the date within a month component. Dates are numbered 1 to 31.
     *
     * @return The date within a month component.
     */
    int getDate();

    /**
     * Accepts the date within a month component. Dates are numbered 1 to 31.
     *
     * @param date The date within a month component.
     */
    void setDate(int date);
}
