package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class ConvertProgressUpdater implements IProgressUpdate
{
    /** lastTimeMillis */
    private long lastTimeMillis;

    /** Reference to the MinecraftServer object. */
    final MinecraftServer mcServer;

    public ConvertProgressUpdater(MinecraftServer par1MinecraftServer)
    {
        this.mcServer = par1MinecraftServer;
        this.lastTimeMillis = System.currentTimeMillis();
    }

    /**
     * Shows the 'Saving level' string.
     */
    public void displaySavingString(String par1Str) {}

    public void setLoadingProgress(int par1)
    {
        if (System.currentTimeMillis() - this.lastTimeMillis >= 1000L)
        {
            this.lastTimeMillis = System.currentTimeMillis();
            MinecraftServer.logger.info("Converting... " + par1 + "%");
        }
    }

    public void displayLoadingString(String par1Str) {}
}
