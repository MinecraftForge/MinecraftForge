package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;

@SideOnly(Side.CLIENT)
public class OpenGlHelper
{
    public static boolean field_148827_a;
    // JAVADOC FIELD $$ field_77478_a
    public static int defaultTexUnit;
    // JAVADOC FIELD $$ field_77476_b
    public static int lightmapTexUnit;
    public static boolean field_148825_d;
    public static int field_148826_e;
    // JAVADOC FIELD $$ field_77477_c
    private static boolean useMultitextureARB;
    private static boolean field_148828_i;
    public static boolean field_148823_f;
    public static boolean field_148824_g;
    private static final String __OBFID = "CL_00001179";

    /* Stores the last values sent into setLightmapTextureCoords */
    public static float lastBrightnessX = 0.0f;
    public static float lastBrightnessY = 0.0f;

    // JAVADOC METHOD $$ func_77474_a
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

        field_148828_i = GLContext.getCapabilities().OpenGL14;
        field_148823_f = field_148828_i && GLContext.getCapabilities().GL_ARB_framebuffer_object;
        field_148825_d = GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic;
        field_148826_e = (int)(field_148825_d ? GL11.glGetFloat(34047) : 0.0F);
        GameSettings.Options.ANISOTROPIC_FILTERING.func_148263_a((float)field_148826_e);
        field_148827_a = GLContext.getCapabilities().OpenGL21;
        field_148824_g = field_148823_f && field_148827_a;
    }

    // JAVADOC METHOD $$ func_77473_a
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

    // JAVADOC METHOD $$ func_77472_b
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

    // JAVADOC METHOD $$ func_77475_a
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

        if (par0 == lightmapTexUnit)
        {
            lastBrightnessX = par1;
            lastBrightnessY = par2;
        }
    }

    public static void func_148821_a(int p_148821_0_, int p_148821_1_, int p_148821_2_, int p_148821_3_)
    {
        if (field_148828_i)
        {
            GL14.glBlendFuncSeparate(p_148821_0_, p_148821_1_, p_148821_2_, p_148821_3_);
        }
        else
        {
            GL11.glBlendFunc(p_148821_0_, p_148821_1_);
        }
    }

    public static boolean func_148822_b()
    {
        return field_148823_f && Minecraft.getMinecraft().gameSettings.field_151448_g;
    }
}