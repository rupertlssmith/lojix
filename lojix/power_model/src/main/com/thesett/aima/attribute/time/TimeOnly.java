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

import java.nio.ByteBuffer;

/**
 * TimeOnly represents a span of time in milliseconds. It can be used to represent a time of day, without a time zone,
 * by representing the number of milliseconds since midnight.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Represent a span of time in milliseconds.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TimeOnly implements TimeOfDay, Comparable<TimeOnly>
{
    /** Holds the number of millisecond ticks. */
    private long timeOfDay;

    /** Creates an uninitialised time of day. */
    private TimeOnly()
    {
    }

    /**
     * Creates a time of day as the number of milliseconds since midnight.
     *
     * @param timeOfDay The number of milliseconds since midnight.
     */
    public TimeOnly(long timeOfDay)
    {
        this.timeOfDay = timeOfDay;
    }

    /**
     * Creates a time span as the specified time offset since midnight.
     *
     * @param hour        The hours since midnight.
     * @param minute      The minutes.
     * @param second      The seconds.
     * @param millisecond The milliseconds.
     */
    public TimeOnly(int hour, int minute, int second, int millisecond)
    {
        timeOfDay = TimeUtils.timeOfDayToTicks(hour, minute, second, millisecond);
    }

    /**
     * Creates a time span as the specified time offset since midnight.
     *
     * @param hour   The hours since midnight.
     * @param minute The minutes.
     */
    public TimeOnly(int hour, int minute)
    {
        timeOfDay = TimeUtils.timeOfDayToTicks(hour, minute);
    }

    /**
     * Parses a string as a time only.
     *
     * @param  timeString The string to parse.
     *
     * @return The string as a TimeOnly.
     */
    public static TimeOnly parseTime(String timeString)
    {
        return null;
    }

    /**
     * Provides the number of millisecond ticks.
     *
     * @return The number of millisecond ticks.
     */
    public long getTimeOfDay()
    {
        return timeOfDay;
    }

    /**
     * Accepts the number of millisecond ticks.
     *
     * @param timeOfDay The number of millisecond ticks.
     */
    public void setTimeOfDay(long timeOfDay)
    {
        this.timeOfDay = timeOfDay;
    }

    /** {@inheritDoc} */
    public int getHours()
    {
        return TimeUtils.ticksToHours(timeOfDay);
    }

    /** {@inheritDoc} */
    public int getMinutes()
    {
        return TimeUtils.ticksToMinutes(timeOfDay);
    }

    /** {@inheritDoc} */
    public int getSeconds()
    {
        return TimeUtils.ticksToSeconds(timeOfDay);
    }

    /** {@inheritDoc} */
    public int getMilliseconds()
    {
        return TimeUtils.ticksToMilliseconds(timeOfDay);
    }

    /** {@inheritDoc} */
    public void setHours(int hours)
    {
        timeOfDay = TimeUtils.ticksWithHoursSetTo(timeOfDay, hours);
    }

    /** {@inheritDoc} */
    public void setMinutes(int minutes)
    {
        timeOfDay = TimeUtils.ticksWithMinutesSetTo(timeOfDay, minutes);
    }

    /** {@inheritDoc} */
    public void setSeconds(int seconds)
    {
        timeOfDay = TimeUtils.ticksWithSecondsSetTo(timeOfDay, seconds);
    }

    /** {@inheritDoc} */
    public void setMilliseconds(int milliseconds)
    {
        timeOfDay = TimeUtils.ticksWithMillisecondsSetTo(timeOfDay, milliseconds);
    }

    /** {@inheritDoc} */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        TimeOnly timeOnly = (TimeOnly) o;

        if (timeOfDay != timeOnly.timeOfDay)
        {
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    public int hashCode()
    {
        return (int) (timeOfDay ^ (timeOfDay >>> 32));
    }

    /** {@inheritDoc} */
    public int compareTo(TimeOnly o)
    {
        return (timeOfDay < o.timeOfDay) ? -1 : ((timeOfDay == o.timeOfDay) ? 0 : 1);
    }

    /**
     * Pretty prints this time span as a string, mainly for debugging purposes.
     *
     * @return This time span as a string.
     */
    public String toString()
    {
        ByteBuffer buffer = ByteBuffer.allocate(TimeUtils.TIME_ONLY_LENGTH_WITH_MILLISECONDS);
        buffer = TimeUtils.putTimeOnlyAsString(buffer, timeOfDay);

        buffer.flip();

        return ByteBufferUtils.asString(buffer, buffer.remaining());
    }
}
