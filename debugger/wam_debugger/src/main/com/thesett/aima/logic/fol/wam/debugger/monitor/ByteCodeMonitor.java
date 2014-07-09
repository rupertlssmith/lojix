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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.thesett.aima.logic.fol.FunctorName;
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
 * <p/>The instructions vary in size, so mapping between addresses in the code, and rows in the table model is not
 * straightforward. This monitor maintains this mapping, and provides the methods {@link #getAddressForRow(int)} and
 * {@link #getRowForAddress(int)} to access it. This can be used to work out which instruction has been selected in the
 * table, or which row in the table to highlight when a particular address is being stepped, and so on.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Maintain a table model containing disassembled byte-code. </td></tr>
 * <tr><td> Maintain a mapping between code addresses and table rows. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ByteCodeMonitor
{
    /** Used for debugging. */
    /* private static final Logger log = Logger.getLogger(ByteCodeMonitor.class.getName()); */

    /** Defines the label for the code address column. */
    public static final String ADDRESS = "address";

    /** Defines the label for the code label column. */
    public static final String LABEL = "label";

    /** Defines the label for the instruction mnemonic column. */
    public static final String MNEMONIC = "mnemonic";

    /** Defines the label for the first instruction arguments column. */
    public static final String ARG_1 = "arg_1";

    /** Defines the label for the second instruction arguments column. */
    public static final String ARG_2 = "arg_2";

    /** Column labels for the code table. */
    public static final String[] BYTE_CODE_COL_LABELS = new String[] { ADDRESS, LABEL, MNEMONIC, ARG_1 };

    /** The table to output the byte code to. */
    private final TextTableModel table;

    /** A column labeled view onto the code table. */
    private DoubleKeyedMap<String, Integer, String> labeledTable;

    /** A mapping from code table rows to code addresses. */
    private List<Integer> rowToAddress = new ArrayList<Integer>();

    /** A mapping from code addresses to code table rows. */
    private ConcurrentSkipListMap<Integer, Integer> addressToRow = new ConcurrentSkipListMap<Integer, Integer>();

    /** A copy of the code buffer, with code update deltas merged into it. */
    private ByteBuffer codeBuffer = ByteBuffer.allocate(1024);

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
     * Provides the code address corresponding to a table row.
     *
     * @param  row The table row.
     *
     * @return The corresponding code address.
     */
    public int getAddressForRow(int row)
    {
        return rowToAddress.get(row);
    }

    /**
     * Provides the table row for a code address.
     *
     * @param  address The code address.
     *
     * @return The corresponding table row.
     */
    public int getRowForAddress(int address)
    {
        return addressToRow.get(address);
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
        /*log.fine("public void onCodeUpdate(ByteBuffer codeBuffer, int start = " + start + ", int length = " + length +
            ", VariableAndFunctorInterner interner, WAMCodeView codeView): called");*/

        // Take a copy of the new bytecode.
        copyAndResizeCodeBuffer(codeBuffer, start, length);

        // Disassemble the new area of byte code.
        SizeableList<WAMInstruction> instructions =
            WAMInstruction.disassemble(start, length, this.codeBuffer, interner, codeView);

        // Figure out where to start writing the disassembled code into the table.
        Map.Entry<Integer, Integer> entry = addressToRow.floorEntry(start);
        int firstRow = (entry == null) ? 0 : (entry.getValue() + 1);

        int address = start;
        int row = firstRow;

        // Build the mapping between addresses and rows.
        for (WAMInstruction instruction : instructions)
        {
            addressToRow.put(address, row);
            rowToAddress.add(row, address);

            row++;
            address += instruction.sizeof();
        }

        // Render the instructions into the table to be displayed.
        renderInstructions(instructions, firstRow, start);
    }

    /**
     * Copies code from the specified code buffer, into the internal one, resizing the internal code buffer if necessary
     * to make enough room.
     *
     * @param codeBuffer The code buffer to copy from.
     * @param start      The start offset within the buffer of the new code.
     * @param length     The length of the new code.
     */
    private void copyAndResizeCodeBuffer(ByteBuffer codeBuffer, int start, int length)
    {
        // Check the internal code buffer is large enough or resize it, then copy in the new instructions.
        int max = start + length;

        if (this.codeBuffer.limit() <= max)
        {
            ByteBuffer newCodeBuffer = ByteBuffer.allocate(max * 2);
            newCodeBuffer.put(this.codeBuffer.array(), 0, this.codeBuffer.limit());

            /*log.fine("Re-sized code buffer to " + (max * 2));*/
        }

        codeBuffer.position(start);
        codeBuffer.get(this.codeBuffer.array(), start, length);
    }

    /**
     * Renders disassembled instructions into the code table, starting at the specified row and instruction address.
     *
     * @param instructions A list of instructions to render into the table.
     * @param row          The table row to start at.
     * @param address      The code address to start at.
     */
    private void renderInstructions(SizeableList<WAMInstruction> instructions, int row, int address)
    {
        for (WAMInstruction instruction : instructions)
        {
            WAMLabel label = instruction.getLabel();

            labeledTable.put(ADDRESS, row, String.format("%08X", address));
            labeledTable.put(LABEL, row, (label == null) ? "" : (label.toPrettyString() + ":"));
            labeledTable.put(MNEMONIC, row, instruction.getMnemonic().getPretty());

            int fieldMask = instruction.getMnemonic().getFieldMask();

            String arg = "";

            for (int i = 2; i < 32; i = i * 2)
            {
                if ((fieldMask & i) != 0)
                {
                    if (!"".equals(arg))
                    {
                        arg += ", ";
                    }

                    switch (i)
                    {
                    case 2:
                        arg += Integer.toString(instruction.getReg1());
                        break;

                    case 4:
                        arg += Integer.toString(instruction.getReg2());
                        break;

                    case 8:

                        FunctorName fn = instruction.getFn();
                        if (fn != null)
                        {
                            arg += fn.getName() + "/" + fn.getArity();
                        }

                        break;

                    case 16:

                        WAMLabel target1 = instruction.getTarget1();
                        if (target1 != null)
                        {
                            arg += target1.getName() + "/" + target1.getArity() + "_" + target1.getId();
                        }

                        break;
                    }
                }
            }

            labeledTable.put(ARG_1, row, arg);

            row++;
            address += instruction.sizeof();
        }
    }
}
