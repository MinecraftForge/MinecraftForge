package net.minecraft.src;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;

public class MinecraftAppletImpl extends Minecraft
{
    /** Reference to the main frame, in this case, the applet window itself. */
    final MinecraftApplet mainFrame;

    public MinecraftAppletImpl(MinecraftApplet par1MinecraftApplet, Component par2Component, Canvas par3Canvas, MinecraftApplet par4MinecraftApplet, int par5, int par6, boolean par7)
    {
        super(par2Component, par3Canvas, par4MinecraftApplet, par5, par6, par7);
        this.mainFrame = par1MinecraftApplet;
    }

    /**
     * Displays an unexpected error that has come up during the game.
     */
    public void displayUnexpectedThrowable(UnexpectedThrowable par1UnexpectedThrowable)
    {
        this.mainFrame.removeAll();
        this.mainFrame.setLayout(new BorderLayout());
        this.mainFrame.add(new PanelCrashReport(par1UnexpectedThrowable), "Center");
        this.mainFrame.validate();
    }
}
