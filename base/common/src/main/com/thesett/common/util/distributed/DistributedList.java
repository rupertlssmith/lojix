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
package com.thesett.common.util.distributed;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A distributed list is a list that is designed to be used with distributed systems. The actual values in the list are
 * held in a java.util.ArrayList. Only the iterator method of ArrayList has been overriden in this class. It provides an
 * iterator that is serializable and can be sent over the network to be used by a client to access the contents of the
 * list one at a time. This means that elements of the list can be accessed as needed rather than sending the whole list
 * over the network at once.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide distributed iterator for a list <td> {@link DistributedIteratorImpl}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DistributedList extends ArrayList
{
    /**
     * Returns a distriubuted iterator that can provide elements of the list on demand over a remote connection.
     *
     * @return A {@link com.thesett.common.util.distributed.DistributedIterator} that provides the elements of the list
     *         on demand instead of all at once.
     */
    public Iterator iterator()
    {
        try
        {
            DistributedIteratorImpl di;

            di = new DistributedIteratorImpl(super.iterator());

            return new ClientIterator(di);
        }
        catch (RemoteException e)
        {
            // Rethrow the RemoteException as a RuntimeException so as not to conflict with the interface of ArrayList
            throw new RuntimeException("There was a RemoteExcpetion.", e);
        }
    }
}
