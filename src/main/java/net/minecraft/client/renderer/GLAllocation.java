package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GLAllocation
{
    private static final Map field_74531_a = new HashMap();
    private static final List field_74530_b = new ArrayList();
    private static final String __OBFID = "CL_00000630";

    // JAVADOC METHOD $$ func_74526_a
    public static synchronized int generateDisplayLists(int par0)
    {
        int j = GL11.glGenLists(par0);
        field_74531_a.put(Integer.valueOf(j), Integer.valueOf(par0));
        return j;
    }

    public static synchronized void deleteDisplayLists(int par0)
    {
        GL11.glDeleteLists(par0, ((Integer)field_74531_a.remove(Integer.valueOf(par0))).intValue());
    }

    // JAVADOC METHOD $$ func_74525_a
    public static synchronized void deleteTexturesAndDisplayLists()
    {
        Iterator iterator = field_74531_a.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();
            GL11.glDeleteLists(((Integer)entry.getKey()).intValue(), ((Integer)entry.getValue()).intValue());
        }

        field_74531_a.clear();
    }

    // JAVADOC METHOD $$ func_74524_c
    public static synchronized ByteBuffer createDirectByteBuffer(int par0)
    {
        return ByteBuffer.allocateDirect(par0).order(ByteOrder.nativeOrder());
    }

    // JAVADOC METHOD $$ func_74527_f
    public static IntBuffer createDirectIntBuffer(int par0)
    {
        // JAVADOC METHOD $$ func_74524_c
        return createDirectByteBuffer(par0 << 2).asIntBuffer();
    }

    // JAVADOC METHOD $$ func_74529_h
    public static FloatBuffer createDirectFloatBuffer(int par0)
    {
        // JAVADOC METHOD $$ func_74524_c
        return createDirectByteBuffer(par0 << 2).asFloatBuffer();
    }
}