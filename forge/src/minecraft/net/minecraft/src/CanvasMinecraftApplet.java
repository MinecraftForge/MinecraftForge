package net.minecraft.src;

import java.awt.Canvas;
import net.minecraft.client.MinecraftApplet;

public class CanvasMinecraftApplet extends Canvas
{
    /** Reference to the MinecraftApplet object. */
    final MinecraftApplet mcApplet;

    public CanvasMinecraftApplet(MinecraftApplet par1MinecraftApplet)
    {
        this.mcApplet = par1MinecraftApplet;
    }

    public synchronized void addNotify()
    {
        super.addNotify();
        this.mcApplet.startMainThread();
    }

    public synchronized void removeNotify()
    {
        this.mcApplet.shutdown();
        super.removeNotify();
    }
}
