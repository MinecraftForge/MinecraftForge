package net.minecraft.src.balkon;

import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class BalkFlailRender extends Render
{
    public BalkFlailRender()
    {
    }

    public void renderArrow(BalkFlailEntity balkflailentity, double d, double d1, double d2,
            float f, float f1)
    {
        if (balkflailentity.prevRotationYaw == 0.0F && balkflailentity.prevRotationPitch == 0.0F)
        {
            return;
        }
        loadTexture("/infibalkon/flailtextures/flail.png");
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d1, (float)d2);
        GL11.glRotatef((balkflailentity.prevRotationYaw + (balkflailentity.rotationYaw - balkflailentity.prevRotationYaw) * f1) - 90F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(balkflailentity.prevRotationPitch + (balkflailentity.rotationPitch - balkflailentity.prevRotationPitch) * f1, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        int i = 0;
        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = (float)(0 + i * 10) / 32F;
        float f5 = (float)(5 + i * 10) / 32F;
        float f6 = 0.0F;
        float f7 = 0.15625F;
        float f8 = (float)(5 + i * 10) / 32F;
        float f9 = (float)(10 + i * 10) / 32F;
        float f10 = 0.15F;
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        float f11 = -f1;
        if (f11 > 0.0F)
        {
            float f12 = -MathHelper.sin(f11 * 3F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }
        GL11.glColor3f(0.6F, 0.4F, 0.1F);
        GL11.glRotatef(45F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(1.5D, -2D, -2D, f6, f8);
        tessellator.addVertexWithUV(1.5D, -2D, 2D, f7, f8);
        tessellator.addVertexWithUV(1.5D, 2D, 2D, f7, f9);
        tessellator.addVertexWithUV(1.5D, 2D, -2D, f6, f9);
        tessellator.draw();
        GL11.glNormal3f(-f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(1.5D, 2D, -2D, f6, f8);
        tessellator.addVertexWithUV(1.5D, 2D, 2D, f7, f8);
        tessellator.addVertexWithUV(1.5D, -2D, 2D, f7, f9);
        tessellator.addVertexWithUV(1.5D, -2D, -2D, f6, f9);
        tessellator.draw();
        for (int j = 0; j < 4; j++)
        {
            GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8D, -2D, 0.0D, f2, f4);
            tessellator.addVertexWithUV(8D, -2D, 0.0D, f3, f4);
            tessellator.addVertexWithUV(8D, 2D, 0.0D, f3, f5);
            tessellator.addVertexWithUV(-8D, 2D, 0.0D, f2, f5);
            tessellator.draw();
        }

        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d1, (float)d2);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        boolean flag = true;
        byte byte0 = 2;
        float f13 = (float)(i * 8 + 0) / 128F;
        float f14 = (float)(i * 8 + 8) / 128F;
        float f15 = (float)(byte0 * 8 + 0) / 128F;
        float f16 = (float)(byte0 * 8 + 8) / 128F;
        float f17 = 1.0F;
        float f18 = 0.5F;
        float f19 = 0.5F;
        GL11.glRotatef(180F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV(0.0F - f18, 0.0F - f19, 0.0D, f13, f16);
        tessellator.addVertexWithUV(f17 - f18, 0.0F - f19, 0.0D, f14, f16);
        tessellator.addVertexWithUV(f17 - f18, 1.0F - f19, 0.0D, f14, f15);
        tessellator.addVertexWithUV(0.0F - f18, 1.0F - f19, 0.0D, f13, f15);
        tessellator.draw();
        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glPopMatrix();
        if (balkflailentity.shootingEntity != null)
        {
            float f20 = ((balkflailentity.shootingEntity.prevRotationYaw + (balkflailentity.shootingEntity.rotationYaw - balkflailentity.shootingEntity.prevRotationYaw) * f1) * 3.141593F) / 180F;
            double d3 = MathHelper.sin(f20);
            double d4 = MathHelper.cos(f20);
            float f21 = ((EntityLiving)balkflailentity.shootingEntity).getSwingProgress(f1);
            float f22 = MathHelper.sin(MathHelper.sqrt_float(f21) * 3.141593F);
            Vec3D vec3d = Vec3D.createVector(-0.5D, 0.029999999999999999D, 0.80000000000000004D);
            vec3d.rotateAroundX((-(balkflailentity.shootingEntity.prevRotationPitch + (balkflailentity.shootingEntity.rotationPitch - balkflailentity.shootingEntity.prevRotationPitch) * f1) * 3.141593F) / 180F);
            vec3d.rotateAroundY((-(balkflailentity.shootingEntity.prevRotationYaw + (balkflailentity.shootingEntity.rotationYaw - balkflailentity.shootingEntity.prevRotationYaw) * f1) * 3.141593F) / 180F);
            vec3d.rotateAroundY(f22 * 0.5F);
            vec3d.rotateAroundX(-f22 * 0.7F);
            double d5 = balkflailentity.shootingEntity.prevPosX + (balkflailentity.shootingEntity.posX - balkflailentity.shootingEntity.prevPosX) * (double)f1 + vec3d.xCoord;
            double d6 = balkflailentity.shootingEntity.prevPosY + (balkflailentity.shootingEntity.posY - balkflailentity.shootingEntity.prevPosY) * (double)f1 + vec3d.yCoord;
            double d7 = balkflailentity.shootingEntity.prevPosZ + (balkflailentity.shootingEntity.posZ - balkflailentity.shootingEntity.prevPosZ) * (double)f1 + vec3d.zCoord;
            if (renderManager.options.thirdPersonView > 0)
            {
                float f23 = ((((EntityLiving)balkflailentity.shootingEntity).prevRenderYawOffset + (((EntityLiving)balkflailentity.shootingEntity).renderYawOffset - ((EntityLiving)balkflailentity.shootingEntity).prevRenderYawOffset) * f1) * 3.141593F) / 180F;
                double d9 = MathHelper.sin(f23);
                double d11 = MathHelper.cos(f23);
                d5 = (balkflailentity.shootingEntity.prevPosX + (balkflailentity.shootingEntity.posX - balkflailentity.shootingEntity.prevPosX) * (double)f1) - d11 * 0.34999999999999998D - d9 * 0.84999999999999998D;
                d6 = (balkflailentity.shootingEntity.prevPosY + (balkflailentity.shootingEntity.posY - balkflailentity.shootingEntity.prevPosY) * (double)f1) - 0.45000000000000001D;
                d7 = ((balkflailentity.shootingEntity.prevPosZ + (balkflailentity.shootingEntity.posZ - balkflailentity.shootingEntity.prevPosZ) * (double)f1) - d9 * 0.34999999999999998D) + d11 * 0.84999999999999998D;
            }
            double d8 = balkflailentity.prevPosX + (balkflailentity.posX - balkflailentity.prevPosX) * (double)f1;
            double d10 = balkflailentity.prevPosY + (balkflailentity.posY - balkflailentity.prevPosY) * (double)f1 + 0.25D;
            double d12 = balkflailentity.prevPosZ + (balkflailentity.posZ - balkflailentity.prevPosZ) * (double)f1;
            double d13 = (float)(d5 - d8);
            double d14 = (float)(d6 - d10);
            double d15 = (float)(d7 - d12);
            GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
            GL11.glDisable(2896 /*GL_LIGHTING*/);
            tessellator.startDrawing(3);
            tessellator.setColorOpaque_I(0);
            int k = 16;
            for (int l = 0; l <= k; l++)
            {
                float f24 = (float)l / (float)k;
                tessellator.addVertex(d + d13 * (double)f24, d1 + d14 * (double)(f24 * f24 + f24) * 0.5D + 0.25D, d2 + d15 * (double)f24);
            }

            tessellator.draw();
            GL11.glEnable(2896 /*GL_LIGHTING*/);
            GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
        }
    }

    public void doRender(Entity entity, double d, double d1, double d2,
            float f, float f1)
    {
        renderArrow((BalkFlailEntity)entity, d, d1, d2, f, f1);
    }
}
