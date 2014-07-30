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
package com.thesett.text.api.model;

import com.thesett.text.api.TextGridListener;

/**
 * TextGridModel defines a model describing a buffer of text that is addressable as a 2d grid, by row and column
 * positions within the text. This is mainly intended for use with monospaced fonts, so that the text forms a regular 2d
 * grid.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide the extent of the grid. </td></tr>
 * <tr><td> Insert text into the grid. </td></tr>
 * <tr><td> Create tables within the grid. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TextGridModel
{
    /**
     * Provides the width of the grid if defined.
     *
     * @return The width of the grid, or <tt>-1</tt> if no extent has been defined and the grid is infinite.
     */
    int getWidth();

    /**
     * Provides the height of the grid if defined.
     *
     * @return The height of the grid, or <tt>-1</tt> if no extent has been defined and the grid is infinite.
     */
    int getHeight();

    /**
     * Inserts a single character into the grid at the specified location.
     *
     * @param character The character to insert.
     * @param c         The column position.
     * @param r         The row position.
     */
    void insert(char character, int c, int r);

    /**
     * Inserts a string into the grid starting at the specified location.
     *
     * @param string The string to insert.
     * @param c      The column position.
     * @param r      The row position.
     */
    void insert(String string, int c, int r);

    /**
     * Reads the character from the grid at the specified location.
     *
     * @param  c The column position.
     * @param  r The row position.
     *
     * @return The character at the specified location.
     */
    char getCharAt(int c, int r);

    /**
     * Creates a child grid within this grid.
     *
     * @param  c The column start position of the table.
     * @param  r The row start position of the table.
     * @param  w The maximum width of the table.
     * @param  h The maximum height of the table.
     *
     * @return A child grid within this grid.
     */
    TextGridModel createInnerGrid(int c, int r, int w, int h);

    /**
     * Creates a child grid within this grid, that is addressable as a table.
     *
     * @param  c The column start position of the table.
     * @param  r The row start position of the table.
     * @param  w The maximum width of the table.
     * @param  h The maximum height of the table.
     *
     * @return A table within this grid.
     */
    TextTableModel createTable(int c, int r, int w, int h);

    /**
     * Adds a listener for updates to this model.
     *
     * @param listener The listener for updates to this model.
     */
    void addTextGridListener(TextGridListener listener);

    /**
     * Removes a listener for updates to this model.
     *
     * @param listener The listener to remove.
     */
    void removeTextGridListener(TextGridListener listener);
}
