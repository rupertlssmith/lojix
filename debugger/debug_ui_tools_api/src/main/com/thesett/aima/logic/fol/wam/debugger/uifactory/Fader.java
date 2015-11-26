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
package com.thesett.aima.logic.fol.wam.debugger.uifactory;

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
 * <tr><td> Apply a color fade between two colors. </td><td> {#link ColorDelta} </td></tr>
 * </table></pre>
 *
 * @param  <C> The type of colors used.
 *
 * @author Rupert Smith
 */
public interface Fader<C>
{
    /**
     * Requests a color fade against the specified target, under a group name.
     *
     * @param target    The color delta target.
     * @param groupName The group name for the fade. See class level comment for {@link Fader} for an explanation of how
     *                  the group name is used.
     */
    void doFade(ColorDelta target, String groupName);
}
