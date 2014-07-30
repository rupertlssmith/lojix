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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JViewport;
import javax.swing.ViewportLayout;

/**
 * FillViewportLayout is a modified ViewportLayout to ensure that the child components within a view-port always fill
 * the view-port when their preferred size is smaller than the view-port itself.
 *
 * <p/>This is a JDK1.2.2 bug (id 4310721).
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Layout a component to fill a view-port. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class FillViewportLayout extends ViewportLayout
{
    /** {@inheritDoc} */
    public void layoutContainer(Container parent)
    {
        JViewport vp = (JViewport) parent;
        Component view = vp.getView();

        if (view == null)
        {
            return;
        }

        Point viewPosition = vp.getViewPosition();
        Dimension viewPrefSize = view.getPreferredSize();
        Dimension vpSize = vp.getSize();
        Dimension viewSize = new Dimension(viewPrefSize);

        if ((viewPosition.x == 0) && (vpSize.width > viewPrefSize.width))
        {
            viewSize.width = vpSize.width;
        }

        if ((viewPosition.y == 0) && (vpSize.height > viewPrefSize.height))
        {
            viewSize.height = vpSize.height;
        }

        if (!viewSize.equals(viewPrefSize))
        {
            vp.setViewSize(viewSize);
        }
    }
}
