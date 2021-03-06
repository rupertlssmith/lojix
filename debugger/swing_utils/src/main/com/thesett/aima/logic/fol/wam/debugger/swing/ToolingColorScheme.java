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

import java.awt.Color;

/**
 * ToolingColorScheme describes a set of colors used to color parts of the UI that implement Swing display widgets, such
 * as borders, scroll bars, button bars and menus, in a consistent manner.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Provide tooling colors for UI widgets. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface ToolingColorScheme
{
    /**
     * Provides a background color for tooling screen areas.
     *
     * @return A background color for tooling screen areas.
     */
    Color getToolingBackground();

    /**
     * Provides a background color for tooling active screen areas.
     *
     * @return A background color for tooling active screen areas.
     */
    Color getToolingActiveBackground();
}
