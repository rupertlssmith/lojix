/*
 * Copyright The Sett Ltd, 2005 to 2009.
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
package com.thesett.common.util;

import junit.framework.TestCase;
import com.thesett.common.util.StringUtils;

/**
 * StringUtilsTest verifies the string manipulation functions in the {@link StringUtils} helper class.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check that the to camel case conversion works as expected.
 * <tr><td>Check that the to camel case with first character in upper case conversion works as expected.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class StringUtilsTest extends TestCase
{
    /**
     * Creates a test with the specified name.
     *
     * @param name The name of the test to run.
     */
    public StringUtilsTest(String name)
    {
        super(name);
    }

    /** Check that the to camel case conversion works as expected. */
    public void testToCamelCase() throws Exception
    {
        String errorMessage = "Camel case version does not match its expected value for input: ";

        assertEquals(errorMessage + "test_string", "testString", StringUtils.toCamelCase("test_string"));
        assertEquals(errorMessage + "test__string", "testString", StringUtils.toCamelCase("test__string"));
        assertEquals(errorMessage + "another_test_string", "anotherTestString",
            StringUtils.toCamelCase("another_test_string"));
        assertEquals(errorMessage + "_test", "Test", StringUtils.toCamelCase("_test"));
        assertEquals(errorMessage + "test_", "test", StringUtils.toCamelCase("test_"));
    }

    /** Check that the to camel case with first character in upper case conversion works as expected. */
    public void testToCamelCaseUpper() throws Exception
    {
        String errorMessage = "Camel case version does not match its expected value for input: ";

        assertEquals(errorMessage + "test_string", "TestString", StringUtils.toCamelCaseUpper("test_string"));
        assertEquals(errorMessage + "test__string", "TestString", StringUtils.toCamelCaseUpper("test__string"));
        assertEquals(errorMessage + "another_test_string", "AnotherTestString",
            StringUtils.toCamelCaseUpper("another_test_string"));
        assertEquals(errorMessage + "_test", "Test", StringUtils.toCamelCaseUpper("_test"));
        assertEquals(errorMessage + "test_", "Test", StringUtils.toCamelCaseUpper("test_"));
    }
}
