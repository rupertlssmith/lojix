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
package com.thesett.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * StringUtils provides some commonly re-used functions on strings.
 *
 * <p/>There are methods to convert between delimited string and array representations. For example, comma seperated
 * strings such as "1,2,3" can be converted to array representations { "1", "2", "3" } and vica versa.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Convert between delimited strings and arrays.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class StringUtils
{
    /**
     * Converts a string listing sub-strings seperated by a delimeter into an array of strings.
     *
     * <p/>For example, suppose the string, "one,two,three" is presented with the delimter ",", then this will be
     * converted into the string array { "one", "two", "three" }.
     *
     * @param  value The string to convert.
     * @param  delim The delimeter.
     *
     * @return The string converted into an array of strings.
     */
    public static String[] listToArray(String value, String delim)
    {
        List<String> result = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(value, delim);

        while (tokenizer.hasMoreTokens())
        {
            result.add(tokenizer.nextToken());
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * Converts a string listing sub-strings seperated by a comma delimeter into an array of strings.
     *
     * @param  value The string to convert.
     *
     * @return The comma seperated string converted into an array of strings.
     */
    public static String[] commaListToArray(String value)
    {
        return listToArray(value, ",");
    }

    /**
     * Converts an array of strings into a delimeter seperated string.
     *
     * @param  array The array to convert.
     * @param  delim The delimeter to use.
     *
     * @return The array of strings converted into a delimeter seperated string.
     */
    public static String arrayToList(String[] array, String delim)
    {
        String result = "";

        for (int i = 0; i < array.length; i++)
        {
            result += array[i] + ((i == (array.length - 1)) ? "" : delim);
        }

        return result;
    }

    /**
     * Converts an array of strings into a comma seperated string.
     *
     * @param  array The array to convert.
     *
     * @return The array of strings converted into a comma seperated string.
     */
    public static String arrayToCommaList(String[] array)
    {
        return arrayToList(array, ",");
    }

    /**
     * Converts a string to camel case.
     *
     * @param  name The string to convert to camel case.
     *
     * @return The string in camel case.
     */
    public static String toCamelCase(String name)
    {
        String[] parts = name.split("_");
        String result = parts[0];

        for (int i = 1; i < parts.length; i++)
        {
            if (parts[i].length() > 0)
            {
                result += upperFirstChar(parts[i]);
            }
        }

        return result;
    }

    /**
     * Converts a string to camel case with the first letter in uppercase.
     *
     * @param  name The string to convert to camel case.
     *
     * @return The string in camel case.
     */
    public static String toCamelCaseUpper(String name)
    {
        return convertCase(name, "", true, false);
    }

    /**
     * Converts a string to camel case with the first letter in lowercase.
     *
     * @param  name The string to convert to camel case.
     *
     * @return The string in camel case.
     */
    public static String toCamelCaseLower(String name)
    {
        return convertCase(name, "", false, false);
    }

    /**
     * Converts a string to snake case with the first letter in uppercase.
     *
     * @param  name The string to convert to snake case.
     *
     * @return The string in snake case.
     */
    public static String toSnakeCaseUpper(String name)
    {
        return convertCase(name, "_", true, false);
    }

    /**
     * Converts a string to snake case with the first letter in lowercase.
     *
     * @param  name The string to convert to snake case.
     *
     * @return The string in snake case.
     */
    public static String toSnakeCaseLower(String name)
    {
        return convertCase(name, "_", false, false);
    }

    /**
     * Converts a string to kebab case with the first letter in uppercase.
     *
     * @param  name The string to convert to kebab case.
     *
     * @return The string in kebab case.
     */
    public static String toKebabCaseUpper(String name)
    {
        return convertCase(name, "-", true, false);
    }

    /**
     * Converts a string to kebab case with the first letter in lowercase.
     *
     * @param  name The string to convert to kebab case.
     *
     * @return The string in kebab case.
     */
    public static String toKebabCaseLower(String name)
    {
        return convertCase(name, "-", false, false);
    }

    /** Used to track the state of the machine that extracts words from variable names. */
    private enum WordMachineState {
        Initial,
        StartWord,
        ContinueWordCaps,
        ContinueWordLower
    }

    /**
     * Converts string between various case forms such as camel case, snake case or kebab case.
     *
     * <p/>Characters in the string are characterized as whitespace, upper case, or other by the following rules.
     *
     * <ol>
     * <li> An upper case letter is marked as upper case (U). </li>
     * <li> A lower case letter or digit is marked as lower case (L). </li>
     * <li> Any other character is marked as whitespace (W). </li>
     * </ol>
     *
     * <p/>The string is processed into words by these rules.
     *
     * <ol>
     * <li> W at the start is discarded. </li>
     * <li> U or L begins a word. </li>
     * <li> U after the initial character of a word continues the same word, so long as no W or L is encountered. </li>
     * <li> L after U or L continues the word, so long as no W is encountered. </li>
     * <li> U after L begins a new word. </li>
     * <li> W after U or L is discarded and ends the current word. </li>
     * </ol>
     *
     * @param value                  The string to convert.
     * @param separator              The separator to place between words.
     * @param firstLetterUpper       <tt>true</tt> iff the first letter should be in capitals.
     * @param firstLetterOfWordUpper <tt>true</tt> iff the first letter of subsequent words should be in capitals.
     *
     * @return The input converted to lower case words separated by spaces, as per the rules described above.
     */
    public static String convertCase(String value, String separator, boolean firstLetterUpper, boolean firstLetterOfWordUpper) {
        final StringBuffer result = new StringBuffer();

        boolean firstLetter = true;
        boolean upper = false;

        WordMachineState state = WordMachineState.Initial;

        Function2<Character, Boolean, StringBuffer> writeChar = new Function2<Character, Boolean, StringBuffer>() {
            public StringBuffer apply(Character nextChar, Boolean upper) {
                if (upper)
                    result.append(Character.toUpperCase(nextChar));
                else
                    result.append(Character.toLowerCase(nextChar));

                return result;
            }
        };

        for (int i = 0; i < value.length(); i++) {
            char nextChar = value.charAt(i);

            if (Character.isUpperCase(nextChar)) {
                switch (state) {
                    case Initial:
                        state = WordMachineState.StartWord;
                        upper = true;
                        break;
                    case StartWord:
                    case ContinueWordCaps:
                        state = WordMachineState.ContinueWordCaps;
                        upper = false;
                        break;
                    case ContinueWordLower:
                        state = WordMachineState.StartWord;
                        upper = true;
                        result.append(separator);
                        break;
                }

                writeChar.apply(nextChar, (!firstLetter && upper) || (firstLetter & firstLetterUpper));
                firstLetter = false;
            } else if (Character.isLetterOrDigit(nextChar)) {
                switch (state) {
                    case Initial:
                        state = WordMachineState.StartWord;
                        upper = true;
                        break;
                    case StartWord:
                    case ContinueWordLower:
                    case ContinueWordCaps:
                        state = WordMachineState.ContinueWordLower;
                        upper = false;
                        break;
                }

                writeChar.apply(nextChar, (!firstLetter && upper) || (firstLetter & firstLetterUpper));
                firstLetter = false;
            } else {
                switch (state) {
                    case Initial:
                        state = WordMachineState.Initial;
                        break;
                    case StartWord:
                    case ContinueWordCaps:
                    case ContinueWordLower:
                        state = WordMachineState.Initial;
                        result.append(separator);
                        break;
                }

                upper = false;
            }
        }

        return result.toString().trim();
    }

    /**
     * Converts the first character of a string to upper case.
     *
     * @param  name The string to convert the first character of.
     *
     * @return The string with its first character in upper case.
     */
    public static String upperFirstChar(String name)
    {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Converts the first character of a string to lower case.
     *
     * @param  name The string to convert the first character of.
     *
     * @return The string with its first character in lower case.
     */
    public static String lowerFirstChar(String name)
    {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    /**
     * Converts a string to camel case.
     *
     * @param  name The string to convert to camel case.
     *
     * @return The string in camel case.
     */
    public String toCamelCaseFunc(String name)
    {
        return toCamelCase(name);
    }

    /**
     * Converts a string to camel case with the first letter in uppercase.
     *
     * @param  name The string to convert to camel case.
     *
     * @return The string in camel case.
     */
    public String toCamelCaseUpperFunc(String name)
    {
        return toCamelCaseUpper(name);
    }

    /**
     * Converts the first character of a string to upper case.
     *
     * @param  name The string to convert the first character of.
     *
     * @return The string with its first character in upper case.
     */
    public String upperFirstCharFunc(String name)
    {
        return upperFirstChar(name);
    }
}
