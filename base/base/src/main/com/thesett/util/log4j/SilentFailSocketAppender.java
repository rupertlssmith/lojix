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
package com.thesett.util.log4j;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * SilentFailSocketAppender is a Log4J socket appender that fails silently and quickly without retrying when a logging
 * socket that it tries to connect to cannot be opened. This is extremely usefull for unit testing where a log reciever
 * may or may not be open on a socket. Running many JUnit tests in different virtual machines means that Log4J is
 * repeatedly restarted. Waiting for a connection to a non-existant socket to time out is time consuming and makes the
 * tests run far slower than if no connection is attempted. This appender fails quickly and silently allowing the tests
 * to run wihtout too much of a noticeable slow down.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities <th> Collaborations
 * <tr><td> Send Log4J events to a socket.
 * </table></pre>
 *
 * @author Rupert Smith
 */
public class SilentFailSocketAppender extends AppenderSkeleton
{
    /** The default port number of remote logging server (4560). */
    static final int DEFAULT_PORT = 4560;

    /** The default reconnection delay (30000 milliseconds or 30 seconds). */
    static final int DEFAULT_RECONNECTION_DELAY = 30000;

    /** Reset the ObjectOutputStream every 1 calls. */
    private static final int RESET_FREQUENCY = 1;

    /**
     * Holds the host name as String in addition to the resolved InetAddress so that it can be returned via getOption().
     */
    String remoteHost;

    /** The address to log to. */
    InetAddress address;

    /** The port to og to. */
    int port = DEFAULT_PORT;

    /** The output stream to write to the socket through. */
    ObjectOutputStream oos;

    /** The delay before attempting to reconnect when a socket cannot be opened. */
    int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;

    /** When set to true location information will be sent to the log event reciever on the other end of the socket. */
    boolean locationInfo;

    /** The connector to open the socket with. */
    private Connector connector;

    /**
     * Used to count the number of log events sent. The object output stream is reset on every n events as counted by
     * this counter. See {@link #RESET_FREQUENCY}.
     */
    int counter;

    /** Creates a new SilentFailSocketAppender object. */
    public SilentFailSocketAppender()
    {
    }

    /**
     * Connects to remote server at <code>address</code> and <code>port</code>.
     *
     * @param address The address to connect to.
     * @param port    The port to connect to.
     */
    public SilentFailSocketAppender(InetAddress address, int port)
    {
        this.address = address;
        this.remoteHost = address.getHostName();
        this.port = port;
        connect(address, port);
    }

    /**
     * Connects to remote server at <code>host</code> and <code>port</code>.
     *
     * @param host The address to connect to.
     * @param port The port to connect to.
     */
    public SilentFailSocketAppender(String host, int port)
    {
        this.port = port;
        this.address = getAddressByName(host);
        this.remoteHost = host;
        connect(address, port);
    }

    /** Connect to the specified <b>RemoteHost</b> and <b>Port</b>. */
    public void activateOptions()
    {
        connect(address, port);
    }

    /**
     * Close this appender.
     *
     * <p/>This will mark the appender as closed and call then {@link #cleanUp} method.
     */
    public synchronized void close()
    {
        if (closed)
        {
            return;
        }

        this.closed = true;
        cleanUp();
    }

    /** Drop the connection to the remote host and release the underlying connector thread if it has been created. */
    public void cleanUp()
    {
        if (oos != null)
        {
            try
            {
                oos.close();
            }
            catch (IOException e)
            {
                LogLog.error("Could not close oos.", e);
            }

            oos = null;
        }

        if (connector != null)
        {
            // LogLog.debug("Interrupting the connector.");
            connector.interrupted = true;
            connector = null; // allow gc
        }
    }

    /**
     * Appends a logging event to the remote event reciever.
     *
     * @param event The logging event to send.
     */
    public void append(LoggingEvent event)
    {
        if (event == null)
        {
            return;
        }

        if (address == null)
        {
            errorHandler.error("No remote host is set for SocketAppender named \"" + this.name + "\".");

            return;
        }

        if (oos != null)
        {
            try
            {
                if (locationInfo)
                {
                    event.getLocationInformation();
                }

                oos.writeObject(event);

                // LogLog.debug("=========Flushing.");
                oos.flush();

                if (++counter >= RESET_FREQUENCY)
                {
                    counter = 0;

                    // Failing to reset the object output stream every now and
                    // then creates a serious memory leak.
                    // System.err.println("Doing oos.reset()");
                    oos.reset();
                }
            }
            catch (IOException e)
            {
                oos = null;
                LogLog.warn("Detected problem with connection: " + e);

                if (reconnectionDelay > 0)
                {
                    fireConnector();
                }
            }
        }
    }

    /**
     * The SocketAppender does not use a layout. Hence, this method returns <code>false</code>.
     *
     * @return Always false.
     */
    public boolean requiresLayout()
    {
        return false;
    }

