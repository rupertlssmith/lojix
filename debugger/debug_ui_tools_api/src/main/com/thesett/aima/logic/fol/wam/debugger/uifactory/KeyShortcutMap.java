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
 * KeyShortcutMap defines a set of keyboard shortcuts for common actions.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Map keyboard shortcuts with actions. </td></tr>
 * </table></pre>
 *
 * @param  <K> The type of key combination representations used.
 *
 * @author Rupert Smith
 */
public interface KeyShortcutMap<K>
{
    /**
     * Defines the step shortcut.
     *
     * @return The key combination for the step action.
     */
    K getStep();

    /**
     * Defines the step over shortcut.
     *
     * @return The key combination for the step over action.
     */
    K getStepOver();

    /**
     * Defines the resume shortcut.
     *
     * @return The key combination for the resume action.
     */
    K getResume();
}
