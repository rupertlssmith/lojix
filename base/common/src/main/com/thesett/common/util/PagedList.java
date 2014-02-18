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
package com.thesett.common.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * PagedList is a {@link java.util.List} that is built from another list that breaks the orignal list into a list of
 * pages or sub-lists from the original. It also provides a current page index which is useful for keeping track of a
 * current page.
 *
 * <p/>There is also a secondary index which can be used simply as a marker. It is very useful when breaking down a list
 * of items to display into pages of X elements and then displaying a page control that lets a user select from Y to Y +
 * M pages. The secondary index can be used to hold Y to remember which page block the user is currently on. For example
 * a web search may return 1000 hits broken into pages of twenty. A page control will let the user select which page of
 * twenty hits from one to ten to view. A more button will let the user chose from pages 11 to 20 and so on. In this
 * case the secondary index would hold the start of the current block, for example, 10 and the primary index would hold
 * the currently viewed page, for example, 15.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Build from list.
 * <tr><td>Build from collection.
 * <tr><td>Extract indexed page.
 * <tr><td>Calculate size.
 * <tr><td>Manage current page index.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class PagedList<E> extends AbstractList<List<E>>
{
    /** Used for logging. */
    /* private static final Logger log = Logger.getLogger(PagedList.class.getName()); */

    /** Used to hold a reference to the list to be broken into pages. */
    private List<E> original;

    /** Holds the page size. */
    private int pageSize;

    /** Holds the current page. */
    private int currentPage;

    /** Holds a secondary page index, useful for grouping pages. */
    private int currentIndex;

    /**
     * Builds a paged list from a list. The list is split into pages of the specified size. The current page is
     * initialized to zero.
     *
     * @param list     the list to split into sub-lists
     * @param pageSize the size of the sub-lists
     */
    public PagedList(List<E> list, int pageSize)
    {
        this.original = list;
        this.pageSize = pageSize;
        currentPage = 0;
    }

    /**
     * Builds a paged list from a {@link java.util.Collection}. The collection is split into pages of the specified
     * size. The current page is initialized to zero.
     *
     * @param collection the collection to split into sub-lists
     * @param pageSize   the size of the sub-lists
     */
    public PagedList(Collection<E> collection, int pageSize)
    {
        this.original = new ArrayList<E>(collection);
        this.pageSize = pageSize;
        currentPage = 0;
    }

    /**
     * Extracts the sub-list at the specified page index. The returned list will have size equal to the page size unless
     * it is the last page in which case it may not be a full page.
     *
     * @param  index The sub-list index to extract.
     *
     * @return The sub-list as a {@link java.util.List}.
     *
     * @throws IndexOutOfBoundsException If the index is < 0 or larger that the number of pages (list size / page size +
     *                                   (list size % page size == 0 ? 0 : 1)).
     */
    public List<E> get(int index)
    {
        /*log.fine("public List<E> get(int index): called");*/

        // Check that the index is not to large.
        int originalSize = original.size();
        int size = (originalSize / pageSize) + (((originalSize % pageSize) == 0) ? 0 : 1);

        /*log.fine("originalSize = " + originalSize);*/
        /*log.fine("size = " + size);*/

        // Check if the size of the underlying list is zero, in which case return an empty list, so long as page zero
        // was requested.
        if ((index == 0) && (originalSize == 0))
        {
            return new ArrayList<E>();
        }

        // Check if the requested index exceeds the number of pages, or is an illegal negative value.
        if ((index >= size) || (index < 0))
        {
            /*log.fine("(index >= size) || (index < 0), throwing out of bounds exception.");*/
            throw new IndexOutOfBoundsException("Index " + index +
                " is less than zero or more than the number of pages: " + size);
        }

        // Extract the appropriate sub-list.
        // Note that if this is the last page it may not be a full page. Just up to the last page will be returned.
        /*log.fine("Requesting sublist from, " + (pageSize * index) + ", to ," +
            (((pageSize * (index + 1)) >= originalSize) ? originalSize : (pageSize * (index + 1))) + ".");*/

        List<E> result =
            original.subList(pageSize * index,
                ((pageSize * (index + 1)) >= originalSize) ? originalSize : (pageSize * (index + 1)));

        return result;
    }

    /**
     * Gets the number of pages.
     *
     * @return The number of pages.
     */
    public int size()
    {
        int size = original.size();

        return (size / pageSize) + (((size % pageSize) == 0) ? 0 : 1);
    }

    /**
     * Gets the index of the current page.
     *
     * @return The index of the current page.
     */
    public int getCurrentPage()
    {
        return currentPage;
    }

    /**
     * Sets the index of the current page. The {@link #getCurrent} method will return this page.
     *
     * @param page The current page to set.
     */
    public void setCurrentPage(int page)
    {
        this.currentPage = page;
    }

    /**
     * Gets the secondary index.
     *
     * @return The secondary index.
     */
    public int getCurrentIndex()
    {
        return currentIndex;
    }

    /**
     * Sets the secondary index.
     *
     * @param index The secondary index.
     */
    public void setCurrentIndex(int index)
    {
        this.currentIndex = index;
    }

    /**
     * Gets the current page as a list.
     *
     * @return The list at the current page.
     */
    public List<E> getCurrent()
    {
        return get(currentPage);
    }

    /**
     * Renders as a string for debugging purposes.
     *
     * @return A string for debugging purposes.
     */
    public String toString()
    {
        return "currentPage: " + currentPage + ", currentIndex: " + currentIndex + ", pageSize: " + pageSize +
            ", original: [" + original + "]";
    }
}
