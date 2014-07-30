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
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * DistributedInputStreamImpl provides a wrapper mechanism for input stream that allows them to be treated as remote
 * objects. It implements the remote version of the InputStream API provides by {@link DistributedInputStream}. Its
 * constructor takes an input stream as an argument and the rest of the methods are simply wrapper methods that delegate
 * their action to the wrapped input stream.
 *
 * <p/>A client proxy to the distributed input stream is implemented by {@link ClientInputStream}. This class is an
 * extension of {@link java.io.InputStream} and is serializable. This allows input streams to passed and returned as
 * arguments to RMI calls and for other classes that work with input streams to use them remotely without being aware of
 * the remote nature of the input stream.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide RMI wrapper to input stream. <td> {@link java.io.InputStream}
 * </table></pre>
 *
 * @author Rupert Smith
 * @todo   Remote input streams will not work efficiently if they are read a byte at a time for large amounts of data.
 *         Existing code may therefore not work well with simple distributed input streams. If this is a problem a
 *         caching/buffered distributed input stream can be written that gets its input in chunks and caches these in
 *         the client proxy and then feeds the byte at a time reader methods from this cache. The chunk size can be set
 *         as a parameter allowing an optimal chunk size to be chosen to fit different purposes.
 */
public class DistributedInputStreamImpl extends UnicastRemoteObject implements DistributedInputStream
{
    /** The local input stream that serves as the source for distributed one. */
    private InputStream source = null;

    /**
     * Builds a distrubted input stream that wraps a local one.
     *
     * @param  src the local source input stream.
     *
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public DistributedInputStreamImpl(InputStream src) throws RemoteException
    {
        super();
        source = src;
    }

    /**
     * Returns the number of bytes that can be read (or skipped over) from this input stream without blocking by the
     * next caller of a method for this input stream.
     *
     * @return The number of bytes that can be read from this input stream without blocking.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public int available() throws IOException
    {
        return source.available();
    }

    /**
     * Closes this input stream and releases any system resources associated with the stream.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public void close() throws IOException
    {
        source.close();
    }

    /**
     * Marks the current position in this input stream.
     *
     * @param  readlimit The maximum limit of bytes that can be read before the mark position becomes invalid.
     *
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public void mark(int readlimit) throws RemoteException
    {
        source.mark(readlimit);
    }

    /**
     * Tests if this input stream supports the mark and reset methods.
     *
     * @return If this stream instance supports the mark and reset methods; false otherwise.
     *
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public boolean markSupported() throws RemoteException
    {
        return source.markSupported();
    }

    /**
     * Reads the next byte of data from the input stream.
     *
     * @return The next byte of data, or -1 if the end of the stream is reached.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public int read() throws IOException
    {
        return source.read();
    }

    /**
     * Reads some number of bytes from the input stream and stores them into the buffer array b. The bytes are also
     * returned wrapped in a byte block so that they can be returnd over RMI.
     *
     * @param  b The byte array to store the results of the read into.
     *
     * @return The same results but wrapped in byte block.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public ByteBlock read(byte[] b) throws IOException
    {
        int count = source.read(b);

        return new ByteBlock(b, count);
    }

    /**
     * Reads up to len bytes of data from the input stream into an array of bytes.
     *
     * @param  b   The byte array to store the results of the read into.
     * @param  off The start offset in array b at which the data is written.
     * @param  len The maximum number of bytes to read.
     *
     * @return The same results as stored in parameter b but wrapped in byte block.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public ByteBlock read(byte[] b, int off, int len) throws IOException
    {
        int count = source.read(b, off, len);

        return new ByteBlock(b, count);
    }

    /**
     * Repositions this stream to the position at the time the mark method was last called on this input stream.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public void reset() throws IOException
    {
        source.reset();
    }

    /**
     * Skips over and discards n bytes of data from this input stream.
     *
     * @param  n The number of bytes to skip.
     *
     * @return The actual number of bytes skipped.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public long skip(long n) throws IOException
    {
        return source.skip(n);
    }
}
