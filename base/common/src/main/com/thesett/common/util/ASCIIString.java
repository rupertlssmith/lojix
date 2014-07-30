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
 * ASCIIString provides string operations over strings that are sequences of bytes. The default Java string class deals
 * with 16-bit characters and can be used to work with more advanced Unicode character sets. ASCII is encoded as 8-bit
 * characters, which is more limiting, and particularly not usefull for i18n. Some applications, however can take
 * advantage of the speed and simplicity of dealing with ASCII over Unicode.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Provide a view onto an ASCII string as a character sequence.
 * <tr><td>Allow ASCII strings to be compared for ordering purposes.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface ASCIIString extends CharSequence, Comparable<ASCIIString>
{
    /**
     * Gets the data that makes up the string as an array of bytes.
     *
     * @return The data that makes up the string as an array of bytes.
     */
    public byte[] getBytes();

    /**
     * Gets the byte of data within the string, at the specified index.
     *
     * @param  index The index within the string to get a byte of data for.
     *
     * @return The byte of data within the string, at the specified index.
     */
    public byte get(int index);

    /**
     * Calculates the index within this string of the first occurrence of the specified substring, starting at the
     * specified index.
     *
     * @param  str       The string to search for.
     * @param  fromIndex The offset to start searching from.
     *
     * @return The index of the substring, or -1 if no occurences of the substring can be found.
     */
    int indexOf(ASCIIString str, int fromIndex);
}
