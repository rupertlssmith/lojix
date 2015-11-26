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

import java.nio.ByteBuffer;

import com.thesett.common.error.NotImplementedException;

/**
 * ASCIIByteBufferString is an {@link ASCIIString} that holds its data in a byte buffer. It essentially provides a view
 * onto a buffer of data, through which string operations may be applied, without having to copy the data out of the
 * buffer into an intermediate buffer or array.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Represent an ASCII string as a view onto a ByteBuffer.
 * <tr><td>Provide a view onto an ASCII string as a character sequence.
 * <tr><td>Allow ASCII strings to be compared for ordering purposes.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public final class ASCIIByteBufferString implements ASCIIString
{
    /** Holds the byte data that makes up the string. */
    private final ByteBuffer data;

    /** Holds the offset into the buffer of the start of the string. */
    private final int offset;

    /** Used to cache the hash code of the string. */
    private int hashCode;

    /** Used to cache the value of the string as a Java String. */
    private String stringValue;

    /** Holds the length of the string. */
    private final int length;

    /**
     * Creates an ASCII string from the specified byte buffer, taking the required number of byte to make up the length
     * of the string required.
     *
     * @param data   The underlying buffer holding the string data.
     * @param length The length of the string.
     */
    public ASCIIByteBufferString(ByteBuffer data, int length)
    {
        this.data = data;
        this.length = length;
        offset = 0;

        computeHashCode();
    }

    /**
     * Creates an ASCII string from the specified byte buffer, taking the required number of byte to make up the length
     * of the string required.
     *
     * @param data   The underlying buffer holding the string data.
     * @param start  The starting offset within the buffer of the string.
     * @param length The length of the string.
     */
    public ASCIIByteBufferString(ByteBuffer data, int start, int length)
    {
        this.data = data;
        this.length = length;
        offset = start;

        computeHashCode();
    }

    /** {@inheritDoc} */
    public int length()
    {
        return length;
    }

    /** {@inheritDoc} */
    public char charAt(int index)
    {
        return (char) data.get(offset + index);
    }

    /** {@inheritDoc} */
    public CharSequence subSequence(int start, int end)
    {
        return new ASCIIByteBufferString(data, offset + start, end - start);
    }

    /** {@inheritDoc} */
    public byte[] getBytes()
    {
        byte[] dst = new byte[length];
        data.get(dst, offset, length);

        return dst;
    }

    /** {@inheritDoc} */
    public byte get(int index)
    {
        return data.get(index + offset);
    }

    /** {@inheritDoc} */
    public int indexOf(ASCIIString str, int fromIndex)
    {
        throw new NotImplementedException();
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Two ASCII strings are equal, if they have the same length, and consist of exactly the same sequence of bytes.
     */
    public boolean equals(Object o)
    {
        if (!(o instanceof ASCIIString))
        {
            return false;
        }

        ASCIIString comparator = (ASCIIString) o;

        if (length != comparator.length())
        {
            return false;
        }

        for (int i = 0; i < length; i++)
        {
            byte b1 = get(i);
            byte b2 = comparator.get(i);

            if (b1 != b2)
            {
                return false;
            }
        }

        return true;
    }

    /** {@inheritDoc} */
    public int hashCode()
    {
        return hashCode;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Two ASCII strings are compared using a lexicographical ordering.
     */
    public int compareTo(ASCIIString comparator)
    {
        int n = Math.min(length, comparator.length());

        for (int i = 0; i < n; i++)
        {
            byte b1 = get(i);
            byte b2 = comparator.get(i);

            if (b1 == b2)
            {
                continue;
            }

            if (b1 < b2)
            {
                return -1;
            }

            return 1;
        }

        return length - comparator.length();
    }

    /** {@inheritDoc} */
    public String toString()
    {
        if (stringValue == null)
        {
            computeStringValue();
        }

        return stringValue;
    }

    /**
     * Computes and caches in the {@link #stringValue} field, a representation of this string as a default Java String.
     */
    private void computeStringValue()
    {
        char[] chars = new char[length];

        for (int i = 0; i < length; i++)
        {
            chars[i] = (char) data.get(i + offset);
        }

        stringValue = new String(chars);
    }

    /** Computes and caches in the {@link #hashCode} field, a hash code for this string. */
    private void computeHashCode()
    {
        hashCode = 0;

        for (int i = 0; i < length; i++)
        {
            hashCode = (31 * hashCode) + data.get(i + offset);
        }
    }
}
