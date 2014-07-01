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
package com.thesett.aima.logic.fol.wam.debugger.uifactory;

/**
 * CScheme defines an abstract color scheme with a limited palette.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Define a color scheme.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface ColorScheme<C>
{
    /**
     * Provides the main text color.
     *
     * @return The main text color.
     */
    C getMainText();

    /**
     * Provides the disabled text color.
     *
     * @return The disabled text color.
     */
    C getDisabledText();

    /**
     * Provides a highlight color.
     *
     * @return A highlight color.
     */
    C getHighlight1();

    /**
     * Provides a highlight color.
     *
     * @return A highlight color.
     */
    C getHighlight2();

    /**
     * Provides a highlight color.
     *
     * @return A highlight color.
     */
    C getHighlight3();

    /**
     * Provides a highlight color.
     *
     * @return A highlight color.
     */
    C getHighlight4();

    /**
     * Provides a background color for active screen areas.
     *
     * @return A background color for active screen areas.
     */
    C getUserWorkingBackground();
}
