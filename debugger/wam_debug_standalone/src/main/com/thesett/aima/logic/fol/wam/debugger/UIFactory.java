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
package com.thesett.aima.logic.fol.wam.debugger;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;

import javax.swing.*;
import javax.swing.text.Document;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class UIFactory
{
    private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    private static final Cursor MOVE_CURSOR = new Cursor(Cursor.MOVE_CURSOR);
    private JFrame frame;
    private DebuggerLayout layout;

    public void createMainWindow()
    {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setPreferredSize(new Dimension(1000, 800));

        layout = new DebuggerLayout();
        frame.getContentPane().setLayout(layout);

        frame.setVisible(true);
    }

    public void addTextPane(Document document)
    {
        JEditorPane textPane = new JEditorPane();
        textPane.setBackground(Color.DARK_GRAY);
        textPane.setCaretColor(Color.LIGHT_GRAY);
        textPane.setForeground(Color.WHITE);
        textPane.setAutoscrolls(true);

        Font font = new Font("Courier New", Font.PLAIN, 12);
        textPane.setFont(font);

        frame.getContentPane().add(textPane, DebuggerLayout.CENTER);

        textPane.setDocument(document);
    }

    public void addConsole()
    {
        JEditorPane textPane = new JEditorPane();
        textPane.setBackground(Color.DARK_GRAY);
        textPane.setCaretColor(Color.LIGHT_GRAY);
        textPane.setForeground(Color.WHITE);
        textPane.setAutoscrolls(true);

        Font font = new Font("Courier New", Font.PLAIN, 12);
        textPane.setFont(font);

        frame.getContentPane().add(textPane, DebuggerLayout.CONSOLE);
    }

    public void addHorizontalBar()
    {
        JEditorPane hbar = new JEditorPane();
        hbar.setBackground(Color.WHITE);
        hbar.setCaretColor(Color.LIGHT_GRAY);
        hbar.setForeground(Color.BLACK);
        hbar.setAutoscrolls(true);
        hbar.setEditable(false);
        hbar.setSelectedTextColor(Color.BLACK);
        hbar.setSelectionColor(Color.WHITE);
        hbar.setCursor(DEFAULT_CURSOR);

        Font font = new Font("Courier New", Font.PLAIN, 12);
        hbar.setFont(font);

        GripComponentMouseResizer resizer =
            new GripComponentMouseResizer(hbar, layout, DEFAULT_CURSOR, MOVE_CURSOR);
        hbar.addMouseMotionListener(resizer);
        hbar.addMouseListener(resizer);

        frame.getContentPane().add(hbar, DebuggerLayout.STATUS_BAR);
    }

    public void addRightPane()
    {
        // Right vertical bar.
        JPanel vbar = new JPanel();
        vbar.setBackground(Color.WHITE);
        vbar.setForeground(Color.BLACK);

        GripComponentMouseResizer resizer =
            new GripComponentMouseResizer(vbar, layout, DEFAULT_CURSOR, MOVE_CURSOR);
        vbar.addMouseMotionListener(resizer);
        vbar.addMouseListener(resizer);

        frame.getContentPane().add(vbar, DebuggerLayout.RIGHT_VERTICAL_BAR);

        // Right pane.
        JEditorPane textPane = new JEditorPane();
        textPane.setBackground(Color.DARK_GRAY);
        textPane.setCaretColor(Color.LIGHT_GRAY);
        textPane.setForeground(Color.WHITE);
        textPane.setAutoscrolls(true);

        Font font = new Font("Courier New", Font.PLAIN, 12);
        textPane.setFont(font);

        frame.getContentPane().add(textPane, DebuggerLayout.RIGHT_PANE);
    }

    public void addLeftPane()
    {
        // Left vertical bar.
        JPanel vbar = new JPanel();
        vbar.setBackground(Color.WHITE);
        vbar.setForeground(Color.BLACK);

        GripComponentMouseResizer resizer =
                new GripComponentMouseResizer(vbar, layout, DEFAULT_CURSOR, MOVE_CURSOR);
        vbar.addMouseMotionListener(resizer);
        vbar.addMouseListener(resizer);

        frame.getContentPane().add(vbar, DebuggerLayout.LEFT_VERTICAL_BAR);

        // Left pane.
        JEditorPane textPane = new JEditorPane();
        textPane.setBackground(Color.DARK_GRAY);
        textPane.setCaretColor(Color.LIGHT_GRAY);
        textPane.setForeground(Color.WHITE);
        textPane.setAutoscrolls(true);

        Font font = new Font("Courier New", Font.PLAIN, 12);
        textPane.setFont(font);

        frame.getContentPane().add(textPane, DebuggerLayout.LEFT_PANE);
    }

    private void addMenu()
    {
        MenuBar mb = new MenuBar();
        Font font = new Font("Tahoma", Font.PLAIN, 11);
        mb.setFont(font);

        Menu m = new Menu("File");
        mb.add(m);
        frame.setMenuBar(mb);
    }
}
