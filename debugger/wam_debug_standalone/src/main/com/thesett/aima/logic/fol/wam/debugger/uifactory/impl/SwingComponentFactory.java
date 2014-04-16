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
package com.thesett.aima.logic.fol.wam.debugger.uifactory.impl;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.Document;

import com.thesett.aima.logic.fol.wam.debugger.swing.ColorScheme;
import com.thesett.aima.logic.fol.wam.debugger.swing.GripComponentMouseMover;
import com.thesett.aima.logic.fol.wam.debugger.swing.MotionDelta;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.MainWindow;

/**
 * SwingComponentFactory implements a UI component factory that produces Swing components.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Create the main window. </td><td> {@link ColorScheme} </td></tr>
 * <tr><td> Create a grip-able panel for adjusting screen layout. </td><td> {@link ColorScheme} </td></tr>
 * <tr><td> Create an editor panel. </td><td> {@link ColorScheme} </td></tr>
 * <tr><td> Create a black panel for filling in unused areas of the screen. </td><td> {@link ColorScheme} </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SwingComponentFactory implements ComponentFactory<Component>
{
    /** The default cursor appearance. */
    private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    /** The cursor for vertical resizing controls mouse-over. */
    private static final Cursor VERTICAL_RESIZE_CURSOR = new Cursor(Cursor.W_RESIZE_CURSOR);

    /** The cursor for horizontal resizing controls mouse-over. */
    private static final Cursor HORIZONTAL_RESIZE_CURSOR = new Cursor(Cursor.N_RESIZE_CURSOR);

    /** The grip cursor appearance to use when moving components. */
    private static final Cursor GRIP_CURSOR = new Cursor(Cursor.MOVE_CURSOR);

    /** The color scheme to apply to components. */
    private final ColorScheme colorScheme;

    /**
     * Creates a component factory that produces Swing components.
     *
     * @param colorScheme The color scheme to use.
     */
    public SwingComponentFactory(ColorScheme colorScheme)
    {
        this.colorScheme = colorScheme;
    }

    /** {@inheritDoc} */
    public MainWindow<Component> createMainWindow()
    {
        return new SwingMainWindow(this);
    }

    /** {@inheritDoc} */
    public Component createEditor(Document document)
    {
        JEditorPane textPane = new JEditorPane();
        textPane.setBackground(colorScheme.getActiveBackground());
        textPane.setCaretColor(colorScheme.getHighlight1());
        textPane.setForeground(colorScheme.getMainText());
        textPane.setAutoscrolls(true);

        Font font = new Font("DejaVu Sans Mono", Font.PLAIN, 12);
        textPane.setFont(font);
        textPane.setDocument(document);

        return textPane;
    }

    /** {@inheritDoc} */
    public Component createGripPanel(MotionDelta motionDelta, boolean vertical)
    {
        JPanel vbar = new JPanel();
        vbar.setBackground(colorScheme.getInactiveBackground());

        GripComponentMouseMover resizer =
            new GripComponentMouseMover(vbar, motionDelta, vertical ? VERTICAL_RESIZE_CURSOR : HORIZONTAL_RESIZE_CURSOR,
                GRIP_CURSOR);
        vbar.addMouseMotionListener(resizer);
        vbar.addMouseListener(resizer);

        return vbar;
    }

    /** {@inheritDoc} */
    public Component createBlankPanel()
    {
        JPanel vbar = new JPanel();
        vbar.setBackground(colorScheme.getActiveBackground());
        vbar.setForeground(colorScheme.getDisabledText());

        return vbar;
    }
}
