package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderFallingSand extends Render
{
    private RenderBlocks renderBlocks = new RenderBlocks();

    public RenderFallingSand()
    {
        this.shadowSize = 0.5F;
    }

    /**
     * The actual render method that is used in doRender
     */
    public void doRenderFallingSand(EntityFallingSand par1EntityFallingSand, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        this.loadTexture("/terrain.png");
        Block var10 = Block.blocksList[par1EntityFallingSand.blockID];
        World var11 = par1EntityFallingSand.getWorld();
        GL11.glDisable(GL11.GL_LIGHTING);

        if (var10 == Block.dragonEgg)
        {
            this.renderBlocks.blockAccess = var11;
            Tessellator var12 = Tessellator.instance;
            var12.startDrawingQuads();
            var12.setTranslationD((double)((float)(-MathHelper.floor_double(par1EntityFallingSand.posX)) - 0.5F), (double)((float)(-MathHelper.floor_double(par1EntityFallingSand.posY)) - 0.5F), (double)((float)(-MathHelper.floor_double(par1EntityFallingSand.posZ)) - 0.5F));
            this.renderBlocks.renderBlockByRenderType(var10, MathHelper.floor_double(par1EntityFallingSand.posX), MathHelper.floor_double(par1EntityFallingSand.posY), MathHelper.floor_double(par1EntityFallingSand.posZ));
            var12.setTranslationD(0.0D, 0.0D, 0.0D);
            var12.draw();
        }
        else
        {
            this.renderBlocks.renderBlockFallingSand(var10, var11, MathHelper.floor_double(par1EntityFallingSand.posX), MathHelper.floor_double(par1EntityFallingSand.posY), MathHelper.floor_double(par1EntityFallingSand.posZ));
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderFallingSand((EntityFallingSand)par1Entity, par2, par4, par6, par8, par9);
    }
}
