package net.minecraft.src;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderFish extends Render
{
    /**
     * Actually renders the fishing line and hook
     */
    public void doRenderFishHook(EntityFishHook par1EntityFishHook, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        byte var10 = 1;
        byte var11 = 2;
        this.loadTexture("/particles.png");
        Tessellator var12 = Tessellator.instance;
        float var13 = (float)(var10 * 8 + 0) / 128.0F;
        float var14 = (float)(var10 * 8 + 8) / 128.0F;
        float var15 = (float)(var11 * 8 + 0) / 128.0F;
        float var16 = (float)(var11 * 8 + 8) / 128.0F;
        float var17 = 1.0F;
        float var18 = 0.5F;
        float var19 = 0.5F;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        var12.startDrawingQuads();
        var12.setNormal(0.0F, 1.0F, 0.0F);
        var12.addVertexWithUV((double)(0.0F - var18), (double)(0.0F - var19), 0.0D, (double)var13, (double)var16);
        var12.addVertexWithUV((double)(var17 - var18), (double)(0.0F - var19), 0.0D, (double)var14, (double)var16);
        var12.addVertexWithUV((double)(var17 - var18), (double)(1.0F - var19), 0.0D, (double)var14, (double)var15);
        var12.addVertexWithUV((double)(0.0F - var18), (double)(1.0F - var19), 0.0D, (double)var13, (double)var15);
        var12.draw();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();

        if (par1EntityFishHook.angler != null)
        {
            float var20 = (par1EntityFishHook.angler.prevRotationYaw + (par1EntityFishHook.angler.rotationYaw - par1EntityFishHook.angler.prevRotationYaw) * par9) * (float)Math.PI / 180.0F;
            double var21 = (double)MathHelper.sin(var20);
            double var23 = (double)MathHelper.cos(var20);
            float var25 = par1EntityFishHook.angler.getSwingProgress(par9);
            float var26 = MathHelper.sin(MathHelper.sqrt_float(var25) * (float)Math.PI);
            Vec3D var27 = Vec3D.createVector(-0.5D, 0.03D, 0.8D);
            var27.rotateAroundX(-(par1EntityFishHook.angler.prevRotationPitch + (par1EntityFishHook.angler.rotationPitch - par1EntityFishHook.angler.prevRotationPitch) * par9) * (float)Math.PI / 180.0F);
            var27.rotateAroundY(-(par1EntityFishHook.angler.prevRotationYaw + (par1EntityFishHook.angler.rotationYaw - par1EntityFishHook.angler.prevRotationYaw) * par9) * (float)Math.PI / 180.0F);
            var27.rotateAroundY(var26 * 0.5F);
            var27.rotateAroundX(-var26 * 0.7F);
            double var28 = par1EntityFishHook.angler.prevPosX + (par1EntityFishHook.angler.posX - par1EntityFishHook.angler.prevPosX) * (double)par9 + var27.xCoord;
            double var30 = par1EntityFishHook.angler.prevPosY + (par1EntityFishHook.angler.posY - par1EntityFishHook.angler.prevPosY) * (double)par9 + var27.yCoord;
            double var32 = par1EntityFishHook.angler.prevPosZ + (par1EntityFishHook.angler.posZ - par1EntityFishHook.angler.prevPosZ) * (double)par9 + var27.zCoord;

            if (this.renderManager.options.thirdPersonView > 0)
            {
                var20 = (par1EntityFishHook.angler.prevRenderYawOffset + (par1EntityFishHook.angler.renderYawOffset - par1EntityFishHook.angler.prevRenderYawOffset) * par9) * (float)Math.PI / 180.0F;
                var21 = (double)MathHelper.sin(var20);
                var23 = (double)MathHelper.cos(var20);
                var28 = par1EntityFishHook.angler.prevPosX + (par1EntityFishHook.angler.posX - par1EntityFishHook.angler.prevPosX) * (double)par9 - var23 * 0.35D - var21 * 0.85D;
                var30 = par1EntityFishHook.angler.prevPosY + (par1EntityFishHook.angler.posY - par1EntityFishHook.angler.prevPosY) * (double)par9 - 0.45D;
                var32 = par1EntityFishHook.angler.prevPosZ + (par1EntityFishHook.angler.posZ - par1EntityFishHook.angler.prevPosZ) * (double)par9 - var21 * 0.35D + var23 * 0.85D;
            }

            double var34 = par1EntityFishHook.prevPosX + (par1EntityFishHook.posX - par1EntityFishHook.prevPosX) * (double)par9;
            double var36 = par1EntityFishHook.prevPosY + (par1EntityFishHook.posY - par1EntityFishHook.prevPosY) * (double)par9 + 0.25D;
            double var38 = par1EntityFishHook.prevPosZ + (par1EntityFishHook.posZ - par1EntityFishHook.prevPosZ) * (double)par9;
            double var40 = (double)((float)(var28 - var34));
            double var42 = (double)((float)(var30 - var36));
            double var44 = (double)((float)(var32 - var38));
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            var12.startDrawing(3);
            var12.setColorOpaque_I(0);
            byte var46 = 16;

            for (int var47 = 0; var47 <= var46; ++var47)
            {
                float var48 = (float)var47 / (float)var46;
                var12.addVertex(par2 + var40 * (double)var48, par4 + var42 * (double)(var48 * var48 + var48) * 0.5D + 0.25D, par6 + var44 * (double)var48);
            }

            var12.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderFishHook((EntityFishHook)par1Entity, par2, par4, par6, par8, par9);
    }
}
