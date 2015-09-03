/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiScrollingList
{
    private final Minecraft client;
    protected final int listWidth;
    protected final int listHeight;
    protected final int top;
    protected final int bottom;
    protected final int right;
    protected final int left;
    protected final int slotHeight;
    private int scrollUpActionId;
    private int scrollDownActionId;
    protected int mouseX;
    protected int mouseY;
    private float initialMouseClickY = -2.0F;
    private float scrollFactor;
    private float scrollDistance;
    protected int selectedIndex = -1;
    private long lastClickTime = 0L;
    private boolean field_25123_p = true;
    private boolean field_27262_q;
    private int field_27261_r;

    public GuiScrollingList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight)
    {
        this.client = client;
        this.listWidth = width;
        this.listHeight = height;
        this.top = top;
        this.bottom = bottom;
        this.slotHeight = entryHeight;
        this.left = left;
        this.right = width + this.left;
    }

    public void func_27258_a(boolean p_27258_1_)
    {
        this.field_25123_p = p_27258_1_;
    }

    protected void func_27259_a(boolean p_27259_1_, int p_27259_2_)
    {
        this.field_27262_q = p_27259_1_;
        this.field_27261_r = p_27259_2_;

        if (!p_27259_1_)
        {
            this.field_27261_r = 0;
        }
    }

    protected abstract int getSize();

    protected abstract void elementClicked(int index, boolean doubleClick);

    protected abstract boolean isSelected(int index);

    protected int getContentHeight()
    {
        return this.getSize() * this.slotHeight + this.field_27261_r;
    }

    protected abstract void drawBackground();

    protected abstract void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5);

    protected void func_27260_a(int p_27260_1_, int p_27260_2_, Tessellator p_27260_3_) {}

    protected void func_27255_a(int p_27255_1_, int p_27255_2_) {}

    protected void func_27257_b(int p_27257_1_, int p_27257_2_) {}

    public int func_27256_c(int p_27256_1_, int p_27256_2_)

    {
        int var3 = this.left + 1;
        int var4 = this.left + this.listWidth - 7;
        int var5 = p_27256_2_ - this.top - this.field_27261_r + (int)this.scrollDistance - 4;
        int var6 = var5 / this.slotHeight;
        return p_27256_1_ >= var3 && p_27256_1_ <= var4 && var6 >= 0 && var5 >= 0 && var6 < this.getSize() ? var6 : -1;
    }

    public void registerScrollButtons(@SuppressWarnings("rawtypes") List p_22240_1_, int p_22240_2_, int p_22240_3_)
    {
        this.scrollUpActionId = p_22240_2_;
        this.scrollDownActionId = p_22240_3_;
    }

    private void applyScrollLimits()
    {
        int var1 = this.getContentHeight() - (this.bottom - this.top - 4);

        if (var1 < 0)
        {
            var1 /= 2;
        }

        if (this.scrollDistance < 0.0F)
        {
            this.scrollDistance = 0.0F;
        }

        if (this.scrollDistance > (float)var1)
        {
            this.scrollDistance = (float)var1;
        }
    }

    public void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == this.scrollUpActionId)
            {
                this.scrollDistance -= (float)(this.slotHeight * 2 / 3);
                this.initialMouseClickY = -2.0F;
                this.applyScrollLimits();
            }
            else if (button.id == this.scrollDownActionId)
            {
                this.scrollDistance += (float)(this.slotHeight * 2 / 3);
                this.initialMouseClickY = -2.0F;
                this.applyScrollLimits();
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float p_22243_3_)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.drawBackground();
        int listLength = this.getSize();
        int scrollBarXStart = this.left + this.listWidth - 6;
        int scrollBarXEnd = scrollBarXStart + 6;
        int boxLeft = this.left;
        int boxRight = scrollBarXStart-1;
        int var10;
        int var11;
        int var13;
        int var19;

        if (Mouse.isButtonDown(0))
        {
            if (this.initialMouseClickY == -1.0F)
            {
                boolean var7 = true;

                if (mouseY >= this.top && mouseY <= this.bottom)
                {
                    var10 = mouseY - this.top - this.field_27261_r + (int)this.scrollDistance - 4;
                    var11 = var10 / this.slotHeight;

                    if (mouseX >= boxLeft && mouseX <= boxRight && var11 >= 0 && var10 >= 0 && var11 < listLength)
                    {
                        boolean var12 = var11 == this.selectedIndex && System.currentTimeMillis() - this.lastClickTime < 250L;
                        this.elementClicked(var11, var12);
                        this.selectedIndex = var11;
                        this.lastClickTime = System.currentTimeMillis();
                    }
                    else if (mouseX >= boxLeft && mouseX <= boxRight && var10 < 0)
                    {
                        this.func_27255_a(mouseX - boxLeft, mouseY - this.top + (int)this.scrollDistance - 4);
                        var7 = false;
                    }

                    if (mouseX >= scrollBarXStart && mouseX <= scrollBarXEnd)
                    {
                        this.scrollFactor = -1.0F;
                        var19 = this.getContentHeight() - (this.bottom - this.top - 4);

                        if (var19 < 1)
                        {
                            var19 = 1;
                        }

                        var13 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());

                        if (var13 < 32)
                        {
                            var13 = 32;
                        }

                        if (var13 > this.bottom - this.top - 8)
                        {
                            var13 = this.bottom - this.top - 8;
                        }

                        this.scrollFactor /= (float)(this.bottom - this.top - var13) / (float)var19;
                    }
                    else
                    {
                        this.scrollFactor = 1.0F;
                    }

                    if (var7)
                    {
                        this.initialMouseClickY = (float)mouseY;
                    }
                    else
                    {
                        this.initialMouseClickY = -2.0F;
                    }
                }
                else
                {
                    this.initialMouseClickY = -2.0F;
                }
            }
            else if (this.initialMouseClickY >= 0.0F)
            {
                this.scrollDistance -= ((float)mouseY - this.initialMouseClickY) * this.scrollFactor;
                this.initialMouseClickY = (float)mouseY;
            }
        }
        else
        {
            while (Mouse.next())
            {
                int var16 = Mouse.getEventDWheel();

                if (var16 != 0)
                {
                    if (var16 > 0)
                    {
                        var16 = -1;
                    }
                    else if (var16 < 0)
                    {
                        var16 = 1;
                    }

                    this.scrollDistance += (float)(var16 * this.slotHeight / 2);
                }
            }

            this.initialMouseClickY = -1.0F;
        }

        this.applyScrollLimits();
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer worldr = tess.getWorldRenderer();
        if (this.client.theWorld != null)
        {
            this.drawGradientRect(this.left, this.top, this.right, this.bottom, -1072689136, -804253680);
        }
        else
        {
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            this.client.renderEngine.bindTexture(Gui.optionsBackground);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float var17 = 32.0F;
            worldr.startDrawingQuads();
            worldr.setColorOpaque_I(2105376);
            worldr.addVertexWithUV((double)this.left, (double)this.bottom, 0.0D, (double)((float)this.left / var17), (double)((float)(this.bottom + (int)this.scrollDistance) / var17));
            worldr.addVertexWithUV((double)this.right, (double)this.bottom, 0.0D, (double)((float)this.right / var17), (double)((float)(this.bottom + (int)this.scrollDistance) / var17));
            worldr.addVertexWithUV((double)this.right, (double)this.top, 0.0D, (double)((float)this.right / var17), (double)((float)(this.top + (int)this.scrollDistance) / var17));
            worldr.addVertexWithUV((double)this.left, (double)this.top, 0.0D, (double)((float)this.left / var17), (double)((float)(this.top + (int)this.scrollDistance) / var17));
            tess.draw();
        }
 //        boxRight = this.listWidth / 2 - 92 - 16;
        var10 = this.top + 4 - (int)this.scrollDistance;

        if (this.field_27262_q)
        {
            this.func_27260_a(boxRight, var10, tess);
        }

        int var14;

        for (var11 = 0; var11 < listLength; ++var11)
        {
            var19 = var10 + var11 * this.slotHeight + this.field_27261_r;
            var13 = this.slotHeight - 4;

            if (var19 <= this.bottom && var19 + var13 >= this.top)
            {
                if (this.field_25123_p && this.isSelected(var11))
                {
                    var14 = boxLeft;
                    int var15 = boxRight;
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableTexture2D();
                    worldr.startDrawingQuads();
                    worldr.setColorOpaque_I(8421504);
                    worldr.addVertexWithUV((double)var14, (double)(var19 + var13 + 2), 0.0D, 0.0D, 1.0D);
                    worldr.addVertexWithUV((double)var15, (double)(var19 + var13 + 2), 0.0D, 1.0D, 1.0D);
                    worldr.addVertexWithUV((double)var15, (double)(var19 - 2), 0.0D, 1.0D, 0.0D);
                    worldr.addVertexWithUV((double)var14, (double)(var19 - 2), 0.0D, 0.0D, 0.0D);
                    worldr.setColorOpaque_I(0);
                    worldr.addVertexWithUV((double)(var14 + 1), (double)(var19 + var13 + 1), 0.0D, 0.0D, 1.0D);
                    worldr.addVertexWithUV((double)(var15 - 1), (double)(var19 + var13 + 1), 0.0D, 1.0D, 1.0D);
                    worldr.addVertexWithUV((double)(var15 - 1), (double)(var19 - 1), 0.0D, 1.0D, 0.0D);
                    worldr.addVertexWithUV((double)(var14 + 1), (double)(var19 - 1), 0.0D, 0.0D, 0.0D);
                    tess.draw();
                    GlStateManager.enableTexture2D();
                }

                this.drawSlot(var11, boxRight, var19, var13, tess);
            }
        }

        GlStateManager.disableDepth();
        byte border = 4;
        if (this.client.theWorld == null)
        {
            this.overlayBackground(0, this.top, 255, 255);
            this.overlayBackground(this.bottom, this.listHeight, 255, 255);
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableTexture2D();
        worldr.startDrawingQuads();
        worldr.setColorRGBA_I(0, 0);
        worldr.addVertexWithUV((double)this.left, (double)(this.top + border), 0.0D, 0.0D, 1.0D);
        worldr.addVertexWithUV((double)this.right, (double)(this.top + border), 0.0D, 1.0D, 1.0D);
        worldr.setColorRGBA_I(0, 255);
        worldr.addVertexWithUV((double)this.right, (double)this.top, 0.0D, 1.0D, 0.0D);
        worldr.addVertexWithUV((double)this.left, (double)this.top, 0.0D, 0.0D, 0.0D);
        tess.draw();
        worldr.startDrawingQuads();
        worldr.setColorRGBA_I(0, 255);
        worldr.addVertexWithUV((double)this.left, (double)this.bottom, 0.0D, 0.0D, 1.0D);
        worldr.addVertexWithUV((double)this.right, (double)this.bottom, 0.0D, 1.0D, 1.0D);
        worldr.setColorRGBA_I(0, 0);
        worldr.addVertexWithUV((double)this.right, (double)(this.bottom - border), 0.0D, 1.0D, 0.0D);
        worldr.addVertexWithUV((double)this.left, (double)(this.bottom - border), 0.0D, 0.0D, 0.0D);
        tess.draw();
        var19 = this.getContentHeight() - (this.bottom - this.top - 4);

        if (var19 > 0)
        {
            var13 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();

            if (var13 < 32)
            {
                var13 = 32;
            }

            if (var13 > this.bottom - this.top - 8)
            {
                var13 = this.bottom - this.top - 8;
            }

            var14 = (int)this.scrollDistance * (this.bottom - this.top - var13) / var19 + this.top;

            if (var14 < this.top)
            {
                var14 = this.top;
            }

            worldr.startDrawingQuads();
            worldr.setColorRGBA_I(0, 255);
            worldr.addVertexWithUV((double)scrollBarXStart, (double)this.bottom, 0.0D, 0.0D, 1.0D);
            worldr.addVertexWithUV((double)scrollBarXEnd, (double)this.bottom, 0.0D, 1.0D, 1.0D);
            worldr.addVertexWithUV((double)scrollBarXEnd, (double)this.top, 0.0D, 1.0D, 0.0D);
            worldr.addVertexWithUV((double)scrollBarXStart, (double)this.top, 0.0D, 0.0D, 0.0D);
            tess.draw();
            worldr.startDrawingQuads();
            worldr.setColorRGBA_I(8421504, 255);
            worldr.addVertexWithUV((double)scrollBarXStart, (double)(var14 + var13), 0.0D, 0.0D, 1.0D);
            worldr.addVertexWithUV((double)scrollBarXEnd, (double)(var14 + var13), 0.0D, 1.0D, 1.0D);
            worldr.addVertexWithUV((double)scrollBarXEnd, (double)var14, 0.0D, 1.0D, 0.0D);
            worldr.addVertexWithUV((double)scrollBarXStart, (double)var14, 0.0D, 0.0D, 0.0D);
            tess.draw();
            worldr.startDrawingQuads();
            worldr.setColorRGBA_I(12632256, 255);
            worldr.addVertexWithUV((double)scrollBarXStart, (double)(var14 + var13 - 1), 0.0D, 0.0D, 1.0D);
            worldr.addVertexWithUV((double)(scrollBarXEnd - 1), (double)(var14 + var13 - 1), 0.0D, 1.0D, 1.0D);
            worldr.addVertexWithUV((double)(scrollBarXEnd - 1), (double)var14, 0.0D, 1.0D, 0.0D);
            worldr.addVertexWithUV((double)scrollBarXStart, (double)var14, 0.0D, 0.0D, 0.0D);
            tess.draw();
        }

        this.func_27257_b(mouseX, mouseY);
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    private void overlayBackground(int p_22239_1_, int p_22239_2_, int p_22239_3_, int p_22239_4_)
    {
        Tessellator var5 = Tessellator.getInstance();
        WorldRenderer worldr = var5.getWorldRenderer();
        this.client.renderEngine.bindTexture(Gui.optionsBackground);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float var6 = 32.0F;
        worldr.startDrawingQuads();
        worldr.setColorRGBA_I(4210752, p_22239_4_);
        worldr.addVertexWithUV(0.0D, (double)p_22239_2_, 0.0D, 0.0D, (double)((float)p_22239_2_ / var6));
        worldr.addVertexWithUV((double)this.listWidth + 30, (double)p_22239_2_, 0.0D, (double)((float)(this.listWidth + 30) / var6), (double)((float)p_22239_2_ / var6));
        worldr.setColorRGBA_I(4210752, p_22239_3_);
        worldr.addVertexWithUV((double)this.listWidth + 30, (double)p_22239_1_, 0.0D, (double)((float)(this.listWidth + 30) / var6), (double)((float)p_22239_1_ / var6));
        worldr.addVertexWithUV(0.0D, (double)p_22239_1_, 0.0D, 0.0D, (double)((float)p_22239_1_ / var6));
        var5.draw();
    }

    protected void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float f = (float)(par5 >> 24 & 255) / 255.0F;
        float f1 = (float)(par5 >> 16 & 255) / 255.0F;
        float f2 = (float)(par5 >> 8 & 255) / 255.0F;
        float f3 = (float)(par5 & 255) / 255.0F;
        float f4 = (float)(par6 >> 24 & 255) / 255.0F;
        float f5 = (float)(par6 >> 16 & 255) / 255.0F;
        float f6 = (float)(par6 >> 8 & 255) / 255.0F;
        float f7 = (float)(par6 & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();
        worldrenderer.setColorRGBA_F(f1, f2, f3, f);
        worldrenderer.addVertex((double)par3, (double)par2, 0.0D);
        worldrenderer.addVertex((double)par1, (double)par2, 0.0D);
        worldrenderer.setColorRGBA_F(f5, f6, f7, f4);
        worldrenderer.addVertex((double)par1, (double)par4, 0.0D);
        worldrenderer.addVertex((double)par3, (double)par4, 0.0D);
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
