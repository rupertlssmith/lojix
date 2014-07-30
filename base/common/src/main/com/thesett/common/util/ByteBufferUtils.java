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

/**
 * ByteBufferUtils provides some helper methods for encoding and decoding values in a byte array.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Read/Write int in byte array.
 * <tr><td>Read/Write 24-bit int in byte array.
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   These encodings are all little endian. May need to write big endian versions too. A decision on standardizing
 *         the endian-ness of compiled programs needs to be taken. Little endian best for intel CPUs, big endian is the
 *         standard that Java uses.
 */
public class ByteBufferUtils
{
    /**
     * Extracts an int from an array of bytes.
     *
     * @param  buf    The array of bytes to extract the int from.
     * @param  offset The offset of the start of the int in the byte array.
     *
     * @return An integer extracted from the 4 bytes of data.
     */
    public static int getIntFromBytes(byte[] buf, int offset)
    {
        int result = 0;

        result += buf[offset++] & 0xFF;
        result += ((buf[offset++] & 0xFF) << 8);
        result += ((buf[offset++] & 0xFF) << 16);
        result += ((buf[offset]) << 24);

        return result;
    }

    /**
     * Outputs an int into a byte array.
     *
     * @param buf    The byte array to write to.
     * @param offset The offset within the byte array to output the int.
     * @param value  The int value to write into the array.
     */
    public static void writeIntToByteArray(byte[] buf, int offset, int value)
    {
        buf[offset++] = (byte) (value & 0x000000ff);
        buf[offset++] = (byte) ((value & 0x0000ff00) >> 8);
        buf[offset++] = (byte) ((value & 0x00ff0000) >> 16);
        buf[offset] = (byte) ((value & 0xff000000) >> 24);
    }

    /**
     * Outputs an int into a byte array, copying only the bottom 24 bits of the integer. The top, sign bit, is lost by
     * this operation, so this only works on positive ints below 2^24.
     *
     * @param buf    The byte array to write to.
     * @param offset The offset within the byte array to output the int.
     * @param value  The int value to write into the array.
     */
    public static void write24BitIntToByteArray(byte[] buf, int offset, int value)
    {
        buf[offset++] = (byte) (value & 0x000000ff);
        buf[offset++] = (byte) ((value & 0x0000ff00) >> 8);
        buf[offset] = (byte) ((value & 0x00ff0000) >> 16);
    }

    /**
     * Extracts an int from an array of bytes. Only three bytes are pulled together from the array to make a 24 bit
     * integer, albeit returned as a java 32 bit int.
     *
     * @param  buf    The array of bytes to extract the int from.
     * @param  offset The offset of the start of the int in the byte array.
     *
     * @return An integer extracted from the 4 bytes of data.
     */
    public static int get24BitIntFromBytes(byte[] buf, int offset)
    {
        int i = 0;

        offset++;
        i += buf[offset++] & 0xFF;
        i += ((buf[offset++] & 0xFF) << 8);
        i += ((buf[offset] & 0xFF) << 16);

        return i;
    }

    /**
     * Extracts a short from an array of bytes.
     *
     * @param  buf    The array of bytes to extract the short from.
     * @param  offset The offset of the start of the short in the byte array.
     *
     * @return A short extracted from the 2 bytes of data.
     */
    public static short getShortFromBytes(byte[] buf, int offset)
    {
        short result = 0;

        result += buf[offset++] & 0xFF;
        result += ((buf[offset]) << 8);

        return result;
    }

    /**
     * Outputs a short into a byte array.
     *
     * @param buf    The byte array to write to.
     * @param offset The offset within the byte array to output the short.
     * @param value  The short value to write into the array.
     */
    public static void writeShortToByteArray(byte[] buf, int offset, short value)
    {
        buf[offset++] = (byte) (value & 0x000000ff);
        buf[offset] = (byte) ((value & 0x0000ff00) >> 8);
    }
}
