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
 * UndoStackBase implements a stack of operations with nested save points, and the ability to undo operations up to the
 * most recent save point. This assists in the implementation of arbitrary depth undo operations. Specific
 * implementations need to define and implement a set of operators for the operations they are capable of undoing.
 *
 * <p/>The {@link #save} and {@link #restore} methods are nestable, in that a sequence of saves will place save markers
 * onto the stack, and subsequent restores will back up to subsequent save points.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class UndoStackBase implements UndoStack
{
    /** Holds the stack of undoable operations. */
    protected Queue<Undoable> undoStack = new StackQueue<Undoable>();

    /** {@inheritDoc} */
    public void save()
    {
        undoStack.offer(new SavePoint());
    }

    /** {@inheritDoc} */
    public void restore()
    {
        boolean foundSavePoint = false;

        while (!foundSavePoint && !undoStack.isEmpty())
        {
            Undoable undoable = undoStack.poll();
            undoable.undo();

            foundSavePoint = (undoable instanceof SavePoint);
        }
    }

    /**
     * A save point is a special marker undoable operation that represents a point up to which operations should be
     * undone.
     */
    public static class SavePoint implements Undoable
    {
        /**
         * {@inheritDoc}
         *
         * <p/>This implementation does nothing, as the save point is simply a marker.
         */
        public void undo()
        {
        }
    }
}
