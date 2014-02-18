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

import java.nio.ByteBuffer;

/**
 * TimeUtils provides static methods for performing time related calculations, particularly conversions between unix
 * timestamps and dates and times.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Perform conversions to and from times and dates in milliseconds and ASCII formats.
 * <tr><td> Write times and dates to byte buffers.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TimeUtils
{
    /** The number of milliseconds in a second. */
    private static final int MILLIS_PER_SECOND = 1000;

    /** The number of milliseconds in a minute. */
    public static final int MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;

    /** The number of milliseconds in an hour. */
    public static final int MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    /** The number of milliseconds in a day. */
    private static final int MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

    /** The number of milliseconds in a year. */
    public static final long MILLIS_PER_YEAR = 365L * MILLIS_PER_DAY;

    /** The number of milliseconds in a leap year. */
    public static final long MILLIS_PER_LEAP_YEAR = 366L * MILLIS_PER_DAY;

    /** The number of ticks in a minute. */
    public static final long TICKS_PER_MINUTE = MILLIS_PER_MINUTE;

    /** The number of milliseconds in 365.2425 days. */
    private static final long MILLIS_PER_REAL_YEAR = (long) (365.2425 * MILLIS_PER_DAY);

    /** The number of days from 0000 to 1970. */
    private static final int DAYS_TO_1970 = 719527;

    /** Tables the number of days in the year prior to the start of each month, for leap years. */
    private static final int[] LEAP_DAYS_IN_YEAR_PRIOR_TO_MONTH =
        { 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335 };

    /** Tables the number of days in the year prior to the start of each month. */
    private static final int[] USUAL_DAYS_IN_YEAR_PRIOR_TO_MONTH =
        { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };

    /** Tables the number of days in each month, for leap years. */
    private static final int[] LEAP_DAYS_IN_MONTH = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    /** Tables the number of days in each month, for non leap years. */
    private static final int[] USUAL_DAYS_IN_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    /** The number of milliseconds per day divided by 1024. */
    private static final long MILLIS_PER_DAY_OVER_1024 = MILLIS_PER_DAY >> 10;

    /** The length of a date only in YYYYMMDD format. */
    public static final int DATE_ONLY_LENGTH = 8;

    /** The length of a timestamp in YYYYMMDD-HH:MM:SS format. */
    public static final int DATE_TIME_WITHOUT_MILLISECONDS_LENGTH = 17;

    /** The length of a timestamp in YYYYMMDD-HH:MM:SS.sss format. */
    public static final int DATE_TIME_WITH_MILLISECONDS_LENGTH = 21;

    /** The length of a time of day in HH:MM:SS format. */
    public static final int TIME_ONLY_LENGTH_WIHTOUT_MILLISECONDS = 8;

    /** The length of a time of day in HH:MM:SS.sss format. */
    public static final int TIME_ONLY_LENGTH_WITH_MILLISECONDS = 12;

    /** The length of a time zone offset in [+/-]HH format. */
    public static final int TIME_ZONE_WITHOUT_MINUTES_LENGTH = 3;

    /** The length of a time zone offset in [+/-]HH:MM format. */
    public static final int TIME_ZONE_WITH_MINUTES_LENGTH = 6;

    /**
     * Converts a field by field timestamp into millisecond ticks.
     *
     * @param  year         The year.
     * @param  month        The month.
     * @param  day          The day.
     * @param  hours        The hours.
     * @param  minutes      The minutes.
     * @param  seconds      The seconds.
     * @param  milliseconds The milliseconds.
     *
     * @return Millisecond ticks since midnight 01/01/1970.
     */
    public static long timestampToTicks(int year, int month, int day, int hours, int minutes, int seconds,
        int milliseconds)
    {
        boolean isLeapYear = isLeapYear(year);

        long dayComponent = (long) (day - 1) * MILLIS_PER_DAY;
        long monthComponent = millisToStartOfMonth(month, isLeapYear);
        long yearComponent = millisToYearStart(year);
        long hoursComponent = (long) hours * MILLIS_PER_HOUR;
        long minutesComponent = (long) minutes * MILLIS_PER_MINUTE;
        long secondsComponent = (long) seconds * MILLIS_PER_SECOND;

        return dayComponent + monthComponent + yearComponent + hoursComponent + minutesComponent + secondsComponent +
            milliseconds;
    }

    /**
     * Converts a field by field timestamp into millisecond ticks.
     *
     * @param  year    The year.
     * @param  month   The month.
     * @param  day     The day.
     * @param  hours   The hours.
     * @param  minutes The minutes.
     * @param  seconds The seconds.
     *
     * @return Millisecond ticks since midnight 01/01/1970.
     */
    public static long timestampToTicks(int year, int month, int day, int hours, int minutes, int seconds)
    {
        boolean isLeapYear = isLeapYear(year);

        long dayComponent = (long) (day - 1) * MILLIS_PER_DAY;
        long monthComponent = millisToStartOfMonth(month, isLeapYear);
        long yearComponent = millisToYearStart(year);
        long hoursComponent = (long) hours * MILLIS_PER_HOUR;
        long minutesComponent = (long) minutes * MILLIS_PER_MINUTE;
        long secondsComponent = (long) seconds * MILLIS_PER_SECOND;

        return dayComponent + monthComponent + yearComponent + hoursComponent + minutesComponent + secondsComponent;
    }

    /**
     * Converts a field by field timestamp into millisecond ticks.
     *
     * @param  year  The year.
     * @param  month The month.
     * @param  day   The day.
     *
     * @return Millisecond ticks since midnight 01/01/1970.
     */
    public static long timestampToTicks(int year, int month, int day)
    {
        boolean isLeapYear = isLeapYear(year);

        long dayComponent = (long) (day - 1) * MILLIS_PER_DAY;
        long monthComponent = millisToStartOfMonth(month, isLeapYear);
        long yearComponent = millisToYearStart(year);

        return dayComponent + monthComponent + yearComponent;
    }

    /**
     * Converts a field by field time of day into millisecond ticks.
     *
     * @param  hour        The hours since midnight.
     * @param  minute      The minutes.
     * @param  second      The seconds.
     * @param  millisecond The milliseconds.
     *
     * @return Millisecond ticks since midnight.
     */
    public static long timeOfDayToTicks(int hour, int minute, int second, int millisecond)
    {
        return millisecond + (MILLIS_PER_SECOND * second) + (MILLIS_PER_MINUTE * minute) + (MILLIS_PER_HOUR * hour);
    }

    /**
     * Converts a field by field time of day into millisecond ticks.
     *
     * @param  hour   The hours since midnight.
     * @param  minute The minutes.
     *
     * @return Millisecond ticks since midnight.
     */
    public static long timeOfDayToTicks(int hour, int minute)
    {
        return (MILLIS_PER_MINUTE * minute) + (MILLIS_PER_HOUR * hour);
    }

    /**
     * Extracts the years component of a time in millisecond ticks.
     *
     * @param  ticks The time in ticks.
     *
     * @return The years component of the time.
     */
    public static int ticksToYears(long ticks)
    {
        // The number of years is ticks floor divided by number of milliseconds in 365 1/4 days.
        //return flooredDiv(ticks, MILLIS_PER_REAL_YEAR) + 1970;
        //return flooredDiv(ticks + ((long)DAYS_TO_1970 * MILLIS_PER_DAY), MILLIS_PER_REAL_YEAR);

        long unitMillis = MILLIS_PER_YEAR / 2;
        long i2 = (ticks >> 1) + ((1970L * MILLIS_PER_YEAR) / 2);

        if (i2 < 0)
        {
            i2 = i2 - unitMillis + 1;
        }

        int year = (int) (i2 / unitMillis);

        long yearStart = millisToYearStart(year);
        long diff = ticks - yearStart;

        if (diff < 0)
        {
            year--;
        }
        else if (diff >= (MILLIS_PER_DAY * 365L))
        {
            // One year may need to be added to fix estimate.
            long oneYear;

            if (isLeapYear(year))
            {
                oneYear = MILLIS_PER_DAY * 366L;
            }
            else
            {
                oneYear = MILLIS_PER_DAY * 365L;
            }

            yearStart += oneYear;

            if (yearStart <= ticks)
            {
                year++;
            }
        }

        return year;
    }

    /**
     * Extracts the months component of a time in millisecond ticks.
     *
     * @param  ticks The time in ticks.
     *
     * @return The months component of the time.
     */
    public static int ticksToMonths(long ticks)
    {
        return getMonthOfYear(ticks, ticksToYears(ticks));
    }

    /**
     * Extracts the date (day in month) component of a time in millisecond ticks.
     *
     * @param  ticks The time in ticks.
     *
     * @return The date (day in month) component of the time.
     */
    public static int ticksToDate(long ticks)
    {
        int year = ticksToYears(ticks);
        int month = ticksToMonths(ticks);

        long dayOffset = ticks;
        dayOffset -= millisToYearStart(year);
        dayOffset -=
            isLeapYear(year) ? ((long) LEAP_DAYS_IN_YEAR_PRIOR_TO_MONTH[month - 1] * MILLIS_PER_DAY)
                             : ((long) USUAL_DAYS_IN_YEAR_PRIOR_TO_MONTH[month - 1] * MILLIS_PER_DAY);

        return (int) (dayOffset / MILLIS_PER_DAY) + 1;
    }

    /**
     * Extracts the hours component of a time in millisecond ticks.
     *
     * @param  ticks The time in ticks.
     *
     * @return The hours component of the time.
     */
    public static int ticksToHours(long ticks)
    {
        return (int) (ticks % MILLIS_PER_DAY) / MILLIS_PER_HOUR;
    }

    /**
     * Extracts the minutes component of a time in millisecond ticks.
     *
     * @param  ticks The time in ticks.
     *
     * @return The minutes component of the time.
     */
    public static int ticksToMinutes(long ticks)
    {
        return (int) (ticks % MILLIS_PER_HOUR) / MILLIS_PER_MINUTE;
    }

    /**
     * Extracts the seconds component of a time in millisecond ticks.
     *
     * @param  ticks The time in ticks.
     *
     * @return The seconds component of the time.
     */
    public static int ticksToSeconds(long ticks)
    {
        return (int) (ticks % MILLIS_PER_MINUTE) / MILLIS_PER_SECOND;
    }

    /**
     * Extracts the milliseconds component of a time in millisecond ticks.
     *
     * @param  ticks The time in ticks.
     *
     * @return The milliseconds component of the time.
     */
    public static int ticksToMilliseconds(long ticks)
    {
        return (int) (ticks % MILLIS_PER_SECOND);
    }

    /**
     * Sets the component of the timestamp, returning the new timestamp with updated component.
     *
     * @param  ticks The time in ticks.
     * @param  hours The new hours field.
     *
     * @return The new time in ticks.
     */
    public static long ticksWithHoursSetTo(long ticks, int hours)
    {
        long oldHours = ticksToHours(ticks);

        return ticks - (oldHours * MILLIS_PER_HOUR) + (hours * MILLIS_PER_HOUR);
    }

    /**
     * Sets the minutes component of the timestamp, returning the new timestamp with updated component.
     *
     * @param  ticks   The time in ticks.
     * @param  minutes The new minutes field.
     *
     * @return The new time in ticks.
     */
    public static long ticksWithMinutesSetTo(long ticks, int minutes)
    {
        long oldMinutes = ticksToMinutes(ticks);

        return ticks - (oldMinutes * MILLIS_PER_MINUTE) + (minutes * MILLIS_PER_MINUTE);
    }

    /**
     * Sets the seconds component of the timestamp, returning the new timestamp with updated component.
     *
     * @param  ticks   The time in ticks.
     * @param  seconds The new seconds field.
     *
     * @return The new time in ticks.
     */
    public static long ticksWithSecondsSetTo(long ticks, int seconds)
    {
        long oldSeconds = ticksToSeconds(ticks);

        return ticks - (oldSeconds * MILLIS_PER_SECOND) + (seconds * MILLIS_PER_SECOND);
    }

    /**
     * Sets the milliseconds component of the timestamp, returning the new timestamp with updated component.
     *
     * @param  ticks        The time in ticks.
     * @param  milliseconds The new milliseconds field.
     *
     * @return The new time in ticks.
     */
    public static long ticksWithMillisecondsSetTo(long ticks, int milliseconds)
    {
        long oldMillis = ticksToMilliseconds(ticks);

        return ticks - oldMillis + milliseconds;
    }

    /**
     * Sets the year component of the timestamp, returning the new timestamp with updated component.
     *
     * @param  ticks The time in ticks.
     * @param  year  The new year field.
     *
     * @return The new time in ticks.
     */
    public static long ticksWithYearSetTo(long ticks, int year)
    {
        int oldYear = ticksToYears(ticks);

        return ticks - millisToYearStart(oldYear) + millisToYearStart(year);
    }

    /**
     * Sets the month component of the timestamp, returning the new timestamp with updated component.
     *
     * @param  ticks The time in ticks.
     * @param  month The new month field.
     *
     * @return The new time in ticks.
     */
    public static long ticksWithMonthSetTo(long ticks, int month)
    {
        int year = ticksToYears(ticks);
        boolean isLeapYear = isLeapYear(year);
        int oldMonth = ticksToMonths(ticks);

        return ticks - millisToStartOfMonth(oldMonth, isLeapYear) + millisToStartOfMonth(month, isLeapYear);
    }

    /**
     * Sets the date component of the timestamp, returning the new timestamp with updated component.
     *
     * @param  ticks The time in ticks.
     * @param  date  The new date field.
     *
     * @return The new time in ticks.
     */
    public static long ticksWithDateSetTo(long ticks, int date)
    {
        int oldDays = ticksToDate(ticks);

        return ticks - (oldDays * MILLIS_PER_DAY) + (date * MILLIS_PER_DAY);
    }

    /**
     * Calculates the number of milliseconds to the start of the specified year, taking 1970 as zero.
     *
     * @param  year The year.
     *
     * @return The number of milliseconds to the start of the specified year, taking 1970 as zero.
     */
    public static long millisToYearStart(int year)
    {
        // Calculate how many leap years elapsed prior to the year in question.
        int leapYears = year / 100;

        if (year < 0)
        {
            leapYears = ((year + 3) >> 2) - leapYears + ((leapYears + 3) >> 2) - 1;
        }
        else
        {
            leapYears = (year >> 2) - leapYears + (leapYears >> 2);

            if (isLeapYear(year))
            {
                leapYears--;
            }
        }

        return ((year * 365L) + leapYears - DAYS_TO_1970) * MILLIS_PER_DAY;
    }

    /**
     * Writes a time of day with milliseconds in the format HH:MM:SS[.sss], into a byte buffer. The specified buffer
     * will be enlarged if necessary using the specified byte buffer pool.
     *
     * @param  buffer The initial buffer to write to.
     * @param  value  The time of day since midnight, in milliseconds.
     *
     * @return The buffer written to.
     */
    public static ByteBuffer putTimeOnlyAsString(ByteBuffer buffer, long value)
    {
        buffer = putTimeOnlyAsString(buffer, value, true);

        return buffer;
    }

    /**
     * Calculate the maximum number of ASCII characters needed to represent a time zone, with or without minutes.
     *
     * @param  offset         The time zone offset.
     * @param  includeMinutes <tt>true</tt> to include minutes.
     *
     * @return The maximum number of ASCII characters needed to represent the time zone.
     */
    public static int getCharacterCountTimeZone(long offset, boolean includeMinutes)
    {
        if (offset == 0)
        {
            return 1;
        }
        else if (includeMinutes)
        {
            return TIME_ZONE_WITH_MINUTES_LENGTH;
        }
        else
        {
            return TIME_ZONE_WITHOUT_MINUTES_LENGTH;
        }
    }

    /**
     * Writes only the time component of a time of day timestamp to a byte array in the following format:
     * HH:MM:SS[.sss]. The millisecond value is optional and is only written if requested. If there is insufficient
     * space in the buffer to write the value into, then the buffer size is increased using the supplied byte buffer
     * pool.
     *
     * @param  buffer              The byte buffer to write to.
     * @param  value               The value to write as a millisecond timestamp.
     * @param  includeMilliseconds <tt>true</tt> to include milliseconds in the output.
     *
     * @return The byte buffer with the value written to it. This may be a different buffer to the one passed in if the
     *         buffer had to be copied in order to increase its size.
     */
    public static ByteBuffer putTimeOnlyAsString(ByteBuffer buffer, long value, boolean includeMilliseconds)
    {
        // Ensure there is sufficient space in the buffer for the date.
        int charsRequired =
            includeMilliseconds ? TIME_ONLY_LENGTH_WITH_MILLISECONDS : TIME_ONLY_LENGTH_WIHTOUT_MILLISECONDS;

        buffer = ByteBufferUtils.putPaddedInt32AsString(buffer, ticksToHours(value), 2);
        buffer = ByteBufferUtils.putByteAsString(buffer, (byte) ':');
        buffer = ByteBufferUtils.putPaddedInt32AsString(buffer, ticksToMinutes(value), 2);
        buffer = ByteBufferUtils.putByteAsString(buffer, (byte) ':');
        buffer = ByteBufferUtils.putPaddedInt32AsString(buffer, ticksToSeconds(value), 2);

        if (includeMilliseconds)
        {
            buffer = ByteBufferUtils.putByteAsString(buffer, (byte) '.');
            buffer = ByteBufferUtils.putPaddedInt32AsString(buffer, ticksToMilliseconds(value), 3);
        }

        return buffer;
    }

    /**
     * Determines whether or not a year is a leap year. This takes no account of when leap years were first introduced,
     * so will not be accurate for ancient dates.
     *
     * @param  year The year.
     *
     * @return <tt>true</tt> if the year is a leap year.
     */
    private static boolean isLeapYear(int year)
    {
        if ((year & 3) != 0)
        {
            return false;
        }

        if ((year % 100) != 0)
        {
            return true;
        }

        return (year % 400) == 0;
    }

    /**
     * Given a millisecond timestamp that lands in a specified year, calculate what month the timestamp corresponds to.
     *
     * @param  ticks The timestamp.
     * @param  year  The year that the timestamp falls in.
     *
     * @return The month that the timestamp falls in, in the range 1 to 12.
     */
    private static int getMonthOfYear(long ticks, int year)
    {
        int i = (int) ((ticks - millisToYearStart(year)) >> 10);

        return (isLeapYear(year))
            ? ((i < (182 * MILLIS_PER_DAY_OVER_1024))
                ? ((i < (91 * MILLIS_PER_DAY_OVER_1024))
                    ? ((i < (31 * MILLIS_PER_DAY_OVER_1024)) ? 1 : ((i < (60 * MILLIS_PER_DAY_OVER_1024)) ? 2 : 3))
                    : ((i < (121 * MILLIS_PER_DAY_OVER_1024)) ? 4 : ((i < (152 * MILLIS_PER_DAY_OVER_1024)) ? 5 : 6)))
                : ((i < (274 * MILLIS_PER_DAY_OVER_1024))
                    ? ((i < (213 * MILLIS_PER_DAY_OVER_1024)) ? 7 : ((i < (244 * MILLIS_PER_DAY_OVER_1024)) ? 8 : 9))
                    : ((i < (305 * MILLIS_PER_DAY_OVER_1024)) ? 10
                                                              : ((i < (335 * MILLIS_PER_DAY_OVER_1024)) ? 11 : 12))))
            : ((i < (181 * MILLIS_PER_DAY_OVER_1024))
                ? ((i < (90 * MILLIS_PER_DAY_OVER_1024))
                    ? ((i < (31 * MILLIS_PER_DAY_OVER_1024)) ? 1 : ((i < (59 * MILLIS_PER_DAY_OVER_1024)) ? 2 : 3))
                    : ((i < (120 * MILLIS_PER_DAY_OVER_1024)) ? 4 : ((i < (151 * MILLIS_PER_DAY_OVER_1024)) ? 5 : 6)))
                : ((i < (273 * MILLIS_PER_DAY_OVER_1024))
                    ? ((i < (212 * MILLIS_PER_DAY_OVER_1024)) ? 7 : ((i < (243 * MILLIS_PER_DAY_OVER_1024)) ? 8 : 9))
                    : ((i < (304 * MILLIS_PER_DAY_OVER_1024)) ? 10
                                                              : ((i < (334 * MILLIS_PER_DAY_OVER_1024)) ? 11 : 12))));
    }

    /**
     * Given a month, numbered from 1 to 12, calculates the number of milliseconds to the start of that month in a year.
     *
     * @param  month    The month.
     * @param  leapYear <tt>true</tt> if the year is a leap year.
     *
     * @return The number of milliseconds to the start of the month.
     */
    private static long millisToStartOfMonth(int month, boolean leapYear)
    {
        return
            (long) (leapYear ? LEAP_DAYS_IN_YEAR_PRIOR_TO_MONTH[month - 1]
                             : USUAL_DAYS_IN_YEAR_PRIOR_TO_MONTH[month - 1]) * MILLIS_PER_DAY;
    }
}
