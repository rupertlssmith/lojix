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
package com.thesett.aima.logic.fol.wam.debugger.uifactory.impl;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

import com.thesett.aima.logic.fol.wam.debugger.swing.DiscreetScrollBarUI;
import com.thesett.aima.logic.fol.wam.debugger.swing.FillViewportLayout;
import com.thesett.aima.logic.fol.wam.debugger.swing.GripComponentMouseMover;
import com.thesett.aima.logic.fol.wam.debugger.swing.JTextGrid;
import com.thesett.aima.logic.fol.wam.debugger.swing.MotionDelta;
import com.thesett.aima.logic.fol.wam.debugger.swing.ToolingColorScheme;
import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextGrid;
import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextGridImpl;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ColorFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ColorScheme;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ComponentFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.KeyCombinationBuilder;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.KeyShortcutMap;
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
public class SwingComponentFactory implements ComponentFactory<JComponent, Color, KeyStroke>
{
    /** The default cursor appearance. */
    private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    /** The cursor for vertical resizing controls mouse-over. */
    private static final Cursor VERTICAL_RESIZE_CURSOR = new Cursor(Cursor.W_RESIZE_CURSOR);

    /** The cursor for horizontal resizing controls mouse-over. */
    private static final Cursor HORIZONTAL_RESIZE_CURSOR = new Cursor(Cursor.N_RESIZE_CURSOR);

    /** The grip cursor appearance to use when moving components. */
    private static final Cursor GRIP_CURSOR = new Cursor(Cursor.MOVE_CURSOR);

    /** The color factory. */
    private final SwingColorFactory colorFactory = new SwingColorFactory();

    /** The color scheme to apply to user areas within components. */
    private ColorScheme<Color> colorScheme;

    /** The key combination builder. */
    private final SwingKeyCombinationBuilder keyCombinationBuilder = new SwingKeyCombinationBuilder();

    /** The keyboard short cut map to use across the components. */
    private KeyShortcutMap<KeyStroke> keyShortcutMap = new DefaultKeyShortcutMap<KeyStroke>(keyCombinationBuilder);

    private final ToolingColorScheme toolingColorScheme =
        new ToolingColorScheme()
        {
            public Color getToolingBackground()
            {
                return Color.DARK_GRAY;
            }

            public Color getToolingActiveBackground()
            {
                return Color.LIGHT_GRAY;
            }
        };

    /** Creates a component factory that produces Swing components. */
    public SwingComponentFactory()
    {
        colorScheme = new DefaultColorScheme(colorFactory);
    }

    /** {@inheritDoc} */
    public MainWindow<JComponent, KeyStroke> createMainWindow()
    {
        return new SwingMainWindow(this);
    }

    /** {@inheritDoc} */
    public EnhancedTextGrid createTextGrid()
    {
        return new EnhancedTextGridImpl();
    }

    /** {@inheritDoc} */
    public JComponent createTextGridPanel(EnhancedTextGrid model)
    {
        JTextGrid textPane = new JTextGrid();

        textPane.setBackground(colorScheme.getBackground());
        textPane.setForeground(colorScheme.getMainText());
        textPane.setAutoscrolls(true);

        Font font = new Font("DejaVu Sans Mono", Font.PLAIN, 12);
        textPane.setFont(font);
        textPane.setModel(model);
        textPane.initializeStandardMouseHandling();

        JScrollPane scrollPane =
            new JScrollPane(textPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUI(new DiscreetScrollBarUI(toolingColorScheme));
        scrollPane.getHorizontalScrollBar().setUI(new DiscreetScrollBarUI(toolingColorScheme));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setLayout(new FillViewportLayout());

        return scrollPane;
    }

    /** {@inheritDoc} */
    public JComponent createGripPanel(MotionDelta motionDelta, boolean vertical)
    {
        JPanel vbar = new JPanel();
        vbar.setBackground(toolingColorScheme.getToolingBackground());

        GripComponentMouseMover resizer =
            new GripComponentMouseMover(vbar, motionDelta, vertical ? VERTICAL_RESIZE_CURSOR : HORIZONTAL_RESIZE_CURSOR,
                GRIP_CURSOR);
        vbar.addMouseMotionListener(resizer);
        vbar.addMouseListener(resizer);

        return vbar;
    }

    /** {@inheritDoc} */
    public JComponent createBlankPanel()
    {
        JPanel vbar = new JPanel();
        vbar.setBackground(colorScheme.getBackground());
        vbar.setForeground(colorScheme.getDisabledText());

        return vbar;
    }

    /** {@inheritDoc} */
    public void setColorScheme(ColorScheme<Color> colorScheme)
    {
        this.colorScheme = colorScheme;
    }

    /** {@inheritDoc} */
    public ColorScheme<Color> getColorScheme()
    {
        return colorScheme;
    }

    /** {@inheritDoc} */
    public ColorFactory<Color> getColorFactory()
    {
        return colorFactory;
    }

    /** {@inheritDoc} */
    public KeyCombinationBuilder<KeyStroke> getKeyCombinationBuilder()
    {
        return keyCombinationBuilder;
    }

    /** {@inheritDoc} */
    public void setKeyShortcutMap(KeyShortcutMap<KeyStroke> shortcutMap)
    {
        this.keyShortcutMap = shortcutMap;
    }

    /** {@inheritDoc} */
    public KeyShortcutMap<KeyStroke> getKeyShortcutMap()
    {
        return keyShortcutMap;
    }
}
