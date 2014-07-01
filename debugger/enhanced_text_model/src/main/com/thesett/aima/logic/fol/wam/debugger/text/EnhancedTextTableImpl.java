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

import java.util.SortedMap;

import javax.swing.text.AttributeSet;

import com.thesett.text.api.TextTableEvent;
import com.thesett.text.api.TextTableListener;
import com.thesett.text.impl.model.TextTableImpl;

/**
 * EnhancedTextTableImpl provides an implementation of the {@link EnhancedTextTable} model.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Insert text attributes into table cells. </td></tr>
 * <tr><td> Insert text attributes into table columns. </td></tr>
 * <tr><td> Insert text attributes into table rows. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EnhancedTextTableImpl extends TextTableImpl implements EnhancedTextTable
{
    /** The attributes arranged in a grid. */
    protected AttributeGrid attributeGrid = new AttributeGridImpl();

    /** The horizontal and vertical separators. */
    protected XYGridSeparators separators = new XYGridSeparatorsImpl();

    /** {@inheritDoc} */
    public void insertAttribute(AttributeSet attributes, int c, int r)
    {
        attributeGrid.insertAttribute(attributes, c, r);
        updateListenersOnAttributeChange(c, r);
    }

    /** {@inheritDoc} */
    public void insertColumnAttribute(AttributeSet attributes, int c)
    {
        attributeGrid.insertColumnAttribute(attributes, c);
        updateListenersOnAttributeChange(c, -1);
    }

    /** {@inheritDoc} */
    public void insertRowAttribute(AttributeSet attributes, int r)
    {
        attributeGrid.insertRowAttribute(attributes, r);
        updateListenersOnAttributeChange(-1, r);
    }

    /** {@inheritDoc} */
    public AttributeSet getAttributeAt(int c, int r)
    {
        return attributeGrid.getAttributeAt(c, r);
    }

    /** {@inheritDoc} */
    public void insertHorizontalSeparator(int r, int pixelHeight)
    {
        separators.insertHorizontalSeparator(r, pixelHeight);
    }

    /** {@inheritDoc} */
    public void insertVerticalSeparator(int c, int pixelWidth)
    {
        separators.insertVerticalSeparator(c, pixelWidth);
    }

    /** {@inheritDoc} */
    public SortedMap<Integer, Integer> getHorizontalSeparators()
    {
        return separators.getHorizontalSeparators();
    }

    /** {@inheritDoc} */
    public SortedMap<Integer, Integer> getVerticalSeparators()
    {
        return separators.getVerticalSeparators();
    }

    /**
     * Notifies all interested listeners of an update to this model.
     *
     * @param col The column that changed, or <tt>-1</tt> if change not applicable to columns.
     * @param row The row that changed, or <tt>-1</tt> if change not applicable to rows.
     */
    protected void updateListenersOnAttributeChange(int col, int row)
    {
        TextTableEvent event = new TextTableEvent(this, row, col, true);

        for (TextTableListener listener : listeners)
        {
            listener.changedUpdate(event);
        }
    }
}
