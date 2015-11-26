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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.thesett.common.util.maps.CircularArrayMap;
import com.thesett.common.util.maps.SequentialCuckooFunction;
import com.thesett.common.util.maps.SequentialFunction;

/**
 * SymbolTableImpl is a hash map, that against the primary key, stores a set of fields indexed by the secondary key.
 * This arrangement creates a table of extensible records, to which new fields can be added, or old ones removed. Such
 * an arrangement may be advantageous for processes that want to conserve memory, and carry out computations over a set
 * of records, where fields or groups of fields may only be required for some of the computation; they can be added or
 * computed as needed, and removed when no longer needed.
 *
 * <p/>There are a number of options that can be explored here:
 *
 * <pre><ol>
 * <li>Computing a hash of each key, returns an index into an array, which is the hashtable. A two dimensional array
 * could be used. This would grow (and possibly) shrink with this data structure, but the wasted memory would be the
 * product of the wastage on each dimension of the array.</li>
 * <li>The primary key could hash to another hash table, and use the secondary key to retrieve fields from it. This
 * would mean that each record would only hold the fields it needs, and there would no be space allocated where some
 * records use a field, but others don't, but those that don't would still have the space allocated anyway.</li>
 * <li>The secondary key could hash to another hash table, and use the primary key to retrieve fields from it. This would
 * mean that each field would only manage a table big enough for all the records that currently have a value of that
 * field assigned.</li>
 * </ol></pre>
 *
 * <p/>This implementation uses the third option above.
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
public class SymbolTableImpl<K, L, E> implements SymbolTable<K, L, E>
{
    /** Used for debugging purposes. */
    /* private static final Logger log = Logger.getLogger(SymbolTableImpl.class.getName()); */

    /** Default initial size for the field arrays. */
    public static final int DEFAULT_INITIAL_FIELD_SIZE = 128;

    /** Keeps a count of the total number of entries in the table. */
    private int count;

    /** Holds a map of all of the fields that the symbol table contains. */
    private Map<L, CircularArrayMap<E>> fieldMap;

    /** Holds the symbol to array offset hashing function. */
    private SequentialFunction<CompositeKey<K>> hashFunction;

    /** Holds the parent lexical scope. */
    private SymbolTableImpl<K, L, E> parentScope;

    /** Holds the lexical depth of this table. */
    private final int depth;

    /**
     * Holds the sequence key of the parent symbol table that this one is a nested scope within. The top-level table
     * uses a parent sequence key of -1, to ensure that it is not confused with the table that is nested within symbol
     * 0.
     */
    private final int parentSequenceKey;

    /** Creates a new top-level scope empty symbol table. */
    public SymbolTableImpl()
    {
        fieldMap = new LinkedHashMap<L, CircularArrayMap<E>>();
        hashFunction = new SequentialCuckooFunction<CompositeKey<K>>();
        parentSequenceKey = -1;
        depth = 0;
    }

    /**
     * Creates a child symbol table within the scope of the specified parent table. The child table shares the fields
     * and sequence function with the parent table.
     *
     * @param parentScope       The parent table.
     * @param fieldMap          The field map shared with the parent table.
     * @param hashFunction      The symbol sequence function shared with the parent table.
     * @param depth             The lexical depth of the parent table.
     * @param parentSequenceKey The base key path that leads to this scope.
     */
    private SymbolTableImpl(SymbolTableImpl<K, L, E> parentScope, Map<L, CircularArrayMap<E>> fieldMap,
        SequentialFunction<CompositeKey<K>> hashFunction, int depth, int parentSequenceKey)
    {
        this.fieldMap = fieldMap;
        this.hashFunction = hashFunction;
        this.parentScope = parentScope;
        this.depth = depth + 1;

        // Copy the base key path into a new base key, one larger than the parent one.
        this.parentSequenceKey = parentSequenceKey;
    }

    /** {@inheritDoc} */
    public void clear()
    {
        // Clea all fields and records from the table.
        fieldMap = new LinkedHashMap<L, CircularArrayMap<E>>();
        hashFunction = new SequentialCuckooFunction<CompositeKey<K>>();
    }

    /** {@inheritDoc} */
    public boolean isEmpty()
    {
        return count == 0;
    }

    /** {@inheritDoc} */
    public int size()
    {
        return count;
    }

    /** {@inheritDoc} */
    public boolean containsKey(K primaryKey, L secondaryKey)
    {
        // Check that the field exists in the table.
        CircularArrayMap<E> field = fieldMap.get(secondaryKey);

        if (field == null)
        {
            return false;
        }

        // Check that the symbol exists in the table.
        CompositeKey<K> compositeKey = new CompositeKey<K>(parentSequenceKey, primaryKey);
        SymbolTableImpl<K, L, E> nextParentScope = parentScope;

        while (true)
        {
            if (hashFunction.containsKey(compositeKey))
            {
                break;
            }

            if (nextParentScope != null)
            {
                compositeKey = new CompositeKey(nextParentScope.parentSequenceKey, primaryKey);
                nextParentScope = nextParentScope.parentScope;
            }
            else
            {
                return false;
            }
        }

        // Calculate the symbols index in the table, and use it to check if a value for that field exists.
        E value = field.get(hashFunction.apply(compositeKey));

        return value != null;
    }

    /** {@inheritDoc} */
    public E put(K primaryKey, L secondaryKey, E value)
    {
        // Create the field column for the secondary key, if it does not already exist.
        CircularArrayMap<E> field = fieldMap.get(secondaryKey);

        if (field == null)
        {
            field = new CircularArrayMap<E>(DEFAULT_INITIAL_FIELD_SIZE);
            fieldMap.put(secondaryKey, field);
        }

        // Create the mapping for the symbol if it does not already exist.
        Integer index = hashFunction.apply(new CompositeKey<K>(parentSequenceKey, primaryKey));

        // Insert the new value for the field into the field map.
        E oldValue = field.put(index, value);
        count++;

        return oldValue;
    }

    /** {@inheritDoc} */
    public E get(K primaryKey, L secondaryKey)
    {
        // Check that the field for the secondary key exists, and return null if not.
        CircularArrayMap<E> field = fieldMap.get(secondaryKey);

        if (field == null)
        {
            return null;
        }

        // Check that the symbol exists in the table.
        CompositeKey<K> compositeKey = new CompositeKey<K>(parentSequenceKey, primaryKey);
        SymbolTableImpl<K, L, E> nextParentScope = parentScope;

        while (true)
        {
            if (hashFunction.containsKey(compositeKey))
            {
                break;
            }

            if (nextParentScope != null)
            {
                compositeKey = new CompositeKey(nextParentScope.parentSequenceKey, primaryKey);
                nextParentScope = nextParentScope.parentScope;
            }
            else
            {
                return null;
            }
        }

        // Calculate the symbols index in the table, and use it to fetch a value for the field if one exists.
        return field.get(hashFunction.apply(compositeKey));
    }

    /** {@inheritDoc} */
    public E remove(K primaryKey, L secondaryKey)
    {
        CompositeKey<K> compositeKey = new CompositeKey<K>(parentSequenceKey, primaryKey);

        // Check that the symbol exists in the table, and return null if not.
        if (!hashFunction.containsKey(compositeKey))
        {
            return null;
        }

        // Check that the field for the secondary key exists, and return null if not.
        CircularArrayMap<E> field = fieldMap.get(secondaryKey);

        if (field == null)
        {
            return null;
        }

        // Calculate the symbols index in the table, and use it to fetch remove a value for the field if one exists.
        E oldValue = field.remove(hashFunction.apply(compositeKey));

        // Check if the fields size has been reduced to zero, in which case purge that whole field from the symbol
        // table.
        if (field.isEmpty())
        {
            fieldMap.remove(secondaryKey);
        }

        // Check if an item was really removed from the table, and decrement the size count if so.
        if (oldValue != null)
        {
            count--;
        }

        return oldValue;
    }

    /** {@inheritDoc} */
    public SymbolTable<K, L, E> enterScope(K key)
    {
        // Create an entry in the sequence function for the key, if one does not already exist.
        int scopeSequenceKey = hashFunction.apply(new CompositeKey<K>(parentSequenceKey, key));

        // Create a new child table for the symbol within this table at depth one greater than this.
        return new SymbolTableImpl<K, L, E>(this, fieldMap, hashFunction, depth, scopeSequenceKey);
    }

    /** {@inheritDoc} */
    public SymbolTable<K, L, E> leaveScope()
    {
        return parentScope;
    }

    /** {@inheritDoc} */
    public SymbolKey getSymbolKey(K key)
    {
        // Create an entry in the sequence function for the key, if one does not already exist.
        int scopeSequenceKey = hashFunction.apply(new CompositeKey<K>(parentSequenceKey, key));

        return new SymbolKeyImpl(scopeSequenceKey);
    }

    /** {@inheritDoc} */
    public E get(SymbolKey key, L secondaryKey)
    {
        // Extract the sequence key from the symbol key.
        int sequenceKey = ((SymbolKeyImpl) key).sequenceKey;

        // Check that the field for the secondary key exists, and return null if not.
        CircularArrayMap<E> field = fieldMap.get(secondaryKey);

        if (field == null)
        {
            return null;
        }

        // Look up the value directly by its sequence key.
        return field.get(sequenceKey);
    }

    /** {@inheritDoc} */
    public E put(SymbolKey key, L secondaryKey, E value)
    {
        // Extract the sequence key from the symbol key.
        int sequenceKey = ((SymbolKeyImpl) key).sequenceKey;

        // Create the field column for the secondary key, if it does not already exist.
        CircularArrayMap<E> field = fieldMap.get(secondaryKey);

        if (field == null)
        {
            field = new CircularArrayMap<E>(DEFAULT_INITIAL_FIELD_SIZE);
            fieldMap.put(secondaryKey, field);
        }

        // Insert the new value for the field into the field map.
        E oldValue = field.put(sequenceKey, value);
        count++;

        return oldValue;
    }

    /** {@inheritDoc} */
    public void clearUpTo(SymbolKey key, L secondaryKey)
    {
        // Extract the sequence key from the symbol key, and clear the field up to it.
        int sequenceKey = ((SymbolKeyImpl) key).sequenceKey;
        CircularArrayMap<E> field = fieldMap.get(secondaryKey);

        if (field != null)
        {
            field.clearUpTo(sequenceKey);
        }
    }

    /** {@inheritDoc} */
    public void setLowMark(SymbolKey key, L secondaryKey)
    {
        // Extract the sequence key from the symbol key, and low mark the field up to it.
        int sequenceKey = ((SymbolKeyImpl) key).sequenceKey;
        CircularArrayMap<E> field = fieldMap.get(secondaryKey);

        if (field != null)
        {
            field.setLowMark(sequenceKey);
        }
    }

    /** {@inheritDoc} */
    public void clearUpToLowMark(L secondaryKey)
    {
        CircularArrayMap<E> field = fieldMap.get(secondaryKey);

        if (field != null)
        {
            field.clearUpToLowMark();
        }
    }

    /** {@inheritDoc} */
    public int getDepth()
    {
        return depth;
    }

    /** {@inheritDoc} */
    public Iterable<E> getValues(L field)
    {
        return fieldMap.get(field);
    }

    /** {@inheritDoc} */
    public String toString()
    {
        StringBuffer result =
            new StringBuffer("SymbolTableImpl: [ count = ").append(count).append(", depth = ").append(depth).append(
                ", [ ");

        for (Iterator<Map.Entry<L, CircularArrayMap<E>>> iterator = fieldMap.entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry<L, CircularArrayMap<E>> entry = iterator.next();

            L key = entry.getKey();
            CircularArrayMap<E> array = entry.getValue();

            result.append(key).append(".sizeof() = ").append(array.sizeof()).append(iterator.hasNext() ? ", " : " ");
        }

        return result.append("] ]").toString();
    }

    /**
     * Implements the {@link SymbolKey} as the sequence number assigned to a symbol by the sequencing function. This
     * index is used to directly look up values in the field tables.
     */
    private static class SymbolKeyImpl implements SymbolKey
    {
        /** Holds the unique sequence key for a symbol. */
        public int sequenceKey;

        /**
         * Creates a sequence key with the specified sequence number.
         *
         * @param sequenceKey The sequence number for the key.
         */
        private SymbolKeyImpl(int sequenceKey)
        {
            this.sequenceKey = sequenceKey;
        }

        /**
         * Prints the symbol key as a string, mainly for debugging purposes.
         *
         * @return The symbol key as a string.
         */
        public String toString()
        {
            return "SymbolKeyImpl: [ sequenceKey = " + sequenceKey + " ]";
        }
    }

    /**
     * Every symbol key is mapped by the sequential function as a CompositeKey. The composite key, combines the symbol
     * key with the unique sequence number of its parent symbol (or zero, if it has no parent symbol). This allows
     * symbol sequences leading to deeper levels of a table with nested scopes from having to compare the entire key
     * sequence for equality, or compute over the entire key sequence when generating hash codes. These calculations are
     * only performed for the additional step beyond the parent key sequence, which means that the calculations take
     * constant time and do not grow linearly with table depth.
     *
     * <pre><p/><table id="crc"><caption>CRC Card</caption>
     * <tr><th>Responsibilities<th>Collaborations
     * <tr><td>Combine a symbol key with a parent symbol sequence number.
     * <tr><td>Provide constant time equals and hashCode over arbitrarily nested symbols.
     * </table></pre>
     *
     * @author Rupert Smith
     */
    private static class CompositeKey<K>
    {
        /** The unique sequence key of the parent composite symbol. */
        int parentSequenceKey;

        /** The symbol key to combine with the parent to for a composite key. */
        K key;

        /**
         * Creates a composite key from a parent keys unique sequence number and a symbol key.
         *
         * @param parentSequenceKey The parent keys unique sequence number, or zero for the top-level table.
         * @param key               The symbol key.
         */
        private CompositeKey(int parentSequenceKey, K key)
        {
            this.parentSequenceKey = parentSequenceKey;
            this.key = key;
        }

        /**
         * Compares this composite key to another for equality. They are equal if their parent composite sequence keys
         * are equal, and their keys are equal.
         *
         * @param  o The object to compare to.
         *
         * @return <tt>true</tt> If the comparator is a composite key and their parent composite sequence keys are
         *         equal, and their keys are equal.
         */
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }

            if (!(o instanceof CompositeKey))
            {
                return false;
            }

            CompositeKey that = (CompositeKey) o;

            return (parentSequenceKey == that.parentSequenceKey) &&
                !((key != null) ? (!key.equals(that.key)) : (that.key != null));
        }

        /**
         * Computes a hash code for the composite key, based on combing the parent sequence key with the symbol keys
         * hashcode.
         *
         * @return A hash code for the composite key.
         */
        public int hashCode()
        {
            int result;
            result = parentSequenceKey;
            result = (31 * result) + ((key != null) ? key.hashCode() : 0);

            return result;
        }
    }
}
