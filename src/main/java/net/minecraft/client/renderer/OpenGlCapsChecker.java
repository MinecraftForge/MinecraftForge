package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GLContext;

@SideOnly(Side.CLIENT)
public class OpenGlCapsChecker
{
    private static final String __OBFID = "CL_00000649";

    // JAVADOC METHOD $$ func_74371_a
    public static boolean checkARBOcclusion()
    {
        return GLContext.getCapabilities().GL_ARB_occlusion_query;
    }
}