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

import com.thesett.aima.logic.fol.wam.debugger.uifactory.ColorFactory;
import com.thesett.aima.logic.fol.wam.debugger.uifactory.ColorSchemeBase;

/**
 * Provides a default white on black color scheme.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide dark colors.
 * </table></pre>
 *
 * @param  <C> The color implementation type.
 *
 * @author Rupert Smith
 */
public class DefaultColorScheme<C> extends ColorSchemeBase<C>
{
    /**
     * Creates a default white on balck color scheme.
     *
     * @param colorFactory The color factory to use.
     */
    public DefaultColorScheme(ColorFactory<C> colorFactory)
    {
        super(colorFactory);
    }

    /** {@inheritDoc} */
    public C getMainText()
    {
        return color(BLACK);
    }

    /** {@inheritDoc} */
    public C getDisabledText()
    {
        return color(LIGHT_GRAY);
    }

    /** {@inheritDoc} */
    public C getHighlight1()
    {
        return color(BLACK);
    }

    /** {@inheritDoc} */
    public C getHighlight2()
    {
        return color(BLACK);
    }

    /** {@inheritDoc} */
    public C getHighlight3()
    {
        return color(BLACK);
    }

    /** {@inheritDoc} */
    public C getHighlight4()
    {
        return color(BLACK);
    }

    /** {@inheritDoc} */
    public C getBackground()
    {
        return color(WHITE);
    }

    /** {@inheritDoc} */
    public C getSelectionBackground()
    {
        return color(DARK_GRAY);
    }

    /** {@inheritDoc} */
    public C getLowLight()
    {
        return color(LIGHT_GRAY);
    }
}
