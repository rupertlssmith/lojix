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
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * This is a client proxy to a distributed output stream. It extends the {@link java.io.OutputStream} class and is
 * therefore an OutputStream itself and can be used in any situation in which an OutputStream may be used. This client
 * is serializable and built from a distributed output stream. It provides a convenient way to return an output stream
 * accross an RMI call. It also hides the RemoteExceptions that may result from using RMI and returns these as
 * IOExceptions instead.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide proxy wrapper to distributed output stream
 * <th> {@link DistributedOutputStream}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ClientOutputStream extends OutputStream implements Serializable
{
    /** The sink end of the distributed output stream. */
    DistributedOutputStream sink;

    /**
     * Builds a client output stream proxy from a distributed output stream.
     *
     * @param dest The distributed output stream to wrap and send all data to.
     */
    public ClientOutputStream(DistributedOutputStream dest)
    {
        super();
        sink = dest;
    }

    /**
     * Flushes any buffered output to be written to this output stream.
     *
     * @throws IOException If an IOException occurs in the wrapped sink stream or a remote exception occurs.
     */
    public void flush() throws IOException
    {
        try
        {
            sink.flush();
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }

    /**
     * Closes this output stream and releases any system resources associated with the stream.
     *
     * @throws IOException If an IOException occurs in the wrapped sink stream or a remote exception occurs.
     */
    public void close() throws IOException
    {
        try
        {
            sink.close();
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }

    /**
     * Writes the specified byte <code>b</code> to this output stream.
     *
     * @param  b The byte to write.
     *
     * @throws IOException If an IOException occurs in the wrapped sink stream or a remote exception occurs.
     */
    public void write(int b) throws IOException
    {
        try
        {
            sink.write(b);
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }

    /**
     * Writes all bytes from the specified byte array <code>b</code> to this output stream.
     *
     * @param  b The bytes to write.
     *
     * @throws IOException If an IOException occurs in the wrapped sink stream or a remote exception occurs.
     */
    public void write(byte[] b) throws IOException
    {
        try
        {
            sink.write(b);
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }

    /**
     * Writes bytes from the specified byte array <code>b</code>, starting from index specified by <code>off</code>, and
     * continuing for <code>len</code> bytes, to this output stream.
     *
     * @param  b   The data.
     * @param  off The start offset in the data.
     * @param  len The number of bytes to write.
     *
     * @throws IOException If an IOException occurs in the wrapped sink stream or a remote exception occurs.
     */
    public void write(byte[] b, int off, int len) throws IOException
    {
        try
        {
            sink.write(b, off, len);
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }
}
