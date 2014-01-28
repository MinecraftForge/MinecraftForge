package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

@SideOnly(Side.CLIENT)
public class MouseHelper
{
    // JAVADOC FIELD $$ field_74377_a
    public int deltaX;
    // JAVADOC FIELD $$ field_74375_b
    public int deltaY;
    private static final String __OBFID = "CL_00000648";

    // JAVADOC METHOD $$ func_74372_a
    public void grabMouseCursor()
    {
        if (Boolean.parseBoolean(System.getProperty("fml.noGrab","false"))) return;
        Mouse.setGrabbed(true);
        this.deltaX = 0;
        this.deltaY = 0;
    }

    // JAVADOC METHOD $$ func_74373_b
    public void ungrabMouseCursor()
    {
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        Mouse.setGrabbed(false);
    }

    public void mouseXYChange()
    {
        this.deltaX = Mouse.getDX();
        this.deltaY = Mouse.getDY();
    }
}