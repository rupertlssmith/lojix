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

import com.thesett.aima.logic.fol.wam.debugger.text.EnhancedTextGrid;

/**
 * ComponentFactory builds UI components that are compatible with other elements of the UI.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Create the main window. </td></tr>
 * <tr><td> Create a grip-able panel for adjusting screen layout. </td></tr>
 * <tr><td> Create an editor panel. </td></tr>
 * <tr><td> Create a black panel for filling in unused areas of the screen. </td></tr>
 * </table></pre>
 *
 * @param  <Comp> The type of ui components this factory produces.
 * @param  <Col>  The type of colors this factory uses.
 * @param  <K>    The type key combinations this factory uses for shortcuts.
 *
 * @author Rupert Smith
 */
public interface ComponentFactory<Comp, Col, K>
{
    /**
     * Creates the main debugger window.
     *
     * @return The main debugger window.
     */
    MainWindow<Comp, K> createMainWindow();

    /**
     * Creates an empty text grid model, that is compatible with this UI factory.
     *
     * @return An empty text grid model.
     */
    EnhancedTextGrid createTextGrid();

    /**
     * Creates an non-editable text panel.
     *
     * @param  model The underlying document data model.
     *
     * @return An editor panel.
     */
    Comp createTextGridPanel(EnhancedTextGrid model);

    /**
     * Creates a blank panel for filling empty space.
     *
     * @return A blank panel for filling empty space.
     */
    Comp createBlankPanel();

    /**
     * Establishes the color scheme used to render all factory UI components with.
     *
     * @param colorScheme The color scheme to use.
     */
    void setColorScheme(ColorScheme<Col> colorScheme);

    /**
     * Provides the current color scheme.
     *
     * @return The current color scheme.
     */
    ColorScheme<Col> getColorScheme();

    /**
     * Provides a color factory compatible with the components.
     *
     * @return The color factory.
     */
    ColorFactory<Col> getColorFactory();

    /**
     * Provides a key combination factory compatible with the components.
     *
     * @return The key combination factory.
     */
    KeyCombinationBuilder<K> getKeyCombinationBuilder();

    /**
     * Establishes the keyboard short cut map to use accross all UI components.
     *
     * @param shortcutMap The keyboard shortcut map to use.
     */
    void setKeyShortcutMap(KeyShortcutMap<K> shortcutMap);

    /**
     * Provides the current keyboard short cut map.
     *
     * @return The current keyboard short cut map.
     */
    KeyShortcutMap<K> getKeyShortcutMap();
}