    /**
     * The <b>RemoteHost</b> option takes a string value which should be the host name of the server where a SocketNode
     * is running.
     *
     * @param host The host to connect to.
     */
    public void setRemoteHost(String host)
    {
        address = getAddressByName(host);
        remoteHost = host;
    }

    /**
     * Returns value of the <b>RemoteHost</b> option.
     *
     * @return The remote host.
     */
    public String getRemoteHost()
    {
        return remoteHost;
    }

    /**
     * The <b>Port</b> option takes a positive integer representing the port where the server is waiting for
     * connections.
     *
     * @param port The port to connect to.
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * Returns value of the <b>Port</b> option.
     *
     * @return The remote port.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * The <b>LocationInfo</b> option takes a boolean value. If true, the information sent to the remote host will
     * include location information. By default no location information is sent to the server.
     *
     * @param locationInfo Set to true to send location information to the reciever.
     */
    public void setLocationInfo(boolean locationInfo)
    {
        this.locationInfo = locationInfo;
    }

    /**
     * Returns value of the <b>LocationInfo</b> option.
     *
     * @return The setting of the location info flag.
     */
    public boolean getLocationInfo()
    {
        return locationInfo;
    }

    /**
     * The <b>ReconnectionDelay</b> option takes a positive integer representing the number of milliseconds to wait
     * between each failed connection attempt to the server. The default value of this option is 30000 which corresponds
     * to 30 seconds.
     *
     * <p/>Setting this option to zero turns off reconnection capability.
     *
     * @param delay The time to wait between re-connection attempts.
     */
    public void setReconnectionDelay(int delay)
    {
        this.reconnectionDelay = delay;
    }

    /**
     * Returns value of the <b>ReconnectionDelay</b> option.
     *
     * @return The reconnection delay in force.
     */
    public int getReconnectionDelay()
    {
        return reconnectionDelay;
    }

    /**
     * Get an inet address from a host name.
     *
     * @param  host The host name to look up.
     *
     * @return An inet address for that host.
     */
    static InetAddress getAddressByName(String host)
    {
        try
        {
            return InetAddress.getByName(host);
        }
        catch (Exception e)
        {
            LogLog.error("Could not find address of [" + host + "].", e);

            return null;
        }
    }

    /**
     * Connects to remote server at <code>address</code> and <code>port</code>.
     *
     * @param address The address to connect to.
     * @param port    The port to connect to.
     */
    void connect(InetAddress address, int port)
    {
        if (this.address == null)
        {
            return;
        }

        try
        {
            // First, close the previous connection if any.
            cleanUp();

            Socket socket = new Socket();
            SocketAddress socketAddr = new InetSocketAddress(address, port);

            socket.connect(socketAddr, 10);

            oos = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e)
        {
            // Silently fail and do not retry later.
            // Exception noted so can be ignored.
            e = null;

            /*
             * String msg = "Could not connect to remote log4j server at [" +address.getHostName()+"].";
             * if(reconnectionDelay > 0) { msg += " We will try again later."; fireConnector(); // fire the connector
             * thread } LogLog.error(msg, e); */
        }
    }

    /** Starts a new connector thread to do? */
    void fireConnector()
    {
        if (connector == null)
        {
            LogLog.debug("Starting a new connector thread.");
            connector = new Connector();
            connector.setDaemon(true);
            connector.setPriority(Thread.MIN_PRIORITY);
            connector.start();
        }
    }

    /**
     * The Connector will reconnect when the server becomes available again. It does this by attempting to open a new
     * connection every <code>reconnectionDelay</code> milliseconds.
     *
     * <p/>It stops trying whenever a connection is established. It will restart to try reconnect to the server when
     * previpously open connection is droppped.
     *
     * <p>
     * <table id="crc">
     * <caption>CRC Card</caption>
     * <tr>
     * <th>Responsibilities
     * <th>Collaborations
     *
     * <tr>
     * <td>Automatically re-connect a connection if it closes.
     * </table>
     *
     * @author Rupert Smith
     */
    class Connector extends Thread
    {
        /** Signals that the connection has been lost and needs to be re-established. */
        boolean interrupted;

        /** The main thread. */
        public void run()
        {
            Socket socket;

            while (!interrupted)
            {
                try
                {
                    sleep(reconnectionDelay);
                    LogLog.debug("Attempting connection to " + address.getHostName());
                    socket = new Socket(address, port);

                    synchronized (this)
                    {
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        connector = null;
                        LogLog.debug("Connection established. Exiting connector thread.");

                        break;
                    }
                }
                catch (InterruptedException e)
                {
                    LogLog.debug("Connector interrupted. Leaving loop.");

                    // Exception noted so can be ignored.
                    e = null;

                    return;
                }
                catch (java.net.ConnectException e)
                {
                    LogLog.debug("Remote host " + address.getHostName() + " refused connection.", e);
                }
                catch (IOException e)
                {
                    LogLog.debug("Could not connect to " + address.getHostName() + ". Exception is " + e);
                }
            }
            // LogLog.debug("Exiting Connector.run() method.");
        }
    }
}
