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
package com.thesett.aima.logic.fol.wam.debugger.monitor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.thesett.common.util.doublemaps.DoubleKeyedMap;
import com.thesett.text.api.model.TextTableModel;

/**
 * MemoryLayoutMonitor watches the memory layout registers of the machine, and updates a table model that contains the
 * layout values to those in the register set.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Create a table model to hold the layout register set values. </td></tr>
 * <tr><td> Update a table model to the values in the layout register set. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class MemoryLayoutMonitor implements PropertyChangeListener
{
    /** Label for the column providing the register names. */
    public static final String REG_NAME_COL = "regName";

    /** Label for the column holding the register values. */
    public static final String REG_VALUE_COL = "regValue";

    /** The register names. */
    public static final String[] REGISTER_NAMES =
        new String[]
        {
            "regBase", "regSize", "heapBase", "heapSize", "stackBase", "stackSize", "trailBase", "trailSize", "pdlBase",
            "pdlSize"
        };

    /** The register property names, used to label the table rows. */
    public static final String[] REGISTER_LABELS =
        new String[]
        {
            "regBase", "regSize", "heapBase", "heapSize", "stackBase", "stackSize", "trailBase", "trailSize", "pdlBase",
            "pdlSize"
        };

    /** The table to store the registers in. */
    private final TextTableModel table;

    /** A labeled view onto the register table. */
    private DoubleKeyedMap<String, String, String> labeledTable;

    /**
     * Constructs a table model with labelled rows and columns to hold the register values.
     *
     * @param table The table to output the register values to.
     */
    public MemoryLayoutMonitor(TextTableModel table)
    {
        this.table = table;
        labeledTable = table.withLabels();

        this.table.labelColumn(REG_NAME_COL, 0);
        this.table.labelColumn(REG_VALUE_COL, 1);

        for (int i = 0; i < REGISTER_NAMES.length; i++)
        {
            this.table.labelRow(REGISTER_LABELS[i], i);
            labeledTable.put(REG_NAME_COL, REGISTER_LABELS[i], REGISTER_NAMES[i]);
        }
    }

    /** {@inheritDoc} */
    public void propertyChange(PropertyChangeEvent evt)
    {
        String hex = String.format("%08X", evt.getNewValue());
        labeledTable.put(REG_VALUE_COL, evt.getPropertyName(), hex);
    }
}
