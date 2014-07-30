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
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * DistributedInputStream is a remote version of the {@link java.io.InputStream} API. See
 * {@link DistributedInputStreamImpl} for information about how to use distributed input streams.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Provide remote version of InputStream API.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface DistributedInputStream extends Remote
{
    /**
     * Returns the number of bytes that can be read (or skipped over) from this input stream without blocking by the
     * next caller of a method for this input stream.
     *
     * @return The number of bytes that can be read from this input stream without blocking.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public int available() throws IOException;

    /**
     * Closes this input stream and releases any system resources associated with the stream.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public void close() throws IOException;

    /**
     * Marks the current position in this input stream.
     *
     * @param  readlimit The maximum limit of bytes that can be read before the mark position becomes invalid.
     *
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public void mark(int readlimit) throws RemoteException;

    /**
     * Tests if this input stream supports the mark and reset methods.
     *
     * @return If this stream instance supports the mark and reset methods; false otherwise.
     *
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public boolean markSupported() throws RemoteException;

    /**
     * Reads the next byte of data from the input stream.
     *
     * @return The next byte of data, or -1 if the end of the stream is reached.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public int read() throws IOException;

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
    public ByteBlock read(byte[] b) throws IOException;

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
    public ByteBlock read(byte[] b, int off, int len) throws IOException;

    /**
     * Repositions this stream to the position at the time the mark method was last called on this input stream.
     *
     * @throws IOException     If an I/O error occurs when accessing the stream.
     * @throws RemoteException If an RMI error occurs during remote access.
     */
    public void reset() throws IOException;

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
    public long skip(long n) throws IOException;
}
