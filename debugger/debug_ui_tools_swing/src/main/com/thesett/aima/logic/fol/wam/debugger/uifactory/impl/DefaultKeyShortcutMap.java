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
package com.thesett.aima.logic.fol.wam.debugger.uifactory.impl;

import com.thesett.aima.logic.fol.wam.debugger.uifactory.KeyCombinationBuilder;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.KeyShortcutMap;

/**
 * Provides a default keyboard shortcut map.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide default key bindings.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DefaultKeyShortcutMap<K> implements KeyShortcutMap<K>
{
    /** The key combination factory. */
    protected final KeyCombinationBuilder<K> builder;

    /**
     * Creates a default key binding.
     *
     * @param builder The keyboard combination factory.
     */
    public DefaultKeyShortcutMap(KeyCombinationBuilder<K> builder)
    {
        this.builder = builder;
    }

    /** {@inheritDoc} */
    public K getStep()
    {
        return builder.withKey("F7");
    }

    /** {@inheritDoc} */
    public K getStepOver()
    {
        return builder.withKey("F8");
    }

    /** {@inheritDoc} */
    public K getResume()
    {
        return builder.withKey("F9");
    }
}
