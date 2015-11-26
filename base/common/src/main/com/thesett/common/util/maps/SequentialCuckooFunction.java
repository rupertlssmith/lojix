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
package com.thesett.common.util.maps;

import java.util.Random;

/**
 * SequentialCuckooFunction uses cuckoo hashing, to compute a sequentially increasing function of its input. When new
 * inputs are encountered, they are assigned sequence numbers, and they are recorded in a mapping using cuckoo hashing.
 * When inputs are re-visited their existing sequence numbers are looked up very quickly (at most two probes), again
 * using cuckoo hashing.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Compute a sequentially increasing function of arbitrary keys using cuckoo hashing.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SequentialCuckooFunction<K> implements SequentialFunction<K>
{
    /** A source of random numbers to rejig the hash functions with. */
    private static final Random r = new Random();

    /** Holds the current size of the hash table. */
    private int hashTableSize = 16;

    /** Holds the maximum size of hash table that will be attempted to be allocated. */
    private final int maxSize = Integer.MAX_VALUE;

    /** Holds the count of elements currently in the hash table. */
    private int count;

    /** Holds a sequence number, for the monotonically increasing function. */
    private int nextSequenceNumber;

    /** Holds the actual hash table as an array of entries. */
    private Entry<K>[] hashTable = (Entry<K>[]) new Entry[hashTableSize];

    /** Holds the length of the hash table. */
    private int length = hashTable.length;

    /** A random seed for hash function 1. */
    private int hash1seed;

    /** A random seed for hash function 2. */
    private int hash2seed;

    /**
     * {@inheritDoc}
     *
     * <p/>Looks up the specified key in hash table, using cuckoo hashing. If the key cannot be found in the table, then
     * the next available sequence number is allocated to it, and a new entry is added to the hash table for the key,
     * again using cuckoo hashing.
     *
     * @param  key The key to get a sequence number for.
     *
     * @return A sequence number for the key.
     *
     * @throws IllegalStateException If adding a new key to the function causes more space to be allocated than is
     *                               allowed by the maximum size limit.
     */
    public Integer apply(K key)
    {
        return applyWithEntry(key, null, true);
    }

    /** {@inheritDoc} */
    public boolean containsKey(K key)
    {
        int keyHashCode = key.hashCode();
        int hash1 = hash1(keyHashCode);

        Entry<K> entry = hashTable[indexFor(hash1)];

        if ((entry != null) && key.equals(entry.key))
        {
            return true;
        }

        entry = hashTable[indexFor(hash2(hash1, keyHashCode))];

        if ((entry != null) && key.equals(entry.key))
        {
            return true;
        }

        return false;
    }

    /** {@inheritDoc} */
    public Integer remove(Object objectKey)
    {
        K key = (K) objectKey;

        int keyHashCode = key.hashCode();
        int hash1 = hash1(keyHashCode);
        int index1 = indexFor(hash1);

        Entry<K> entry = hashTable[index1];

        if ((entry != null) && key.equals(entry.key))
        {
            hashTable[index1] = null;

            return entry.seq;
        }

        int hash2 = hash2(hash1, keyHashCode);
        int index2 = indexFor(hash2);

        entry = hashTable[index2];

        if ((entry != null) && key.equals(entry.key))
        {
            hashTable[index2] = null;

            return entry.seq;
        }

        return null;
    }

    /** {@inheritDoc} */
    public void clear()
    {
        count = 0;
        nextSequenceNumber = 0;
        hashTable = (Entry<K>[]) new Entry[hashTableSize];
        length = hashTable.length;
    }

    /**
     * Reports the number of elements held in the map.
     *
     * @return The number of elements held in the map.
     */
    public int size()
    {
        return count;
    }

    /**
     * Prints all entries in the sequential function, for debugging purposes.
     *
     * @return All entries in the sequential function.
     */
    public String toString()
    {
        String result = "SequentialCuckooFunction: [ entries = { ";

        boolean prevEntry = false;

        for (Entry<K> entry : hashTable)
        {
            if (entry != null)
            {
                result +=
                    (prevEntry ? ", " : "") + entry.seq + "->" + entry.key + "(" + entry.hash1 + ", " + entry.hash2 +
                    ")";
                prevEntry = true;
            }
        }

        return result + " } ]";
    }

    /**
     * Looks up the specified key in hash table, using cuckoo hashing. If the key cannot be found in the table, then the
     * next available sequence number is allocated to it, and a new entry is added to the hash table for the key, again
     * using cuckoo hashing.
     *
     * <p/>To save creating entries that already exist, if an entry for the key already exists, because this method is
     * being called to rehash an old table into a new one, then the entry may be passed into this method. In that case,
     * no new entry will be created and the existing one will be re-used. It is assumed that the entry passed in is the
     * correct one for the key.
     *
     * @param  key          The key to get a sequence number for.
     * @param  entry        The entry for the key, or <tt>null</tt> to create a new one.
     * @param  tryRehashing <tt>true</tt> if rehashing should be trued, otherwise just fail.
     *
     * @return A sequence number for the key, or <tt>null</tt> if this fails.
     */
    private Integer applyWithEntry(K key, Entry<K> entry, boolean tryRehashing)
    {
        // Used to hold a new entry if one has to be created, or can re-use an entry passed in as a parameter.
        Entry<K> uninsertedEntry = entry;

        // Holds a flag to indicate that a new sequence number has been taken.
        boolean createdNewEntry = false;

        // Check if there is already an entry for the key, and return it if so.
        Entry<K> existingEntry = entryForKey(key);

        Integer result = null;

        if (existingEntry != null)
        {
            result = existingEntry.seq;
        }
        else
        {
            // Create a new entry, if one has not already been created and cached.
            if (uninsertedEntry == null)
            {
                uninsertedEntry = new Entry<K>();
                uninsertedEntry.key = key;
                uninsertedEntry.seq = nextSequenceNumber;
                nextSequenceNumber++;
                count++;
                createdNewEntry = true;
                result = uninsertedEntry.seq;
            }

            // Attempt to insert the new entry. The sequence number is only incremented when this succeeds for a new
            // entry. Existing entries that are being re-hashed into a new table will not increment the sequence
            // number.
            while (true)
            {
                // Hash the entry for the current hash functions.
                int keyHashCode = uninsertedEntry.key.hashCode();
                uninsertedEntry.hash1 = hash1(keyHashCode);
                uninsertedEntry.hash2 = hash2(uninsertedEntry.hash1, keyHashCode);

                // Try and insert the entry, checking that no entry is left uninserted as a result.
                uninsertedEntry = cuckoo(uninsertedEntry);

                if (uninsertedEntry == null)
                {
                    result = createdNewEntry ? result : -1;

                    break;
                }

                // If the cuckoo algorithm fails then change the hash function/table size and try again.
                if (tryRehashing)
                {
                    rehash();
                }
                else
                {
                    result = null;

                    break;
                }

            }
        }

        return result;
    }

    /**
     * Checks if the specified key can be found in the set, and returns its entry if so.
     *
     * @param  key The key to check for in the entry set.
     *
     * @return The keys entry if it is already in the set, <tt>null</tt> if not.
     */
    private Entry<K> entryForKey(K key)
    {
        int keyHashCode = key.hashCode();
        int hash1 = hash1(keyHashCode);

        Entry<K> entry = hashTable[indexFor(hash1)];

        if ((entry != null) && key.equals(entry.key))
        {
            return entry;
        }

        int hash2 = hash2(hash1, keyHashCode);
        entry = hashTable[indexFor(hash2)];

        if ((entry != null) && key.equals(entry.key))
        {
            return entry;
        }

        return null;
    }

    /**
     * Calculates the index offset in the table for a hash code.
     *
     * @param  h The hash code.
     *
     * @return An index for the hash code that lies within the table.
     */
    private int indexFor(int h)
    {
        return ((h < 0) ? ((length + (h % length)) % length) : (h % length));
    }

    /**
     * Adds a new entry to a hash table, using the cuckoo algorithm.
     *
     * @param  entry The new key to add.
     *
     * @return <tt>null</tt> if the cuckoo algorithm inserted the new entry, or the currently uninserted entry if it
     *         could not complete the insertion. Note that the currently uninserted entry may not be the entry
     *         originally requested to be inserted, as that may have succeeded, and a displaced entry further down the
     *         chain of cuckoo insertions may be the current uninserted one, that remains to be inserted.
     */
    private Entry<K> cuckoo(Entry<K> entry)
    {
        // Holds the entry currently being placed in the hash table.
        Entry<K> currentEntry = entry;

        // Holds the index into the hash table where the current entry will be placed.
        int hash = entry.hash1;
        int index = indexFor(hash);
        Entry<K> nextEntry = hashTable[index];

        int previousFlag = 0;
        int[] previousIndex = new int[2];
        int[] previousSeq = new int[2];

        for (int i = 0; i < hashTableSize; i++)
        {
            // Check the current index, to see if it is an empty slot. If it is an empty slot then the current
            // entry is placed there and the algorithm completes.
            if (nextEntry == null)
            {
                hashTable[index] = currentEntry;

                return null;
            }

            // If the current index does not point to an empty slot, the current entry is placed there anyway, but the
            // displaced entry (the egg displaced by the cuckoo) becomes the current entry for placing.
            hashTable[index] = currentEntry;
            currentEntry = nextEntry;

            // A new index is selected depending on whether the entry is currently at its primary or secondary hashing.
            int firstPosition = indexFor(currentEntry.hash1);
            hash = (index == firstPosition) ? currentEntry.hash2 : currentEntry.hash1;
            index = indexFor(hash);

            // A check for infinite loops of size 2 is made here, to circumvent the simplest and most common infinite
            // looping condition.
            previousIndex[previousFlag] = index;
            previousSeq[previousFlag] = nextEntry.seq;
            previousFlag = (previousFlag == 1) ? 0 : 1;

            nextEntry = hashTable[index];

            if ((nextEntry != null) && (index == previousIndex[previousFlag]) &&
                    (nextEntry.seq == previousSeq[previousFlag]))
            {
                break;
            }

        }

        return currentEntry;
    }

    /**
     * Creates a new hashtable that is twice the size of the old one, then re-hashes everything from the old table into
     * the new table.
     *
     * @throws IllegalStateException If the table cannot be increased in size because the maximum size limit would be
     *                               breached.
     */
    private void rehash()
    {
        // Increase the table size, to keep the load factory < 0.5.
        int newSize = hashTableSize;

        if (hashTableSize < (count * 2))
        {
            newSize = hashTableSize * 2;

            if (newSize > maxSize)
            {
                throw new IllegalStateException("'newSize' of " + newSize +
                    " would put the table over the maximum size limit of " + maxSize);
            }
        }

        // Keep hold of the old table, until a new one is succesfully buily.
        Entry<K>[] oldTable = hashTable;
        hashTableSize = newSize;
        length = hashTable.length;

        // Keep rehashing the table until it is succesfully rebuilt.
        boolean rehashedOk;

        do
        {
            // Start by assuming that this will work.
            rehashedOk = true;

            // Alter the hash functions.
            changeHashFunctions();

            // Create a new table from the old one, to rehash everything into.
            hashTable = (Entry<K>[]) new Entry[hashTableSize];

            for (Entry<K> entry : oldTable)
            {
                if (entry != null)
                {
                    // Add the entry to the new table, dropping out if this fails.
                    if (applyWithEntry(entry.key, entry, false) == null)
                    {
                        rehashedOk = false;

                        break;
                    }
                }
            }
        }
        while (!rehashedOk);
    }

    /** Selects new random seeds to alter the hash functions. */
    private void changeHashFunctions()
    {
        hash1seed = r.nextInt();
        hash2seed = r.nextInt();
    }

    /**
     * Implements Robert Jenkins' 32-bit integer hash function. <a
     * href="http://www.concentric.net/~Ttwang/tech/inthash.htm"/>http://www.concentric.net/~Ttwang/tech/inthash.htm</a>
     *
     * @param  key The integer to compute a hash of.
     *
     * @return A hash of the integer.
     */
    private int hash1(int key)
    {
        key += hash1seed;
        key = (key + 0x7ed55d16) + (key << 12);
        key = (key ^ 0xc761c23c) ^ (key >> 19);
        key = (key + 0x165667b1) + (key << 5);
        key = (key + 0xd3a2646c) ^ (key << 9);
        key = (key + 0xfd7046c5) + (key << 3);
        key = (key ^ 0xb55a4f09) ^ (key >> 16);

        return key;
    }

    /**
     * Thomas Wang's 32-bit shift hash function. <a href="http://www.concentric.net/~Ttwang/tech/inthash.htm"/>
     * http://www.concentric.net/~Ttwang/tech/inthash.htm</a>
     *
     * @param  key The integer to compute a hash of.
     *
     * @return A hash of the integer.
     */
    private int hash32shift(int key)
    {
        key += hash2seed;
        key = ~key + (key << 15);
        key = key ^ (key >>> 12);
        key = key + (key << 2);
        key = key ^ (key >>> 4);
        key = key * 2057;
        key = key ^ (key >>> 16);

        return key;
    }

    /**
     * Implements a secondary hash. This uses the 32-bit shift hash implemented by {@link #hash32shift(int)} and then
     * successively applies {@link #hash1(int)} if the generated hash code is not different to the hash code generated
     * by running {@link #hash1(int)} on the key. This ensures that the hash code returned by this will be different to
     * the one generated by {@link #hash1(int)}.
     *
     * @param  hash1 The output of the hash1 function.
     * @param  key   The integer to compute a hash of.
     *
     * @return A hash of the integer which is different to that generated by {@link #hash1(int)}.
     */
    private int hash2(int hash1, int key)
    {
        key = hash32shift(key);

        while (key == hash1)
        {
            key = hash32shift(key);
        }

        return key;
    }

    /**
     * Entry is used to hold a key/value pair, along with its cached hash codes.
     *
     * <pre><p/><table id="crc"><caption>CRC Card</caption>
     * <tr><th>Responsibilities<th>Collaborations
     * <tr><td>Hold a key/value pair.
     * <tr><td>Cache two different hashes of the key.
     * </table></pre>
     */
    private static class Entry<K>
    {
        /** Holds the key. */
        K key;

        /** Holds the allocated sequence number. */
        int seq;

        /** Holds the first cached hash code of the key. */
        int hash1;

        /** Holds the second cached hash code of the key. */
        int hash2;

        /**
         * Pretty prints the entry for debugging purposes.
         *
         * @return The entries key and sequence number as a string.
         */
        public String toString()
        {
            return "Entry: [ seq = " + seq + ", key = " + key.toString() + " ]";
        }
    }
}
