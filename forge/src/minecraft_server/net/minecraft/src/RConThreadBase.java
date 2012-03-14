package net.minecraft.src;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class RConThreadBase implements Runnable
{
    /** True i */
    protected boolean running = false;

    /** Reference to the server object */
    protected IServer server;

    /** Thread for this runnable class */
    protected Thread rconThread;
    protected int field_40415_d = 5;

    /** A list of registered DatagramSockets */
    protected List socketList = new ArrayList();

    /** A list of registered ServerSockets */
    protected List serverSocketList = new ArrayList();

    RConThreadBase(IServer par1IServer)
    {
        this.server = par1IServer;

        if (this.server.isDebuggingEnabled())
        {
            this.logWarning("Debugging is enabled, performance maybe reduced!");
        }
    }

    /**
     * Creates a new Thread object from this class and starts running
     */
    public synchronized void startThread()
    {
        this.rconThread = new Thread(this);
        this.rconThread.start();
        this.running = true;
    }

    /**
     * Returns true if the Thread is running, false otherwise
     */
    public boolean isRunning()
    {
        return this.running;
    }

    /**
     * Log information message
     */
    protected void logInfo(String par1Str)
    {
        this.server.logIn(par1Str);
    }

    /**
     * Log message
     */
    protected void log(String par1Str)
    {
        this.server.log(par1Str);
    }

    /**
     * Log warning message
     */
    protected void logWarning(String par1Str)
    {
        this.server.logWarning(par1Str);
    }

    /**
     * Log severe error message
     */
    protected void logSevere(String par1Str)
    {
        this.server.logSevere(par1Str);
    }

    /**
     * Returns the number of players on the server
     */
    protected int getNumberOfPlayers()
    {
        return this.server.playersOnline();
    }

    /**
     * Registers a DatagramSocket with this thread
     */
    protected void registerSocket(DatagramSocket par1DatagramSocket)
    {
        this.logInfo("registerSocket: " + par1DatagramSocket);
        this.socketList.add(par1DatagramSocket);
    }

    /**
     * Closes the specified Da
     */
    protected boolean closeSocket(DatagramSocket par1DatagramSocket, boolean par2)
    {
        this.logInfo("closeSocket: " + par1DatagramSocket);

        if (null == par1DatagramSocket)
        {
            return false;
        }
        else
        {
            boolean var3 = false;

            if (!par1DatagramSocket.isClosed())
            {
                par1DatagramSocket.close();
                var3 = true;
            }

            if (par2)
            {
                this.socketList.remove(par1DatagramSocket);
            }

            return var3;
        }
    }

    /**
     * Closes the specified ServerSocket
     */
    protected boolean closeServerSocket(ServerSocket par1ServerSocket)
    {
        return this.closeServerSocket_do(par1ServerSocket, true);
    }

    /**
     * Closes the specified ServerSocket
     */
    protected boolean closeServerSocket_do(ServerSocket par1ServerSocket, boolean par2)
    {
        this.logInfo("closeSocket: " + par1ServerSocket);

        if (null == par1ServerSocket)
        {
            return false;
        }
        else
        {
            boolean var3 = false;

            try
            {
                if (!par1ServerSocket.isClosed())
                {
                    par1ServerSocket.close();
                    var3 = true;
                }
            }
            catch (IOException var5)
            {
                this.logWarning("IO: " + var5.getMessage());
            }

            if (par2)
            {
                this.serverSocketList.remove(par1ServerSocket);
            }

            return var3;
        }
    }

    /**
     * Closes all of the opened sockets
     */
    protected void closeAllSockets()
    {
        this.clos(false);
    }

    protected void clos(boolean par1)
    {
        int var2 = 0;
        Iterator var3 = this.socketList.iterator();

        while (var3.hasNext())
        {
            DatagramSocket var4 = (DatagramSocket)var3.next();

            if (this.closeSocket(var4, false))
            {
                ++var2;
            }
        }

        this.socketList.clear();
        var3 = this.serverSocketList.iterator();

        while (var3.hasNext())
        {
            ServerSocket var5 = (ServerSocket)var3.next();

            if (this.closeServerSocket_do(var5, false))
            {
                ++var2;
            }
        }

        this.serverSocketList.clear();

        if (par1 && 0 < var2)
        {
            this.logWarning("Force closed " + var2 + " sockets");
        }
    }
}
