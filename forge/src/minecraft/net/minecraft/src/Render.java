package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public abstract class Render
{
    protected RenderManager renderManager;
    private ModelBase modelBase = new ModelBiped();
    protected RenderBlocks renderBlocks = new RenderBlocks();
    protected float shadowSize = 0.0F;

    /**
     * Determines the darkness of the object's shadow. Higher value makes a darker shadow.
     */
    protected float shadowOpaque = 1.0F;

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public abstract void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9);

    /**
     * loads the specified texture
     */
    protected void loadTexture(String par1Str)
    {
        RenderEngine var2 = this.renderManager.renderEngine;
        var2.bindTexture(var2.getTexture(par1Str));
    }

    /**
     * loads the specified downloadable texture or alternative built in texture
     */
    protected boolean loadDownloadableImageTexture(String par1Str, String par2Str)
    {
        RenderEngine var3 = this.renderManager.renderEngine;
        int var4 = var3.getTextureForDownloadableImage(par1Str, par2Str);

        if (var4 >= 0)
        {
            var3.bindTexture(var4);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Renders fire on top of the entity. Args: entity, x, y, z, partialTickTime
     */
    private void renderEntityOnFire(Entity par1Entity, double par2, double par4, double par6, float par8)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        int var9 = Block.fire.blockIndexInTexture;
        int var10 = (var9 & 15) << 4;
        int var11 = var9 & 240;
        float var12 = (float)var10 / 256.0F;
        float var13 = ((float)var10 + 15.99F) / 256.0F;
        float var14 = (float)var11 / 256.0F;
        float var15 = ((float)var11 + 15.99F) / 256.0F;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        float var16 = par1Entity.width * 1.4F;
        GL11.glScalef(var16, var16, var16);
        this.loadTexture("/terrain.png");
        Tessellator var17 = Tessellator.instance;
        float var18 = 0.5F;
        float var19 = 0.0F;
        float var20 = par1Entity.height / var16;
        float var21 = (float)(par1Entity.posY - par1Entity.boundingBox.minY);
        GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.0F, -0.3F + (float)((int)var20) * 0.02F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var22 = 0.0F;
        int var23 = 0;
        var17.startDrawingQuads();

        while (var20 > 0.0F)
        {
            if (var23 % 2 == 0)
            {
                var12 = (float)var10 / 256.0F;
                var13 = ((float)var10 + 15.99F) / 256.0F;
                var14 = (float)var11 / 256.0F;
                var15 = ((float)var11 + 15.99F) / 256.0F;
            }
            else
            {
                var12 = (float)var10 / 256.0F;
                var13 = ((float)var10 + 15.99F) / 256.0F;
                var14 = (float)(var11 + 16) / 256.0F;
                var15 = ((float)(var11 + 16) + 15.99F) / 256.0F;
            }

            if (var23 / 2 % 2 == 0)
            {
                float var24 = var13;
                var13 = var12;
                var12 = var24;
            }

            var17.addVertexWithUV((double)(var18 - var19), (double)(0.0F - var21), (double)var22, (double)var13, (double)var15);
            var17.addVertexWithUV((double)(-var18 - var19), (double)(0.0F - var21), (double)var22, (double)var12, (double)var15);
            var17.addVertexWithUV((double)(-var18 - var19), (double)(1.4F - var21), (double)var22, (double)var12, (double)var14);
            var17.addVertexWithUV((double)(var18 - var19), (double)(1.4F - var21), (double)var22, (double)var13, (double)var14);
            var20 -= 0.45F;
            var21 -= 0.45F;
            var18 *= 0.9F;
            var22 += 0.03F;
            ++var23;
        }

        var17.draw();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * Renders the entity shadows at the position, shadow alpha and partialTickTime. Args: entity, x, y, z, shadowAlpha,
     * partialTickTime
     */
    private void renderShadow(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderEngine var10 = this.renderManager.renderEngine;
        var10.bindTexture(var10.getTexture("%clamp%/misc/shadow.png"));
        World var11 = this.getWorldFromRenderManager();
        GL11.glDepthMask(false);
        float var12 = this.shadowSize;

        if (par1Entity instanceof EntityLiving)
        {
            EntityLiving var13 = (EntityLiving)par1Entity;
            var12 *= var13.getRenderSizeModifier();

            if (var13 instanceof EntityAnimal)
            {
                EntityAnimal var14 = (EntityAnimal)var13;

                if (var14.isChild())
                {
                    var12 *= 0.5F;
                }
            }
        }

        double var36 = par1Entity.lastTickPosX + (par1Entity.posX - par1Entity.lastTickPosX) * (double)par9;
        double var15 = par1Entity.lastTickPosY + (par1Entity.posY - par1Entity.lastTickPosY) * (double)par9 + (double)par1Entity.getShadowSize();
        double var17 = par1Entity.lastTickPosZ + (par1Entity.posZ - par1Entity.lastTickPosZ) * (double)par9;
        int var19 = MathHelper.floor_double(var36 - (double)var12);
        int var20 = MathHelper.floor_double(var36 + (double)var12);
        int var21 = MathHelper.floor_double(var15 - (double)var12);
        int var22 = MathHelper.floor_double(var15);
        int var23 = MathHelper.floor_double(var17 - (double)var12);
        int var24 = MathHelper.floor_double(var17 + (double)var12);
        double var25 = par2 - var36;
        double var27 = par4 - var15;
        double var29 = par6 - var17;
        Tessellator var31 = Tessellator.instance;
        var31.startDrawingQuads();

        for (int var32 = var19; var32 <= var20; ++var32)
        {
            for (int var33 = var21; var33 <= var22; ++var33)
            {
                for (int var34 = var23; var34 <= var24; ++var34)
                {
                    int var35 = var11.getBlockId(var32, var33 - 1, var34);

                    if (var35 > 0 && var11.getBlockLightValue(var32, var33, var34) > 3)
                    {
                        this.renderShadowOnBlock(Block.blocksList[var35], par2, par4 + (double)par1Entity.getShadowSize(), par6, var32, var33, var34, par8, var12, var25, var27 + (double)par1Entity.getShadowSize(), var29);
                    }
                }
            }
        }

        var31.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }

    /**
     * Returns the render manager's world object
     */
    private World getWorldFromRenderManager()
    {
        return this.renderManager.worldObj;
    }

    /**
     * Renders a shadow projected down onto the specified block. Brightness of the block plus how far away on the Y axis
     * determines the alpha of the shadow.  Args: block, centerX, centerY, centerZ, blockX, blockY, blockZ, baseAlpha,
     * shadowSize, xOffset, yOffset, zOffset
     */
    private void renderShadowOnBlock(Block par1Block, double par2, double par4, double par6, int par8, int par9, int par10, float par11, float par12, double par13, double par15, double par17)
    {
        Tessellator var19 = Tessellator.instance;

        if (par1Block.renderAsNormalBlock())
        {
            double var20 = ((double)par11 - (par4 - ((double)par9 + par15)) / 2.0D) * 0.5D * (double)this.getWorldFromRenderManager().getLightBrightness(par8, par9, par10);

            if (var20 >= 0.0D)
            {
                if (var20 > 1.0D)
                {
                    var20 = 1.0D;
                }

                var19.setColorRGBA_F(1.0F, 1.0F, 1.0F, (float)var20);
                double var22 = (double)par8 + par1Block.minX + par13;
                double var24 = (double)par8 + par1Block.maxX + par13;
                double var26 = (double)par9 + par1Block.minY + par15 + 0.015625D;
                double var28 = (double)par10 + par1Block.minZ + par17;
                double var30 = (double)par10 + par1Block.maxZ + par17;
                float var32 = (float)((par2 - var22) / 2.0D / (double)par12 + 0.5D);
                float var33 = (float)((par2 - var24) / 2.0D / (double)par12 + 0.5D);
                float var34 = (float)((par6 - var28) / 2.0D / (double)par12 + 0.5D);
                float var35 = (float)((par6 - var30) / 2.0D / (double)par12 + 0.5D);
                var19.addVertexWithUV(var22, var26, var28, (double)var32, (double)var34);
                var19.addVertexWithUV(var22, var26, var30, (double)var32, (double)var35);
                var19.addVertexWithUV(var24, var26, var30, (double)var33, (double)var35);
                var19.addVertexWithUV(var24, var26, var28, (double)var33, (double)var34);
            }
        }
    }

    /**
     * Renders a white box with the bounds of the AABB translated by the offset. Args: aabb, x, y, z
     */
    public static void renderOffsetAABB(AxisAlignedBB par0AxisAlignedBB, double par1, double par3, double par5)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator var7 = Tessellator.instance;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        var7.startDrawingQuads();
        var7.setTranslationD(par1, par3, par5);
        var7.setNormal(0.0F, 0.0F, -1.0F);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var7.setNormal(0.0F, 0.0F, 1.0F);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var7.setNormal(0.0F, -1.0F, 0.0F);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var7.setNormal(0.0F, 1.0F, 0.0F);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var7.setNormal(-1.0F, 0.0F, 0.0F);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var7.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var7.setNormal(1.0F, 0.0F, 0.0F);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var7.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var7.setTranslationD(0.0D, 0.0D, 0.0D);
        var7.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Adds to the tesselator a box using the aabb for the bounds. Args: aabb
     */
    public static void renderAABB(AxisAlignedBB par0AxisAlignedBB)
    {
        Tessellator var1 = Tessellator.instance;
        var1.startDrawingQuads();
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        var1.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        var1.draw();
    }

    /**
     * Sets the RenderManager.
     */
    public void setRenderManager(RenderManager par1RenderManager)
    {
        this.renderManager = par1RenderManager;
    }

    /**
     * Renders the entity's shadow and fire (if its on fire). Args: entity, x, y, z, yaw, partialTickTime
     */
    public void doRenderShadowAndFire(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        if (this.renderManager.options.fancyGraphics && this.shadowSize > 0.0F)
        {
            double var10 = this.renderManager.getDistanceToCamera(par1Entity.posX, par1Entity.posY, par1Entity.posZ);
            float var12 = (float)((1.0D - var10 / 256.0D) * (double)this.shadowOpaque);

            if (var12 > 0.0F)
            {
                this.renderShadow(par1Entity, par2, par4, par6, var12, par9);
            }
        }

        if (par1Entity.isBurning())
        {
            this.renderEntityOnFire(par1Entity, par2, par4, par6, par9);
        }
    }

    /**
     * Returns the font renderer from the set render manager
     */
    public FontRenderer getFontRendererFromRenderManager()
    {
        return this.renderManager.getFontRenderer();
    }
}
