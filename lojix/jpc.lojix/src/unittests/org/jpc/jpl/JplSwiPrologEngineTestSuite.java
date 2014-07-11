package org.jpc.jpl;

import org.jpc.util.config.EngineConfigurationManager;
import org.junit.BeforeClass;

public class JplSwiPrologEngineTestSuite extends JplPrologEngineTestSuite {
	@BeforeClass
	public static void setUp() {
		EngineConfigurationManager engineConfigurationManager = EngineConfigurationManager.createFromFile("jpc_swi.settings");
		EngineConfigurationManager.setDefault(engineConfigurationManager);
	}
}
