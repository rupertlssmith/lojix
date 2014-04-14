/*
 * © Copyright Rupert Smith, 2005 to 2013.
 *
 * ALL RIGHTS RESERVED. Any unauthorized reproduction or use of this
 * material is prohibited. No part of this work may be reproduced or
 * transmitted in any form or by any means, electronic or mechanical,
 * including photocopying, recording, or by any information storage
 * and retrieval system without express written permission from the
 * author.
 */
package com.thesett.text.impl.model;

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
