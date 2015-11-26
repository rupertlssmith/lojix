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

/**
 * DistributedOutputStream is a remote version of the {@link java.io.OutputStream} API. See
 * {@link DistributedOutputStreamImpl} for information about how to use distributed output streams.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities
 * <tr>
 * <td>Provide Remote version of OutputStream API
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface DistributedOutputStream extends Remote
{
    /**
     * Flushes any buffered output to be written to this output stream.
     *
     * @throws IOException              If an I/O error occurs when accessing the stream.
     * @throws java.rmi.RemoteException If an RMI error occurs during remote access.
     */
    void flush() throws IOException;

    /**
     * Closes this output stream and releases any system resources associated with this output stream.
     *
     * @throws IOException              If an I/O error occurs when accessing the stream.
     * @throws java.rmi.RemoteException If an RMI error occurs during remote access.
     */
    void close() throws IOException;

    /**
     * Writes the specified byte <code>b</code> to this output stream.
     *
     * @param  b The byte to write.
     *
     * @throws IOException              If an I/O error occurs when accessing the stream.
     * @throws java.rmi.RemoteException If an RMI error occurs during remote access.
     */
    void write(int b) throws IOException;

    /**
     * Writes all bytes from the specified byte array <code>b</code> to this output stream.
     *
     * @param  b The bytes to write.
     *
     * @throws IOException              If an I/O error occurs when accessing the stream.
     * @throws java.rmi.RemoteException If an RMI error occurs during remote access.
     */
    void write(byte[] b) throws IOException;

    /**
     * Writes bytes from the specified byte array <code>b</code>, starting from index specified by <code>off</code>, and
     * continuing for <code>len</code> bytes, to this output stream.
     *
     * @param  b   The data.
     * @param  off The start offset in the data.
     * @param  len The number of bytes to write.
     *
     * @throws IOException              If an I/O error occurs when accessing the stream.
     * @throws java.rmi.RemoteException If an RMI error occurs during remote access.
     */
    void write(byte[] b, int off, int len) throws IOException;
}
