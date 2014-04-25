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
package com.thesett.text.impl.model;

import com.thesett.text.api.model.TextGridModel;
import com.thesett.text.api.model.TextTableModel;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class NestedTextGridImpl implements TextGridModel
{
    /** Start column of this child grid. */
    protected final int column;

    /** Start row of this child grid. */
    protected final int row;

    /** Width of this child grid. */
    protected final int width;

    /** Height of this child grid. */
    protected final int height;

    /** The parent grid. */
    private final TextGridModel parent;

    public NestedTextGridImpl(int column, int row, int width, int height, TextGridModel parent)
    {
        this.column = column;
        this.row = row;
        this.width = width;
        this.height = height;

        this.parent = parent;
    }

    /** {@inheritDoc} */
    public int getWidth()
    {
        return width;
    }

    /** {@inheritDoc} */
    public int getHeight()
    {
        return height;
    }

    /** {@inheritDoc} */
    public void insert(char character, int c, int r)
    {
        parent.insert(character, c + column, r + row);
    }

    /** {@inheritDoc} */
    public void insert(String string, int c, int r)
    {
        parent.insert(string, c + column, r + row);
    }

    /** {@inheritDoc} */
    public char getCharAt(int c, int r)
    {
        return parent.getCharAt(c + column, r + row);
    }

    /** {@inheritDoc} */
    public TextGridModel createInnerGrid(int c, int r, int w, int h)
    {
        return parent.createInnerGrid(c + column, r + row, w, h);
    }

    /** {@inheritDoc} */
    public TextTableModel createTable(int c, int r, int w, int h)
    {
        return parent.createTable(c + column, r + row, w, h);
    }
}
