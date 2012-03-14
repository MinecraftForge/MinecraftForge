package net.minecraft.src;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

public class OpenGlHelper
{
    /**
     * An OpenGL constant corresponding to GL_TEXTURE0, used when setting data pertaining to auxiliary OpenGL texture
     * units.
     */
    public static int defaultTexUnit;

    /**
     * An OpenGL constant corresponding to GL_TEXTURE1, used when setting data pertaining to auxiliary OpenGL texture
     * units.
     */
    public static int lightmapTexUnit;

    /**
     * True if the renderer supports multitextures and the OpenGL version != 1.3
     */
    private static boolean useMultitextureARB = false;

    /**
     * Initializes the texture constants to be used when rendering lightmap values
     */
    public static void initializeTextures()
    {
        useMultitextureARB = GLContext.getCapabilities().GL_ARB_multitexture && !GLContext.getCapabilities().OpenGL13;

        if (useMultitextureARB)
        {
            defaultTexUnit = 33984;
            lightmapTexUnit = 33985;
        }
        else
        {
            defaultTexUnit = 33984;
            lightmapTexUnit = 33985;
        }
    }

    /**
     * Sets the current lightmap texture to the specified OpenGL constant
     */
    public static void setActiveTexture(int par0)
    {
        if (useMultitextureARB)
        {
            ARBMultitexture.glActiveTextureARB(par0);
        }
        else
        {
            GL13.glActiveTexture(par0);
        }
    }

    /**
     * Sets the current lightmap texture to the specified OpenGL constant
     */
    public static void setClientActiveTexture(int par0)
    {
        if (useMultitextureARB)
        {
            ARBMultitexture.glClientActiveTextureARB(par0);
        }
        else
        {
            GL13.glClientActiveTexture(par0);
        }
    }

    /**
     * Sets the current coordinates of the given lightmap texture
     */
    public static void setLightmapTextureCoords(int par0, float par1, float par2)
    {
        if (useMultitextureARB)
        {
            ARBMultitexture.glMultiTexCoord2fARB(par0, par1, par2);
        }
        else
        {
            GL13.glMultiTexCoord2f(par0, par1, par2);
        }
    }
}
