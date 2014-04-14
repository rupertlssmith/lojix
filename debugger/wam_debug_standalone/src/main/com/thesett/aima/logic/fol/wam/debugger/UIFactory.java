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
import java.awt.MenuItem;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
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
    private JEditorPane textPane;
    private JEditorPane consolePane;
    private JEditorPane statusPane;
    private TextLayout layout;

    public void createMainWindow()
    {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setPreferredSize(new Dimension(1000, 800));

        layout = new TextLayout();
        frame.getContentPane().setLayout(layout);

        frame.setVisible(true);

        MenuBar mb = new MenuBar();
        Menu m = new Menu("File");
        m.add(new MenuItem("test"));
        mb.add(m);
        frame.setMenuBar(mb);
    }

    public void addTextPane(Document document)
    {
        textPane = new JEditorPane();
        textPane.setBackground(Color.DARK_GRAY);
        textPane.setCaretColor(Color.LIGHT_GRAY);
        textPane.setForeground(Color.WHITE);
        textPane.setAutoscrolls(true);

        Font font = new Font("Courier New", Font.PLAIN, 12);
        textPane.setFont(font);

        frame.getContentPane().add(textPane, TextLayout.CENTER);

        textPane.setDocument(document);
    }

    public void showConsole(Document document)
    {
        textPane = new JEditorPane();
        textPane.setBackground(Color.DARK_GRAY);
        textPane.setCaretColor(Color.LIGHT_GRAY);
        textPane.setForeground(Color.WHITE);
        textPane.setAutoscrolls(true);

        Font font = new Font("Courier New", Font.PLAIN, 12);
        textPane.setFont(font);

        frame.getContentPane().add(textPane, TextLayout.CENTER);

        textPane.setDocument(document);
    }

    public void showStatusBar(Document document)
    {
        statusPane = new JEditorPane();
        statusPane.setBackground(Color.WHITE);
        statusPane.setCaretColor(Color.LIGHT_GRAY);
        statusPane.setForeground(Color.BLACK);
        statusPane.setAutoscrolls(true);
        statusPane.setEditable(false);
        statusPane.setSelectedTextColor(Color.BLACK);
        statusPane.setSelectionColor(Color.WHITE);
        statusPane.setText("==  ==  ==  =====================");
        statusPane.setCursor(DEFAULT_CURSOR);

        Font font = new Font("Courier New", Font.PLAIN, 12);
        statusPane.setFont(font);

        GripComponentMouseResizer resizer =
            new GripComponentMouseResizer(statusPane, layout, DEFAULT_CURSOR, MOVE_CURSOR);
        statusPane.addMouseMotionListener(resizer);
        statusPane.addMouseListener(resizer);

        frame.getContentPane().add(statusPane, TextLayout.STATUS_BAR);
    }

    public void showConsoleOrig(Document document)
    {
        consolePane = new JEditorPane();
        consolePane.setBackground(Color.DARK_GRAY);
        consolePane.setCaretColor(Color.LIGHT_GRAY);
        consolePane.setForeground(Color.WHITE);
        consolePane.setAutoscrolls(true);

        Font font = new Font("Courier New", Font.PLAIN, 12);
        consolePane.setFont(font);

        frame.getContentPane().add(consolePane, TextLayout.CONSOLE);
    }

    public void hideConsole(JComponent console)
    {
        frame.getContentPane().remove(console);
    }
}
