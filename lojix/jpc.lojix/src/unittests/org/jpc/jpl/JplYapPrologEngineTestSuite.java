package org.jpc.jpl;

import org.jpc.util.config.EngineConfigurationManager;
import org.junit.BeforeClass;

public class JplYapPrologEngineTestSuite extends JplPrologEngineTestSuite {
	@BeforeClass
	public static void setUp() {
		EngineConfigurationManager engineConfigurationManager = EngineConfigurationManager.createFromFile("jpc_yap.settings");
		EngineConfigurationManager.setDefault(engineConfigurationManager);
	}
}
