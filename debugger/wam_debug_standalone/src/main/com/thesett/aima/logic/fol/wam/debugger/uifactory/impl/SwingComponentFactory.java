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
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SwingComponentFactory implements ComponentFactory<Component>
{
    private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    private static final Cursor MOVE_CURSOR = new Cursor(Cursor.MOVE_CURSOR);

    private final ColorScheme colorScheme;

    public SwingComponentFactory(ColorScheme colorScheme)
    {
        this.colorScheme = colorScheme;
    }

    public MainWindow<Component> createMainWindow()
    {
        return new SwingMainWindow(this);
    }

    public Component createEditor(Document document)
    {
        JEditorPane textPane = new JEditorPane();
        textPane.setBackground(colorScheme.getActiveBackground());
        textPane.setCaretColor(colorScheme.getHighlight1());
        textPane.setForeground(colorScheme.getMainText());
        textPane.setAutoscrolls(true);

        Font font = new Font("Courier New", Font.PLAIN, 12);
        textPane.setFont(font);
        textPane.setDocument(document);

        return textPane;
    }

    public Component createGripPanel(MotionDelta motionDelta)
    {
        JPanel vbar = new JPanel();
        vbar.setBackground(colorScheme.getInactiveBackground());
        vbar.setForeground(colorScheme.getMainText());

        GripComponentMouseMover resizer = new GripComponentMouseMover(vbar, motionDelta, DEFAULT_CURSOR, MOVE_CURSOR);
        vbar.addMouseMotionListener(resizer);
        vbar.addMouseListener(resizer);

        return vbar;
    }

    public Component createBlankPanel()
    {
        JPanel vbar = new JPanel();
        vbar.setBackground(colorScheme.getInactiveBackground());
        vbar.setForeground(colorScheme.getDisabledText());

        return vbar;
    }
}
