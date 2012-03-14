package net.minecraft.src;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.src.forge.ForgeHooks;

public class NetworkManager
{
    /** Synchronization object used for read and write threads. */
    public static final Object threadSyncObject = new Object();

    /** The number of read threads spawned. Not really used on client side. */
    public static int numReadThreads;

    /** The number of write threads spawned. Not really used on client side. */
    public static int numWriteThreads;

    /** The object used for synchronization on the send queue. */
    private Object sendQueueLock = new Object();

    /** The socket used by this network manager. */
    private Socket networkSocket;
    private final SocketAddress remoteSocketAddress;

    /** The input stream connected to the socket. */
    private DataInputStream socketInputStream;

    /** The output stream connected to the socket. */
    private DataOutputStream socketOutputStream;

    /** Whether the network is currently operational. */
    private boolean isRunning = true;

    /**
     * Linked list of packets that have been read and are awaiting processing.
     */
    private List readPackets = Collections.synchronizedList(new ArrayList());

    /** Linked list of packets awaiting sending. */
    private List dataPackets = Collections.synchronizedList(new ArrayList());

    /** Linked list of packets with chunk data that are awaiting sending. */
    private List chunkDataPackets = Collections.synchronizedList(new ArrayList());

    /** A reference to the NetHandler object. */
    private NetHandler netHandler;

    /**
     * Whether this server is currently terminating. If this is a client, this is always false.
     */
    private boolean isServerTerminating = false;

    /** The thread used for writing. */
    private Thread writeThread;

    /** The thread used for reading. */
    private Thread readThread;

    /**
     * Whether this network manager is currently terminating (and should ignore further errors).
     */
    private boolean isTerminating = false;

    /** A String indicating why the network has shutdown. */
    private String terminationReason = "";
    private Object[] field_20101_t;

    /**
     * Counter used to detect read timeouts after 1200 failed attempts to read a packet.
     */
    private int timeSinceLastRead = 0;

    /**
     * The length in bytes of the packets in both send queues (data and chunkData).
     */
    private int sendQueueByteLength = 0;
    public static int[] field_28145_d = new int[256];
    public static int[] field_28144_e = new int[256];

    /**
     * Counter used to prevent us from sending too many chunk data packets one after another. The delay appears to be
     * set to 50.
     */
    public int chunkDataSendCounter = 0;
    private int field_20100_w = 50;

    public NetworkManager(Socket par1Socket, String par2Str, NetHandler par3NetHandler) throws IOException
    {
        this.networkSocket = par1Socket;
        this.remoteSocketAddress = par1Socket.getRemoteSocketAddress();
        this.netHandler = par3NetHandler;

        try
        {
            par1Socket.setSoTimeout(30000);
            par1Socket.setTrafficClass(24);
        }
        catch (SocketException var5)
        {
            System.err.println(var5.getMessage());
        }

        this.socketInputStream = new DataInputStream(par1Socket.getInputStream());
        this.socketOutputStream = new DataOutputStream(new BufferedOutputStream(par1Socket.getOutputStream(), 5120));
        this.readThread = new NetworkReaderThread(this, par2Str + " read thread");
        this.writeThread = new NetworkWriterThread(this, par2Str + " write thread");
        this.readThread.start();
        this.writeThread.start();
    }

    /**
     * Adds the packet to the correct send queue (chunk data packets go to a separate queue).
     */
    public void addToSendQueue(Packet par1Packet)
    {
        if (!this.isServerTerminating)
        {
            Object var2 = this.sendQueueLock;

            synchronized (this.sendQueueLock)
            {
                this.sendQueueByteLength += par1Packet.getPacketSize() + 1;

                if (par1Packet.isChunkDataPacket)
                {
                    this.chunkDataPackets.add(par1Packet);
                }
                else
                {
                    this.dataPackets.add(par1Packet);
                }
            }
        }
    }

    /**
     * Sends a data packet if there is one to send, or sends a chunk data packet if there is one and the counter is up,
     * or does nothing. If it sends a packet, it sleeps for 10ms.
     */
    private boolean sendPacket()
    {
        boolean var1 = false;

        try
        {
            Packet var2;
            Object var3;
            int var10001;
            int[] var10000;

            if (!this.dataPackets.isEmpty() && (this.chunkDataSendCounter == 0 || System.currentTimeMillis() - ((Packet)this.dataPackets.get(0)).creationTimeMillis >= (long)this.chunkDataSendCounter))
            {
                var3 = this.sendQueueLock;

                synchronized (this.sendQueueLock)
                {
                    var2 = (Packet)this.dataPackets.remove(0);
                    this.sendQueueByteLength -= var2.getPacketSize() + 1;
                }

                Packet.writePacket(var2, this.socketOutputStream);
                var10000 = field_28144_e;
                var10001 = var2.getPacketId();
                var10000[var10001] += var2.getPacketSize() + 1;
                var1 = true;
            }

            if (this.field_20100_w-- <= 0 && !this.chunkDataPackets.isEmpty() && (this.chunkDataSendCounter == 0 || System.currentTimeMillis() - ((Packet)this.chunkDataPackets.get(0)).creationTimeMillis >= (long)this.chunkDataSendCounter))
            {
                var3 = this.sendQueueLock;

                synchronized (this.sendQueueLock)
                {
                    var2 = (Packet)this.chunkDataPackets.remove(0);
                    this.sendQueueByteLength -= var2.getPacketSize() + 1;
                }

                Packet.writePacket(var2, this.socketOutputStream);
                var10000 = field_28144_e;
                var10001 = var2.getPacketId();
                var10000[var10001] += var2.getPacketSize() + 1;
                this.field_20100_w = 0;
                var1 = true;
            }

            return var1;
        }
        catch (Exception var8)
        {
            if (!this.isTerminating)
            {
                this.onNetworkError(var8);
            }

            return false;
        }
    }

