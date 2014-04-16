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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 * GripComponentMouseMover applies a 'grip' cursor to a component when the mouse is pressed on it. When pressed mouse
 * motion is applied to a {@link MotionDelta}, which in turn can be used to move or re-size some aspect of a UI under
 * mouse control.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Show a different cursor when moving a component.
 * <tr><td> Apply mouse motion on a component to a re-sizable object. <td> {@link MotionDelta}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class GripComponentMouseMover extends MouseInputAdapter
{
    /** The grip-able component. */
    private final Component gripComponent;

    /** The motion delta to update as the mouse is moved. */
    private final MotionDelta resizeable;

    /** The non-pressed hover cursor appearance. */
    private final Cursor hoverCursor;

    /** The cursor appearance to use when pressed and moving the component. */
    private final Cursor moveCursor;

    /** Indicates whether the mouse button is down. */
    private boolean pressed = false;

    /** Holds the previous y coordinate. */
    private int lastY;

    /** Holds the previous x coordinate. */
    private int lastX;

    /**
     * Creates a mouse controlled re-sizer.
     *
     * @param gripComponent The component to 'grip'
     * @param resizeable    The delta re-sizable to apply mouse motion to.
     * @param hoverCursor   The hover cursor to show when not gripping.
     * @param moveCursor    The move cursor to show when gripping.
     */
    public GripComponentMouseMover(Component gripComponent, MotionDelta resizeable, Cursor hoverCursor,
        Cursor moveCursor)
    {
        this.gripComponent = gripComponent;
        this.resizeable = resizeable;
        this.hoverCursor = hoverCursor;
        this.moveCursor = moveCursor;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Changes the cursor to the move variant, and records that the mouse is pressed.
     */
    public void mousePressed(MouseEvent e)
    {
        gripComponent.setCursor(moveCursor);
        pressed = true;
        lastY = e.getYOnScreen();
        lastX = e.getXOnScreen();
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Restores the cursor to the hover variant.
     */
    public void mouseReleased(MouseEvent e)
    {
        gripComponent.setCursor(hoverCursor);
        pressed = false;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Sets the hover cursor.
     */
    public void mouseEntered(MouseEvent e)
    {
        gripComponent.setCursor(hoverCursor);
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Applies mouse dragging as a resizing delta to the re-sizer.
     */
    public void mouseDragged(MouseEvent e)
    {
        if (pressed)
        {
            int deltaY = e.getYOnScreen() - lastY;
            lastY = e.getYOnScreen();

            int deltaX = e.getXOnScreen() - lastX;
            lastX = e.getXOnScreen();

            boolean revalidate = false;

            if (deltaY != 0)
            {
                resizeable.deltaY(-deltaY);
                revalidate = true;
            }

            if (deltaX != 0)
            {
                resizeable.deltaX(-deltaX);
                revalidate = true;
            }

            if (revalidate)
            {
                SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            gripComponent.getParent().revalidate();
                        }
                    });
            }
        }
    }
}
