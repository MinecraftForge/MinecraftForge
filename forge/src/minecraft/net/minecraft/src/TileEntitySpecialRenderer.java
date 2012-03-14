package net.minecraft.src;

public abstract class TileEntitySpecialRenderer
{
    /**
     * The TileEntityRenderer instance associated with this TileEntitySpecialRenderer
     */
    protected TileEntityRenderer tileEntityRenderer;

    public abstract void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8);

    /**
     * Binds a texture to the renderEngine given a filename from the JAR.
     */
    protected void bindTextureByName(String par1Str)
    {
        RenderEngine var2 = this.tileEntityRenderer.renderEngine;

        if (var2 != null)
        {
            var2.bindTexture(var2.getTexture(par1Str));
        }
    }

    /**
     * Associate a TileEntityRenderer with this TileEntitySpecialRenderer
     */
    public void setTileEntityRenderer(TileEntityRenderer par1TileEntityRenderer)
    {
        this.tileEntityRenderer = par1TileEntityRenderer;
    }

    /**
     * Called from TileEntityRenderer.cacheSpecialRenderInfo() to cache render-related references (currently world
     * only). Used by TileEntityRendererPiston to create and store a RenderBlocks instance in the blockRenderer field.
     */
    public void cacheSpecialRenderInfo(World par1World) {}

    public FontRenderer getFontRenderer()
    {
        return this.tileEntityRenderer.getFontRenderer();
    }
}
