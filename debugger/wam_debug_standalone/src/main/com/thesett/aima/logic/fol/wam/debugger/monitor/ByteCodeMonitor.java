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

import com.thesett.common.util.doublemaps.DoubleKeyedMap;
import com.thesett.text.api.model.TextTableModel;

/**
 * ByteCodeMonitor responds to changes in the byte code loaded into the target machine. Byte-code is disassembled and
 * inserted into a {@link TextTableModel}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ByteCodeMonitor
{
    public static final String LABEL = "label";
    public static final String MNEMONIC = "mnemonic";
    public static final String ARG_1 = "arg1";
    public static final String ARG_2 = "arg2";

    /** Column labels for the code table. */
    public static final String[] BYTE_CODE_COL_LABELS = new String[] { LABEL, MNEMONIC, ARG_1, ARG_2 };

    /** The table to output the byte code to. */
    private final TextTableModel table;

    /** A column labeled view onto the register table. */
    private DoubleKeyedMap<String, Integer, String> labeledTable;

    /**
     * Constructs a table model with labelled columns to hold byte code.
     *
     * @param table The table to output the byte code to.
     */
    public ByteCodeMonitor(TextTableModel table)
    {
        this.table = table;
        labeledTable = table.withColumnLabels();

        for (int j = 0; j < BYTE_CODE_COL_LABELS.length; j++)
        {
            String label = BYTE_CODE_COL_LABELS[j];
            this.table.labelColumn(label, j);
        }

        for (int i = 0; i < 10; i++)
        {
            labeledTable.put(LABEL, i, "");
            labeledTable.put(MNEMONIC, i, "PUT_VAL");
            labeledTable.put(ARG_1, i, "X0");
            labeledTable.put(ARG_2, i, "Y1");
        }
    }
}
