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
package org.jpc.jpl;

import org.jpc.engine.prolog.PrologEngineTestSuite;
import org.jpc.examples.PrologExamplesTestSuite;
import org.jpc.salt.jpl.JplTransformationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * All the non-Logtalk tests the logic engine should pass
 *
 * @author sergioc
 */
@RunWith(Suite.class)
@SuiteClasses({ PrologEngineTestSuite.class, PrologExamplesTestSuite.class, JplTransformationTest.class })
public abstract class JplPrologEngineTestSuite
{
}
