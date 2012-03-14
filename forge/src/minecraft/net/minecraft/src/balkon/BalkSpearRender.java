package net.minecraft.src.balkon;

import java.io.PrintStream;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class BalkSpearRender extends Render
{
    public BalkSpearRender()
    {
    }

    public void renderArrow(BalkSpearEntity balkspearentity, double d, double d1, double d2,
            float f, float f1)
    {
        if (balkspearentity.prevRotationYaw == 0.0F && balkspearentity.prevRotationPitch == 0.0F)
        {
            return;
        }
        loadTexture("/infibalkon/speartextures/spear.png");
        System.out.println("Spear renderer loaded");
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d1, (float)d2);
        GL11.glRotatef((balkspearentity.prevRotationYaw + (balkspearentity.rotationYaw - balkspearentity.prevRotationYaw) * f1) - 90F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(balkspearentity.prevRotationPitch + (balkspearentity.rotationPitch - balkspearentity.prevRotationPitch) * f1, 0.0F, 0.0F, 1.0F);
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
        float f10 = 0.05625F;
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        float f11 = (float)balkspearentity.arrowShake - f1;
        if (f11 > 0.0F)
        {
            float f12 = -MathHelper.sin(f11 * 3F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }
        GL11.glRotatef(45F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-14D, -2D, -2D, f6, f8);
        tessellator.addVertexWithUV(-14D, -2D, 2D, f7, f8);
        tessellator.addVertexWithUV(-14D, 2D, 2D, f7, f9);
        tessellator.addVertexWithUV(-14D, 2D, -2D, f6, f9);
        tessellator.draw();
        GL11.glNormal3f(-f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-14D, 2D, -2D, f6, f8);
        tessellator.addVertexWithUV(-14D, 2D, 2D, f7, f8);
        tessellator.addVertexWithUV(-14D, -2D, 2D, f7, f9);
        tessellator.addVertexWithUV(-14D, -2D, -2D, f6, f9);
        tessellator.draw();
        for (int j = 0; j < 4; j++)
        {
            GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-14D, -2D, 0.0D, f2, f4);
            tessellator.addVertexWithUV(14D, -2D, 0.0D, f3, f4);
            tessellator.addVertexWithUV(14D, 2D, 0.0D, f3, f5);
            tessellator.addVertexWithUV(-14D, 2D, 0.0D, f2, f5);
            tessellator.draw();
        }

        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glPopMatrix();
    }

    public void doRender(Entity entity, double d, double d1, double d2,
            float f, float f1)
    {
        renderArrow((BalkSpearEntity)entity, d, d1, d2, f, f1);
    }
}
