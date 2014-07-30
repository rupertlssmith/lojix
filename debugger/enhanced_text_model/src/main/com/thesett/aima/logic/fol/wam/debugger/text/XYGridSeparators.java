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
package com.thesett.aima.logic.fol.wam.debugger.text;

import java.util.SortedMap;

/**
 * XYGridSeparators describes the position of horizontal and vertical separators within a grid.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Allow horizontal and vertical separators to be inserted into a grid. </td></tr>
 * <tr><td> Provide horizontal and vertical separator positions within the grid. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface XYGridSeparators
{
    /**
     * Inserts a horizontal separator into the grid, with the given height.
     *
     * @param r           The row to insert the separator before.
     * @param pixelHeight The height in pixels.
     */
    void insertHorizontalSeparator(int r, int pixelHeight);

    /**
     * Inserts a vertical separator into the grid, with the given width.
     *
     * @param c          The column to insert the separator before.
     * @param pixelWidth The width in pixels.
     */
    void insertVerticalSeparator(int c, int pixelWidth);

    /**
     * Provides a sorted map of the positions and sizes of the horizontal separators in the grid. This will also include
     * any separators inserted by child grids or tables.
     *
     * @return A listing of the horizontal separators by increasing order of position.
     */
    SortedMap<Integer, Integer> getHorizontalSeparators();

    /**
     * Provides a sorted map of the positions and sizes of the vertical separators in the grid. This will also include
     * any separators inserted by child grids or tables.
     *
     * @return A listing of the vertical separators by increasing order of position.
     */
    SortedMap<Integer, Integer> getVerticalSeparators();
}
