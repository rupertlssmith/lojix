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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;

import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.event.MouseInputAdapter;

import com.thesett.aima.logic.fol.wam.debugger.text.AttributeSet;
import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextGrid;
import com.thesett.common.util.Function;
import com.thesett.text.api.TextGridEvent;
import com.thesett.text.api.TextGridListener;

/**
 * JTextGrid is a Swing component that renders the text in an {@link EnhancedTextGrid}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Render a text grid. </td><td> {@link EnhancedTextGrid} </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class JTextGrid extends JComponent implements Scrollable
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
        return computeGridSize();
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

    /** {@inheritDoc} */
    public Dimension getPreferredScrollableViewportSize()
    {
        return getPreferredSize();
    }

    /** {@inheritDoc} */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        return charHeight;
    }

    /** {@inheritDoc} */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        return charHeight * 4;
    }

    /** {@inheritDoc} */
    public boolean getScrollableTracksViewportWidth()
    {
        return false;
    }

    /** {@inheritDoc} */
    public boolean getScrollableTracksViewportHeight()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Renders the text grid model.
     */
    protected void paintComponent(Graphics g)
    {
        Graphics2D graphics2D = (Graphics2D) g.create();
        Rectangle clipRect = (Rectangle) g.getClip();

        // Work out the area to be painted in grid coordinates against the clipping rectangle.
        int startCol = xToCol(clipRect.x);
        int startRow = yToRow(clipRect.y);
        int cols = xToCol(clipRect.x + clipRect.width);
        int rows = yToRow(clipRect.y + clipRect.height);

        graphics2D.setFont(getFont());
        graphics2D.setColor(getBackground());
        graphics2D.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);

        initializeFontMetrics();

        if (useAntiAliasing)
        {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        SortedMap<Integer, Integer> hSeps = model.getHorizontalSeparators();
        int hSepOffset = 0;

        for (int row = 0; row <= rows; row++)
        {
            Integer hNextSep = null;

            if (!hSeps.isEmpty())
            {
                hNextSep = hSeps.firstKey();
            }

            if ((hNextSep != null) && (hNextSep == row))
            {
                hSepOffset += hSeps.get(hNextSep);
                hSeps.remove(hNextSep);
            }

            SortedMap<Integer, Integer> vSeps = model.getVerticalSeparators();
            int vSepOffset = 0;

            for (int col = 0; col <= cols; col++)
            {
                Integer vNextSep = null;

                if (!vSeps.isEmpty())
                {
                    vNextSep = vSeps.firstKey();
                }

                if ((vNextSep != null) && (vNextSep == col))
                {
                    vSepOffset += vSeps.get(vNextSep);
                    vSeps.remove(vNextSep);
                }

                // Only render if within the clip rectangle
                if ((col >= startCol) && (row >= startRow))
                {
                    char character = model.getCharAt(col, row);

                    AttributeSet attributes = model.getAttributeAt(col, row);

                    Color bgColor =
                        (attributes != null) ? (Color) attributes.get(AttributeSet.BACKGROUND_COLOR) : null;
                    bgColor = (bgColor == null) ? getBackground() : bgColor;

                    graphics2D.setColor(bgColor);
                    graphics2D.fillRect(colToX(col) + vSepOffset, rowToY(row) + hSepOffset, charWidth, charHeight);

                    graphics2D.setColor(getForeground());
                    graphics2D.drawString(Character.toString(character), colToX(col) + vSepOffset,
                        (rowToY((row + 1))) - descent + hSepOffset);
                }
            }
        }

        graphics2D.dispose();
    }

    /**
     * Computes the rendered dimensions of the text grid model, on screen. Used for sizing this component.
     *
     * @return The on-screen dimensions of the rendered text grid model.
     */
    protected Dimension computeGridSize()
    {
        int cols = model.getWidth();
        int rows = model.getHeight();

        int horizSeparatorSize = 0;

        for (int size : model.getHorizontalSeparators().values())
        {
            horizSeparatorSize += size;
        }

        int vertSeparatorSize = 0;

        for (int size : model.getVerticalSeparators().values())
        {
            vertSeparatorSize += size;
        }

        return new Dimension(vertSeparatorSize + colToX(cols), horizSeparatorSize + rowToY(rows));
    }

    /**
     * Sets up metrics relating to the size of the font used to display the text grid. This only needs to be done once
     * but this method can be called many times as it is guarded by an initialization flag to prevent these being
     * calculated many times.
     */
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

    /**
     * Notifies event listeners attached to the text grid of a mouse motion event on the grid.
     *
     * @param switchFunction A function to apply to mouse listeners to effect the notification against them.
     */
    private void fireTextGridMouseMotionEvent(Function<MouseMotionListener, Object> switchFunction)
    {
        for (MouseMotionListener listener : textGridMouseMotionListeners)
        {
            switchFunction.apply(listener);
        }
    }

    /**
     * Notifies event listeners attached to the text grid of a mouse event on the grid.
     *
     * @param switchFunction A function to apply to mouse listeners to effect the notification against them.
     */
    private void fireTextGridMouseEvent(Function<MouseListener, Object> switchFunction)
    {
        for (MouseListener listener : textGridMouseListeners)
        {
            switchFunction.apply(listener);
        }
    }

    /**
     * Converts a column within the text grid space to an x coordinate within the component space.
     *
     * @param  col The column to convert to an x coordinate.
     *
     * @return The corresponding x coordinate within the component space.
     */
    private int colToX(int col)
    {
        return col * charWidth;
    }

    /**
     * Converts a row within the text grid space to an y coordinate within the component space.
     *
     * @param  row The row to convert to an y coordinate.
     *
     * @return The corresponding y coordinate within the component space.
     */
    private int rowToY(int row)
    {
        return row * charHeight;
    }

    /**
     * Converts an x coordinate within the component space to a column within the text grid space.
     *
     * @param  x The x coordinate to convert to a column.
     *
     * @return The corresponding column in the text grid space.
     */
    private int xToCol(int x)
    {
        return x / charWidth;
    }

    /**
     * Converts a y coordinate within the component space to a row within the text grid space.
     *
     * @param  y The y coordinate to convert to a row.
     *
     * @return The corresponding row in the text grid space.
     */
    private int yToRow(int y)
    {
        return y / charHeight;
    }

    /**
     * Listens for changes to the model, and initiates a revalidate and repaint. Revalidate is needed because the size
     * of the table may change, which will change the components preferred size. Repaint is needed to force re-rendering
     * the tables contents.
     */
    private class ModelListener implements TextGridListener
    {
        /** {@inheritDoc} */
        public void changedUpdate(TextGridEvent event)
        {
            JTextGrid.this.revalidate();
            JTextGrid.this.repaint();
        }
    }

    /**
     * MouseHandler translates mouse events on the UI component, into row/column coordinates in the text grid space.
     */
    private class MouseHandler extends MouseInputAdapter
    {
        /** The current row that the mouse has moved to. */
        int curRow = -1;

        /** The current column that the mouse has moved to. */
        int curCol = -1;

        /** Creates the mouse handler for mouse activity within the text grid. */
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
            return new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), xToCol(e.getX()),
                yToRow(e.getY()), e.getClickCount(), e.isPopupTrigger(), e.getButton());
        }
    }
}
