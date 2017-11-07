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
package com.thesett.common.util;

import junit.framework.TestCase;

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

    public static final int CAMEL_CASE_LOWER = 1;
    public static final int CAMEL_CASE_UPPER = 2;
    public static final int SNAKE_CASE_LOWER = 3;
    public static final int SNAKE_CASE_UPPER = 4;
    public static final int KEBAB_CASE_LOWER = 5;
    public static final int KEBAB_CASE_UPPER = 6;

    public static final String[][] testCases = new String[][] {
            new String[] {"test_string", "testString", "TestString", "test_string", "Test_String", "test-string", "Test-String" },
            new String[] {"test__string", "testString", "TestString", "test_string", "Test_String", "test-string", "Test-String" },
            new String[] {"a_test_string", "aTestString", "ATestString", "a_test_string", "A_Test_String", "a-test-string", "A-Test-String" },
            new String[] {"_test", "test", "Test", "test", "Test", "test", "Test" },
            new String[] {"test_", "test", "Test", "test", "Test", "test", "Test" },
            new String[] {"testString", "testString", "TestString", "test_string", "Test_String", "test-string", "Test-String" },
            new String[] {"testString", "testString", "TestString", "test_string", "Test_String", "test-string", "Test-String" },
            new String[] {"test-string", "testString", "TestString", "test_string", "Test_String", "test-string", "Test-String" },
            new String[] {"test-string", "testString", "TestString", "test_string", "Test_String", "test-string", "Test-String" }
    };

    public void testSpaceCase()
    {
        assertEquals("", StringUtils.convertCase("", " ", false, false));
        assertEquals("word", StringUtils.convertCase("word", " ", false, false));
        assertEquals("word", StringUtils.convertCase(" word", " ", false, false));
        assertEquals("word", StringUtils.convertCase("-word", " ", false, false));
        assertEquals("word", StringUtils.convertCase("_word", " ", false, false));
        assertEquals("word", StringUtils.convertCase("--word", " ", false, false));
        assertEquals("1word", StringUtils.convertCase("_1word", " ", false, false));
        assertEquals("word", StringUtils.convertCase("word ", " ", false, false));
        assertEquals("word", StringUtils.convertCase("word-", " ", false, false));
        assertEquals("word", StringUtils.convertCase("word_", " ", false, false));
        assertEquals("word1 word2", StringUtils.convertCase("_word1_word2", " ", false, false));
        assertEquals("word1 word2", StringUtils.convertCase("word1Word2", " ", false, false));
        assertEquals("gdpuk", StringUtils.convertCase("GDPuk", " ", false, false));
        assertEquals("gdp uk", StringUtils.convertCase("GDP-uk", " ", false, false));
    }

    /** Check that the to camel case conversion works as expected. */
    public void testToCamelCaseLower() throws Exception
    {
        int resultIndex = CAMEL_CASE_LOWER;

        for (String[] testCase : testCases) {
            assertEquals(testCase[resultIndex], StringUtils.toCamelCaseLower(testCase[0]));
        }
    }

    /** Check that the to camel case with first character in upper case conversion works as expected. */
    public void testToCamelCaseUpper() throws Exception
    {
        int resultIndex = CAMEL_CASE_UPPER;

        for (String[] testCase : testCases) {
            assertEquals(testCase[resultIndex], StringUtils.toCamelCaseUpper(testCase[0]));
        }
    }

    /** Check that the to snake case conversion works as expected. */
    public void testToSnakeCaseLower() throws Exception
    {
        int resultIndex = SNAKE_CASE_LOWER;

        for (String[] testCase : testCases) {
            assertEquals(testCase[resultIndex], StringUtils.toSnakeCaseLower(testCase[0]));
        }
    }

    /** Check that the to snake case with first character in upper case conversion works as expected. */
    public void testToSnakeCaseUpper() throws Exception
    {
        int resultIndex = SNAKE_CASE_UPPER;

        for (String[] testCase : testCases) {
            assertEquals(testCase[resultIndex], StringUtils.toSnakeCaseUpper(testCase[0]));
        }
    }

    /** Check that the to kebab case conversion works as expected. */
    public void testToKebabCaseLower() throws Exception
    {
        int resultIndex = KEBAB_CASE_LOWER;

        for (String[] testCase : testCases) {
            assertEquals(testCase[resultIndex], StringUtils.toKebabCaseLower(testCase[0]));
        }
    }

    /** Check that the to kebab case with first character in upper case conversion works as expected. */
    public void testToKebabCaseUpper() throws Exception
    {
        int resultIndex = KEBAB_CASE_UPPER;

        for (String[] testCase : testCases) {
            assertEquals(testCase[resultIndex], StringUtils.toKebabCaseUpper(testCase[0]));
        }
    }
}
