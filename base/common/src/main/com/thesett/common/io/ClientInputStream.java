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
package com.thesett.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * This is a client proxy to a distributed input stream. It extends the {@link java.io.InputStream} class and is
 * therefore an InputStream itself and can be used in any situation in which an InputStream may be used. This client is
 * serializable and built from a distributed input stream. It provides a convenient way to return an input stream
 * accross an RMI call. It also hides the RemoteExceptions that may result from using RMI and returns these as
 * IOExceptions instead.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * <tr><td>Provide proxy wrapper to distributed input stream
 * <th> {@link DistributedInputStream}
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class ClientInputStream extends InputStream implements Serializable
{
    /** Used for logging. */
    //private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(ClientInputStream.class.getName());

    /** The source end of the distributed input stream. */
    DistributedInputStream source;

    /**
     * Builds a client input stream proxy from a distributed input stream.
     *
     * @param src The distributes input stream to wrap as an InputStream.
     */
    public ClientInputStream(DistributedInputStream src)
    {
        super();
        source = src;
    }

    /**
     * Returns the number of bytes that can be read (or skipped over) from this input stream without blocking by the
     * next caller of a method for this input stream.
     *
     * @return The number of bytes that can be read from this input stream.
     *
     * @throws IOException If an I/O error occurs when accessing the stream or a remote exception occurs.
     */
    public int available() throws IOException
    {
        try
        {
            return source.available();
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }

    /**
     * Closes this input stream and releases any system resources associated with the stream.
     *
     * @throws IOException If an I/O error occurs when accessing the stream or a remote exception occurs.
     */
    public void close() throws IOException
    {
        try
        {
            source.close();
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }

    /**
     * Marks the current position in this input stream.
     *
     * @param readlimit The maximum limit of bytes that can be read before the mark position becomes invalid.
     */
    public void mark(int readlimit)
    {
        try
        {
            source.mark(readlimit);
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("There was a RemoteException.", e);
        }
    }

    /**
     * Tests if this input stream supports the mark and reset methods.
     *
     * @return True if this stream instance supports the mark and reset methods, false otherwise.
     */
    public boolean markSupported()
    {
        try
        {
            return source.markSupported();
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("There was a RemoteException.", e);
        }
    }

    /**
     * Reads the next byte of data from the input stream.
     *
     * @return The next byte of data, or -1 if the end of the stream is reached.
     *
     * @throws IOException If an I/O error occurs when accessing the stream or a remote exception occurs.
     */
    public int read() throws IOException
    {
        try
        {
            return source.read();
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }

    /**
     * Reads some number of bytes from the input stream and stores them into the buffer array b.
     *
     * @param  b The buffer into which the data is read.
     *
     * @return The total number of bytes read into the buffer, or -1 is there is no more data because the end of the
     *         stream has been reached.
     *
     * @throws IOException If an I/O error occurs when accessing the stream or a remote exception occurs.
     */
    public int read(byte[] b) throws IOException
    {
        try
        {
            ByteBlock block = source.read(b);

            System.arraycopy(block.data, 0, b, 0, block.count);

            return block.count;
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }

    /**
     * Reads up to len bytes of data from the input stream into an array of bytes.
     *
     * @param  b   The buffer into which the data is read.
     * @param  off The start offset in array b at which the data is written.
     * @param  len The maximum number of bytes to read.
     *
     * @return The total number of bytes read into the buffer, or -1 is there is no more data because the end of the
     *         stream has been reached.
     *
     * @throws IOException If an IOException occurs in the wrapped distributed input stream or a remote exception
     *                     occurs.
     */
    public int read(byte[] b, int off, int len) throws IOException
    {
        try
        {
            ByteBlock block = source.read(b, off, len);

            System.arraycopy(block.data, off, b, off, len);

            return block.count;
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }

    /**
     * Repositions this stream to the position at the time the mark method was last called on this input stream.
     *
     * @throws IOException If a remote exception occurs.
     */
    public void reset() throws IOException
    {
        try
        {
            source.reset();
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }

    /**
     * Skips over and discards n bytes of data from this input stream.
     *
     * @param  n The number of bytes to skip.
     *
     * @return The actual number of bytes skipped.
     *
     * @throws IOException If an IOException occurs in the wrapped distributed input stream or a remote exception
     *                     occurs.
     */
    public long skip(long n) throws IOException
    {
        try
        {
            return source.skip(n);
        }
        catch (RemoteException e)
        {
            throw new IOException("There was a Remote Exception.", e);
        }
    }
}
