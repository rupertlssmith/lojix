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
package com.thesett.common.util.doublemaps;

import java.util.LinkedList;
import java.util.List;

import com.thesett.common.util.maps.Dictionary;
import com.thesett.common.util.maps.DictionaryTestBase;

import junit.framework.TestCase;

/**
 * Tests that symbol tables with nested scopes work as associative maps. Each nested scope combined with a field in the
 * table works independently as a map.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Test clearing a field of the symbol table makes it empty.
 * <tr><td>Test clearing a field of the symbol table really removes all its keys.
 * <tr><td>Test containsKey reports false for non existent key.
 * <tr><td>Test containsKey reports true for existing key.
 * <tr><td>Test isEmpty reports empty on empty field of the symbol table.
 * <tr><td>Test isEmpty reports not empty on non empty field of the symbol table.
 * <tr><td>Test put/get returns null for a non-existent key.
 * <tr><td>Test put/get returns null for a key with a null value.
 * <tr><td>Test putting/getting a null key returns the matching value.
 * <tr><td>Test put/get returns the value for a key.
 * <tr><td>Test putting a key replaces existing key of same value.
 * <tr><td>Test removing a key really removes it.
 * <tr><td>Test removing a key returns its value.
 * <tr><td>Test clearing up to a key in a field of the table really removes all keys before it, and none after it.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class SymbolTableTestBase<K, L, E> extends TestCase
{
    /** Holds the symbol table to test. */
    private SymbolTable<K, L, E> testTable;

    /**
     * Creates a test with the specified name, over the specified symbol table.
     *
     * @param name  The name of the test.
     * @param table The table to test.
     */
    public SymbolTableTestBase(String name, SymbolTable<K, L, E> table)
    {
        super(name);

        this.testTable = table;
    }

    /** Test clearing a field of the symbol table makes it empty. */
    public void testClearIsEmpty() throws Exception
    {
        checkClearIsEmpty(testTable);
    }

    /** Test clearing a field of the symbol table really removes all its keys. */
    public void testClearRemovesAllKeys() throws Exception
    {
        checkClearRemovesAllKeys(testTable);
    }

    /** Test containsKey reports false for non existant key. */
    public void testContainsKeyFalse() throws Exception
    {
        checkContainsKeyFalse(testTable);
    }

    /** Test containsKey reports true for existing key. */
    public void testContainsKeyTrue() throws Exception
    {
        checkContainsKeyTrue(testTable);
    }

    /** Test isEmpty reports empty on empty field of the symbol table. */
    public void testIsEmpty() throws Exception
    {
        checkIsEmpty(testTable);
    }

    /** Test isEmpty reports not empty on non empty field of the symbol table. */
    public void testIsNotEmpty() throws Exception
    {
        checkIsNotEmpty(testTable);
    }

    /** Test put/get returns null for a non-existant key. */
    public void testPutGetNullForNonExistantKey() throws Exception
    {
        checkPutGetNullForNonExistantKey(testTable);
    }

    /** Test put/get returns null for a key with a null value. */
    public void testPutGetNullForNullValue() throws Exception
    {
        checkPutGetNullForNullValue(testTable);
    }

    /** Test putting/getting a null key returns the matching value. */
    public void testPutGetNullKeyOk() throws Exception
    {
        checkPutGetNullKeyOk(testTable);
    }

    /** Test put/get returns the value for a key. */
    public void testPutGetOk() throws Exception
    {
        checkPutGetOk(testTable);
    }

    /** Test putting a key replaces existing key of same value. */
    public void testPutNewReplace() throws Exception
    {
        checkPutNewReplace(testTable);
    }

    /** Test removing a key really removes it. */
    public void testRemoveKeyOk() throws Exception
    {
        checkRemoveKeyOk(testTable);
    }

    /** Test removing a key returns its value. */
    public void testRemoveKeyValue() throws Exception
    {
        checkRemoveKeyValue(testTable);
    }

    /** Test clearing a field of the symbol table makes it empty. */
    public void testNestedScopeClearIsEmpty() throws Exception
    {
        checkClearIsEmpty(testTable.enterScope(createTestKey()));
    }

    /** Test clearing a field of the symbol table really removes all its keys. */
    public void testNestedScopeClearRemovesAllKeys() throws Exception
    {
        checkClearRemovesAllKeys(testTable.enterScope(createTestKey()));
    }

    /** Test containsKey reports false for non existant key. */
    public void testNestedScopeContainsKeyFalse() throws Exception
    {
        checkContainsKeyFalse(testTable.enterScope(createTestKey()));
    }

    /** Test containsKey reports true for existing key. */
    public void testNestedScopeContainsKeyTrue() throws Exception
    {
        checkContainsKeyTrue(testTable.enterScope(createTestKey()));
    }

    /** Test isEmpty reports empty on empty field of the symbol table. */
    public void testNestedScopeIsEmpty() throws Exception
    {
        checkIsEmpty(testTable.enterScope(createTestKey()));
    }

    /** Test isEmpty reports not empty on non empty field of the symbol table. */
    public void testNestedScopeIsNotEmpty() throws Exception
    {
        checkIsNotEmpty(testTable.enterScope(createTestKey()));
    }

    /** Test put/get returns null for a non-existant key. */
    public void testNestedScopePutGetNullForNonExistantKey() throws Exception
    {
        checkPutGetNullForNonExistantKey(testTable.enterScope(createTestKey()));
    }

    /** Test put/get returns null for a key with a null value. */
    public void testNestedScopePutGetNullForNullValue() throws Exception
    {
        checkPutGetNullForNullValue(testTable.enterScope(createTestKey()));
    }

    /** Test putting/getting a null key returns the matching value. */
    public void testNestedScopePutGetNullKeyOk() throws Exception
    {
        checkPutGetNullKeyOk(testTable.enterScope(createTestKey()));
    }

    /** Test put/get returns the value for a key. */
    public void testNestedScopePutGetOk() throws Exception
    {
        checkPutGetOk(testTable.enterScope(createTestKey()));
    }

    /** Test putting a key replaces existing key of same value. */
    public void testNestedScopePutNewReplace() throws Exception
    {
        checkPutNewReplace(testTable.enterScope(createTestKey()));
    }

    /** Test removing a key really removes it. */
    public void testNestedScopeRemoveKeyOk() throws Exception
    {
        checkRemoveKeyOk(testTable.enterScope(createTestKey()));
    }

    /** Test removing a key returns its value. */
    public void testNestedScopeRemoveKeyValue() throws Exception
    {
        checkRemoveKeyValue(testTable.enterScope(createTestKey()));
    }

    /** Check that non-masked keys in a parent scope are visible within nested scopes. */
    public void testParentScopeVisibleFromNestedScope()
    {
        K testKey = createTestKey();
        K testScopeParent = createTestKey();
        L testField = createTestField();
        E testValue = createTestValue();

        testTable.put(testKey, testField, testValue);

        SymbolTable<K, L, E> testNestedTable = testTable.enterScope(testScopeParent);

        E retrievedInnerValue = testNestedTable.get(testKey, testField);
        assertEquals("nested scope with parent containing value for key " + testKey + " does not provide that value.",
            testValue, retrievedInnerValue);
    }

    /** Check that equal keys in nested scopes, mask their values in parent scopes. */
    public void testEqualKeyInNestedScopeMasksParentScope()
    {
        K testKey = createTestKey();
        K testScopeParent = createTestKey();
        L testField = createTestField();
        E testValue = createTestValue();
        E testInnerValue = createTestValue();

        testTable.put(testKey, testField, testValue);

        SymbolTable<K, L, E> testNestedTable = testTable.enterScope(testScopeParent);
        testNestedTable.put(testKey, testField, testInnerValue);

        E retrievedInnerValue = testNestedTable.get(testKey, testField);
        assertEquals("nested scope masking parent key " + testKey + " reports it does not the correct value.",
            testInnerValue, retrievedInnerValue);
    }

    /** Check that a symbol key for a nested scope provides values from within that scope only. */
    public void testSymbolKeyReturnsToNestedScope()
    {
        K testKey = createTestKey();
        K testScopeParent = createTestKey();
        L testField = createTestField();
        E testValue = createTestValue();
        E testInnerValue = createTestValue();

        testTable.put(testKey, testField, testValue);

        SymbolTable<K, L, E> testNestedTable = testTable.enterScope(testScopeParent);
        testNestedTable.put(testKey, testField, testInnerValue);

        SymbolKey symbolKey = testNestedTable.getSymbolKey(testKey);

        E retrievedInnerValue = testTable.get(symbolKey, testField);
        assertEquals("nested scope masking parent key " + testKey + " reports it does not the correct value.",
            testInnerValue, retrievedInnerValue);
    }

    /** Test clearing up to a key in a field of the table really removes all keys before it, and none after it. */
    public void testClearUpToKeyClearsCorrectValues()
    {
        L testField = createTestField();

        List<SymbolKey> testKeysToRemove = new LinkedList<SymbolKey>();
        List<SymbolKey> testKeysToKeep = new LinkedList<SymbolKey>();
        SymbolKey testKeyToRemoveUpTo = null;
        int clearPoint = 500;

        // Put some keys into the symbol table.
        for (int i = 0; i < 1000; i++)
        {
            K testKey = createTestKey();
            E testValue = createTestValue();

            SymbolKey symbolKey = testTable.getSymbolKey(testKey);
            testTable.put(symbolKey, testField, testValue);

            if (i <= clearPoint)
            {
                testKeysToRemove.add(symbolKey);
            }
            else
            {
                testKeysToKeep.add(symbolKey);
            }

            if (i == clearPoint)
            {
                testKeyToRemoveUpTo = symbolKey;
            }
        }

        // Clear the dictionary up to a point.
        testTable.clearUpTo(testKeyToRemoveUpTo, testField);

        // Check that none of the original lower keys are now in the dictionary, but all of the higher ones are.
        for (SymbolKey testKey : testKeysToRemove)
        {
            if (testTable.get(testKey, testField) != null)
            {
                fail("Dictionary contains key " + testKey + " after the dictionary was cleared up to " +
                    testKeyToRemoveUpTo + ".");
            }
        }

        for (SymbolKey testKey : testKeysToKeep)
        {
            if (testTable.get(testKey, testField) == null)
            {
                fail("Dictionary does not contain key " + testKey + " after the dictionary was cleared up to " +
                    testKeyToRemoveUpTo + ".");
            }
        }
    }

    /**
     * Concrete test implementations should override this to supply test keys. All generated keys should be different.
     *
     * @return A unique test key.
     */
    protected abstract K createTestKey();

    /**
     * Concrete test implementations should override this to supply test fields. All generated fields should be
     * different.
     *
     * @return A unique test field.
     */
    protected abstract L createTestField();

    /**
     * Concrete test implementations should override this to supply test values. All generated values do not have to be
     * different and can be random.
     *
     * @return A test value.
     */
    protected abstract E createTestValue();

    private void checkClearIsEmpty(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testClearIsEmpty();
    }

    private void checkClearRemovesAllKeys(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testClearRemovesAllKeys();
    }

    private void checkContainsKeyFalse(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testContainsKeyFalse();
    }

    private void checkContainsKeyTrue(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testContainsKeyTrue();
    }

    private void checkIsEmpty(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testIsEmpty();
    }

    private void checkIsNotEmpty(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testIsNotEmpty();
    }

    private void checkPutGetNullForNonExistantKey(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testPutGetNullForNonExistantKey();
    }

    private void checkPutGetNullForNullValue(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testPutGetNullForNullValue();
    }

    private void checkPutGetNullKeyOk(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testPutGetNullKeyOk();
    }

    private void checkPutGetOk(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testPutGetOk();
    }

    private void checkPutNewReplace(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testPutNewReplace();
    }

    private void checkRemoveKeyOk(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testRemoveKeyOk();
    }

    private void checkRemoveKeyValue(SymbolTable<K, L, E> testTable) throws Exception
    {
        DictionaryTestOnSymbolTable dictTest =
            new DictionaryTestOnSymbolTable(getName(), new FieldDictionary<K, L, E>(testTable, createTestField()));
        dictTest.testRemoveKeyValue();
    }

    /**
     * DictionaryTestOnSymbolTable specializes a dictionary test to use the {@link #createTestKey()} and
     * {@link #createTestValue()} methods on the symbol table test, allowing a symbol table with a dictionary facade,
     * created by fixing the choice of field, to be run through the dictionary tests.
     *
     * <pre><p/><table id="crc"><caption>CRC Card</caption>
    * <tr><th>Responsibilities<th>Collaborations
     *
     * <tr>
     * <td>Use the test value generation methods from a symbol table test in a dictionary test.
     * </table></pre>
     */
    private class DictionaryTestOnSymbolTable extends DictionaryTestBase<K, E>
    {
        private DictionaryTestOnSymbolTable(String testName, Dictionary<K, E> testDictionary)
        {
            super(testName, testDictionary);
        }

        protected K createTestKey()
        {
            return SymbolTableTestBase.this.createTestKey();
        }

        protected E createTestValue()
        {
            return SymbolTableTestBase.this.createTestValue();
        }
    }

    /**
     * Turns a symbol table into a dictionary, by selecting by a fixed key. This enables the dictionary tests to be
     * applied over symbol tables.
     *
     * <pre><p/><table id="crc"><caption>CRC Card</caption>
     * <tr><th>Responsibilities<th>Collaborations
     * <tr><td>Turn a symbol table into a dictionary over one field of the symbol table.
     * </table></pre>
     */
    private static class FieldDictionary<K, L, E> implements Dictionary<K, E>
    {
        private SymbolTable<K, L, E> table;
        private L field;

        private FieldDictionary(SymbolTable<K, L, E> table, L field)
        {
            this.table = table;
            this.field = field;
        }

        public boolean containsKey(Object key)
        {
            return table.containsKey((K) key, field);
        }

        public E get(Object key)
        {
            return table.get((K) key, field);
        }

        public E put(K key, E value)
        {
            return table.put(key, field, value);
        }

        public E remove(Object key)
        {
            return table.remove((K) key, field);
        }

        public void clear()
        {
            table.clear();
        }

        public int size()
        {
            return table.size();
        }

        public boolean isEmpty()
        {
            return table.isEmpty();
        }
    }
}
