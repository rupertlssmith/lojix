/*
 * Copyright The Sett Ltd, 2005 to 2009.
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
package com.thesett.common.validate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thesett.common.error.NotImplementedException;

/**
 * Validation contains many common validation methods as static methods. This class is a repository for useful
 * validation code.
 *
 * <p>All the methods are public an static so that they can easily be re-used anywhere. Any class can extend this one to
 * use its methods locally.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Convert a string to an integer
 * <tr><td>Convert a string to a date
 * <tr><td>Validate string as integer
 * <tr><td>Validate string as date
 * <tr><td>Validate string as empty (null or "")
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Validation
{
    /**
     * Constant representing the default date format to use.
     *
     * @todo Replace with a Locale specific setting.
     */
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    /**
     * Constant representing the default time format to use.
     *
     * @todo Replace with Locale specific setting.
     */
    public static final String TIME_FORMAT = "HH:mm";

    /**
     * Constant representing the default datetime format to use.
     *
     * @todo Replace with Locale specific setting.
     */
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";

    /** Holds the current date format. */
    public static String dateFormat = DATE_FORMAT;

    /** Holds the current time format. */
    public static String timeFormat = TIME_FORMAT;

    /** Holds the current datetime format. */
    public static String dateTimeFormat = DATE_TIME_FORMAT;

    /**
     * Converts a string to an integer. The string must be a valid integer or the result will be zero.
     *
     * @param  s The string to convert.
     *
     * @return The string as an integer, or 0 if it is not a valid integer.
     */
    public static int toInteger(String s)
    {
        try
        {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            // Exception noted so can be ignored.
            e = null;

            return 0;
        }
    }

    /**
     * Converts a string to a date. The string must be a date in the correct format or this method will return null.
     *
     * @param  s The string to convert.
     *
     * @return The string as a date or null if it is not a valid date.
     */
    public static Date toDate(String s)
    {
        // Build a date formatter using the format specified by dateFormat
        DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

        try
        {
            return dateFormatter.parse(s);
        }
        catch (ParseException e)
        {
            // Exception noted so can be ignored.
            e = null;

            return null;
        }
    }

    /**
     * Checks that a string is an integer.
     *
     * @param  s The string to check for being an integer.
     *
     * @return True if the string is an integer.
     */
    public static boolean isInteger(String s)
    {
        try
        {
            Integer.parseInt(s);

            return true;
        }
        catch (NumberFormatException e)
        {
            // Exception noted so can be ignored.
            e = null;

            return false;
        }
    }

    /**
     * Check that a string is a date in the format specified by dateFormat.
     *
     * @param  s The string to check for being a date.
     *
     * @return True if the string is a date.
     */
    public static boolean isDate(String s)
    {
        // Build a date formatter using the format specified by dateFormat
        DateFormat dateFormatter = new SimpleDateFormat(dateFormat);

        try
        {
            dateFormatter.parse(s);

            return true;
        }
        catch (ParseException e)
        {
            // Exception noted so can be ignored.
            e = null;

            return false;
        }
    }

    /**
     * Checks that a string is a time in the format specified by timeFormat.
     *
     * @param  s The string to check for being a time.
     *
     * @return True if the string is a time.
     */
    public static boolean isTime(String s)
    {
        // Build a time formatter using the format specified by timeFormat
        DateFormat dateFormatter = new SimpleDateFormat(timeFormat);

        try
        {
            dateFormatter.parse(s);

            return true;
        }
        catch (ParseException e)
        {
            // Exception noted so can be ignored.
            e = null;

            return false;
        }
    }

    /**
     * Checks that a string is a datetime in the format specified by dateTimeFormat.
     *
     * @param  s The string to check for being a timestamp.
     *
     * @return True if the string is a timestamp.
     */
    public static boolean isDateTime(String s)
    {
        DateFormat dateFormatter = new SimpleDateFormat(dateTimeFormat);

        try
        {
            dateFormatter.parse(s);

            return true;
        }
        catch (ParseException e)
        {
            // Exception noted so can be ignored.
            e = null;

            return false;
        }
    }

    /**
     * Checks that a string is an integer if it is not null.
     *
     * @param  s The string to check for being an integer or empty.
     *
     * @return True if the string is an integer or empty.
     */
    public static boolean isIntegerOrEmpty(String s)
    {
        return (isEmpty(s) || isInteger(s));
    }

    /**
     * Checks that a string is a date if it is not empty.
     *
     * @param  s The string to check for being a date or empty.
     *
     * @return True if the string is a date or empty.
     */
    public static boolean isDateOrEmpty(String s)
    {
        return (isEmpty(s) || isDate(s));
    }

    /**
     * Checks that a string is a time if it is not empty.
     *
     * @param  s The string to check for being a time or empty.
     *
     * @return True if the string is a time or empty.
     */
    public static boolean isTimeOrEmpty(String s)
    {
        return (isEmpty(s) || isTime(s));
    }

    /**
     * Checks that a string is a dateTime if it is not empty.
     *
     * @param  s The string to check for being a timestamp or empty.
     *
     * @return True if the string is a timestamp or empty.
     */
    public static boolean isDateTimeOrEmpty(String s)
    {
        return (isEmpty(s) || isDateTime(s));
    }

    /**
     * Checks that a string is empty (== "") or null.
     *
     * @param  s The string to check for being empty or null.
     *
     * @return True if the string is empty or null.
     */
    public static boolean isEmpty(String s)
    {
        return ((s == null) || "".equals(s));
    }

    /**
     * Checks that a string contains only letters.
     *
     * @param  s The string to check for containing only letters.
     *
     * @return True if the string contains only letters.
     */
    public static boolean isAlpha(String s)
    {
        throw new NotImplementedException();
    }

    /**
     * Checks that a string contains only number.
     *
     * @param  s The string to check for containing only numbers.
     *
     * @return True if the string contains only number.
     */
    public static boolean isNumeric(String s)
    {
        throw new NotImplementedException();
    }

    /**
     * Checks that a string contains only numbers or letters.
     *
     * @param  s The string to check for containing only numbers or letters.
     *
     * @return True if the string contains only number or letters.
     */
    public static boolean isAlphanumeric(String s)
    {
        throw new NotImplementedException();
    }

    /**
     * Checks that a string has the format of a credit card number.
     *
     * <p>A string passes this test if it consists of 16 numeric characters.
     *
     * <p>This method does not validate the number. It only check it has the right format.
     *
     * @param  s The string to test for being in the right format for being a credit card number.
     *
     * @return True if the string consists of 16 numeric digits.
     */
    public static boolean isCreditCardNumber(String s)
    {
        throw new NotImplementedException();
    }

    /**
     * Checks that a string contains the name of a credit card type.
     *
     * @param  s The string to test for being a credit card type.
     *
     * @return True if the string is a valid credit card type.
     */
    public static boolean isCreditCardType(String s)
    {
        throw new NotImplementedException();
    }

    /**
     * Checks that a string has the right format of an email address.
     *
     * <p>A string passes this test if it is in the format XXX@YYY.ZZZ. Where X, Y and Z are not empty and are
     * alphanumeric plus the symbols '-' and '_'.
     *
     * <p/>To really confirm that an email address is valid an email should be sent to it to which the owner must send a
     * confirmation back again.
     *
     * @param  s The string to check for having the correct format for an email address.
     *
     * @return True if the string has the correct format for an email address.
     */
    public static boolean isEmailAddress(String s)
    {
        throw new NotImplementedException();
    }
}
