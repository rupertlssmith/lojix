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
package com.thesett.common.tx;

import javax.transaction.xa.Xid;

/**
 * TxId is simply a marker object that is used to identify a transaction. Object already defines the {@link #equals} and
 * {@link #hashCode} methods but this interface restates them to formalize the properties of a transaction id. This id
 * also contains methods to invalidate the id and to test its validity. Once a transaction id has been used it should be
 * invalidated (at commit or rollback), to ensure that it does not outlive it transaction.
 *
 * <p/>A TxId is also a java.transaction.xa.Xid. It does not have to be, as transaction ids could be used for local only
 * transactions, in which case a simple long identifier would suffice. When internally managed transactions are being
 * mapped onto a global transaction using an Xid, to interface to an external transaction manager, a mapping between
 * internal transaction ids and external global transaction ids would need to be maintained. Maintaining this mapping
 * seems a little unnecessary, and is the reason for the decision to use Xids for local transactions.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Act as identifier for a transaction.
 * <tr><td>Maintain transaction id validity.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface TxId extends Xid
{
    /** Invalidates the transaction id. */
    public void invalidate();

    /**
     * Checks whether or not this transaction id is valid.
     *
     * @return <tt>true if this is a valid 'live' transaction id, <tt>false</tt> otherwise.
     */
    public boolean isValid();

    /**
     * Checks if this transaction id is the same as another one.
     *
     * @param  o The object to compare to.
     *
     * @return <tt>true</tt>If the comparator is also an tx id the same as this one, <tt>false</tt> otherwise.
     */
    public boolean equals(Object o);

    /**
     * Computes a hashCode of tx ids to allow them to be used efficiently in hashing data structures.
     *
     * @return A hash code of the transaction id.
     */
    public int hashCode();
}
