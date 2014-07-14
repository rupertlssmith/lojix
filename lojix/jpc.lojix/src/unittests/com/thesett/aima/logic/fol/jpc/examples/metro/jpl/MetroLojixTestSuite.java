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
package com.thesett.aima.logic.fol.jpc.examples.metro.jpl;

import org.jpc.examples.metro.MetroTestSuite;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suit exists only for testing individually the metro example for the jpl implementation. It will be deleted
 * soon...
 *
 * @author sergioc
 */
@RunWith(Suite.class)
@SuiteClasses({ MetroTestSuite.class })
public class MetroLojixTestSuite
{
    @BeforeClass
    public static void oneTimeSetUp()
    {
        //ThreadLocalPrologEngine.setPrologEngine(new DefaultJplYapConfiguration().getEngine());
        //ThreadLocalPrologEngine.setPrologEngine(new DefaultJplSwiConfiguration().getEngine());
    }
}
