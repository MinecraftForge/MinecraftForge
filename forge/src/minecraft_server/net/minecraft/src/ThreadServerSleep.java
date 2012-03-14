package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class ThreadServerSleep extends Thread
{
    /** A reference to the Minecraft object. */
    final MinecraftServer mc;

    public ThreadServerSleep(MinecraftServer par1MinecraftServer)
    {
        this.mc = par1MinecraftServer;
        this.setDaemon(true);
        this.start();
    }

    public void run()
    {
        while (true)
        {
            try
            {
                while (true)
                {
                    Thread.sleep(2147483647L);
                }
            }
            catch (InterruptedException var2)
            {
                ;
            }
        }
    }
}
