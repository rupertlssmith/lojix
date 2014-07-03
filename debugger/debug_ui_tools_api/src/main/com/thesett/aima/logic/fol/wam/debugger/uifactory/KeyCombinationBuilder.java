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
 * KeyCombinationBuilder is a builder factory for 'key combinations' representing a key with modifiers used as a
 * short-cut key combination.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td> Build a key combination with modifiers. </td></tr>
 * </table></pre>
 *
 * @param  <K> The type of key combination representations that this factory produces.
 *
 * @author Rupert Smith
 */
public interface KeyCombinationBuilder<K>
{
    /**
     * Adds a Shift modifier to the key combination.
     *
     * @return This factory with the Shift combination added.
     */
    KeyCombinationBuilder<K> withShift();

    /**
     * Adds a Ctrl modifier to the key combination.
     *
     * @return This factory with the Ctrl combination added.
     */
    KeyCombinationBuilder<K> withCtrl();

    /**
     * Adds a Meta modifier to the key combination.
     *
     * @return This factory with the Meta combination added.
     */
    KeyCombinationBuilder<K> withMeta();

    /**
     * Adds a Alt modifier to the key combination.
     *
     * @return This factory with the Alt combination added.
     */
    KeyCombinationBuilder<K> withAlt();

    /**
     * Adds a AltGr modifier to the key combination.
     *
     * @return This factory with the AltGr combination added.
     */
    KeyCombinationBuilder<K> withAltGr();

    /**
     * Creates a key combination for the specified key.
     *
     * @param  key They key to create a combination for.
     *
     * @return The key combination for the key.
     */
    K withKey(String key);
}
