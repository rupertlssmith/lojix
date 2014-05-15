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
package com.thesett.aima.logic.fol.wam.debugger.controller;

import java.awt.Color;

/**
 * MotionDelta provide an interface through which a controller can communicate a color change event. This could be a one
 * off change, or it could be used to animate a fade for example.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities </th></tr>
 * <tr><td> Accept a color change. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface ColorDelta
{
    /**
     * Accepts an updated color.
     *
     * @param color The new color.
     */
    void changeColor(Color color);
}
