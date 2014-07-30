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
package com.thesett.common.util.doublemaps;

/**
 * A SymbolTable is a two-dimensional table that maps symbols as the primary key into the table, and properties of those
 * symbols as a secondary key, onto mapped values. Symbol tables are also strucured with nested scopes, such that each
 * symbol can contain a child table within it, for symbols that are defined within the scope of a parent symbol.
 *
 * <p/>A particular scope within a symbol table functions as a {@link DoubleKeyedMap}; two keys are used to lookup a
 * single value.
 *
 * <p/>The nesting of scopes also functions as a {@link DoubleKeyedMap}, but values of the primary index in a nested
 * scope can mask equal values occurring in any higher parent scope, allowing the same symbol to hold different values
 * in nested scopes. For example, in a C like language there might be a nested scope:
 *
 * <pre><p/>
 * int a = 1;
 * int b = 3;
 * {
 *    int a = 2;
 *    int x = a + b;
 * }
 * </pre>
 *
 * <p/>In the above example, in the nested scope 'a' refers to a different local variable than outside of the scope. The
 * value of 'b' is visible within the nested scope, but as it is not redefined there, it falls through onto the value
 * defined in the parent scope. In this example, the nested scope is also anonymous, so would need to be given an
 * artificial name, so that a nested child scope can be created under it.
 *
 * <p/>The process of navigating down into nested scopes to set or recover values on symbols can be circumvented by the
 * use of a {@link SymbolKey}. A primary key into the current scope can produce a SymbolKey {@link #getSymbolKey}, which
 * can be used to directly navigate back to that symbol, in that same scope using the {@link #get(SymbolKey, Object)} or
 * {@link #put(SymbolKey, Object, Object)} methods on any scope on the symbol table.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Step into a new child scope.
 * <tr><td>Step out into a parent scope.
 * <tr><td>Provide an iterable over fields of the table.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface SymbolTable<K, L, E> extends DoubleKeyedMap<K, L, E>
{
    /**
     * Creates a new table or navigates to an existing table scoped within the specified symbol.
     *
     * @param  key The symbol within the current scope, to access a nested scope within.
     *
     * @return The symbol table for the nested scope.
     */
    SymbolTable<K, L, E> enterScope(K key);

    /**
     * Leaves the scope of the current parent symbol, for the parent scope in which it resides.
     *
     * @return The symbol table for the parent scope.
     */
    SymbolTable<K, L, E> leaveScope();

    /**
     * Creates a {@link SymbolKey} for a symbol. This key is unique over a whole symbol table structure from the table
     * root, over all nested scopes. Generally speaking this key may be used as a short-cut to a symbol without the need
     * to navigate through table scopes; the symbol key will automatically navigate back to this scope.
     *
     * @param  key The symbol within the current scope, to create a unqiue symbol key for.
     *
     * @return A unique symbol key for the symbol.
     */
    SymbolKey getSymbolKey(K key);

    /**
     * Looks up a value for a field in the symbol table for a {@link SymbolKey}. The key may refer to any nested scope
     * from the table root, over all nested scopes.
     *
     * @param  key   The symbol key to look up.
     * @param  field The field to look up.
     *
     * @return The field value at the symbol key, or <tt>null</tt> if none has been set.
     */
    E get(SymbolKey key, L field);

    /**
     * Stores a value for a field in the symbol table for a {@link SymbolKey}. The key may refer to any nested scope
     * from the table root, over all nested scopes.
     *
     * @param  key   The symbol key to store against.
     * @param  field The field to store against.
     * @param  value The value to store.
     *
     * @return The previous value at the symbol key, or <tt>null</tt> if none was previously set.
     */
    E put(SymbolKey key, L field, E value);

    /**
     * Clears all keys up to and including the specified key, from the specified field of the symbol table. This is
     * effectively a garbage collection call on the symbol table to remove processed data from a field once it is no
     * longer needed.
     *
     * @param key   The key to clear up to.
     * @param field The field to clear.
     */
    void clearUpTo(SymbolKey key, L field);

    /**
     * Sets the low mark against a field of the table to the specified value, provided the value given is higher than
     * the current low mark.
     *
     * @param key   The key to use as the new highest low mark.
     * @param field The field to move the mark on.
     */
    void setLowMark(SymbolKey key, L field);

    /**
     * Clears a field of the symbol table to its low mark.
     *
     * @param field The field to clear up to the low mark.
     */
    void clearUpToLowMark(L field);

    /**
     * Provides the nested scoping depth of this symbol table. The root table is at depth zero.
     *
     * @return The nested scoping depth of this symbol table.
     */
    int getDepth();

    /**
     * Provides an iterable over all elements of a given field of the symbol table.
     *
     * @param  field The field to iterate over.
     *
     * @return An iterable over the specified field, or <tt>null</tt> if no matching field can be found.
     */
    Iterable<E> getValues(L field);
}
