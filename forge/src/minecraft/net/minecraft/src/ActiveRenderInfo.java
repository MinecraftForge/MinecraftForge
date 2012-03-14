package net.minecraft.src;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ActiveRenderInfo
{
    /** The calculated view object X coordinate */
    public static float objectX = 0.0F;

    /** The calculated view object Y coordinate */
    public static float objectY = 0.0F;

    /** The calculated view object Z coordinate */
    public static float objectZ = 0.0F;

    /** The current GL viewport */
    private static IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);

    /** The current GL modelview matrix */
    private static FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);

    /** The current GL projection matrix */
    private static FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);

    /** The computed view object coordinates */
    private static FloatBuffer objectCoords = GLAllocation.createDirectFloatBuffer(3);

    /** The X component of the entity's yaw rotation */
    public static float rotationX;

    /** The combined X and Z components of the entity's pitch rotation */
    public static float rotationXZ;

    /** The Z component of the entity's yaw rotation */
    public static float rotationZ;

    /**
     * The Y component (scaled along the Z axis) of the entity's pitch rotation
     */
    public static float rotationYZ;

    /**
     * The Y component (scaled along the X axis) of the entity's pitch rotation
     */
    public static float rotationXY;

    /**
     * Updates the current render info and camera location based on entity look angles and 1st/3rd person view mode
     */
    public static void updateRenderInfo(EntityPlayer par0EntityPlayer, boolean par1)
    {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        float var2 = (float)((viewport.get(0) + viewport.get(2)) / 2);
        float var3 = (float)((viewport.get(1) + viewport.get(3)) / 2);
        GLU.gluUnProject(var2, var3, 0.0F, modelview, projection, viewport, objectCoords);
        objectX = objectCoords.get(0);
        objectY = objectCoords.get(1);
        objectZ = objectCoords.get(2);
        int var4 = par1 ? 1 : 0;
        float var5 = par0EntityPlayer.rotationPitch;
        float var6 = par0EntityPlayer.rotationYaw;
        rotationX = MathHelper.cos(var6 * (float)Math.PI / 180.0F) * (float)(1 - var4 * 2);
        rotationZ = MathHelper.sin(var6 * (float)Math.PI / 180.0F) * (float)(1 - var4 * 2);
        rotationYZ = -rotationZ * MathHelper.sin(var5 * (float)Math.PI / 180.0F) * (float)(1 - var4 * 2);
        rotationXY = rotationX * MathHelper.sin(var5 * (float)Math.PI / 180.0F) * (float)(1 - var4 * 2);
        rotationXZ = MathHelper.cos(var5 * (float)Math.PI / 180.0F);
    }

    /**
     * Returns a vector representing the projection along the given entity's view for the given distance
     */
    public static Vec3D projectViewFromEntity(EntityLiving par0EntityLiving, double par1)
    {
        double var3 = par0EntityLiving.prevPosX + (par0EntityLiving.posX - par0EntityLiving.prevPosX) * par1;
        double var5 = par0EntityLiving.prevPosY + (par0EntityLiving.posY - par0EntityLiving.prevPosY) * par1 + (double)par0EntityLiving.getEyeHeight();
        double var7 = par0EntityLiving.prevPosZ + (par0EntityLiving.posZ - par0EntityLiving.prevPosZ) * par1;
        double var9 = var3 + (double)(objectX * 1.0F);
        double var11 = var5 + (double)(objectY * 1.0F);
        double var13 = var7 + (double)(objectZ * 1.0F);
        return Vec3D.createVector(var9, var11, var13);
    }

    /**
     * Returns the block ID at the current camera location (either air or fluid), taking into account the height of
     * fluid blocks
     */
    public static int getBlockIdAtEntityViewpoint(World par0World, EntityLiving par1EntityLiving, float par2)
    {
        Vec3D var3 = projectViewFromEntity(par1EntityLiving, (double)par2);
        ChunkPosition var4 = new ChunkPosition(var3);
        int var5 = par0World.getBlockId(var4.x, var4.y, var4.z);

        if (var5 != 0 && Block.blocksList[var5].blockMaterial.isLiquid())
        {
            float var6 = BlockFluid.getFluidHeightPercent(par0World.getBlockMetadata(var4.x, var4.y, var4.z)) - 0.11111111F;
            float var7 = (float)(var4.y + 1) - var6;

            if (var3.yCoord >= (double)var7)
            {
                var5 = par0World.getBlockId(var4.x, var4.y + 1, var4.z);
            }
        }

        return var5;
    }
}
