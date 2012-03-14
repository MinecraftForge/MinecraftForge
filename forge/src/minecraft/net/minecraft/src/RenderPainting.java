package net.minecraft.src;

import java.util.Random;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderPainting extends Render
{
    /** RNG. */
    private Random rand = new Random();

    public void func_158_a(EntityPainting par1EntityPainting, double par2, double par4, double par6, float par8, float par9)
    {
        this.rand.setSeed(187L);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glRotatef(par8, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        this.loadTexture("/art/kz.png");
        EnumArt var10 = par1EntityPainting.art;
        float var11 = 0.0625F;
        GL11.glScalef(var11, var11, var11);
        this.func_159_a(par1EntityPainting, var10.sizeX, var10.sizeY, var10.offsetX, var10.offsetY);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    private void func_159_a(EntityPainting par1EntityPainting, int par2, int par3, int par4, int par5)
    {
        float var6 = (float)(-par2) / 2.0F;
        float var7 = (float)(-par3) / 2.0F;
        float var8 = -0.5F;
        float var9 = 0.5F;

        for (int var10 = 0; var10 < par2 / 16; ++var10)
        {
            for (int var11 = 0; var11 < par3 / 16; ++var11)
            {
                float var12 = var6 + (float)((var10 + 1) * 16);
                float var13 = var6 + (float)(var10 * 16);
                float var14 = var7 + (float)((var11 + 1) * 16);
                float var15 = var7 + (float)(var11 * 16);
                this.func_160_a(par1EntityPainting, (var12 + var13) / 2.0F, (var14 + var15) / 2.0F);
                float var16 = (float)(par4 + par2 - var10 * 16) / 256.0F;
                float var17 = (float)(par4 + par2 - (var10 + 1) * 16) / 256.0F;
                float var18 = (float)(par5 + par3 - var11 * 16) / 256.0F;
                float var19 = (float)(par5 + par3 - (var11 + 1) * 16) / 256.0F;
                float var20 = 0.75F;
                float var21 = 0.8125F;
                float var22 = 0.0F;
                float var23 = 0.0625F;
                float var24 = 0.75F;
                float var25 = 0.8125F;
                float var26 = 0.001953125F;
                float var27 = 0.001953125F;
                float var28 = 0.7519531F;
                float var29 = 0.7519531F;
                float var30 = 0.0F;
                float var31 = 0.0625F;
                Tessellator var32 = Tessellator.instance;
                var32.startDrawingQuads();
                var32.setNormal(0.0F, 0.0F, -1.0F);
                var32.addVertexWithUV((double)var12, (double)var15, (double)var8, (double)var17, (double)var18);
                var32.addVertexWithUV((double)var13, (double)var15, (double)var8, (double)var16, (double)var18);
                var32.addVertexWithUV((double)var13, (double)var14, (double)var8, (double)var16, (double)var19);
                var32.addVertexWithUV((double)var12, (double)var14, (double)var8, (double)var17, (double)var19);
                var32.setNormal(0.0F, 0.0F, 1.0F);
                var32.addVertexWithUV((double)var12, (double)var14, (double)var9, (double)var20, (double)var22);
                var32.addVertexWithUV((double)var13, (double)var14, (double)var9, (double)var21, (double)var22);
                var32.addVertexWithUV((double)var13, (double)var15, (double)var9, (double)var21, (double)var23);
                var32.addVertexWithUV((double)var12, (double)var15, (double)var9, (double)var20, (double)var23);
                var32.setNormal(0.0F, 1.0F, 0.0F);
                var32.addVertexWithUV((double)var12, (double)var14, (double)var8, (double)var24, (double)var26);
                var32.addVertexWithUV((double)var13, (double)var14, (double)var8, (double)var25, (double)var26);
                var32.addVertexWithUV((double)var13, (double)var14, (double)var9, (double)var25, (double)var27);
                var32.addVertexWithUV((double)var12, (double)var14, (double)var9, (double)var24, (double)var27);
                var32.setNormal(0.0F, -1.0F, 0.0F);
                var32.addVertexWithUV((double)var12, (double)var15, (double)var9, (double)var24, (double)var26);
                var32.addVertexWithUV((double)var13, (double)var15, (double)var9, (double)var25, (double)var26);
                var32.addVertexWithUV((double)var13, (double)var15, (double)var8, (double)var25, (double)var27);
                var32.addVertexWithUV((double)var12, (double)var15, (double)var8, (double)var24, (double)var27);
                var32.setNormal(-1.0F, 0.0F, 0.0F);
                var32.addVertexWithUV((double)var12, (double)var14, (double)var9, (double)var29, (double)var30);
                var32.addVertexWithUV((double)var12, (double)var15, (double)var9, (double)var29, (double)var31);
                var32.addVertexWithUV((double)var12, (double)var15, (double)var8, (double)var28, (double)var31);
                var32.addVertexWithUV((double)var12, (double)var14, (double)var8, (double)var28, (double)var30);
                var32.setNormal(1.0F, 0.0F, 0.0F);
                var32.addVertexWithUV((double)var13, (double)var14, (double)var8, (double)var29, (double)var30);
                var32.addVertexWithUV((double)var13, (double)var15, (double)var8, (double)var29, (double)var31);
                var32.addVertexWithUV((double)var13, (double)var15, (double)var9, (double)var28, (double)var31);
                var32.addVertexWithUV((double)var13, (double)var14, (double)var9, (double)var28, (double)var30);
                var32.draw();
            }
        }
    }

    private void func_160_a(EntityPainting par1EntityPainting, float par2, float par3)
    {
        int var4 = MathHelper.floor_double(par1EntityPainting.posX);
        int var5 = MathHelper.floor_double(par1EntityPainting.posY + (double)(par3 / 16.0F));
        int var6 = MathHelper.floor_double(par1EntityPainting.posZ);

        if (par1EntityPainting.direction == 0)
        {
            var4 = MathHelper.floor_double(par1EntityPainting.posX + (double)(par2 / 16.0F));
        }

        if (par1EntityPainting.direction == 1)
        {
            var6 = MathHelper.floor_double(par1EntityPainting.posZ - (double)(par2 / 16.0F));
        }

        if (par1EntityPainting.direction == 2)
        {
            var4 = MathHelper.floor_double(par1EntityPainting.posX - (double)(par2 / 16.0F));
        }

        if (par1EntityPainting.direction == 3)
        {
            var6 = MathHelper.floor_double(par1EntityPainting.posZ + (double)(par2 / 16.0F));
        }

        int var7 = this.renderManager.worldObj.getLightBrightnessForSkyBlocks(var4, var5, var6, 0);
        int var8 = var7 % 65536;
        int var9 = var7 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)var8, (float)var9);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.func_158_a((EntityPainting)par1Entity, par2, par4, par6, par8, par9);
    }
}
