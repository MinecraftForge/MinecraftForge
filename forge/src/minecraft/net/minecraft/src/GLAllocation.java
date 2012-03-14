package net.minecraft.src;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class GLAllocation
{
    /**
     * An ArrayList that stores the first index and the length of each display list.
     */
    private static List displayLists = new ArrayList();

    /** An ArrayList that stores all the generated texture names. */
    private static List textureNames = new ArrayList();

    /**
     * Generates the specified number of display lists and returns the first index.
     */
    public static synchronized int generateDisplayLists(int par0)
    {
        int var1 = GL11.glGenLists(par0);
        displayLists.add(Integer.valueOf(var1));
        displayLists.add(Integer.valueOf(par0));
        return var1;
    }

    /**
     * Generates texture names and stores them in the specified buffer.
     */
    public static synchronized void generateTextureNames(IntBuffer par0IntBuffer)
    {
        GL11.glGenTextures(par0IntBuffer);

        for (int var1 = par0IntBuffer.position(); var1 < par0IntBuffer.limit(); ++var1)
        {
            textureNames.add(Integer.valueOf(par0IntBuffer.get(var1)));
        }
    }

    public static synchronized void deleteDisplayLists(int par0)
    {
        int var1 = displayLists.indexOf(Integer.valueOf(par0));
        GL11.glDeleteLists(((Integer)displayLists.get(var1)).intValue(), ((Integer)displayLists.get(var1 + 1)).intValue());
        displayLists.remove(var1);
        displayLists.remove(var1);
    }

    /**
     * Deletes all textures and display lists. Called when Minecraft is shutdown to free up resources.
     */
    public static synchronized void deleteTexturesAndDisplayLists()
    {
        for (int var0 = 0; var0 < displayLists.size(); var0 += 2)
        {
            GL11.glDeleteLists(((Integer)displayLists.get(var0)).intValue(), ((Integer)displayLists.get(var0 + 1)).intValue());
        }

        IntBuffer var2 = createDirectIntBuffer(textureNames.size());
        var2.flip();
        GL11.glDeleteTextures(var2);

        for (int var1 = 0; var1 < textureNames.size(); ++var1)
        {
            var2.put(((Integer)textureNames.get(var1)).intValue());
        }

        var2.flip();
        GL11.glDeleteTextures(var2);
        displayLists.clear();
        textureNames.clear();
    }

    /**
     * Creates and returns a direct byte buffer with the specified capacity. Applies native ordering to speed up access.
     */
    public static synchronized ByteBuffer createDirectByteBuffer(int par0)
    {
        ByteBuffer var1 = ByteBuffer.allocateDirect(par0).order(ByteOrder.nativeOrder());
        return var1;
    }

    /**
     * Creates and returns a direct int buffer with the specified capacity. Applies native ordering to speed up access.
     */
    public static IntBuffer createDirectIntBuffer(int par0)
    {
        return createDirectByteBuffer(par0 << 2).asIntBuffer();
    }

    /**
     * Creates and returns a direct float buffer with the specified capacity. Applies native ordering to speed up
     * access.
     */
    public static FloatBuffer createDirectFloatBuffer(int par0)
    {
        return createDirectByteBuffer(par0 << 2).asFloatBuffer();
    }
}
