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

/**
 * MotionDelta provide an interface through which a controller can communicate a motion event, as a relative delta. This
 * could be used to move or resize some on-screen element for example.
 *
 * <p/>The delta is usually expressed in pixels, but does not have to be as the units are not specified.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th></tr>
 * <tr><td> Accept a relative motion. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface MotionDelta
{
    /**
     * Applies an X-axis motion delta.
     *
     * @param delta The delta to apply.
     */
    void deltaX(int delta);

    /**
     * Applies a Y-axis motion delta.
     *
     * @param delta The delta to apply.
     */
    void deltaY(int delta);
}
