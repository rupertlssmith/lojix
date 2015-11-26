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
package com.thesett.common.util.distributed;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;

/**
 * Implements the {@link com.thesett.common.util.distributed.DistributedIterator} interface by referencing a local
 * {@link java.util.Iterator}.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Check if there is a next elements
 * <td> {@link java.util.Iterator}
 * <tr><td>Return the next element <td> {@link java.util.Iterator}
 * <tr><td>Remove the current element <td> {@link java.util.Iterator}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DistributedIteratorImpl extends UnicastRemoteObject implements DistributedIterator
{
    /** The local iterator that serves as the source for the elements of the distributed iterator. */
    private Iterator source;

    /**
     * Constructs a new <code>DistributedIteratorImpl</code> using the specified local iterator as a data source.
     *
     * @param  src The local iterator.
     *
     * @throws java.rmi.RemoteException Could not export the iterator.
     */
    public DistributedIteratorImpl(Iterator src) throws RemoteException
    {
        super();

        source = src;
    }

    /**
     * Checks if more elements are available in the iterator.
     *
     * @return True if more elements are available in the iterator.
     */
    public boolean hasNext()
    {
        return source.hasNext();
    }

    /**
     * Fetches the next element in the iterator.
     *
     * @return the next element in the iterator.
     */
    public Object next()
    {
        Object ob = source.next();

        return ob;
            // return source.next();
    }

    /**
     * This operation is unsupported in this implementation.
     *
     * @throws java.lang.UnsupportedOperationException Always thrown.
     */
    public void remove()
    {
        throw new UnsupportedOperationException("Cannot remove from a " + "distributed iterator.");
    }
}
