package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class ThreadClientSleep extends Thread
{
    /** A reference to the Minecraft object. */
    final Minecraft mc;

    public ThreadClientSleep(Minecraft par1Minecraft, String par2Str)
    {
        super(par2Str);
        this.mc = par1Minecraft;
        this.setDaemon(true);
        this.start();
    }

    public void run()
    {
        while (this.mc.running)
        {
            try
            {
                Thread.sleep(2147483647L);
            }
            catch (InterruptedException var2)
            {
                ;
            }
        }
    }
}
