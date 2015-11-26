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
package com.thesett.aima.logic.fol.wam.debugger.swing;

import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * DiscreetScrollBarUI implements a new look&feel rendering for swing scroll bars, which is intended to be less
 * obtrusive and chunky.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Render scroll bar track. </td></tr>
 * <tr><td> Render scroll bar position indicator. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DiscreetScrollBarUI extends BasicScrollBarUI
{
    /** The thickness of the scroll bar. */
    public static final int THICKNESS = 12;

    /** Provides the colors. */
    private final ToolingColorScheme colorScheme;

    /**
     * Creates a discreet scroll bar renderer.
     *
     * @param colorScheme The color scheme to use.
     */
    public DiscreetScrollBarUI(ToolingColorScheme colorScheme)
    {
        this.colorScheme = colorScheme;

    }

    /** {@inheritDoc} */
    public void installUI(JComponent c)
    {
        super.installUI(c);
        scrollbar.setFocusable(false);
    }

    /** {@inheritDoc} */
    public Dimension getMaximumSize(JComponent c)
    {
        int thickness = THICKNESS;

        return new Dimension(thickness, thickness);
    }

    /** {@inheritDoc} */
    public Dimension getMinimumSize(JComponent c)
    {
        return getMaximumSize(c);
    }

    /** {@inheritDoc} */
    public Dimension getPreferredSize(JComponent c)
    {
        return getMaximumSize(c);
    }

    /** {@inheritDoc} */
    public boolean getSupportsAbsolutePositioning()
    {
        return true;
    }

    /** {@inheritDoc} */
    protected void installListeners()
    {
        super.installListeners();
    }

    /** {@inheritDoc} */
    protected void uninstallListeners()
    {
        super.uninstallListeners();
    }

    /** {@inheritDoc} */
    protected void paintTrack(Graphics g, JComponent c, Rectangle bounds)
    {
        g.setColor(colorScheme.getToolingBackground());
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /** {@inheritDoc} */
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
    {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled())
        {
            return;
        }

        g.translate(thumbBounds.x, thumbBounds.y);

        boolean vertical = isVertical();
        int hgap = vertical ? 2 : 1;
        int vgap = vertical ? 1 : 2;

        int w = thumbBounds.width - (hgap * 2);
        int h = thumbBounds.height - (vgap * 2);

        // leave one pixel between thumb and right or bottom edge
        if (vertical)
        {
            h -= 1;
        }
        else
        {
            w -= 1;
        }

        g.setColor(colorScheme.getToolingActiveBackground());
        g.fillRect(hgap + 1, vgap + 1, w - 1, h - 1);

        g.setColor(colorScheme.getToolingActiveBackground());
        g.drawRoundRect(hgap, vgap, w, h, 3, 3);
        g.translate(-thumbBounds.x, -thumbBounds.y);
    }

    /** {@inheritDoc} */
    protected Dimension getMinimumThumbSize()
    {
        int thickness = THICKNESS;

        return isVertical() ? new Dimension(thickness, thickness * 2) : new Dimension(thickness * 2, thickness);
    }

    /** {@inheritDoc} */
    protected JButton createIncreaseButton(int orientation)
    {
        return new InvisibleButton();
    }

    /** {@inheritDoc} */
    protected JButton createDecreaseButton(int orientation)
    {
        return new InvisibleButton();
    }

    /**
     * Checks if the scroll bar is a vertical one.
     *
     * @return <tt>true</tt> iff the scroll bar is vertical.
     */
    private boolean isVertical()
    {
        return scrollbar.getOrientation() == Adjustable.VERTICAL;
    }

    /**
     * InvisibleButton is a button with zero size. It is used because the {@link BasicScrollBarUI} component expects to
     * have up and down buttons. Replacing these with zero sized buttons allows the layout algorithm to be re-used from
     * there without having to restructure it.
     */
    private static class InvisibleButton extends JButton
    {
        /** Creates an invisible button. */
        private InvisibleButton()
        {
            setFocusable(false);
            setRequestFocusEnabled(false);
        }

        /** {@inheritDoc} */
        public Dimension getMaximumSize()
        {
            return new Dimension(0, 0);
        }

        /** {@inheritDoc} */
        public Dimension getPreferredSize()
        {
            return getMaximumSize();
        }

        /** {@inheritDoc} */
        public Dimension getMinimumSize()
        {
            return getMaximumSize();
        }
    }
}
