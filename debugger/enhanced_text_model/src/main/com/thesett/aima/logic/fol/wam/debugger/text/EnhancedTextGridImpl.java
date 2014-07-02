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

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;

import com.thesett.common.util.doublemaps.HashMapXY;
import com.thesett.text.api.TextGridEvent;
import com.thesett.text.api.TextTableEvent;
import com.thesett.text.api.TextTableListener;
import com.thesett.text.impl.model.TextGridImpl;
import com.thesett.text.impl.model.TextTableGridRenderer;

/**
 * EnhancedTextGridImpl provides an implementation of the enhanced text grid model.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Insert text attributes into grid cells. </td><td> {@link HashMapXY} </td></tr>
 * <tr><td> Insert text attributes into grid columns. </td></tr>
 * <tr><td> Insert text attributes into grid rows. </td></tr>
 * <tr><td> Provide cascaded text attributes for grid cells. </td><td> {@link HashMapXY} </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EnhancedTextGridImpl extends TextGridImpl implements EnhancedTextGrid
{
    /** The attributes arranged in a grid. */
    protected final AttributeGrid attributeGrid = new AttributeGridImpl();

    /** The horizontal and vertical separators. */
    protected final XYGridSeparators separators = new XYGridSeparatorsImpl();

    /** Holds selection listeners, that will receive mouse events translated to table coordinates. */
    private final Set<TextGridSelectionListener> textGridSelectionListeners = new HashSet<TextGridSelectionListener>();

    /** {@inheritDoc} */
    public void insertAttribute(AttributeSet attributes, int c, int r)
    {
        attributeGrid.insertAttribute(attributes, c, r);
        updateListeners();
    }

    /** {@inheritDoc} */
    public void insertColumnAttribute(AttributeSet attributes, int c)
    {
        attributeGrid.insertColumnAttribute(attributes, c);
        updateListeners();
    }

    /** {@inheritDoc} */
    public void insertRowAttribute(AttributeSet attributes, int r)
    {
        attributeGrid.insertRowAttribute(attributes, r);
        updateListeners();
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

    /** {@inheritDoc} */
    public void select(int row, int col)
    {
        for (TextGridSelectionListener listener : textGridSelectionListeners)
        {
            listener.select(new TextGridEvent(this, row, col));
        }
    }

    /** {@inheritDoc} */
    public void addTextGridSelectionListener(TextGridSelectionListener listener)
    {
        textGridSelectionListeners.add(listener);
    }

    /** {@inheritDoc} */
    public void removeTextGridSelectionListener(TextGridSelectionListener listener)
    {
        textGridSelectionListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Override the table creation to supply an enhanced text table.
     */
    public EnhancedTextTable createTable(int c, int r, int w, int h)
    {
        // Supply a text table, with this grid set up to listen for updates to the table, and to be re-rendered as the
        // table changes.
        EnhancedTextTable textTable = new EnhancedTextTableImpl();

        textTable.addTextTableListener(new EnhancedTableListener());

        return textTable;
    }

    /**
     * Re-renders a table into this grid, when the table changes.
     */
    private class EnhancedTableListener implements TextTableListener
    {
        /** {@inheritDoc} */
        public void changedUpdate(TextTableEvent event)
        {
            TextTableGridRenderer renderer =
                new EnhancedTextTableGridRenderer((EnhancedTextTable) event.getModel(), EnhancedTextGridImpl.this);
            renderer.renderTable();
        }
    }
}
