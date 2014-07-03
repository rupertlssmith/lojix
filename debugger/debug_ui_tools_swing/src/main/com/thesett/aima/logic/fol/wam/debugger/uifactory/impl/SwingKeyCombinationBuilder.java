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
package com.thesett.aima.logic.fol.wam.debugger.uifactory.impl;

import java.awt.event.InputEvent;

import javax.swing.*;

import com.thesett.aima.logic.fol.wam.debugger.uifactory.KeyCombinationBuilder;

/**
 * SwingKeyCombinationBuilder implements a builder for Swing KeyStrokes.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Build a key combination with modifiers. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SwingKeyCombinationBuilder implements KeyCombinationBuilder<KeyStroke>
{
    /** The modifiers. */
    private int modifiers;

    /** {@inheritDoc} */
    public KeyCombinationBuilder<KeyStroke> withShift()
    {
        modifiers |= InputEvent.SHIFT_MASK;

        return this;
    }

    /** {@inheritDoc} */
    public KeyCombinationBuilder<KeyStroke> withCtrl()
    {
        modifiers |= InputEvent.CTRL_MASK;

        return this;
    }

    /** {@inheritDoc} */
    public KeyCombinationBuilder<KeyStroke> withMeta()
    {
        modifiers |= InputEvent.META_MASK;

        return this;
    }

    /** {@inheritDoc} */
    public KeyCombinationBuilder<KeyStroke> withAlt()
    {
        modifiers |= InputEvent.ALT_MASK;

        return this;
    }

    /** {@inheritDoc} */
    public KeyCombinationBuilder<KeyStroke> withAltGr()
    {
        modifiers |= InputEvent.ALT_GRAPH_MASK;

        return this;
    }

    /** {@inheritDoc} */
    public KeyStroke withKey(String key)
    {
        // Extract the modifiers as a specification string.
        String keyString = modifiersToString(modifiers);

        // Reset the modifiers so the builder can be used again.
        modifiers = 0;

        return KeyStroke.getKeyStroke(keyString + key);
    }

    /**
     * Converts the modifiers to a specification string for KeyStroke.
     *
     * @param  modifiers The modifiers.
     *
     * @return A fragment of the key specification string used by KeyStroke.
     */
    private String modifiersToString(int modifiers)
    {
        String result = "";

        if ((modifiers & InputEvent.SHIFT_MASK) != 0)
        {
            result += "shift ";
        }

        if ((modifiers & InputEvent.CTRL_MASK) != 0)
        {
            result += "ctrl ";
        }

        if ((modifiers & InputEvent.META_MASK) != 0)
        {
            result += "meta ";
        }

        if ((modifiers & InputEvent.ALT_MASK) != 0)
        {
            result += "alt ";
        }

        if ((modifiers & InputEvent.ALT_GRAPH_MASK) != 0)
        {
            result += "altGraph ";
        }

        return result;
    }
}
