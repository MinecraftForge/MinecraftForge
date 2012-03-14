package net.minecraft.src;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import net.minecraft.server.MinecraftServer;

final class ServerWindowAdapter extends WindowAdapter
{
    /** Reference to the MinecraftServer object. */
    final MinecraftServer mcServer;

    ServerWindowAdapter(MinecraftServer par1MinecraftServer)
    {
        this.mcServer = par1MinecraftServer;
    }

    public void windowClosing(WindowEvent par1WindowEvent)
    {
        this.mcServer.initiateShutdown();

        while (!this.mcServer.serverStopped)
        {
            try
            {
                Thread.sleep(100L);
            }
            catch (InterruptedException var3)
            {
                var3.printStackTrace();
            }
        }

        System.exit(0);
    }
}
