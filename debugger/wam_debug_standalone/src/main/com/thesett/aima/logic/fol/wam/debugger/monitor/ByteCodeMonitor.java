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

import java.nio.ByteBuffer;

import com.thesett.aima.logic.fol.VariableAndFunctorInterner;
import com.thesett.aima.logic.fol.wam.compiler.WAMInstruction;
import com.thesett.aima.logic.fol.wam.compiler.WAMLabel;
import com.thesett.aima.logic.fol.wam.machine.WAMCodeView;
import com.thesett.common.util.SizeableList;
import com.thesett.common.util.doublemaps.DoubleKeyedMap;
import com.thesett.text.api.model.TextTableModel;

/**
 * ByteCodeMonitor responds to changes in the byte code loaded into the target machine. Byte-code is disassembled and
 * inserted into a {@link TextTableModel}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Maintain a table model containing disassembled byte-code. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ByteCodeMonitor
{
    /** Defines the label for the code address column. */
    public static final String ADDRESS = "address";

    /** Defines the label for the code label column. */
    public static final String LABEL = "label";

    /** Defines the label for the instruction mnemonic column. */
    public static final String MNEMONIC = "mnemonic";

    /** Defines the label for the instruction arguments column. */
    public static final String ARGS = "args";

    /** Column labels for the code table. */
    public static final String[] BYTE_CODE_COL_LABELS = new String[] { ADDRESS, LABEL, MNEMONIC, ARGS };

    /** The table to output the byte code to. */
    private final TextTableModel table;

    /** A column labeled view onto the register table. */
    private DoubleKeyedMap<String, Integer, String> labeledTable;

    private int row = 0;
    private int address = 0;

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
    }

    /**
     * Should be notified every time byte-code is added to the machine.
     *
     * @param codeBuffer A buffer containing the byte-code.
     * @param start      The start offset within the buffer of the new code.
     * @param length     The length of the new code.
     * @param interner   The machines interner to lookup interned names with.
     * @param codeView   The machines code view to find interned names matching addresses.
     */
    public void onCodeUpdate(ByteBuffer codeBuffer, int start, int length, VariableAndFunctorInterner interner,
        WAMCodeView codeView)
    {
        SizeableList<WAMInstruction> instructions =
            WAMInstruction.disassemble(start, length, codeBuffer, interner, codeView);

        for (WAMInstruction instruction : instructions)
        {
            WAMLabel label = instruction.getLabel();

            labeledTable.put(ADDRESS, row, String.format("%08X", address));
            labeledTable.put(LABEL, row, (label == null) ? "" : (label.toPrettyString() + ":"));
            labeledTable.put(MNEMONIC, row, instruction.getMnemonic().name());
            labeledTable.put(ARGS, row, instruction.toString());

            row++;
            address += instruction.sizeof();
        }
    }
}
