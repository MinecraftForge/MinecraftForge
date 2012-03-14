package net.minecraft.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.minecraft.server.MinecraftServer;

public class ThreadCommandReader extends Thread
{
    /** Reference to the MinecraftServer object. */
    final MinecraftServer mcServer;

    public ThreadCommandReader(MinecraftServer par1MinecraftServer)
    {
        this.mcServer = par1MinecraftServer;
    }

    public void run()
    {
        BufferedReader var1 = new BufferedReader(new InputStreamReader(System.in));
        String var2 = null;

        try
        {
            while (!this.mcServer.serverStopped && MinecraftServer.isServerRunning(this.mcServer) && (var2 = var1.readLine()) != null)
            {
                this.mcServer.addCommand(var2, this.mcServer);
            }
        }
        catch (IOException var4)
        {
            var4.printStackTrace();
        }
    }
}
