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
package com.thesett.aima.state.impl;

import com.thesett.aima.state.TestBean;

/**
 * Extends MapBackedState with initialized properties ready for testing with the tests in
 * {@link com.thesett.aima.state.StateTestBase}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TestMapBackedState extends MapBackedState
{
    /** Initialize all the test property values that are expected by the StateTestBase tests. */
    TestMapBackedState()
    {
        setProperty("testBoolean", TestBean.TEST_BOOLEAN);
        setProperty("testCharacter", TestBean.TEST_CHARACTER);
        setProperty("testByte", TestBean.TEST_BYTE);
        setProperty("testShort", TestBean.TEST_SHORT);
        setProperty("testInteger", TestBean.TEST_INTEGER);
        setProperty("testLong", TestBean.TEST_LONG);
        setProperty("testFloat", TestBean.TEST_FLOAT);
        setProperty("testDouble", TestBean.TEST_DOUBLE);
        setProperty("testString", TestBean.TEST_STRING);
        setProperty("testObject", TestBean.TEST_OBJECT);
    }
}
