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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;

import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextGrid;
import com.thesett.common.util.Function;
import com.thesett.text.api.TextGridEvent;
import com.thesett.text.api.TextGridListener;
import com.thesett.text.api.model.TextGridModel;

/**
 * JTextGrid is a Swing component that renders the text in a {@link TextGridModel}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Render a text grid. </td><td> {@link TextGridModel} </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class JTextGrid extends JComponent
{
    /** Flag to indicate that anti-aliasing and quality rendering should be used. */
    private boolean useAntiAliasing = true;

    /** The text grid model to render. */
    private EnhancedTextGrid model;

    /** Indicates whether font metrics have been initialized. */
    private boolean fontMetricsInitialized;

    /** The monospaced character width. */
    private int charWidth;

    /** The monospaced character height. */
    private int charHeight;

    /** The monospaced character descent. */
    private int descent;

    /** Holds mouse event listeners, that will receive mouse events translated to text grid coordinates. */
    private Set<MouseListener> textGridMouseListeners = new HashSet<MouseListener>();

    /** Holds mouse motion event listeners, that will receive mouse events translated to text grid coordinates. */
    private Set<MouseMotionListener> textGridMouseMotionListeners = new HashSet<MouseMotionListener>();

    /** {@inheritDoc} */
    public Dimension getPreferredSize()
    {
        Dimension d = super.getPreferredSize();
        d = (d == null) ? new Dimension(400, 400) : d;

        return d;
    }

    /**
     * Accepts a text grid model to render.
     *
     * @param model The text grid model.
     */
    public void setModel(EnhancedTextGrid model)
    {
        this.model = model;

        model.addTextGridListener(new ModelListener());
    }

    /** Sets up standard mouse handling, to translate mouse events from screen coordinates to text grid coordinates. */
    public void initializeStandardMouseHandling()
    {
        MouseHandler mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    /**
     * Adds a mouse listener, that will receive translated mouse events in text grid coordinates, instead of screen
     * coordinates.
     *
     * @param listener The mouse listener to add.
     */
    public synchronized void addTextGridMouseListener(MouseListener listener)
    {
        textGridMouseListeners.add(listener);
    }

    /**
     * Adds a mouse motion listener, that will receive translated mouse events in text grid coordinates, instead of
     * screen coordinates.
     *
     * @param listener The mouse listener to add.
     */
    public synchronized void addTextGridMouseMotionListener(MouseMotionListener listener)
    {
        textGridMouseMotionListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Renders the text grid model.
     */
    protected void paintComponent(Graphics g)
    {
        Graphics2D graphics2D = (Graphics2D) g.create();

        graphics2D.setFont(getFont());
        graphics2D.setColor(getBackground());
        graphics2D.fillRect(0, 0, getWidth(), getHeight());

        initializeFontMetrics();

        if (useAntiAliasing)
        {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        for (int row = 0; row <= model.getHeight(); row++)
        {
            for (int col = 0; col <= model.getWidth(); col++)
            {
                char character = model.getCharAt(col, row);

                AttributeSet attributes = model.getAttributeAt(col, row);

                Color bgColor =
                    (attributes != null) ? (Color) attributes.getAttribute(StyleConstants.Background) : null;
                bgColor = (bgColor == null) ? getBackground() : bgColor;

                graphics2D.setColor(bgColor);
                graphics2D.fillRect(col * charWidth, row * charHeight, charWidth, charHeight);
                graphics2D.setColor(getForeground());
                graphics2D.drawString(Character.toString(character), col * charWidth,
                    ((row + 1) * charHeight) - descent);
            }
        }

        graphics2D.dispose();
    }

    private void initializeFontMetrics()
    {
        if (!fontMetricsInitialized)
        {
            FontMetrics fontMetrics = getFontMetrics(getFont());
            charWidth = fontMetrics.charWidth(' ');
            charHeight = fontMetrics.getHeight();
            descent = fontMetrics.getDescent();
            fontMetricsInitialized = true;
        }
    }

    private void fireTextGridMouseMotionEvent(Function<MouseMotionListener, Object> switchFunction)
    {
        for (MouseMotionListener listener : textGridMouseMotionListeners)
        {
            switchFunction.apply(listener);
        }
    }

    private void fireTextGridMouseEvent(Function<MouseListener, Object> switchFunction)
    {
        for (MouseListener listener : textGridMouseListeners)
        {
            switchFunction.apply(listener);
        }
    }

    /**
     * Listens for changes to the model, and initiates a repaint.
     */
    private class ModelListener implements TextGridListener
    {
        /** {@inheritDoc} */
        public void changedUpdate(TextGridEvent event)
        {
            JTextGrid.this.repaint();
        }
    }

    /**
     * MouseHandler translates mouse events on the UI component, into row/column coordinates in the text grid space.
     */
    private class MouseHandler extends MouseInputAdapter
    {
        int curRow = -1;
        int curCol = -1;

        private MouseHandler()
        {
            JTextGrid.this.initializeFontMetrics();
        }

        /** {@inheritDoc} */
        public void mouseMoved(MouseEvent e)
        {
            final MouseEvent translatedEvent = translateEvent(e);

            int row = translatedEvent.getX();
            int col = translatedEvent.getY();

            if ((curRow != row) || (curCol != col))
            {
                curRow = row;
                curCol = col;

                fireTextGridMouseMotionEvent(new Function<MouseMotionListener, Object>()
                    {
                        public Object apply(MouseMotionListener mouseMotionListener)
                        {
                            mouseMotionListener.mouseMoved(translatedEvent);

                            return null;
                        }
                    });
            }
        }

        /** {@inheritDoc} */
        public void mousePressed(MouseEvent e)
        {
            final MouseEvent translatedEvent = translateEvent(e);
            fireTextGridMouseEvent(new Function<MouseListener, Object>()
                {
                    public Object apply(MouseListener mouseListener)
                    {
                        mouseListener.mousePressed(translatedEvent);

                        return null;
                    }
                });
        }

        /**
         * Translates a mouse event from screen coordinates to text grid coordinates.
         *
         * @param  e The original mouse event.
         *
         * @return The same mouse event but with translated coordinates.
         */
        private MouseEvent translateEvent(MouseEvent e)
        {
            int x = e.getX();
            int y = e.getY();

            int transX = x / charWidth;
            int transY = y / charHeight;

            return new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), transX, transY,
                e.getClickCount(), e.isPopupTrigger(), e.getButton());
        }
    }
}
