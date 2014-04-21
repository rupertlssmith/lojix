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

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;

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
public class TextGridImpl extends PlainDocument implements TextGridModel
{
    /** {@inheritDoc} */
    public int getWidth()
    {
        return -1;
    }

    /** {@inheritDoc} */
    public int getHeight()
    {
        return -1;
    }

    /** {@inheritDoc} */
    public void insert(char character, int c, int r) throws BadLocationException
    {
        insertString(rowColumnToOffset(r, c), Character.toString(character), null);
    }

    /** {@inheritDoc} */
    public void insert(String string, int c, int r) throws BadLocationException
    {
        insertString(rowColumnToOffset(r, c), string, null);
    }

    /** {@inheritDoc} */
    public TextGridModel createInnerGrid(int c, int r, int w, int h)
    {
        return new NestedTextGridImpl(c, r, w, h, this);
    }

    /** {@inheritDoc} */
    public TextTableModel createTable(int c, int r, int w, int h)
    {
        return new TextTableImpl(c, r, w, h, this);
    }

    /**
     * Converts a row and column to an offset with the document model.
     *
     * @param  r The row position.
     * @param  c The column position.
     *
     * @return The corresponding offset within the document model.
     */
    protected int rowColumnToOffset(int r, int c)
    {
        return rowToOffset(r) + c;
    }

    /**
     * Converts a row number to an offset within the document.
     *
     * @param  r The row number to convert.
     *
     * @return The offset within the document corresponding to the start of the row.
     */
    private int rowToOffset(int r)
    {
        Element element = getDefaultRootElement().getElement(r);

        if (element != null)
        {
            element.getStartOffset();
        }

        return getStartPosition().getOffset();
    }
}
