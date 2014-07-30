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

import com.thesett.common.util.ReflectionUtils;

/**
 * ComponentFactoryBuilder creates instances of the UI component factories.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Provide component factory implementations. </td></tr>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ComponentFactoryBuilder
{
    /** The name of the Swing factory. */
    public static final String SWING_FACTORY =
        "com.thesett.aima.logic.fol.wam.debugger.uifactory.impl.SwingComponentFactory";

    /**
     * Creates an instance of the named component factory.
     *
     * @param  className The name of the component factory class to instantiate.
     *
     * @return An instance of the named component factory.
     */
    public static ComponentFactory createComponentFactory(String className)
    {
        return (ComponentFactory) ReflectionUtils.newInstance(ReflectionUtils.forName(className));
    }
}
