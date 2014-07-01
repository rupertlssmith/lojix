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
package com.thesett.text.api;

import com.thesett.text.api.model.TextGridModel;

/**
 * TextGridEvent is an event that describes a change to a text grid model.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide the changed model.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TextGridEvent
{
    /** The changed model. */
    private final TextGridModel model;

    /** The row affected by this event, or <tt>-1</tt> if not applicable. */
    private final int row;

    /** The column affected by this event, or <tt>-1</tt> if not applicable. */
    private final int column;

    /**
     * Creates a text grid update event.
     *
     * @param model The changed model.
     */
    public TextGridEvent(TextGridModel model)
    {
        this.model = model;
        row = -1;
        column = -1;
    }

    /**
     * Creates a text grid update event.
     *
     * @param model  The changed model.
     * @param row    The affected row.
     * @param column The affected column.
     */
    public TextGridEvent(TextGridModel model, int row, int column)
    {
        this.model = model;
        this.row = row;
        this.column = column;
    }

    /**
     * Provides the row affected by this event.
     *
     * @return The row affected by this event, or <tt>-1</tt> if not applicable.
     */
    public int getRow()
    {
        return row;
    }

    /**
     * Provides the column affected by this event.
     *
     * @return The column affected by this event, or <tt>-1</tt> if not applicable.
     */
    public int getColumn()
    {
        return column;
    }

    /**
     * Provides the changed model.
     *
     * @return The changed model.
     */
    TextGridModel getModel()
    {
        return model;
    }
}
