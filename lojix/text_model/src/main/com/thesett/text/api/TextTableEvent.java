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
package com.thesett.text.api;

import com.thesett.text.api.model.TextTableModel;

/**
 * TextGridEvent is an event that describes a change to a text table model.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide the changed model.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TextTableEvent
{
    /** The changed model. */
    private final TextTableModel model;

    /** The table row changed. <tt>-1</tt> means not relevant to this event. */
    private final int rowChanged;

    /** The table column changed. <tt>-1</tt> means not relevant to this event. */
    private final int columnChanged;

    /** <tt>true</tt> iff this change is to attributes only. */
    private final boolean attributeChangeOnly;

    /**
     * Creates a text Table update event.
     *
     * @param model The changed model.
     */
    public TextTableEvent(TextTableModel model)
    {
        this.model = model;
        rowChanged = -1;
        columnChanged = -1;
        attributeChangeOnly = false;
    }

    /**
     * Creates a text Table update event.
     *
     * @param model         The changed model.
     * @param rowChanged    The row changed.
     * @param columnChanged The column changed.
     */
    public TextTableEvent(TextTableModel model, int rowChanged, int columnChanged)
    {
        this.model = model;
        this.rowChanged = rowChanged;
        this.columnChanged = columnChanged;
        attributeChangeOnly = false;
    }

    /**
     * Creates a text Table update event for attribute changes.
     *
     * @param model               The changed model.
     * @param rowChanged          The row changed.
     * @param columnChanged       The column changed.
     * @param attributeChangeOnly <tt>true</tt> iff this change is to attributes only.
     */
    public TextTableEvent(TextTableModel model, int rowChanged, int columnChanged, boolean attributeChangeOnly)
    {
        this.model = model;
        this.rowChanged = rowChanged;
        this.columnChanged = columnChanged;
        this.attributeChangeOnly = attributeChangeOnly;
    }

    /**
     * Provides the changed model.
     *
     * @return The changed model.
     */
    public TextTableModel getModel()
    {
        return model;
    }

    /**
     * Provides the row updated.
     *
     * @return The table row changed. <tt>-1</tt> means not relevant to this event.
     */
    public int getRowChanged()
    {
        return rowChanged;
    }

    /**
     * Provides the column updated.
     *
     * @return The table column changed. <tt>-1</tt> means not relevant to this event.
     */
    public int getColumnChanged()
    {
        return columnChanged;
    }

    /**
     * Indicates that this update is an attribute change only.
     *
     * @return <tt>true</tt> iff this change is to attributes only.
     */
    public boolean isAttributeChangeOnly()
    {
        return attributeChangeOnly;
    }
}
