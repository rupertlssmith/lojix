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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Wraps an {@link java.util.Iterator} so that it can act as a distributed iterator. A distributed iterator is an
 * iterator where the collection is stored on a server and elements are transmitted across the network one element at a
 * time on demand. This contrasts with serialization of the collection, where the entire collection is transmitted
 * across the network at once.
 *
 * <p/>If you have a collection whose elements you want to make available across the network using the distributed
 * iterator paradigm, you retrieve an iterator for the collection and wrap it with a DistributedIterator implementation.
 * You then pass the distributed iterator to a {@link com.thesett.common.util.distributed.ClientIterator} and pass that
 * across the network. Consider the following RMI method that returns a distributed iterator for its remote method
 * cats():
 *
 * <pre>
 * private ArrayList cats;
 *
 * public Iterator cats() throws RemoteException {
 *     DistributedIterator dist = new DistributedIteratorImpl(cats.iterator());
 *     ClientIterator it = new ClientIterator(dist);
 *
 *     return it;
 * }
 * </pre>
 *
 * <p/>The result of this method is that an empty iterator is sent across the network to the client. That empty iterator
 * knows how to retrieve each cat from the <CODE>cats ArrayList</CODE> from the server on demand as the client
 * application calls for them. If the client only asks for the first cat, only the first cat is ever sent across the
 * network. If the collection of cats contains 1 million cats, the client does not need to wait on that entire
 * collection to be transmitted across the network before it can access the first cat.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Check if there is a next elements
 * <tr><td>Return the next element
 * <tr><td>Remove the current element
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface DistributedIterator extends Remote
{
    /**
     * Checks if more elements are available in the iterator.
     *
     * @return True if more elements are available in the iterator.
     *
     * @throws RemoteException If there is an error during remote access.
     */
    boolean hasNext() throws RemoteException;

    /**
     * Fetches the next element in the iterator.
     *
     * @return The next element in the iterator.
     *
     * @throws RemoteException If there is an error during remote access.
     */
    Object next() throws RemoteException;

    /**
     * Removes the current element from this iterator if the underlying remote iterator supports it.
     *
     * @throws RemoteException If there is an error during remote access.
     */
    void remove() throws RemoteException;
}
