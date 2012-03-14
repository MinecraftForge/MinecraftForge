package net.minecraft.src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import net.minecraft.server.MinecraftServer;

class NetworkAcceptThread extends Thread
{
    /** Reference to the MinecraftServer object. */
    final MinecraftServer mcServer;

    /** The network listener object. */
    final NetworkListenThread netWorkListener;

    NetworkAcceptThread(NetworkListenThread par1NetworkListenThread, String par2Str, MinecraftServer par3MinecraftServer)
    {
        super(par2Str);
        this.netWorkListener = par1NetworkListenThread;
        this.mcServer = par3MinecraftServer;
    }

    public void run()
    {
        while (this.netWorkListener.isListening)
        {
            try
            {
                Socket var1 = NetworkListenThread.getServerSocket(this.netWorkListener).accept();

                if (var1 != null)
                {
                    synchronized (NetworkListenThread.func_35504_b(this.netWorkListener))
                    {
                        InetAddress var3 = var1.getInetAddress();

                        if (NetworkListenThread.func_35504_b(this.netWorkListener).containsKey(var3) && System.currentTimeMillis() - ((Long)NetworkListenThread.func_35504_b(this.netWorkListener).get(var3)).longValue() < 5000L)
                        {
                            NetworkListenThread.func_35504_b(this.netWorkListener).put(var3, Long.valueOf(System.currentTimeMillis()));
                            var1.close();
                            continue;
                        }

                        NetworkListenThread.func_35504_b(this.netWorkListener).put(var3, Long.valueOf(System.currentTimeMillis()));
                    }

                    NetLoginHandler var2 = new NetLoginHandler(this.mcServer, var1, "Connection #" + NetworkListenThread.func_712_b(this.netWorkListener));
                    NetworkListenThread.func_716_a(this.netWorkListener, var2);
                }
            }
            catch (IOException var6)
            {
                var6.printStackTrace();
            }
        }
    }
}
