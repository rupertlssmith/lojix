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

/**
 * BitHackUtils provides static functions that implement various bit twiddling hacks.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Calculate ASCII lengths of decimal numbers.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class BitHackUtils
{
    /** The powers of ten below 2^31-1. */
    private static final long[] POWERS_OF_TEN =
        { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };

    /** The powers of ten below 2^63-1. */
    private static final long[] POWERS_OF_TEN_LONG =
        {
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000L, 100000000000L,
            1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L,
            100000000000000000L, 1000000000000000000L
        };

    /** Holds a table of log base 2 up to 255. */
    private static final int[] LOG_TABLE_256 = new int[256];

    /** Branch values for the v3 log2 method. */
    private static final int[] LOG2_V3_BRANCH_VALUES = new int[] { 0x2, 0xC, 0xF0, 0xFF00, 0xFFFF0000 };

    /** Shift values for the v3 log2 method. */
    private static final int[] LOG2_V3_SHIFT_VALUES = new int[] { 1, 2, 4, 8, 16 };

    /** Unsigned value of 10^19, which is the smallest unsigned value with 20 digits. */
    private static final long TEN_POW_19 = 0x8AC7230489E80000L;

    static
    {
        LOG_TABLE_256[0] = LOG_TABLE_256[1] = 0;

        for (int i = 2; i < 256; i++)
        {
            LOG_TABLE_256[i] = 1 + LOG_TABLE_256[i / 2];
        }

        LOG_TABLE_256[0] = -1;
    }

    /**
     * Calcalates the log base 2 of an integer. This code is tuned to uniformly distributed output values, longer
     * numbers are slightly favoured.
     *
     * @param  value The value to calculate for.
     *
     * @return Log base 2 of the input value.
     */
    public static int intLogBase2(int value)
    {
        int temp1;
        int temp2 = value >> 16;

        if (temp2 > 0)
        {
            temp1 = temp2 >> 8;

            return (temp1 > 0) ? (24 + LOG_TABLE_256[temp1]) : (16 + LOG_TABLE_256[temp2]);
        }
        else
        {
            temp1 = value >> 8;

            return (temp1 > 0) ? (8 + LOG_TABLE_256[temp1]) : LOG_TABLE_256[value];
        }
    }

    /**
     * Calcalates the log base 2 of an integer. This code is tuned to uniformly distributed input values, longer numbers
     * are favoured.
     *
     * @param  value The value to calculate for.
     *
     * @return Log base 2 of the input value.
     */
    public static int intLogBase2v2(int value)
    {
        int temp;

        if ((temp = value >> 24) > 0)
        {
            return 24 + LOG_TABLE_256[temp];
        }
        else if ((temp = value >> 16) > 0)
        {
            return 16 + LOG_TABLE_256[temp];
        }
        else if ((temp = value >> 8) > 0)
        {
            return 8 + LOG_TABLE_256[temp];
        }
        else
        {
            return LOG_TABLE_256[value];
        }
    }

    /**
     * Calcalates the log base 2 of an integer in O(log(N)) steps; shorter numbers are favoured.
     *
     * @param  value The value to calculate for.
     *
     * @return Log base 2 of the input value.
     */
    public static int intLogBase2v3(int value)
    {
        int result = 0;

        if ((value & LOG2_V3_BRANCH_VALUES[4]) > 0)
        {
            value >>= LOG2_V3_SHIFT_VALUES[4];
            result |= LOG2_V3_SHIFT_VALUES[4];
        }

        if ((value & LOG2_V3_BRANCH_VALUES[3]) > 0)
        {
            value >>= LOG2_V3_SHIFT_VALUES[3];
            result |= LOG2_V3_SHIFT_VALUES[3];
        }

        if ((value & LOG2_V3_BRANCH_VALUES[2]) > 0)
        {
            value >>= LOG2_V3_SHIFT_VALUES[2];
            result |= LOG2_V3_SHIFT_VALUES[2];
        }

        if ((value & LOG2_V3_BRANCH_VALUES[1]) > 0)
        {
            value >>= LOG2_V3_SHIFT_VALUES[1];
            result |= LOG2_V3_SHIFT_VALUES[1];
        }

        if ((value & LOG2_V3_BRANCH_VALUES[0]) > 0)
        {
            value >>= LOG2_V3_SHIFT_VALUES[0];
            result |= LOG2_V3_SHIFT_VALUES[0];
        }

        return result;
    }

    /**
     * Calcalates the log base 10 of an integer.
     *
     * @param  value The value to calculate for.
     *
     * @return Log base 10 of the input value.
     */
    public static int intLogBase10(int value)
    {
        int temp = ((intLogBase2v3(value) + 1) * 1233) >> 12;

        return temp - ((value < POWERS_OF_TEN[temp]) ? 1 : 0);
    }

    /**
     * Calcalates the log base 10 of an integer. This produces results faster for longer numbers.
     *
     * @param  value The value to calculate for.
     *
     * @return Log base 10 of the input value.
     */
    public static int intLogBase10v2(int value)
    {
        return (value >= 1000000000)
            ? 9
            : ((value >= 100000000)
                ? 8
                : ((value >= 10000000)
                    ? 7
                    : ((value >= 1000000)
                        ? 6
                        : ((value >= 100000)
                            ? 5
                            : ((value >= 10000)
                                ? 4 : ((value >= 1000) ? 3 : ((value >= 100) ? 2 : ((value >= 10) ? 1 : 0))))))));
    }

    /**
     * Calcalates the log base 10 of an integer. This produces results faster for shorter numbers.
     *
     * @param  value The value to calculate for.
     *
     * @return Log base 10 of the input value.
     */
    public static int intLogBase10v3(int value)
    {
        return (value < 10)
            ? 0
            : ((value < 100)
                ? 1
                : ((value < 1000)
                    ? 2
                    : ((value < 10000)
                        ? 3
                        : ((value < 100000)
                            ? 4
                            : ((value < 1000000)
                                ? 5
                                : ((value < 10000000)
                                    ? 6 : ((value < 100000000) ? 7 : ((value < 1000000000) ? 8 : 9))))))));

    }

    /**
     * Calcalates the log base 10 of an integer. This method favours shorter numbers.
     *
     * @param  value The value to calculate for.
     *
     * @return Log base 10 of the input value.
     */
    public static int intLogBase10(long value)
    {
        return (value >= 1000000000000000000L)
            ? 18
            : ((value >= 100000000000000000L)
                ? 17
                : ((value >= 10000000000000000L)
                    ? 16
                    : ((value >= 1000000000000000L)
                        ? 15
                        : ((value >= 100000000000000L)
                            ? 14
                            : ((value >= 10000000000000L)
                                ? 13
                                : ((value >= 1000000000000L)
                                    ? 12
                                    : ((value >= 100000000000L)
                                        ? 11
                                        : ((value >= 10000000000L)
                                            ? 10
                                            : ((value >= 1000000000L)
                                                ? 9
                                                : ((value >= 100000000L)
                                                    ? 8
                                                    : ((value >= 10000000L)
                                                        ? 7
                                                        : ((value >= 1000000L)
                                                            ? 6
                                                            : ((value >= 100000L)
                                                                ? 5
                                                                : ((value >= 10000L)
                                                                    ? 4
                                                                    : ((value >= 1000L)
                                                                        ? 3
                                                                        : ((value >= 100L)
                                                                            ? 2
                                                                            : ((value >= 10L) ? 1 : 0)))))))))))))))));
    }

    /**
     * Calcalates the log base 10 of an integer. This method favours longer numbers, or evenly distributed input.
     *
     * @param  value The value to calculate for.
     *
     * @return Log base 10 of the input value.
     */
    public static int intLogBase10v2(long value)
    {
        return (value < 10)
            ? 0
            : ((value < 100)
                ? 1
                : ((value < 1000)
                    ? 2
                    : ((value < 10000)
                        ? 3
                        : ((value < 100000)
                            ? 4
                            : ((value < 1000000)
                                ? 5
                                : ((value < 10000000)
                                    ? 6
                                    : ((value < 100000000)
                                        ? 7
                                        : ((value < 1000000000L)
                                            ? 8
                                            : ((value < 10000000000L)
                                                ? 9
                                                : ((value < 100000000000L)
                                                    ? 10
                                                    : ((value < 1000000000000L)
                                                        ? 11
                                                        : ((value < 10000000000000L)
                                                            ? 12
                                                            : ((value < 100000000000000L)
                                                                ? 13
                                                                : ((value < 1000000000000000L)
                                                                    ? 14
                                                                    : ((value < 10000000000000000L)
                                                                        ? 15
                                                                        : ((value < 100000000000000000L)
                                                                            ? 16
                                                                            : ((value < 1000000000000000000L)
                                                                                ? 17 : 18)))))))))))))))));
    }

    /**
     * Calculates the number of ASCII characters that will be needed to represent a specifed signed 32-bit integer.
     *
     * @param  value The value to get the character count for.
     *
     * @return The number of ASCII character need to represent an signed 32-bit integer.
     */
    public static int getCharacterCountInt32(int value)
    {
        if (value >= 0)
        {
            return getCharacterCountUInt32(value);
        }
        else if (value == Integer.MIN_VALUE)
        {
            return getCharacterCountUInt32(Integer.MAX_VALUE) + 1;
        }
        else
        {
            return getCharacterCountUInt32(-value) + 1;
        }
    }

    /**
     * Calculates the number of ASCII characters that will be needed to represent a specified unsigned 32-bit integer.
     *
     * @param  value The value to get the character count for.
     *
     * @return The number of ASCII character need to represent an unsigned 32-bit integer.
     */
    public static int getCharacterCountUInt32(int value)
    {
        // A negative value indicates an unsigned number with a size > Integer.MAX_VALUE. Such numbers always
        // have the maximum size of 10.
        if (value < 0)
        {
            return 10;
        }
        else
        {
            return intLogBase10v3(value) + 1;
        }
    }

    /**
     * Calculates the number of ASCII characters that will be needed to represent a specifed signed 64-bit integer.
     *
     * @param  value The value to get the character count for.
     *
     * @return The number of ASCII character need to represent an signed 64-bit integer.
     */
    public static int getCharacterCountInt64(long value)
    {
        if (value >= 0)
        {
            return getCharacterCountUInt64(value);
        }
        else if (value == Long.MIN_VALUE)
        {
            return getCharacterCountUInt64(Long.MAX_VALUE) + 1;
        }
        else
        {
            return getCharacterCountUInt64(-value) + 1;
        }
    }

    /**
     * Calculates the number of ASCII characters that will be needed to represent a specified unsigned 64-bit integer.
     *
     * @param  value The value to get the character count for.
     *
     * @return The number of ASCII character need to represent an unsigned 64-bit integer.
     */
    public static int getCharacterCountUInt64(long value)
    {
        // A negative value indicates an unsigned number with a size > Long.MAX_VALUE. Such numbers have a maximum size
        // of 19 or 20.
        if (value < 0)
        {
            return (value < TEN_POW_19) ? 19 : 20;
        }
        else
        {
            return intLogBase10v2(value) + 1;
        }
    }

    /**
     * Calculates the number of ASCII characters that will be needed to represent a specified signed decimal number.
     *
     * @param  integerValue The integer component of the decimal number.
     * @param  scale        The scale component of the decimal number.
     *
     * @return The number of ASCII character need to represent a signed decimal number.
     */
    public static int getCharacterCountDecimal(long integerValue, int scale)
    {
        boolean isNeg = integerValue < 0;

        // Work out how many digits will be needed for the number, adding space for the minus sign, the decimal
        // point and leading zeros if needed.
        int totalDigits = BitHackUtils.getCharacterCountInt64(integerValue);
        int totalLength = totalDigits;

        if (isNeg)
        {
            totalDigits--; // Minus sign already accounted for.
        }

        if (scale > 0)
        {
            totalLength++; // For the decimal point.

            if (scale >= totalDigits)
            {
                // For the leading zeros (+ 1 for the zero before decimal point).
                totalLength += (scale - totalDigits) + 1;
            }
        }
        else
        {
            // Add a zero for each negative point in scale
            totalLength -= scale;
        }

        return totalLength;
    }

    /**
     * Counts the number of bits set in a word.
     *
     * @param  word The word to count the bits in.
     *
     * @return The number of bits set in the word.
     */
    public static int ones(int word)
    {
        int result;

        for (result = 0; word != 0; result++)
        {
            word &= word - 1; // Clear the least significant bit set.
        }

        return result;
    }
}
