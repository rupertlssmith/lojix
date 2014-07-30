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

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LazyPagingList is a list implementation that fetches its elements in blocks as needed. It is intended to be used for
 * displaying lists of results in user interfaces where the results list is split into pages (or is displayed in a
 * window that can only display so many elements at once). Implementations must provide the {@link #getBlock} method
 * that gets blocks of elements for the list. This list caching works best when accessed sequentially, rather than
 * randomly.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class LazyPagingList<T> extends AbstractList<T> implements Serializable
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(LazyPagingList.class.getName()); */

    /** Holds the final size of the list. */
    private int size;

    /** Holds the caching block size for the list. */
    private int blockSize;

    /** Holds the cache of blocks. */
    Map<Integer, List<T>> blockMap = new HashMap<Integer, List<T>>();

    /**
     * Sublcasses must call this constructor with the lists size.
     *
     * @param size      The size of the list.
     * @param blockSize The size of data block chunk to use when fetching data.
     */
    public LazyPagingList(int size, int blockSize)
    {
        /*log.fine("public LazyPagingList(int size, int blockSize): called");*/
        /*log.fine("size = " + size);*/
        /*log.fine("blockSize = " + blockSize);*/

        // Keep the sizes.
        this.size = size;
        this.blockSize = blockSize;
    }

    /** No args constructor for serialization. */
    public LazyPagingList()
    {
    }

    /**
     * Fetches a block of elements from within the underlying list. If the last block of the underlying list is
     * requested and it does not completely fill the block that a partial list not containing the full number of
     * elements may be returned.
     *
     * @param  start  The start index to fetch from.
     * @param  number The number of elements to get.
     *
     * @return A list taken from the underlying list starting at, start, and containing up to, number, of elements.
     */
    public abstract List<T> getBlock(int start, int number);

    /**
     * Gets the size of the list.
     *
     * @return the size of the list.
     */
    public int size()
    {
        return size;
    }

    /**
     * Updates the size of the list.
     *
     * @param size The new size.
     */
    public void setSize(int size)
    {
        this.size = size;
    }

    /**
     * Gets the element from the list at the specified index. If the indexes block is not currently cached then a call
     * is made to the {@link #getBlock} method to fetch it.
     *
     * @param  index The offset into the list to retrieve.
     *
     * @return The element at the specified index.
     */
    public T get(int index)
    {
        /*log.fine("public T get(int index): called");*/
        /*log.fine("index = " + index);*/

        // Turn the absolute index into a block and offset.
        int block = index / blockSize;
        int offset = index % blockSize;

        /*log.fine("block = " + block);*/
        /*log.fine("offset = " + offset);*/

        // Check if the desired block is already cached.
        List<T> blockList = blockMap.get(block);

        // Fetch the block if it is not already cached and cache it.
        if (blockList == null)
        {
            blockList = cacheBlock(block);
        }

        // Get the element from the offset within the cached block.
        return blockList.get(offset);
    }

    /**
     * Fetches and caches the specified block.
     *
     * @param  block The block to cache.
     *
     * @return The contents of the cached block.
     */
    public List<T> cacheBlock(int block)
    {
        /*log.fine("public List<T> cacheBlock(int block): called");*/

        // Get the new block.
        List<T> blockList = getBlock(block * blockSize, blockSize);

        // Cache it.
        blockMap.put(block, blockList);
        /*log.fine("Cached block " + block + " with list of size " + blockList.size());*/

        return blockList;
    }

    /**
     * Renders as a string for debugging purposes.
     *
     * @return A string for debugging purposes.
     */
    public String toString()
    {
        String result = "size: " + size + ", blockSize: " + blockSize;

        // Render only the blocks that are currently filled in.
        List<Integer> blocks = new ArrayList<Integer>(blockMap.keySet());
        Collections.sort(blocks);

        for (int block : blocks)
        {
            List<T> list = blockMap.get(block);

            result += ", [from: " + (block * blockSize);
            result += ", to: " + ((block * blockSize) + list.size() - 1);
            result += ", " + list + "]";
        }

        return result;
    }
}
