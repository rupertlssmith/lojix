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
package com.thesett.common.io;

import java.io.Serializable;

/**
 * ByteBlock is a serializable array of bytes plus an integer count of the number of bytes which are legal data. Used to
 * pass around blocks of data.
 *
 * <p/>In particular this is used to represent the return results of calls to a {@link DistributedInputStream} but it
 * could have other uses too.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Hold an array of bytes.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ByteBlock implements Serializable
{
    /** The data block. */
    public byte[] data;

    /** The number of bytes in the data block. */
    public int count;

    /**
     * Creates a new ByteBlock object.
     *
     * @param data  The data block.
     * @param count The number of bytes in the data block.
     */
    public ByteBlock(byte[] data, int count)
    {
        this.data = data;
        this.count = count;
    }
}
