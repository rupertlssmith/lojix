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
package com.thesett.aima.logic.fol.jpc.engine;

import org.jpc.engine.prolog.PrologEngineTestSuite;
import org.jpc.examples.PrologExamplesTestSuite;
import org.jpc.util.config.EngineConfigurationManager;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thesett.aima.logic.fol.jpc.salt.LojixTransformationTest;

@RunWith(Suite.class)
@SuiteClasses({ PrologEngineTestSuite.class, PrologExamplesTestSuite.class, LojixTransformationTest.class })
public class LojixPrologEngineTestSuite
{
    @BeforeClass
    public static void setUp()
    {
        EngineConfigurationManager engineConfigurationManager =
            EngineConfigurationManager.createFromFile("jpc_swi.settings");
        EngineConfigurationManager.setDefault(engineConfigurationManager);
    }
}
