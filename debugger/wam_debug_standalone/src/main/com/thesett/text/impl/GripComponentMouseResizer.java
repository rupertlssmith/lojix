/*
 * © Copyright Rupert Smith, 2005 to 2013.
 *
 * ALL RIGHTS RESERVED. Any unauthorized reproduction or use of this
 * material is prohibited. No part of this work may be reproduced or
 * transmitted in any form or by any means, electronic or mechanical,
 * including photocopying, recording, or by any information storage
 * and retrieval system without express written permission from the
 * author.
 */
package com.thesett.text.impl;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import com.thesett.text.impl.model.ResizeDelta;

/**
 * GripComponentMouseResizer applies a 'grip' cursor to a component when the mouse is pressed on it. When pressed mouse
 * motion is applied to a {@link ResizeDelta}, which in turn can be used to move or re-size some aspect of a UI under
 * mouse control.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Show a different cursor when moving a component.
 * <tr><td> Apply mouse motion on a component to a re-sizable object. <td> {@link ResizeDelta}
 * </table></pre>
 *
 * @author Rupert Smith
 */
class GripComponentMouseResizer extends MouseInputAdapter
{
    private final Component gripComponent;
    private final ResizeDelta resizeable;
    private final Cursor defaultCursor;
    private final Cursor moveCursor;

    private boolean pressed = false;
    private int lastY;

    /**
     * Creates a mouse controlled re-sizer.
     *
     * @param gripComponent The component to 'grip'
     * @param resizeable    The delta re-sizable to apply mouse motion to.
     * @param defaultCursor The default cursor to show when not gripping.
     * @param moveCursor    The move cursor to show when gripping.
     */
    public GripComponentMouseResizer(Component gripComponent, ResizeDelta resizeable, Cursor defaultCursor,
        Cursor moveCursor)
    {
        this.gripComponent = gripComponent;
        this.resizeable = resizeable;
        this.defaultCursor = defaultCursor;
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
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Restores the cursor to the default variant.
     */
    public void mouseReleased(MouseEvent e)
    {
        gripComponent.setCursor(defaultCursor);
        pressed = false;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Applies mouse dragging as a resizing delta to the re-sizer.
     */
    public void mouseDragged(MouseEvent e)
    {
        JSplitPane p;

        if (pressed)
        {
            int deltaY = e.getYOnScreen() - lastY;
            lastY = e.getYOnScreen();

            if (deltaY != 0)
            {
                resizeable.deltaResizeTop(-deltaY);

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
