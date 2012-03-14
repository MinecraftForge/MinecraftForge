package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public final class ThreadServerApplication extends Thread
{
    /** Reference to the MinecraftServer object. */
    final MinecraftServer mcServer;

    public ThreadServerApplication(String par1Str, MinecraftServer par2MinecraftServer)
    {
        super(par1Str);
        this.mcServer = par2MinecraftServer;
    }

    public void run()
    {
        this.mcServer.run();
    }
}
