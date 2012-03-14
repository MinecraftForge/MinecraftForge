package net.minecraft.src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class RConThreadMain extends RConThreadBase
{
    /** Port RCon is running on */
    private int rconPort;

    /** Port the server is running on */
    private int serverPort;

    /** Hostname RCon is running on */
    private String hostname;

    /** The RCon ServerSocke */
    private ServerSocket serverSocket = null;

    /** The RCon password */
    private String rconPassword;

    /** A map of client addresses to their running Threads */
    private HashMap clientThreads;

    public RConThreadMain(IServer par1IServer)
    {
        super(par1IServer);
        this.rconPort = par1IServer.getIntProperty("rcon.port", 0);
        this.rconPassword = par1IServer.getStringProperty("rcon.password", "");
        this.hostname = par1IServer.getHostname();
        this.serverPort = par1IServer.getPort();

        if (0 == this.rconPort)
        {
            this.rconPort = this.serverPort + 10;
            this.log("Setting default rcon port to " + this.rconPort);
            par1IServer.setProperty("rcon.port", Integer.valueOf(this.rconPort));

            if (0 == this.rconPassword.length())
            {
                par1IServer.setProperty("rcon.password", "");
            }

            par1IServer.saveProperties();
        }

        if (0 == this.hostname.length())
        {
            this.hostname = "0.0.0.0";
        }

        this.initClientTh();
        this.serverSocket = null;
    }

    private void initClientTh()
    {
        this.clientThreads = new HashMap();
    }

    /**
     * Cleans up the clientThreads map by removing client Threads that are not running
     */
    private void cleanClientThreadsMap()
    {
        Iterator var1 = this.clientThreads.entrySet().iterator();

        while (var1.hasNext())
        {
            Entry var2 = (Entry)var1.next();

            if (!((RConThreadClient)var2.getValue()).isRunning())
            {
                var1.remove();
            }
        }
    }

    public void run()
    {
        this.log("RCON running on " + this.hostname + ":" + this.rconPort);

        try
        {
            while (this.running)
            {
                try
                {
                    Socket var1 = this.serverSocket.accept();
                    var1.setSoTimeout(500);
                    RConThreadClient var2 = new RConThreadClient(this.server, var1);
                    var2.startThread();
                    this.clientThreads.put(var1.getRemoteSocketAddress(), var2);
                    this.cleanClientThreadsMap();
                }
                catch (SocketTimeoutException var7)
                {
                    this.cleanClientThreadsMap();
                }
                catch (IOException var8)
                {
                    if (this.running)
                    {
                        this.log("IO: " + var8.getMessage());
                    }
                }
            }
        }
        finally
        {
            this.closeServerSocket(this.serverSocket);
        }
    }

    /**
     * Creates a new Thread object from this class and starts running
     */
    public void startThread()
    {
        if (0 == this.rconPassword.length())
        {
            this.logWarning("No rcon password set in \'" + this.server.getSettingsFilename() + "\', rcon disabled!");
        }
        else if (0 < this.rconPort && 65535 >= this.rconPort)
        {
            if (!this.running)
            {
                try
                {
                    this.serverSocket = new ServerSocket(this.rconPort, 0, InetAddress.getByName(this.hostname));
                    this.serverSocket.setSoTimeout(500);
                    super.startThread();
                }
                catch (IOException var2)
                {
                    this.logWarning("Unable to initialise rcon on " + this.hostname + ":" + this.rconPort + " : " + var2.getMessage());
                }
            }
        }
        else
        {
            this.logWarning("Invalid rcon port " + this.rconPort + " found in \'" + this.server.getSettingsFilename() + "\', rcon disabled!");
        }
    }
}
