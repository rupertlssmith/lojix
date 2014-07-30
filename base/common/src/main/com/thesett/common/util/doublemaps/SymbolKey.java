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
 * SymbolKey is a unique key for a symbol into a symbol table, taking into account the nested structure of symbol
 * tables. A SymbolTable is able to supply such keys for symbols, and may be able to make use of them to cache the
 * location of a symbols storage location within the table, in order to be able to quickly return to it.
 *
 * <p/>SymbolKeys are unique accross a whole symbol table, starting from the root table, and covering all child tables
 * within it. Whenever any symbol table that lies within a root, at any level, is queried with a SymbolKey it will
 * identify the symbol uniquely within the whole table structure, and allow access to fields stored against that symbol
 * without the need to navigate through the table structure using the {@link SymbolTable#enterScope} and
 * {@link SymbolTable#leaveScope} methods.
 *
 * <p/>This interface is fully opaque, allowing symbol table implementations complete freedom in how it is implemented.
 * It can be assumed that implementations will provide equals and hashCode methods suitable for the key.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Uniquely identiy symbols falling under a root symbol table.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface SymbolKey
{
}
