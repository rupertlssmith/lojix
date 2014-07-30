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
package com.thesett.text.impl.model;

import com.thesett.text.api.model.TextGridModel;
import com.thesett.text.api.model.TextTableModel;

/**
 * Renders the contents of a {@link TextTableModel} into a {@link TextGridModel}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Render a text table into a grid model. </td><td> {@link TextTableModel}, {@link TextGridModel} </td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TextTableGridRenderer
{
    /** The table model to render. */
    private final TextTableModel tableModel;

    /** The grid to render into. */
    private final TextGridModel gridModel;

    /**
     * Creates a text table renderer.
     *
     * @param tableModel The table model to render.
     * @param gridModel  The grid to render into.
     */
    public TextTableGridRenderer(TextTableModel tableModel, TextGridModel gridModel)
    {
        this.tableModel = tableModel;
        this.gridModel = gridModel;
    }

    /** Renders the table. */
    public void renderTable()
    {
        for (int i = 0; i < tableModel.getRowCount(); i++)
        {
            int colOffset = 0;

            for (int j = 0; j < tableModel.getColumnCount(); j++)
            {
                // Print the contents of the table cell.
                String valueToPrint = tableModel.get(j, i);
                valueToPrint = (valueToPrint == null) ? "" : valueToPrint;
                gridModel.insert(valueToPrint, colOffset, i);

                // Pad spaces up to the column width if the contents are shorted.
                Integer maxColumnSize = tableModel.getMaxColumnSize(j);
                int spaces = maxColumnSize - valueToPrint.length();

                while (spaces > 0)
                {
                    gridModel.insert(" ", colOffset + valueToPrint.length() + spaces-- - 1, i);
                }

                // Shift to the next column.
                colOffset += maxColumnSize;
            }
        }
    }
}
