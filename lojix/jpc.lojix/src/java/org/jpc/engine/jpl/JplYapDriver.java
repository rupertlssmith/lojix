package org.jpc.engine.jpl;

import org.jpc.util.JpcPreferences;
import org.jpc.util.engine.supported.Yap;



public class JplYapDriver extends JplDriver {
	
	public static final String JPLPATH_YAP_ENV_VAR = "JPLPATH_YAP";
	
	public static void configure() {
		new JplYapDriver().readyOrThrow();
	}
	
	public JplYapDriver() {
		super(new Yap(), JPLPATH_YAP_ENV_VAR, new JpcPreferences());
	}
	
}
