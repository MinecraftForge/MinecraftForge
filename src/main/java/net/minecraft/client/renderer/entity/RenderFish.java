package net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RenderFish extends Render
{
    private static final ResourceLocation field_110792_a = new ResourceLocation("textures/particle/particles.png");
    private static final String __OBFID = "CL_00000996";

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(EntityFishHook p_147922_1_, double p_147922_2_, double p_147922_4_, double p_147922_6_, float p_147922_8_, float p_147922_9_)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)p_147922_2_, (float)p_147922_4_, (float)p_147922_6_);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        this.bindEntityTexture(p_147922_1_);
        Tessellator tessellator = Tessellator.instance;
        byte b0 = 1;
        byte b1 = 2;
        float f2 = (float)(b0 * 8 + 0) / 128.0F;
        float f3 = (float)(b0 * 8 + 8) / 128.0F;
        float f4 = (float)(b1 * 8 + 0) / 128.0F;
        float f5 = (float)(b1 * 8 + 8) / 128.0F;
        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.5F;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV((double)(0.0F - f7), (double)(0.0F - f8), 0.0D, (double)f2, (double)f5);
        tessellator.addVertexWithUV((double)(f6 - f7), (double)(0.0F - f8), 0.0D, (double)f3, (double)f5);
        tessellator.addVertexWithUV((double)(f6 - f7), (double)(1.0F - f8), 0.0D, (double)f3, (double)f4);
        tessellator.addVertexWithUV((double)(0.0F - f7), (double)(1.0F - f8), 0.0D, (double)f2, (double)f4);
        tessellator.draw();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();

        if (p_147922_1_.field_146042_b != null)
        {
            float f9 = p_147922_1_.field_146042_b.getSwingProgress(p_147922_9_);
            float f10 = MathHelper.sin(MathHelper.sqrt_float(f9) * (float)Math.PI);
            Vec3 vec3 = p_147922_1_.worldObj.getWorldVec3Pool().getVecFromPool(-0.5D, 0.03D, 0.8D);
            vec3.rotateAroundX(-(p_147922_1_.field_146042_b.prevRotationPitch + (p_147922_1_.field_146042_b.rotationPitch - p_147922_1_.field_146042_b.prevRotationPitch) * p_147922_9_) * (float)Math.PI / 180.0F);
            vec3.rotateAroundY(-(p_147922_1_.field_146042_b.prevRotationYaw + (p_147922_1_.field_146042_b.rotationYaw - p_147922_1_.field_146042_b.prevRotationYaw) * p_147922_9_) * (float)Math.PI / 180.0F);
            vec3.rotateAroundY(f10 * 0.5F);
            vec3.rotateAroundX(-f10 * 0.7F);
            double d3 = p_147922_1_.field_146042_b.prevPosX + (p_147922_1_.field_146042_b.posX - p_147922_1_.field_146042_b.prevPosX) * (double)p_147922_9_ + vec3.xCoord;
            double d4 = p_147922_1_.field_146042_b.prevPosY + (p_147922_1_.field_146042_b.posY - p_147922_1_.field_146042_b.prevPosY) * (double)p_147922_9_ + vec3.yCoord;
            double d5 = p_147922_1_.field_146042_b.prevPosZ + (p_147922_1_.field_146042_b.posZ - p_147922_1_.field_146042_b.prevPosZ) * (double)p_147922_9_ + vec3.zCoord;
            double d6 = p_147922_1_.field_146042_b == Minecraft.getMinecraft().thePlayer ? 0.0D : (double)p_147922_1_.field_146042_b.getEyeHeight();

            if (this.renderManager.options.thirdPersonView > 0 || p_147922_1_.field_146042_b != Minecraft.getMinecraft().thePlayer)
            {
                float f11 = (p_147922_1_.field_146042_b.prevRenderYawOffset + (p_147922_1_.field_146042_b.renderYawOffset - p_147922_1_.field_146042_b.prevRenderYawOffset) * p_147922_9_) * (float)Math.PI / 180.0F;
                double d7 = (double)MathHelper.sin(f11);
                double d9 = (double)MathHelper.cos(f11);
                d3 = p_147922_1_.field_146042_b.prevPosX + (p_147922_1_.field_146042_b.posX - p_147922_1_.field_146042_b.prevPosX) * (double)p_147922_9_ - d9 * 0.35D - d7 * 0.85D;
                d4 = p_147922_1_.field_146042_b.prevPosY + d6 + (p_147922_1_.field_146042_b.posY - p_147922_1_.field_146042_b.prevPosY) * (double)p_147922_9_ - 0.45D;
                d5 = p_147922_1_.field_146042_b.prevPosZ + (p_147922_1_.field_146042_b.posZ - p_147922_1_.field_146042_b.prevPosZ) * (double)p_147922_9_ - d7 * 0.35D + d9 * 0.85D;
            }

            double d14 = p_147922_1_.prevPosX + (p_147922_1_.posX - p_147922_1_.prevPosX) * (double)p_147922_9_;
            double d8 = p_147922_1_.prevPosY + (p_147922_1_.posY - p_147922_1_.prevPosY) * (double)p_147922_9_ + 0.25D;
            double d10 = p_147922_1_.prevPosZ + (p_147922_1_.posZ - p_147922_1_.prevPosZ) * (double)p_147922_9_;
            double d11 = (double)((float)(d3 - d14));
            double d12 = (double)((float)(d4 - d8));
            double d13 = (double)((float)(d5 - d10));
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            tessellator.startDrawing(3);
            tessellator.setColorOpaque_I(0);
            byte b2 = 16;

            for (int i = 0; i <= b2; ++i)
            {
                float f12 = (float)i / (float)b2;
                tessellator.addVertex(p_147922_2_ + d11 * (double)f12, p_147922_4_ + d12 * (double)(f12 * f12 + f12) * 0.5D + 0.25D, p_147922_6_ + d13 * (double)f12);
            }

            tessellator.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(EntityFishHook p_147921_1_)
    {
        return field_110792_a;
    }

    // JAVADOC METHOD $$ func_110775_a
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return this.getEntityTexture((EntityFishHook)par1Entity);
    }

    // JAVADOC METHOD $$ func_76986_a
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRender((EntityFishHook)par1Entity, par2, par4, par6, par8, par9);
    }
}