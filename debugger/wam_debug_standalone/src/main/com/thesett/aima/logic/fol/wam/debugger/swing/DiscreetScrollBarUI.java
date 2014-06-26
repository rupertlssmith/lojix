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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * DiscreetScrollBarUI implements a new look&feel rendering for swing scroll bars, which is intended to be less
 * obtrusive and chunky.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Render scroll bar track. </td></tr>
 * <tr><td> Render scroll bad position indicator. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DiscreetScrollBarUI extends BasicScrollBarUI
{
    private final ColorScheme colorScheme;

    public DiscreetScrollBarUI(ColorScheme colorScheme)
    {
        this.colorScheme = colorScheme;
    }

    /** {@inheritDoc} */
    public void layoutContainer(Container parent)
    {
        Dimension sbSize = parent.getSize();
        Insets sbInsets = parent.getInsets();

        int x = sbInsets.left;
        int y = sbInsets.top;
        int width = sbSize.width - (sbInsets.right + sbInsets.left);
        int height = sbSize.height - (sbInsets.top + sbInsets.bottom);

        trackRect.setBounds(x, y, width, height);
    }

    /** {@inheritDoc} */
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
    {
        g.setColor(colorScheme.getInactiveBackground());
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

        /*if (this.trackHighlight == BasicScrollBarUI.DECREASE_HIGHLIGHT)
        {
            this.paintDecreaseHighlight(g);
        }
        else if (this.trackHighlight == BasicScrollBarUI.INCREASE_HIGHLIGHT)
        {
            this.paintIncreaseHighlight(g);
        }*/
    }

    /** {@inheritDoc} */
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
    {
        if (thumbBounds.isEmpty() || !this.scrollbar.isEnabled())
        {
            return;
        }

        g.translate(thumbBounds.x, thumbBounds.y);
        g.setColor(this.thumbDarkShadowColor);
        g.drawOval(2, 0, 14, 14);
        g.setColor(this.thumbColor);
        g.fillOval(2, 0, 14, 14);
        g.setColor(this.thumbHighlightColor);
        g.setColor(this.thumbLightShadowColor);
        g.translate(-thumbBounds.x, -thumbBounds.y);
    }

    protected void installComponents()
    {
    }

    protected void uninstallComponents()
    {
    }
}
