package net.minecraft.src;

import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;

public class RenderHelper
{
    /** Float buffer used to set OpenGL material colors */
    private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);

    /**
     * Disables the OpenGL lighting properties enabled by enableStandardItemLighting
     */
    public static void disableStandardItemLighting()
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LIGHT0);
        GL11.glDisable(GL11.GL_LIGHT1);
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
    }

    /**
     * Sets the OpenGL lighting properties to the values used when rendering blocks as items
     */
    public static void enableStandardItemLighting()
    {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_LIGHT1);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        float var0 = 0.4F;
        float var1 = 0.6F;
        float var2 = 0.0F;
        Vec3D var3 = Vec3D.createVector(0.20000000298023224D, 1.0D, -0.699999988079071D).normalize();
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, setColorBuffer(var3.xCoord, var3.yCoord, var3.zCoord, 0.0D));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, setColorBuffer(var1, var1, var1, 1.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, setColorBuffer(var2, var2, var2, 1.0F));
        var3 = Vec3D.createVector(-0.20000000298023224D, 1.0D, 0.699999988079071D).normalize();
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, setColorBuffer(var3.xCoord, var3.yCoord, var3.zCoord, 0.0D));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, setColorBuffer(var1, var1, var1, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, setColorBuffer(var2, var2, var2, 1.0F));
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, setColorBuffer(var0, var0, var0, 1.0F));
    }

    /**
     * Update and return colorBuffer with the RGBA values passed as arguments
     */
    private static FloatBuffer setColorBuffer(double par0, double par2, double par4, double par6)
    {
        return setColorBuffer((float)par0, (float)par2, (float)par4, (float)par6);
    }

    /**
     * Update and return colorBuffer with the RGBA values passed as arguments
     */
    private static FloatBuffer setColorBuffer(float par0, float par1, float par2, float par3)
    {
        colorBuffer.clear();
        colorBuffer.put(par0).put(par1).put(par2).put(par3);
        colorBuffer.flip();
        return colorBuffer;
    }

    /**
     * Sets OpenGL lighting for rendering blocks as items inside GUI screens (such as containers).
     */
    public static void enableGUIStandardItemLighting()
    {
        GL11.glPushMatrix();
        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(165.0F, 1.0F, 0.0F, 0.0F);
        enableStandardItemLighting();
        GL11.glPopMatrix();
    }
}
