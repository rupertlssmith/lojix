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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;

import com.thesett.aima.logic.fol.wam.debugger.controller.ColorDelta;

/**
 * Fader animates color fades by feeding color changes to a {@link ColorDelta} that is under the control of a timer. A
 * Fader is setup up to fade between two colors, and can then be invoked against a target to apply a timed fade.
 *
 * <p/>Fade also supports the concepts of 'grouping' fades by name. If the group name of two fade requests match, the
 * second request will stop the first one, and re-use its timer. A group name not matching any currently running fade,
 * will run under its own timer. This scheme allows the Fader to support multiple fades running in parallel or to
 * restrict fades within a group to run only one at a time.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class Fader implements ActionListener
{
    /** The color to start at. */
    private final Color startColor;

    /** The color to finish at. */
    private final Color endColor;

    /** The internal fade states by group name. */
    private Map<String, FadeState> timers = new HashMap<String, FadeState>();

    /**
     * Builds a color fader between two colors.
     *
     * @param startColor The color to start at.
     * @param endColor   The color to finish at.
     */
    public Fader(Color startColor, Color endColor)
    {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    /** {@inheritDoc} */
    public void actionPerformed(ActionEvent e)
    {
        String groupName = e.getActionCommand();
        FadeState fadeState = timers.get(groupName);

        if (fadeState.interpolator.hasNext())
        {
            Color color = fadeState.interpolator.next();

            fadeState.target.changeColor(color);
            fadeState.timer.restart();
        }
        else
        {
            timers.remove(groupName);
        }
    }

    /**
     * Requests a color fade against the specified target, under a group name.
     *
     * @param target    The color delta target.
     * @param groupName The group name for the fade. See class level comment for {@link Fader} for an explanation of how
     *                  the group name is used.
     */
    public void doFade(ColorDelta target, String groupName)
    {
        FadeState fadeState = timers.get(groupName);

        // Set up the color interpolator.
        Iterator<Color> interpolator = new ColorInterpolator(startColor, endColor, 8).iterator();

        if (fadeState == null)
        {
            // Create a new fade state for the target group, and a timer to run it.
            Timer timer = new Timer(10, this);
            timer.setInitialDelay(100);

            fadeState = new FadeState(timer, target, interpolator);
            timers.put(groupName, fadeState);
        }
        else
        {
            // Kill any previous fade and replace the target with the new one.
            fadeState.timer.stop();
            fadeState.target = target;
            fadeState.interpolator = interpolator;
        }

        // Kick off the fade timer.
        fadeState.timer.setActionCommand(groupName);
        fadeState.timer.start();
    }

    /**
     * Holds the current fade state for a fade group.
     */
    private static class FadeState
    {
        Timer timer;
        ColorDelta target;
        Iterator<Color> interpolator;

        /**
         * Creates a fade state.
         *
         * @param timer        The timer.
         * @param target       The color delta target.
         * @param interpolator The color interpolator.
         */
        private FadeState(Timer timer, ColorDelta target, Iterator<Color> interpolator)
        {
            this.timer = timer;
            this.target = target;
            this.interpolator = interpolator;
        }
    }
}
