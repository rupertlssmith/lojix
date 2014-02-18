/*
 * Copyright The Sett Ltd.
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

import java.util.Calendar;

/**
 * DateOnly implements a pure date with no time of day component.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Manage a reference count against a date.
 * <tr><td> Export a date from an object pool.
 * <tr><td> Copy a date into another date.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DateOnly implements DayOfYear, Comparable<DateOnly>
{
    /** Holds the year. */
    private int year;

    /** Holds the month. */
    private int month;

    /** Holds the day. */
    private int day;

    /**
     * Creates a date initialised to the day denominated by the unix timestamp given.
     *
     * @param epochOffset An offset in milliseconds since UTC midnight 01/01/1970.
     */
    public DateOnly(long epochOffset)
    {
        setTicks(epochOffset);
    }

    /**
     * Creates a data initialised to the specified value.
     *
     * @param year  The year.
     * @param month The month.
     * @param day   The day.
     */
    public DateOnly(int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * Creates a fresh date, initialised to today. This method makes use of java.util.Calendar to initialise today's
     * date.
     */
    public DateOnly()
    {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DATE);
    }

    /**
     * Parses a string as a date.
     *
     * @param  dateString The string to parse.
     *
     * @return The string as a DateOnly.
     */
    public static DateOnly parseDate(String dateString)
    {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Returns the UTC timestamp representing this date as midnight UTC on the date that this represents. If this
     * date is not a UTC date, this value will not be a UTC timestamp.
     */
    public long getTicks()
    {
        return TimeUtils.timestampToTicks(year, month, day);
    }

    /** {@inheritDoc} */
    public int getYear()
    {
        return year;
    }

    /** {@inheritDoc} */
    public int getMonth()
    {
        return month;
    }

    /** {@inheritDoc} */
    public int getDate()
    {
        return day;
    }

    /** {@inheritDoc} */
    public void setYear(int year)
    {
        this.year = year;
    }

    /** {@inheritDoc} */
    public void setMonth(int month)
    {
        this.month = month;
    }

    /** {@inheritDoc} */
    public void setDate(int date)
    {
        day = date;
    }

    /** {@inheritDoc} */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof DateOnly))
        {
            return false;
        }

        DateOnly dateOnly = (DateOnly) o;

        if (day != dateOnly.day)
        {
            return false;
        }

        if (month != dateOnly.month)
        {
            return false;
        }

        if (year != dateOnly.year)
        {
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    public int hashCode()
    {
        int result;
        result = year;
        result = (31 * result) + month;
        result = (31 * result) + day;

        return result;
    }

    /** {@inheritDoc} */
    public int compareTo(DateOnly o)
    {
        if (year < o.year)
        {
            return -1;
        }
        else if (year > o.year)
        {
            return 1;
        }
        else if (month < o.month)
        {
            return -1;
        }
        else if (month > o.month)
        {
            return 1;
        }
        else if (day < o.day)
        {
            return -1;
        }
        else if (day > o.day)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Pretty prints this timestamp with offset as a string, mainly for debugging purposes.
     *
     * @return This time span as a string.
     */
    public String toString()
    {
        return year + "/" + month + "/" + day;
    }

    /**
     * Sets this date from a milliseconds timestamp.
     *
     * @param ticks The timestamp.
     */
    void setTicks(long ticks)
    {
        year = TimeUtils.ticksToYears(ticks);
        month = TimeUtils.ticksToMonths(ticks);
        day = TimeUtils.ticksToDate(ticks);
    }
}
