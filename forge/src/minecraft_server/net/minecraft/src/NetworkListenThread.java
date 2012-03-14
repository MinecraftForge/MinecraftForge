package net.minecraft.src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;

public class NetworkListenThread
{
    /** Reference to the logger. */
    public static Logger logger = Logger.getLogger("Minecraft");
    private ServerSocket serverSocket;
    private Thread networkAcceptThread;

    /** Whether the network listener object is listening. */
    public volatile boolean isListening = false;
    private int field_977_f = 0;

    /** list of all people currently trying to connect to the server */
    private ArrayList pendingConnections = new ArrayList();

    /** list of all currently connected players */
    private ArrayList playerList = new ArrayList();

    /** Reference to the MinecraftServer object. */
    public MinecraftServer mcServer;
    private HashMap field_35506_i = new HashMap();

    public NetworkListenThread(MinecraftServer par1MinecraftServer, InetAddress par2InetAddress, int par3) throws IOException
    {
        this.mcServer = par1MinecraftServer;
        this.serverSocket = new ServerSocket(par3, 0, par2InetAddress);
        this.serverSocket.setPerformancePreferences(0, 2, 1);
        this.isListening = true;
        this.networkAcceptThread = new NetworkAcceptThread(this, "Listen thread", par1MinecraftServer);
        this.networkAcceptThread.start();
    }

    public void func_35505_a(Socket par1Socket)
    {
        InetAddress var2 = par1Socket.getInetAddress();
        HashMap var3 = this.field_35506_i;

        synchronized (this.field_35506_i)
        {
            this.field_35506_i.remove(var2);
        }
    }

    /**
     * adds this connection to the list of currently connected players
     */
    public void addPlayer(NetServerHandler par1NetServerHandler)
    {
        this.playerList.add(par1NetServerHandler);
    }

    /**
     * adds a new pending connection to the waiting list
     */
    private void addPendingConnection(NetLoginHandler par1NetLoginHandler)
    {
        if (par1NetLoginHandler == null)
        {
            throw new IllegalArgumentException("Got null pendingconnection!");
        }
        else
        {
            this.pendingConnections.add(par1NetLoginHandler);
        }
    }

    /**
     * Handles all incoming connections and packets
     */
    public void handleNetworkListenThread()
    {
        int var1;

        for (var1 = 0; var1 < this.pendingConnections.size(); ++var1)
        {
            NetLoginHandler var2 = (NetLoginHandler)this.pendingConnections.get(var1);

            try
            {
                var2.tryLogin();
            }
            catch (Exception var5)
            {
                var2.kickUser("Internal server error");
                logger.log(Level.WARNING, "Failed to handle packet: " + var5, var5);
            }

            if (var2.finishedProcessing)
            {
                this.pendingConnections.remove(var1--);
            }

            var2.netManager.wakeThreads();
        }

        for (var1 = 0; var1 < this.playerList.size(); ++var1)
        {
            NetServerHandler var6 = (NetServerHandler)this.playerList.get(var1);

            try
            {
                var6.handlePackets();
            }
            catch (Exception var4)
            {
                logger.log(Level.WARNING, "Failed to handle packet: " + var4, var4);
                var6.kickPlayer("Internal server error");
            }

            if (var6.connectionClosed)
            {
                this.playerList.remove(var1--);
            }

            var6.netManager.wakeThreads();
        }
    }

    /**
     * Gets the server socket.
     */
    static ServerSocket getServerSocket(NetworkListenThread par0NetworkListenThread)
    {
        return par0NetworkListenThread.serverSocket;
    }

    static HashMap func_35504_b(NetworkListenThread par0NetworkListenThread)
    {
        return par0NetworkListenThread.field_35506_i;
    }

    static int func_712_b(NetworkListenThread par0NetworkListenThread)
    {
        return par0NetworkListenThread.field_977_f++;
    }

    static void func_716_a(NetworkListenThread par0NetworkListenThread, NetLoginHandler par1NetLoginHandler)
    {
        par0NetworkListenThread.addPendingConnection(par1NetLoginHandler);
    }
}
