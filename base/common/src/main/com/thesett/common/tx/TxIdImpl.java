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
package com.thesett.common.tx;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Provides an implementation of the {@link com.thesett.common.tx.TxId} interface.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Act as identifier for a transaction.
 * <tr><td>Maintain transaction id validity.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class TxIdImpl implements TxId
{
    /** Holds the current transaction id generation sequence number. */
    private static final AtomicLong currentTxId = new AtomicLong(1L);

    /** The default global id for local transactions. */
    private static final byte[] LOCAL_TX_ID = "L".getBytes();

    /** Holds the transaction branch identifier part of the XID. */
    private final byte[] branchQualifier;

    /** Holds the format identifier part of the XID. */
    private final int formatId;

    /** Holds the global transaction identifier part of this XID. */
    private final byte[] globalTransactionID;

    /** Flag used to mark this transaction id as invalid. */
    private boolean valid = true;

    /** Creates a new transaction id, for a local transaction. */
    private TxIdImpl()
    {
        long id = currentTxId.getAndIncrement();

        branchQualifier = new byte[8];

        branchQualifier[0] = (byte) (id & 0xff00000000000000L);
        branchQualifier[1] = (byte) (id & 0x00ff000000000000L);
        branchQualifier[2] = (byte) (id & 0x0000ff0000000000L);
        branchQualifier[3] = (byte) (id & 0x000000ff00000000L);
        branchQualifier[4] = (byte) (id & 0x00000000ff000000L);
        branchQualifier[5] = (byte) (id & 0x0000000000ff0000L);
        branchQualifier[6] = (byte) (id & 0x000000000000ff00L);
        branchQualifier[7] = (byte) (id & 0x00000000000000ffL);

        formatId = 1;
        globalTransactionID = LOCAL_TX_ID;
    }

    /**
     * Thread safe method to generate a new transaction id. The newly created id is considered live.
     *
     * @return A new and unique transaction id.
     */
    public static TxId createTxId()
    {
        // Create the new transaction id.
        return new TxIdImpl();
    }

    /** Invalidates the transaction id. */
    public void invalidate()
    {
        valid = false;
    }

    /**
     * Checks whether or not this transaction id is valid.
     *
     * @return <tt>true if this is a valid 'live' transaction id, <tt>false</tt> otherwise.
     */
    public boolean isValid()
    {
        return valid;
    }

    /**
     * Obtain the format identifier part of the XID.
     *
     * @return Format identifier. O means the OSI CCR format.
     */
    public int getFormatId()
    {
        return formatId;
    }

    /**
     * Obtain the global transaction identifier part of XID as an array of bytes.
     *
     * @return Global transaction identifier.
     */
    public byte[] getGlobalTransactionId()
    {
        return globalTransactionID;
    }

    /**
     * Obtain the transaction branch identifier part of XID as an array of bytes.
     *
     * @return Global transaction identifier.
     */
    public byte[] getBranchQualifier()
    {
        return branchQualifier;
    }

    /**
     * Checks if this index transaction id is the same as another one. Two ids match if all three of their Xid component
     * parts match.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt>If the comparator is also an index tx id the same as this one, <tt>false</tt> otherwise.
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        TxIdImpl txId = (TxIdImpl) o;

        return (formatId == txId.formatId) && Arrays.equals(branchQualifier, txId.branchQualifier) &&
            Arrays.equals(globalTransactionID, txId.globalTransactionID);
    }

    /**
     * Computes a hashCode of the tx id to allow them to be used efficiently in hashing data structures.
     *
     * @return A hash code of the transaction id.
     */
    public int hashCode()
    {
        int result;
        result = ((branchQualifier != null) ? Arrays.hashCode(branchQualifier) : 0);
        result = (31 * result) + formatId;
        result = (31 * result) + ((globalTransactionID != null) ? Arrays.hashCode(globalTransactionID) : 0);

        return result;
    }

    /**
     * Returns a string containing this transaction id. Used for debugging purposes.
     *
     * @return A string containing this transaction id. Used for debugging purposes.
     */
    public String toString()
    {
        return "TxIdImpl: [ branchQualifier = " + branchQualifier + ", formatId = " + formatId +
            ", globalTransactionID = " + globalTransactionID + "]";
    }
}
