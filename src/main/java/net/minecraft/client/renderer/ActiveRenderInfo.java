package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

@SideOnly(Side.CLIENT)
public class ActiveRenderInfo
{
    // JAVADOC FIELD $$ field_74592_a
    public static float objectX;
    // JAVADOC FIELD $$ field_74590_b
    public static float objectY;
    // JAVADOC FIELD $$ field_74591_c
    public static float objectZ;
    // JAVADOC FIELD $$ field_74597_i
    private static IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    // JAVADOC FIELD $$ field_74594_j
    private static FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    // JAVADOC FIELD $$ field_74595_k
    private static FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    // JAVADOC FIELD $$ field_74593_l
    private static FloatBuffer objectCoords = GLAllocation.createDirectFloatBuffer(3);
    // JAVADOC FIELD $$ field_74588_d
    public static float rotationX;
    // JAVADOC FIELD $$ field_74589_e
    public static float rotationXZ;
    // JAVADOC FIELD $$ field_74586_f
    public static float rotationZ;
    // JAVADOC FIELD $$ field_74587_g
    public static float rotationYZ;
    // JAVADOC FIELD $$ field_74596_h
    public static float rotationXY;
    private static final String __OBFID = "CL_00000626";

    // JAVADOC METHOD $$ func_74583_a
    public static void updateRenderInfo(EntityPlayer par0EntityPlayer, boolean par1)
    {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        float f = (float)((viewport.get(0) + viewport.get(2)) / 2);
        float f1 = (float)((viewport.get(1) + viewport.get(3)) / 2);
        GLU.gluUnProject(f, f1, 0.0F, modelview, projection, viewport, objectCoords);
        objectX = objectCoords.get(0);
        objectY = objectCoords.get(1);
        objectZ = objectCoords.get(2);
        int i = par1 ? 1 : 0;
        float f2 = par0EntityPlayer.rotationPitch;
        float f3 = par0EntityPlayer.rotationYaw;
        rotationX = MathHelper.cos(f3 * (float)Math.PI / 180.0F) * (float)(1 - i * 2);
        rotationZ = MathHelper.sin(f3 * (float)Math.PI / 180.0F) * (float)(1 - i * 2);
        rotationYZ = -rotationZ * MathHelper.sin(f2 * (float)Math.PI / 180.0F) * (float)(1 - i * 2);
        rotationXY = rotationX * MathHelper.sin(f2 * (float)Math.PI / 180.0F) * (float)(1 - i * 2);
        rotationXZ = MathHelper.cos(f2 * (float)Math.PI / 180.0F);
    }

    // JAVADOC METHOD $$ func_74585_b
    public static Vec3 projectViewFromEntity(EntityLivingBase par0EntityLivingBase, double par1)
    {
        double d1 = par0EntityLivingBase.prevPosX + (par0EntityLivingBase.posX - par0EntityLivingBase.prevPosX) * par1;
        double d2 = par0EntityLivingBase.prevPosY + (par0EntityLivingBase.posY - par0EntityLivingBase.prevPosY) * par1 + (double)par0EntityLivingBase.getEyeHeight();
        double d3 = par0EntityLivingBase.prevPosZ + (par0EntityLivingBase.posZ - par0EntityLivingBase.prevPosZ) * par1;
        double d4 = d1 + (double)(objectX * 1.0F);
        double d5 = d2 + (double)(objectY * 1.0F);
        double d6 = d3 + (double)(objectZ * 1.0F);
        return par0EntityLivingBase.worldObj.getWorldVec3Pool().getVecFromPool(d4, d5, d6);
    }

    public static Block func_151460_a(World p_151460_0_, EntityLivingBase p_151460_1_, float p_151460_2_)
    {
        Vec3 vec3 = projectViewFromEntity(p_151460_1_, (double)p_151460_2_);
        ChunkPosition chunkposition = new ChunkPosition(vec3);
        Block block = p_151460_0_.func_147439_a(chunkposition.field_151329_a, chunkposition.field_151327_b, chunkposition.field_151328_c);

        if (block.func_149688_o().isLiquid())
        {
            float f1 = BlockLiquid.func_149801_b(p_151460_0_.getBlockMetadata(chunkposition.field_151329_a, chunkposition.field_151327_b, chunkposition.field_151328_c)) - 0.11111111F;
            float f2 = (float)(chunkposition.field_151327_b + 1) - f1;

            if (vec3.yCoord >= (double)f2)
            {
                block = p_151460_0_.func_147439_a(chunkposition.field_151329_a, chunkposition.field_151327_b + 1, chunkposition.field_151328_c);
            }
        }

        return block;
    }
}