    /**
     * Wakes reader and writer threads
     */
    public void wakeThreads()
    {
        this.readThread.interrupt();
        this.writeThread.interrupt();
    }

    /**
     * Reads a single packet from the input stream and adds it to the read queue. If no packet is read, it shuts down
     * the network.
     */
    private boolean readPacket()
    {
        boolean var1 = false;

        try
        {
            Packet var2 = Packet.readPacket(this.socketInputStream, this.netHandler.isServerHandler());

            if (var2 != null)
            {
                int[] var10000 = field_28145_d;
                int var10001 = var2.getPacketId();
                var10000[var10001] += var2.getPacketSize() + 1;

                if (!this.isServerTerminating)
                {
                    this.readPackets.add(var2);
                }

                var1 = true;
            }
            else
            {
                this.networkShutdown("disconnect.endOfStream", new Object[0]);
            }

            return var1;
        }
        catch (Exception var3)
        {
            if (!this.isTerminating)
            {
                this.onNetworkError(var3);
            }

            return false;
        }
    }

    /**
     * Used to report network errors and causes a network shutdown.
     */
    private void onNetworkError(Exception par1Exception)
    {
        par1Exception.printStackTrace();
        this.networkShutdown("disconnect.genericReason", new Object[] {"Internal exception: " + par1Exception.toString()});
    }

    /**
     * Shuts down the network with the specified reason. Closes all streams and sockets, spawns NetworkMasterThread to
     * stop reading and writing threads.
     */
    public void networkShutdown(String par1Str, Object ... par2ArrayOfObj)
    {
        if (this.isRunning)
        {
            this.isTerminating = true;
            this.terminationReason = par1Str;
            this.field_20101_t = par2ArrayOfObj;
            (new NetworkMasterThread(this)).start();
            this.isRunning = false;

            try
            {
                this.socketInputStream.close();
                this.socketInputStream = null;
            }
            catch (Throwable var6)
            {
                ;
            }

            try
            {
                this.socketOutputStream.close();
                this.socketOutputStream = null;
            }
            catch (Throwable var5)
            {
                ;
            }

            try
            {
                this.networkSocket.close();
                this.networkSocket = null;
            }
            catch (Throwable var4)
            {
                ;
            }
            ForgeHooks.onDisconnect(this, par1Str, par2ArrayOfObj);
        }
    }

    /**
     * Checks timeouts and processes all pending read packets.
     */
    public void processReadPackets()
    {
        if (this.sendQueueByteLength > 1048576)
        {
            this.networkShutdown("disconnect.overflow", new Object[0]);
        }

        if (this.readPackets.isEmpty())
        {
            if (this.timeSinceLastRead++ == 1200)
            {
                this.networkShutdown("disconnect.timeout", new Object[0]);
            }
        }
        else
        {
            this.timeSinceLastRead = 0;
        }

        int var1 = 1000;

        while (!this.readPackets.isEmpty() && var1-- >= 0)
        {
            Packet var2 = (Packet)this.readPackets.remove(0);
            var2.processPacket(this.netHandler);
        }

        this.wakeThreads();

        if (this.isTerminating && this.readPackets.isEmpty())
        {
            this.netHandler.handleErrorMessage(this.terminationReason, this.field_20101_t);
        }
    }

    /**
     * Shuts down the server. (Only actually used on the server)
     */
    public void serverShutdown()
    {
        if (!this.isServerTerminating)
        {
            this.wakeThreads();
            this.isServerTerminating = true;
            this.readThread.interrupt();
            (new ThreadMonitorConnection(this)).start();
        }
    }

    /**
     * Whether the network is operational.
     */
    static boolean isRunning(NetworkManager par0NetworkManager)
    {
        return par0NetworkManager.isRunning;
    }

    /**
     * Is the server terminating? Client side aways returns false.
     */
    static boolean isServerTerminating(NetworkManager par0NetworkManager)
    {
        return par0NetworkManager.isServerTerminating;
    }

    /**
     * Static accessor to readPacket.
     */
    static boolean readNetworkPacket(NetworkManager par0NetworkManager)
    {
        return par0NetworkManager.readPacket();
    }

    /**
     * Static accessor to sendPacket.
     */
    static boolean sendNetworkPacket(NetworkManager par0NetworkManager)
    {
        return par0NetworkManager.sendPacket();
    }

    static DataOutputStream getOutputStream(NetworkManager par0NetworkManager)
    {
        return par0NetworkManager.socketOutputStream;
    }

    static boolean isTerminating(NetworkManager par0NetworkManager)
    {
        return par0NetworkManager.isTerminating;
    }

    /**
     * Sends the network manager an error
     */
    static void sendError(NetworkManager par0NetworkManager, Exception par1Exception)
    {
        par0NetworkManager.onNetworkError(par1Exception);
    }

    /**
     * Returns the read thread.
     */
    static Thread getReadThread(NetworkManager par0NetworkManager)
    {
        return par0NetworkManager.readThread;
    }

    /**
     * Returns the write thread.
     */
    static Thread getWriteThread(NetworkManager par0NetworkManager)
    {
        return par0NetworkManager.writeThread;
    }
    
    /**
     * Retrieves the current associated network handler.
     * Added so modders don't have to use reflection.
     * @return The current registered Network Handler
     */
    public NetHandler getNetHandler()
    {
        return netHandler;
    }
}
