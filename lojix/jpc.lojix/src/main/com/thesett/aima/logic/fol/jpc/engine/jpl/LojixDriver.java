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
package com.thesett.aima.logic.fol.jpc.engine.jpl;

import org.jpc.engine.prolog.driver.AbstractPrologEngineDriver;
import org.jpc.engine.prolog.driver.PrologEngineFactory;
import org.jpc.util.JpcPreferences;
import org.jpc.util.engine.supported.EngineDescription;

public abstract class LojixDriver extends AbstractPrologEngineDriver<LojixEngine>
{
    protected LojixDriver(EngineDescription engineDescription)
    {
        super(engineDescription);
    }

    protected LojixDriver(EngineDescription engineDescription, JpcPreferences preferences)
    {
        super(engineDescription, preferences);
    }

    public String getLibraryName()
    {
        return null;
    }

    protected PrologEngineFactory<LojixEngine> defaultBasicFactory()
    {
        return null;
    }
}
