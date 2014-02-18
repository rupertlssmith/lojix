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
 * ByteBufferUtils provides helper methods for working with ByteBuffers.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Grows a buffer if needed.
 * <tr><td> Write common types as ASCII strings into a byte buffer.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ByteBufferUtils
{
    /** The minus sign in ASCII. */
    private static final byte MINUS_ASCII = (byte) '-';

    /** Zero in ASCII. */
    private static final byte ZERO_ASCII = (byte) '0';

    /**
     * Writes the specified integer value as an ASCII string into the specified byte buffer. If the integer value is
     * shorted than the specified length, the number will be padded with leading zeros, so that it fills the required
     * length. If there is insufficient space in the buffer to write the value into, then the buffer size is increased
     * using the supplied byte buffer pool.
     *
     * @param  buffer The byte buffer to write to.
     * @param  value  The value to write.
     * @param  length The length to pad the number to with leading zeros.
     *
     * @return The byte buffer with the value written to it. This may be a different buffer to the one passed in if the
     *         buffer had to be copied in order to increase its size.
     */
    public static ByteBuffer putPaddedInt32AsString(ByteBuffer buffer, int value, int length)
    {
        // Ensure there is sufficient space in the buffer to hold the result.
        int charsRequired = BitHackUtils.getCharacterCountInt32(value);
        length = (charsRequired < length) ? length : charsRequired;

        // Take an explicit index into the buffer to start writing to, as the numbers will be written backwards.
        int index = buffer.position() + length - 1;

        // Record the start position, to remember if a minus sign was written or not, so that it does not get
        // overwritten by the zero padding.
        int start = buffer.position();

        // Advance the buffer position manually, as the characters will be written to specific indexes backwards.
        buffer.position(buffer.position() + length);

        // Take care of the minus sign for negative numbers.
        if (value < 0)
        {
            buffer.put(MINUS_ASCII);
            start++; // Stop padding code overwriting minus sign.
        }
        else
        {
            value = -value;
        }

        // Write the digits least significant to most significant into the buffer. As the number was converted to be
        // negative the remainders will be negative too.
        do
        {
            int remainder = value % 10;
            value = value / 10;

            buffer.put(index--, ((byte) (ZERO_ASCII - remainder)));
        }
        while (value != 0);

        // Write out the padding zeros.
        while (index >= start)
        {
            buffer.put(index--, ZERO_ASCII);
        }

        return buffer;
    }

    /**
     * Writes a single byte as raw characer data into the specified buffer. The buffer is increased in size using the
     * supplied buffer pool, if needed.
     *
     * @param  buffer The byte buffer to write to.
     * @param  value  The value to write.
     *
     * @return The byte buffer with the value written to it. This may be a different buffer to the one passed in if the
     *         buffer had to be copied in order to increase its size.
     */
    public static ByteBuffer putByteAsString(ByteBuffer buffer, byte value)
    {
        buffer.put(value);

        return buffer;
    }

    /**
     * Returns the contents of a buffer as a string, converting ASCII characters in the buffer, into unicode string
     * characters.
     *
     * @param  buffer The buffer.
     * @param  length The length of the string to get from the buffer.
     *
     * @return The buffer as a string.
     */
    public static String asString(ByteBuffer buffer, int length)
    {
        char[] chars = new char[length];

        for (int i = 0; i < length; i++)
        {
            chars[i] = (char) buffer.get(i);
        }

        return String.valueOf(chars);
    }
}
