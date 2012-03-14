package net.minecraft.src;

import java.awt.Component;
import java.nio.IntBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

public class MouseHelper
{
    private Component windowComponent;
    private Cursor cursor;

    /** Mouse delta X this frame */
    public int deltaX;

    /** Mouse delta Y this frame */
    public int deltaY;
    private int field_1115_e = 10;

    public MouseHelper(Component par1Component)
    {
        this.windowComponent = par1Component;
        IntBuffer var2 = GLAllocation.createDirectIntBuffer(1);
        var2.put(0);
        var2.flip();
        IntBuffer var3 = GLAllocation.createDirectIntBuffer(1024);

        try
        {
            this.cursor = new Cursor(32, 32, 16, 16, 1, var3, var2);
        }
        catch (LWJGLException var5)
        {
            var5.printStackTrace();
        }
    }

    /**
     * Grabs the mouse cursor it doesn't move and isn't seen.
     */
    public void grabMouseCursor()
    {
        Mouse.setGrabbed(true);
        this.deltaX = 0;
        this.deltaY = 0;
    }

    /**
     * Ungrabs the mouse cursor so it can be moved and set it to the center of the screen
     */
    public void ungrabMouseCursor()
    {
        Mouse.setCursorPosition(this.windowComponent.getWidth() / 2, this.windowComponent.getHeight() / 2);
        Mouse.setGrabbed(false);
    }

    public void mouseXYChange()
    {
        this.deltaX = Mouse.getDX();
        this.deltaY = Mouse.getDY();
    }
}
