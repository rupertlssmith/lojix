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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DebuggerLayout is a custom LayoutManager for a set of debugger window panels. It divides the window up in a fairly
 * standard way for a GUI type tool. There are left and right tool panels, a status bar, a console panel at the bottom,
 * and a main area in the window centre.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Add/remove components to the layout in the available positions. </td></tr>
 * <tr><td> Calculate layout dimensions. </td></tr>
 * <tr><td> Provide motion receivers for the resizable console and left and right panels. </td>
 *     <td> {@link MotionDelta} </td></tr>
 * <tr><td> Layout the components to the positioning defined. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DebuggerLayout implements LayoutManager
{
    /** Label identifying the center position. */
    public static final String CENTER = "center";

    /** Label identifying the status bar position. */
    public static final String STATUS_BAR = "status";

    /** Label identifying the left grip-bar position. */
    public static final String LEFT_VERTICAL_BAR = "left_vertical_bar";

    /** Label identifying the right grip-bar position. */
    public static final String RIGHT_VERTICAL_BAR = "right_vertical_bar";

    /** Label identifying the right position. */
    public static final String RIGHT_PANE = "right_pane";

    /** Label identifying the left position. */
    public static final String LEFT_PANE = "left_pane";

    /** Label identifying the console position. */
    public static final String CONSOLE = "console";

    /** Defines the default console height. */
    public static final int DEFAULT_CONSOLE_HEIGHT = 90;

    /** Defines the default left or right panel width. */
    public static final int DEFAULT_PANE_WIDTH = 140;

    /** Defines the default status bar height. */
    public static final int DEFAULT_STATUS_BAR_HEIGHT = 20;

    /** Defines the default vertical grip-bar width. */
    public static final int DEFAULT_VBAR_WIDTH = 3;

    /** Defines the default scroll bar width in pixels. */
    public static final int DEFAULT_SCROLL_BAR_SIZE = 25;

    /** Map of all components in the layout by position label. */
    private final Map<String, Component> componentMap = new HashMap<String, Component>();

    /** Map from components to position label, in order to identify position when presented with a component. */
    private final Map<Component, String> reverseMap = new HashMap<Component, String>();

    /** Flags indicating which components in the layout have vertical scroll bars. */
    private final Set<String> verticalScrollBars = new HashSet<String>();

    /** Flags indicating which components in the layout have horizontal scroll bars. */
    private final Set<String> horizontalScrollBars = new HashSet<String>();

    /** The current console height. */
    private int consoleHeight = DEFAULT_CONSOLE_HEIGHT;

    /** The current left panel width. */
    private int leftPaneWidth = DEFAULT_PANE_WIDTH;

    /** The current right panel width. */
    private int rightPaneWidth = DEFAULT_PANE_WIDTH;

    /** Indicates console is in the layout. */
    private boolean hasConsole;

    /** Indicates status bar is in the layout. */
    private boolean hasStatusBar;

    /** Indicates left grip-bar is in the layout. */
    private boolean hasLeftBar;

    /** Indicates left panel is in the layout. */
    private boolean hasLeftPane;

    /** Indicates right grip-bar is in the layout. */
    private boolean hasRightBar;

    /** Indicates right panel is in the layout. */
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

        int centerHeight =
            maxHeight - ((hasConsole ? consoleHeight : 0) + (hasStatusBar ? DEFAULT_STATUS_BAR_HEIGHT : 0));
        int statusBarTop = centerHeight;
        int consoleTop = statusBarTop + DEFAULT_STATUS_BAR_HEIGHT;

        int centerLeft = (hasLeftPane ? leftPaneWidth : 0) + (hasLeftBar ? DEFAULT_VBAR_WIDTH : 0);
        int centerRight = maxWidth - (hasRightPane ? rightPaneWidth : 0) - (hasRightBar ? DEFAULT_VBAR_WIDTH : 0);
        int centerWidth = centerRight - centerLeft;

        int leftBarRight = centerLeft;
        int leftBarLeft = leftBarRight - DEFAULT_VBAR_WIDTH;
        int leftPaneRight = leftBarLeft;

        int rightBarLeft = centerRight;
        int rightPaneLeft = rightBarLeft + DEFAULT_VBAR_WIDTH;

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
