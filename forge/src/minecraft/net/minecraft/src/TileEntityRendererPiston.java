package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.minecraft.src.forge.ForgeHooksClient;

import org.lwjgl.opengl.GL11;

public class TileEntityRendererPiston extends TileEntitySpecialRenderer
{
    /** instance of RenderBlocks used to draw the piston base and extension. */
    private RenderBlocks blockRenderer;

    public void renderPiston(TileEntityPiston par1TileEntityPiston, double par2, double par4, double par6, float par8)
    {
        Block var9 = Block.blocksList[par1TileEntityPiston.getStoredBlockID()];

        if (var9 != null && par1TileEntityPiston.getProgress(par8) < 1.0F)
        {
            Tessellator var10 = Tessellator.instance;
            this.bindTextureByName("/terrain.png");
            RenderHelper.disableStandardItemLighting();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);

            if (Minecraft.isAmbientOcclusionEnabled())
            {
                GL11.glShadeModel(GL11.GL_SMOOTH);
            }
            else
            {
                GL11.glShadeModel(GL11.GL_FLAT);
            }
            ForgeHooksClient.beforeBlockRender(var9, blockRenderer);
            var10.startDrawingQuads();
            var10.setTranslationD((double)((float)par2 - (float)par1TileEntityPiston.xCoord + par1TileEntityPiston.getOffsetX(par8)), (double)((float)par4 - (float)par1TileEntityPiston.yCoord + par1TileEntityPiston.getOffsetY(par8)), (double)((float)par6 - (float)par1TileEntityPiston.zCoord + par1TileEntityPiston.getOffsetZ(par8)));
            var10.setColorOpaque(1, 1, 1);

            if (var9 == Block.pistonExtension && par1TileEntityPiston.getProgress(par8) < 0.5F)
            {
                this.blockRenderer.renderPistonExtensionAllFaces(var9, par1TileEntityPiston.xCoord, par1TileEntityPiston.yCoord, par1TileEntityPiston.zCoord, false);
            }
            else if (par1TileEntityPiston.shouldRenderHead() && !par1TileEntityPiston.isExtending())
            {
                Block.pistonExtension.setHeadTexture(((BlockPistonBase)var9).getPistonExtensionTexture());
                this.blockRenderer.renderPistonExtensionAllFaces(Block.pistonExtension, par1TileEntityPiston.xCoord, par1TileEntityPiston.yCoord, par1TileEntityPiston.zCoord, par1TileEntityPiston.getProgress(par8) < 0.5F);
                Block.pistonExtension.clearHeadTexture();
                var10.setTranslationD((double)((float)par2 - (float)par1TileEntityPiston.xCoord), (double)((float)par4 - (float)par1TileEntityPiston.yCoord), (double)((float)par6 - (float)par1TileEntityPiston.zCoord));
                this.blockRenderer.renderPistonBaseAllFaces(var9, par1TileEntityPiston.xCoord, par1TileEntityPiston.yCoord, par1TileEntityPiston.zCoord);
            }
            else
            {
                this.blockRenderer.renderBlockAllFaces(var9, par1TileEntityPiston.xCoord, par1TileEntityPiston.yCoord, par1TileEntityPiston.zCoord);
            }

            var10.setTranslationD(0.0D, 0.0D, 0.0D);
            var10.draw();
            ForgeHooksClient.afterBlockRender(var9, blockRenderer);
            RenderHelper.enableStandardItemLighting();
        }
    }

    /**
     * Called from TileEntityRenderer.cacheSpecialRenderInfo() to cache render-related references (currently world
     * only). Used by TileEntityRendererPiston to create and store a RenderBlocks instance in the blockRenderer field.
     */
    public void cacheSpecialRenderInfo(World par1World)
    {
        this.blockRenderer = new RenderBlocks(par1World);
    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
        this.renderPiston((TileEntityPiston)par1TileEntity, par2, par4, par6, par8);
    }
}
