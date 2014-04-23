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

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

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
    private TextGridModel model;

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
    public void setModel(TextGridModel model)
    {
        this.model = model;
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

        if (useAntiAliasing)
        {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        FontMetrics fontMetrics = getGraphics().getFontMetrics(getFont());
        int charWidth = fontMetrics.charWidth(' ');
        int charHeight = fontMetrics.getHeight();

        for (int row = 0; row <= model.getHeight(); row++)
        {
            for (int col = 0; col <= model.getWidth(); col++)
            {
                char character = model.getCharAt(col, row);

                graphics2D.setColor(getBackground());
                graphics2D.fillRect(col * charWidth, row * charHeight, charWidth, charHeight);
                graphics2D.setColor(getForeground());
                graphics2D.drawString(Character.toString(character), col * charWidth,
                    ((row + 1) * charHeight) - fontMetrics.getDescent());
            }
        }

        graphics2D.dispose();
    }
}
