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

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Iterator;

/**
 * The client portion of the distributed iterator support. This class implements the {@link Iterator} interface for a
 * distributed iterator. Using distributed iterators, you can ship a collection across the network one element at a
 * time, thus transmitting only the data required by the application. Furthermore, by avoiding transmitting the entire
 * collection, you enable access to the initial elements of the collection quicker than would be possible through raw
 * serialization of a collection.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide client proxy to remote iterator.
 * <td> {@link DistributedIterator}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ClientIterator implements Iterator, Serializable
{
    /**
     * The remote iterator to which this client is referencing.
     *
     * @serial
     */
    private DistributedIterator source = null;

    /** Required constructor for serialization. */
    public ClientIterator()
    {
        super();
    }

    /**
     * Constructs a new ClientIterator using the named DistributedIterator as its remote source.
     *
     * @param src The server-based distributed iterator.
     */
    public ClientIterator(DistributedIterator src)
    {
        super();
        source = src;
    }

    /**
     * Check if more elements are available in this iterator.
     *
     * @return True if more elements are available in the iterator.
     */
    public boolean hasNext()
    {
        try
        {
            return source.hasNext();
        }
        catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches the next element from this iterator.
     *
     * @return The next element in the iterator.
     */
    public Object next()
    {
        try
        {
            Object ob = source.next();

            return ob;
        }
        catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** Removes the current element from this iterator if the underlying remote iterator supports it. */
    public void remove()
    {
        try
        {
            source.remove();
        }
        catch (RemoteException e)
        {
            throw new RuntimeException(e);
        }
    }
}
