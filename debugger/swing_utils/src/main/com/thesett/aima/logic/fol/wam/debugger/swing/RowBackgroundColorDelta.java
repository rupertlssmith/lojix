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
package com.thesett.aima.logic.fol.wam.debugger.swing;

import java.awt.Color;

import com.thesett.aima.logic.fol.wam.debugger.text.AttributeGrid;
import com.thesett.aima.logic.fol.wam.debugger.text.AttributeSet;

/**
 * Implements a color delta that modifies the background color of a row of a text grid.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Apply a background color change to a row of a text grid. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class RowBackgroundColorDelta implements ColorDelta
{
    /** The row to make color changes to. */
    private final int row;

    /** The text grid model to alter the background color of. */
    private final AttributeGrid grid;

    /**
     * Creates a color delta for a table row.
     *
     * @param row  The row to make color changes to.
     * @param grid The text grid model to alter the background color of.
     */
    public RowBackgroundColorDelta(int row, AttributeGrid grid)
    {
        this.row = row;
        this.grid = grid;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Applies color changes to the background of a row.
     */
    public void changeColor(Color color)
    {
        AttributeSet aset = new AttributeSet();
        aset.put(AttributeSet.BACKGROUND_COLOR, color);
        grid.insertRowAttribute(aset, row);
    }
}
