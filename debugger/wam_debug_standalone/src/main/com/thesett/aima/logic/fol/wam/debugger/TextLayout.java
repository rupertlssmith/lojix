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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TextLayout implements LayoutManager, ResizeDelta
{
    public static final String CENTER = "center";
    public static final String STATUS_BAR = "status";
    public static final String CONSOLE = "console";

    private final Map<String, Component> componentMap = new HashMap<String, Component>();
    private final Map<Component, String> reverseMap = new HashMap<Component, String>();
    private int consoleHeight = 80;

    public void addLayoutComponent(String name, Component comp)
    {
        componentMap.put(name, comp);
        reverseMap.put(comp, name);
    }

    public void removeLayoutComponent(Component comp)
    {
        String name = reverseMap.remove(comp);

        if (name != null)
        {
            componentMap.remove(name);
        }
    }

    public Dimension preferredLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();

        return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
    }

    public Dimension minimumLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();

        return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
    }

    public void layoutContainer(Container parent)
    {
        Insets insets = parent.getInsets();
        int maxWidth = parent.getWidth() - (insets.left + insets.right);
        int maxHeight = parent.getHeight() - (insets.top + insets.bottom);

        int statusBarHeight = 20;
        int centerHeight = maxHeight - (consoleHeight + statusBarHeight);

        int centerTop = 0;
        int statusBarTop = centerHeight + 1;
        int consoleTop = statusBarTop + statusBarHeight + 1;

        for (Component component : parent.getComponents())
        {
            String type = reverseMap.get(component);

            int top = 0;
            int height = 0;

            if (CENTER.equals(type))
            {
                top = centerTop;
                height = centerHeight;
            }
            else if (STATUS_BAR.equals(type))
            {
                top = statusBarTop;
                height = statusBarHeight;
            }
            else if (CONSOLE.equals(type))
            {
                top = consoleTop;
                height = consoleHeight;
            }

            component.setBounds(0, top, maxWidth, height);
        }
    }

    /** {@inheritDoc} */
    public void deltaResizeTop(int delta)
    {
        consoleHeight += delta;
    }
}
