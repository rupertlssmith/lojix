package org.jpc.jpl;

import org.jpc.util.config.EngineConfigurationManager;
import org.junit.BeforeClass;

public class JplSwiLogtalkEngineTestSuite extends JplLogtalkEngineTestSuite {
	@BeforeClass
	public static void setUp() {
		EngineConfigurationManager engineConfigurationManager = EngineConfigurationManager.createFromFile("jpc_swi_logtalk.settings");
		EngineConfigurationManager.setDefault(engineConfigurationManager);
	}
}
