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

import javax.swing.text.AttributeSet;

import com.thesett.text.api.model.TextGridModel;
import com.thesett.text.api.model.TextTableModel;
import com.thesett.text.impl.model.TextTableGridRenderer;

/**
 * Renders the contents of a {@link EnhancedTextTable} into a {@link EnhancedTextGrid}. In addition to rendering the
 * contents of the table cells, this renderer also renders the attributes of the table cells into the grid.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Render a text table into a grid model. </td><td> {@link TextTableModel}, {@link TextGridModel} </td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class EnhancedTextTableGridRenderer extends TextTableGridRenderer
{
    /** The table model to render. */
    private final EnhancedTextTable tableModel;

    /** The grid to render into. */
    private final EnhancedTextGrid gridModel;

    /**
     * Creates an enhanced text table renderer.
     *
     * @param table The table model to render.
     * @param grid  The grid to render into.
     */
    public EnhancedTextTableGridRenderer(EnhancedTextTable table, EnhancedTextGridImpl grid)
    {
        super(table, grid);

        this.tableModel = table;
        this.gridModel = grid;
    }

    /** Renders the table. */
    public void renderTable()
    {
        for (int i = 0; i < tableModel.getRowCount(); i++)
        {
            int colOffset = 0;

            for (int j = 0; j < tableModel.getColumnCount(); j++)
            {
                String valueToPrint = tableModel.get(j, i);
                valueToPrint = (valueToPrint == null) ? "" : valueToPrint;
                gridModel.insert(valueToPrint, colOffset, i);

                AttributeSet attributes = tableModel.getAttributeAt(j, i);

                if (attributes != null)
                {
                    gridModel.insertAttribute(attributes, colOffset, i);
                }

                Integer maxColumnSize = tableModel.getMaxColumnSize(j);
                colOffset += maxColumnSize;
            }
        }
    }
}
