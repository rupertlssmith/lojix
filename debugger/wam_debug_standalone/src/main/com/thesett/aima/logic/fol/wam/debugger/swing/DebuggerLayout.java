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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;

/**
 * DebuggerLayout is a custom LayoutManager for a set of debugger window panels. It divides the window up in a fairly
 * standard way for a GUI type tool. There are left and right tool panels, a status bar, a console panel at the bottom,
 * and a main area in the window centre.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DebuggerLayout implements LayoutManager
{
    public static final String CENTER = "center";
    public static final String STATUS_BAR = "status";
    public static final String LEFT_VERTICAL_BAR = "left_vertical_bar";
    public static final String RIGHT_VERTICAL_BAR = "right_vertical_bar";
    public static final String RIGHT_PANE = "right_pane";
    public static final String LEFT_PANE = "left_pane";
    public static final String CONSOLE = "console";

    public static final int DEFAULT_CONSOLE_HEIGHT = 80;
    public static final int DEFAULT_PANE_WIDTH = 80;
    public static final int DEFAULT_STATUS_BAR_HEIGHT = 20;
    public static final int DEFAULT_VBAR_WIDTH = 8;

    private final Map<String, Component> componentMap = new HashMap<String, Component>();
    private final Map<Component, String> reverseMap = new HashMap<Component, String>();

    private int consoleHeight = DEFAULT_CONSOLE_HEIGHT;
    private int leftPaneWidth = DEFAULT_PANE_WIDTH;
    private int rightPaneWidth = DEFAULT_PANE_WIDTH;

    private boolean hasConsole;
    private boolean hasStatusBar;
    private boolean hasLeftBar;
    private boolean hasLeftPane;
    private boolean hasRightBar;
    private boolean hasRightPane;

    /** {@inheritDoc} */
    public void addLayoutComponent(String name, Component comp)
    {
        componentMap.put(name, comp);
        reverseMap.put(comp, name);
    }

    /** {@inheritDoc} */
    public void removeLayoutComponent(Component comp)
    {
        String name = reverseMap.remove(comp);

        if (name != null)
        {
            componentMap.remove(name);
        }
    }

    /** {@inheritDoc} */
    public Dimension preferredLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();

        return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
    }

    /** {@inheritDoc} */
    public Dimension minimumLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();

        return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
    }

    /**
     * Provides a MotionDelta to capture console height resizing through.
     *
     * @return A MotionDelta to capture console height resizing through.
     */
    public MotionDelta getConsoleHeightResizer()
    {
        return new ConsoleHeightResizer();
    }

    /**
     * Provides a MotionDelta to capture left pane width resizing through.
     *
     * @return A MotionDelta to capture left pane width resizing through.
     */
    public MotionDelta getLeftPaneWidthResizer()
    {
        return new LeftPaneWidthResizer();
    }

    /**
     * Provides a MotionDelta to capture right pane width resizing through.
     *
     * @return A MotionDelta to capture right pane width resizing through.
     */
    public MotionDelta getRightPaneWidthResizer()
    {
        return new RightPaneWidthResizer();
    }

    /** {@inheritDoc} */
    public void layoutContainer(Container parent)
    {
        // Get the available area to layout within.
        Insets insets = parent.getInsets();
        int maxWidth = parent.getWidth() - (insets.left + insets.right);
        int maxHeight = parent.getHeight() - (insets.top + insets.bottom);

        // Check which optional components are present.
        updatePresentComponentFlags();

        int centerHeight = maxHeight - (addConsole(consoleHeight) + addStatusBar(DEFAULT_STATUS_BAR_HEIGHT));
        int statusBarTop = centerHeight + 1;
        int consoleTop = statusBarTop + DEFAULT_STATUS_BAR_HEIGHT + 1;

        int centerLeft = addLeftPane(leftPaneWidth) + addLeftBar(DEFAULT_VBAR_WIDTH);
        int centerRight = maxWidth - addRightPane(rightPaneWidth) - addRightBar(DEFAULT_VBAR_WIDTH);
        int centerWidth = centerRight - centerLeft;

        int leftBarRight = centerLeft - 1;
        int leftBarLeft = leftBarRight - DEFAULT_VBAR_WIDTH;
        int leftPaneRight = leftBarLeft - 1;

        int rightBarLeft = centerRight + 1;
        int rightPaneLeft = rightBarLeft + DEFAULT_VBAR_WIDTH + 1;

        for (Component component : parent.getComponents())
        {
            String type = reverseMap.get(component);

            int left = 0;
            int top = 0;
            int width = maxWidth;
            int height = maxHeight;

            if (CENTER.equals(type))
            {
                left = centerLeft;
                width = centerWidth;
                height = centerHeight;
            }
            else if (STATUS_BAR.equals(type))
            {
                top = statusBarTop;
                height = DEFAULT_STATUS_BAR_HEIGHT;
            }
            else if (CONSOLE.equals(type))
            {
                top = consoleTop;
                height = consoleHeight;
            }
            else if (LEFT_PANE.equals(type))
            {
                width = leftPaneRight;
                height = centerHeight;
            }
            else if (LEFT_VERTICAL_BAR.equals(type))
            {
                left = leftBarLeft;
                width = DEFAULT_VBAR_WIDTH;
                height = centerHeight;
            }
            else if (RIGHT_VERTICAL_BAR.equals(type))
            {
                left = rightBarLeft;
                width = DEFAULT_VBAR_WIDTH;
                height = centerHeight;
            }
            else if (RIGHT_PANE.equals(type))
            {
                left = rightPaneLeft;
                width = rightPaneWidth;
                height = centerHeight;
            }

            component.setBounds(left, top, width, height);
        }
    }

    private int addRightBar(int size)
    {
        return hasRightBar ? size : 0;
    }

    private int addRightPane(int size)
    {
        return hasRightPane ? size : 0;
    }

    private int addLeftBar(int size)
    {
        return hasLeftBar ? size : 0;
    }

    private int addLeftPane(int size)
    {
        return hasLeftPane ? size : 0;
    }

    private int addConsole(int size)
    {
        return hasConsole ? size : 0;
    }

    private int addStatusBar(int size)
    {
        return hasStatusBar ? size : 0;
    }

    /** Keeps the set of flags indicating which window components are present, up-to-date. */
    private void updatePresentComponentFlags()
    {
        hasConsole = componentMap.containsKey(CONSOLE);
        hasStatusBar = componentMap.containsKey(STATUS_BAR);
        hasLeftBar = componentMap.containsKey(LEFT_VERTICAL_BAR);
        hasLeftPane = componentMap.containsKey(LEFT_PANE);
        hasRightBar = componentMap.containsKey(RIGHT_VERTICAL_BAR);
        hasRightPane = componentMap.containsKey(RIGHT_PANE);
    }

    /**
     * Provides a motion delta to capture resize events for the console height.
     */
    private class ConsoleHeightResizer implements MotionDelta
    {
        /** {@inheritDoc} */
        public void deltaX(int delta)
        {
        }

        /** {@inheritDoc} */
        public void deltaY(int delta)
        {
            consoleHeight += delta;
        }
    }

    /**
     * Provides a motion delta to capture resize events for the left pane width.
     */
    private class LeftPaneWidthResizer implements MotionDelta
    {
        /** {@inheritDoc} */
        public void deltaX(int delta)
        {
            leftPaneWidth -= delta;
        }

        /** {@inheritDoc} */
        public void deltaY(int delta)
        {
        }
    }

    /**
     * Provides a motion delta to capture resize events for the right pane width.
     */
    private class RightPaneWidthResizer implements MotionDelta
    {
        /** {@inheritDoc} */
        public void deltaX(int delta)
        {
            rightPaneWidth += delta;
        }

        /** {@inheritDoc} */
        public void deltaY(int delta)
        {
        }
    }
}
