package org.jpc.engine.jpl;

import org.jpc.util.JpcPreferences;
import org.jpc.util.engine.supported.Swi;

public class JplSwiDriver extends JplDriver {
	
	public static final String JPLPATH_SWI_ENV_VAR = "JPLPATH_SWI"; 
	
	public static void configure() {
		new JplSwiDriver().readyOrThrow();
	}
	
	public JplSwiDriver() {
		super(new Swi(), JPLPATH_SWI_ENV_VAR, new JpcPreferences());
	}

}
