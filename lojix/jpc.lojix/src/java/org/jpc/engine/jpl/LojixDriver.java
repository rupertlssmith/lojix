package org.jpc.engine.jpl;

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

    protected PrologEngineFactory<LojixEngine> defaultBasicFactory()
    {
        return null;
    }

    public String getLibraryName()
    {
        return null;
    }
}

