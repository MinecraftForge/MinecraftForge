package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class Gui
{
    protected float zLevel = 0.0F;

    protected void drawHorizontalLine(int par1, int par2, int par3, int par4)
    {
        if (par2 < par1)
        {
            int var5 = par1;
            par1 = par2;
            par2 = var5;
        }

        this.drawRect(par1, par3, par2 + 1, par3 + 1, par4);
    }

    protected void drawVerticalLine(int par1, int par2, int par3, int par4)
    {
        if (par3 < par2)
        {
            int var5 = par2;
            par2 = par3;
            par3 = var5;
        }

        this.drawRect(par1, par2 + 1, par1 + 1, par3, par4);
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color.
     */
    protected void drawRect(int par1, int par2, int par3, int par4, int par5)
    {
        int var6;

        if (par1 < par3)
        {
            var6 = par1;
            par1 = par3;
            par3 = var6;
        }

        if (par2 < par4)
        {
            var6 = par2;
            par2 = par4;
            par4 = var6;
        }

        float var11 = (float)(par5 >> 24 & 255) / 255.0F;
        float var7 = (float)(par5 >> 16 & 255) / 255.0F;
        float var8 = (float)(par5 >> 8 & 255) / 255.0F;
        float var9 = (float)(par5 & 255) / 255.0F;
        Tessellator var10 = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(var7, var8, var9, var11);
        var10.startDrawingQuads();
        var10.addVertex((double)par1, (double)par4, 0.0D);
        var10.addVertex((double)par3, (double)par4, 0.0D);
        var10.addVertex((double)par3, (double)par2, 0.0D);
        var10.addVertex((double)par1, (double)par2, 0.0D);
        var10.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors.
     */
    protected void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float var7 = (float)(par5 >> 24 & 255) / 255.0F;
        float var8 = (float)(par5 >> 16 & 255) / 255.0F;
        float var9 = (float)(par5 >> 8 & 255) / 255.0F;
        float var10 = (float)(par5 & 255) / 255.0F;
        float var11 = (float)(par6 >> 24 & 255) / 255.0F;
        float var12 = (float)(par6 >> 16 & 255) / 255.0F;
        float var13 = (float)(par6 >> 8 & 255) / 255.0F;
        float var14 = (float)(par6 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator var15 = Tessellator.instance;
        var15.startDrawingQuads();
        var15.setColorRGBA_F(var8, var9, var10, var7);
        var15.addVertex((double)par3, (double)par2, (double)this.zLevel);
        var15.addVertex((double)par1, (double)par2, (double)this.zLevel);
        var15.setColorRGBA_F(var12, var13, var14, var11);
        var15.addVertex((double)par1, (double)par4, (double)this.zLevel);
        var15.addVertex((double)par3, (double)par4, (double)this.zLevel);
        var15.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Renders the specified text to the screen, center-aligned.
     */
    public void drawCenteredString(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5)
    {
        par1FontRenderer.drawStringWithShadow(par2Str, par3 - par1FontRenderer.getStringWidth(par2Str) / 2, par4, par5);
    }

    /**
     * Renders the specified text to the screen.
     */
    public void drawString(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5)
    {
        par1FontRenderer.drawStringWithShadow(par2Str, par3, par4, par5);
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + 0) * var8));
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + 0) * var8));
        var9.draw();
    }
}
