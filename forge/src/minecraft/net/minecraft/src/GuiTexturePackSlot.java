package net.minecraft.src;

import java.util.List;
import org.lwjgl.opengl.GL11;

class GuiTexturePackSlot extends GuiSlot
{
    final GuiTexturePacks parentTexturePackGui;

    public GuiTexturePackSlot(GuiTexturePacks par1GuiTexturePacks)
    {
        super(GuiTexturePacks.func_22124_a(par1GuiTexturePacks), par1GuiTexturePacks.width, par1GuiTexturePacks.height, 32, par1GuiTexturePacks.height - 55 + 4, 36);
        this.parentTexturePackGui = par1GuiTexturePacks;
    }

    /**
     * Gets the size of the current slot list.
     */
    protected int getSize()
    {
        List var1 = GuiTexturePacks.func_22126_b(this.parentTexturePackGui).texturePackList.availableTexturePacks();
        return var1.size();
    }

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected void elementClicked(int par1, boolean par2)
    {
        List var3 = GuiTexturePacks.func_22119_c(this.parentTexturePackGui).texturePackList.availableTexturePacks();

        try
        {
            GuiTexturePacks.func_22122_d(this.parentTexturePackGui).texturePackList.setTexturePack((TexturePackBase)var3.get(par1));
            GuiTexturePacks.func_22117_e(this.parentTexturePackGui).renderEngine.refreshTextures();
        }
        catch (Exception var5)
        {
            GuiTexturePacks.func_35307_f(this.parentTexturePackGui).texturePackList.setTexturePack((TexturePackBase)var3.get(0));
            GuiTexturePacks.func_35308_g(this.parentTexturePackGui).renderEngine.refreshTextures();
        }
    }

    /**
     * returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int par1)
    {
        List var2 = GuiTexturePacks.func_22118_f(this.parentTexturePackGui).texturePackList.availableTexturePacks();
        return GuiTexturePacks.func_22116_g(this.parentTexturePackGui).texturePackList.selectedTexturePack == var2.get(par1);
    }

    /**
     * return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.getSize() * 36;
    }

    protected void drawBackground()
    {
        this.parentTexturePackGui.drawDefaultBackground();
    }

    protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator)
    {
        TexturePackBase var6 = (TexturePackBase)GuiTexturePacks.func_22121_h(this.parentTexturePackGui).texturePackList.availableTexturePacks().get(par1);
        var6.bindThumbnailTexture(GuiTexturePacks.func_22123_i(this.parentTexturePackGui));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        par5Tessellator.startDrawingQuads();
        par5Tessellator.setColorOpaque_I(16777215);
        par5Tessellator.addVertexWithUV((double)par2, (double)(par3 + par4), 0.0D, 0.0D, 1.0D);
        par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)(par3 + par4), 0.0D, 1.0D, 1.0D);
        par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)par3, 0.0D, 1.0D, 0.0D);
        par5Tessellator.addVertexWithUV((double)par2, (double)par3, 0.0D, 0.0D, 0.0D);
        par5Tessellator.draw();
        this.parentTexturePackGui.drawString(GuiTexturePacks.func_22127_j(this.parentTexturePackGui), var6.texturePackFileName, par2 + 32 + 2, par3 + 1, 16777215);
        this.parentTexturePackGui.drawString(GuiTexturePacks.func_22120_k(this.parentTexturePackGui), var6.firstDescriptionLine, par2 + 32 + 2, par3 + 12, 8421504);
        this.parentTexturePackGui.drawString(GuiTexturePacks.func_22125_l(this.parentTexturePackGui), var6.secondDescriptionLine, par2 + 32 + 2, par3 + 12 + 10, 8421504);
    }
}
