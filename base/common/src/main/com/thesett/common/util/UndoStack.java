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
package com.thesett.common.util;

/**
 * UndoStack provides nested save and restore points for undoable operations.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Mark a save point.
 * <tr><td>Restore state to a save point.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface UndoStack
{
    /** Places a restoreable save point onto the operation stack. */
    void save();

    /** Undoes operations from the undo stack until the most recent save point is encountered. */
    void restore();

    /**
     * Defines a type of undoable operations.
     */
    interface Undoable
    {
        /** Undoes an operation restoring state to how it was previous to the operation. */
        void undo();
    }
}
