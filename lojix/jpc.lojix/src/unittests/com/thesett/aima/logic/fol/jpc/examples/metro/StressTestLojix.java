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
package com.thesett.aima.logic.fol.jpc.examples.metro;

import org.jpc.examples.metro.MetroExample;
import org.jpc.examples.metro.StressTest;
import org.jpc.util.config.EngineConfigurationManager;
import org.junit.BeforeClass;

public class StressTestLojix extends StressTest
{
    @BeforeClass
    public static void oneTimeSetUp()
    {
        EngineConfigurationManager engineConfigurationManager =
            EngineConfigurationManager.createFromFile("jpc_yap_logtalk.settings");
        EngineConfigurationManager.setDefault(engineConfigurationManager);
        MetroExample.loadAll();
    }
}
