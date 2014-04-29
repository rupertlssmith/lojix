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
package com.thesett.aima.logic.fol.wam.debugger.text;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet;

import com.thesett.common.util.doublemaps.HashMapXY;
import com.thesett.text.impl.model.TextGridImpl;

/**
 * EnhancedTextGridImpl provides an implementation of the enhanced text grid model.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Insert text attributes into the grid. </td><td> {@link HashMapXY} </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EnhancedTextGridImpl extends TextGridImpl implements EnhancedTextGrid
{
    /** Holds the grid data. */
    HashMapXY<AttributeSet> gridAttributes = new HashMapXY<AttributeSet>(100);

    /** Holds the per column attributes. */
    List<AttributeSet> columnAttributes = new ArrayList<AttributeSet>();

    /** Holds the per line attributes. */
    List<AttributeSet> rowAttributes = new ArrayList<AttributeSet>();

    /** {@inheritDoc} */
    public void insertAttribute(AttributeSet attributes, int c, int r)
    {
        internalInsert(attributes, c, r);
        updateListeners();
    }

    /** {@inheritDoc} */
    public void insertColumnAttribute(AttributeSet attributes, int c)
    {
        setColumnAttributeWithPadding(attributes, c);
        updateListeners();
    }

    /** {@inheritDoc} */
    public void insertRowAttribute(AttributeSet attributes, int r)
    {
        setRowAttributeWithPadding(attributes, r);
        updateListeners();
    }

    /** {@inheritDoc} */
    public AttributeSet getAttributeAt(int c, int r)
    {
        AttributeSet attributeSet = gridAttributes.get((long) c, (long) r);
        attributeSet = (attributeSet == null) ? getColumnAttributeOrNull(c) : attributeSet;
        attributeSet = (attributeSet == null) ? getRowAttributeOrNull(r) : attributeSet;

        return attributeSet;
    }

    /**
     * Sets a column attribute, adding padding to the underlying array as necessary to ensure it is large enough to hold
     * the attribute at the requested position.
     *
     * @param attributes The attribute to set.
     * @param c          The column to set it on.
     */
    private void setColumnAttributeWithPadding(AttributeSet attributes, int c)
    {
        if (c >= columnAttributes.size())
        {
            for (int i = columnAttributes.size(); i <= c; i++)
            {
                columnAttributes.add(null);
            }
        }

        columnAttributes.set(c, attributes);
    }

    /**
     * Sets a row attribute, adding padding to the underlying array as necessary to ensure it is large enough to hold
     * the attribute at the requested position.
     *
     * @param attributes The attribute to set.
     * @param r          The row to set it on.
     */
    private void setRowAttributeWithPadding(AttributeSet attributes, int r)
    {
        if (r >= rowAttributes.size())
        {
            for (int i = rowAttributes.size(); i <= r; i++)
            {
                rowAttributes.add(null);
            }
        }

        rowAttributes.set(r, attributes);
    }

    /**
     * Gets a columns attribute if possible, without overflowing the underlying array.
     *
     * @param  c The column to get the attributes for.
     *
     * @return The column attributes or <tt>null</tt> if none are set.
     */
    private AttributeSet getColumnAttributeOrNull(int c)
    {
        if ((c >= 0) && (c < columnAttributes.size()))
        {
            return columnAttributes.get(c);
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets a rows attribute if possible, without overflowing the underlying array.
     *
     * @param  r The row to get the attributes for.
     *
     * @return The row attributes or <tt>null</tt> if none are set.
     */
    private AttributeSet getRowAttributeOrNull(int r)
    {
        if ((r >= 0) && (r < rowAttributes.size()))
        {
            return rowAttributes.get(r);
        }
        else
        {
            return null;
        }
    }

    /**
     * Inserts a set of attributes into the grid at the specified location. This is a private insert method, that does
     * not notify model listeners, so that the public insert methods can do that as a separate step.
     *
     * @param attributes The attribute set to insert.
     * @param c          The column position.
     * @param r          The row position.
     */
    private void internalInsert(AttributeSet attributes, int c, int r)
    {
        width = (c > width) ? c : width;
        height = (r > height) ? r : height;

        gridAttributes.put((long) c, (long) r, attributes);
    }
}
