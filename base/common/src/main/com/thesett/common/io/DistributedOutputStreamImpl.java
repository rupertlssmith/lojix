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
package com.thesett.common.io;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * DistributedOutputStreamImpl provides a wrapper mechanism for output stream that allows them to be treated as remote
 * objects. It implements the remote version of the OutputStream API provides by {@link DistributedOutputStream}. Its
 * constructor takes an output stream as an argument and the rest of the methods are simply wrapper methods that
 * delegate their action to the wrapped output stream.
 *
 * <p>A client proxy to the distributed output stream is implemented by {@link ClientOutputStream}. This class is an
 * extension of {@link java.io.OutputStream} and is serializable. This allows output streams to passed and returned as
 * arguments to RMI calls and for other classes that work with output streams to use them remotely without being aware
 * of the remote nature of the output stream.
 *
 * <p>@todo Remote output streams will not work efficiently if they are written a byte at a time for large amounts of
 * data. Existing code may therefore not work well with simple distributed output streams. If this is a problem a
 * caching/buffered distributed output stream can be written that gets its output in chunks and caches these in the
 * client proxy and then feeds the byte at a time reader methods from this cache. The chunk size can be set as a
 * parameter allowing an optimal chunk size to be chosen to fit different purposes.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide RMI wrapper to output stream <td> {@link java.io.OutputStream}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class DistributedOutputStreamImpl extends UnicastRemoteObject implements DistributedOutputStream
{
    /** The local output stream that serves as the sink for this distributed one. */
    private OutputStream stream = null;

    /**
     * Builds a distrubted output stream that wraps a local one.
     *
     * @param  dest the local destination output stream.
     *
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public DistributedOutputStreamImpl(OutputStream dest) throws RemoteException
    {
        super();
        stream = dest;
    }

    /**
     * Flushes any buffered output to be written to this output stream.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public void flush() throws IOException
    {
        stream.flush();
    }

    /**
     * Closes this output stream and releases any system resources associated with this output stream.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public void close() throws IOException
    {
        stream.close();
    }

    /**
     * Writes the specified byte <code>b</code> to this output stream.
     *
     * @param  b The byte to write.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public void write(int b) throws IOException
    {
        stream.write(b);
    }

    /**
     * Writes all bytes from the specified byte array <code>b</code> to this output stream.
     *
     * @param  b The bytes to write.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public void write(byte[] b) throws IOException
    {
        stream.write(b);
    }

    /**
     * Writes bytes from the specified byte array <code>b</code>, starting from index specified by <code>off</code>, and
     * continuing for <code>len</code> bytes, to this output stream.
     *
     * @param  b   The data.
     * @param  off The start offset in the data.
     * @param  len The number of bytes to write.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public void write(byte[] b, int off, int len) throws IOException
    {
        stream.write(b, off, len);
    }
}
