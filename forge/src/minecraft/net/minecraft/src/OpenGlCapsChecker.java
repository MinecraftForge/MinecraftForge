package net.minecraft.src;

import org.lwjgl.opengl.GLContext;

public class OpenGlCapsChecker
{
    /**
     * Whether or not we should try to check occlusion - defaults to false and is never changed in 1.2.2.
     */
    private static boolean tryCheckOcclusionCapable = true;

    /**
     * Checks if we support OpenGL occlusion.
     */
    public static boolean checkARBOcclusion()
    {
        return tryCheckOcclusionCapable && GLContext.getCapabilities().GL_ARB_occlusion_query;
    }
}
