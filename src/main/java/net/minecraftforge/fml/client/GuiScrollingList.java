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
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiScrollingList
{
    private final Minecraft client;
    protected final int listWidth;
    protected final int listHeight;
    protected final int screenWidth;
    protected final int screenHeight;
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
    private boolean highlightSelected = true;
    private boolean hasHeader;
    private int headerHeight;
    protected boolean captureMouse = true;

    @Deprecated // We need to know screen size.
    public GuiScrollingList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight)
    {
       this(client, width, height, top, bottom, left, entryHeight, width, height);
    }
    public GuiScrollingList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight, int screenWidth, int screenHeight)
    {
        this.client = client;
        this.listWidth = width;
        this.listHeight = height;
        this.top = top;
        this.bottom = bottom;
        this.slotHeight = entryHeight;
        this.left = left;
        this.right = width + this.left;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void func_27258_a(boolean p_27258_1_)
    {
        this.highlightSelected = p_27258_1_;
    }

    @Deprecated protected void func_27259_a(boolean hasFooter, int footerHeight){ setHeaderInfo(hasFooter, footerHeight); }
    protected void setHeaderInfo(boolean hasHeader, int headerHeight)
    {
        this.hasHeader = hasHeader;
        this.headerHeight = headerHeight;
        if (!hasHeader) this.headerHeight = 0;
    }

    protected abstract int getSize();

    protected abstract void elementClicked(int index, boolean doubleClick);

    protected abstract boolean isSelected(int index);

    protected int getContentHeight()
    {
        return this.getSize() * this.slotHeight + this.headerHeight;
    }

    protected abstract void drawBackground();

    protected abstract void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess);

    @Deprecated protected void func_27260_a(int entryRight, int relativeY, Tessellator tess) {}
    protected void drawHeader(int entryRight, int relativeY, Tessellator tess) { func_27260_a(entryRight, relativeY, tess); }

    @Deprecated protected void func_27255_a(int x, int y) {}
    protected void clickHeader(int x, int y) { func_27255_a(x, y); }

    @Deprecated protected void func_27257_b(int mouseX, int mouseY) {}
    protected void drawScreen(int mouseX, int mouseY) { func_27257_b(mouseX, mouseY); }

    public int func_27256_c(int x, int y)
    {
        int left = this.left + 1;
        int right = this.left + this.listWidth - 7;
        int relativeY = y - this.top - this.headerHeight + (int)this.scrollDistance - 4;
        int entryIndex = relativeY / this.slotHeight;
        return x >= left && x <= right && entryIndex >= 0 && relativeY >= 0 && entryIndex < this.getSize() ? entryIndex : -1;
    }

    public void registerScrollButtons(@SuppressWarnings("rawtypes") List buttons, int upActionID, int downActionID)
    {
        this.scrollUpActionId = upActionID;
        this.scrollDownActionId = downActionID;
    }

    private void applyScrollLimits()
    {
        int listHeight = this.getContentHeight() - (this.bottom - this.top - 4);

        if (listHeight < 0)
        {
            listHeight /= 2;
        }

        if (this.scrollDistance < 0.0F)
        {
            this.scrollDistance = 0.0F;
        }

        if (this.scrollDistance > (float)listHeight)
        {
            this.scrollDistance = (float)listHeight;
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

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.drawBackground();

        boolean isHovering = mouseX >= this.left && mouseX <= this.left + this.listWidth &&
                             mouseY >= this.top && mouseY <= this.bottom;
        int listLength     = this.getSize();
        int scrollBarWidth = 6;
        int scrollBarRight = this.left + this.listWidth;
        int scrollBarLeft  = scrollBarRight - scrollBarWidth;
        int entryLeft      = this.left;
        int entryRight     = scrollBarLeft - 1;
        int viewHeight     = this.bottom - this.top;
        int border         = 4;

        if (Mouse.isButtonDown(0))
        {
            if (this.initialMouseClickY == -1.0F)
            {
                if (isHovering)
                {
                    int mouseListY = mouseY - this.top - this.headerHeight + (int)this.scrollDistance - border;
                    int slotIndex = mouseListY / this.slotHeight;

                    if (mouseX >= entryLeft && mouseX <= entryRight && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength)
                    {
                        this.elementClicked(slotIndex, slotIndex == this.selectedIndex && System.currentTimeMillis() - this.lastClickTime < 250L);
                        this.selectedIndex = slotIndex;
                        this.lastClickTime = System.currentTimeMillis();
                    }
                    else if (mouseX >= entryLeft && mouseX <= entryRight && mouseListY < 0)
                    {
                        this.clickHeader(mouseX - entryLeft, mouseY - this.top + (int)this.scrollDistance - border);
                    }

                    if (mouseX >= scrollBarLeft && mouseX <= scrollBarRight)
                    {
                        this.scrollFactor = -1.0F;
                        int scrollHeight = this.getContentHeight() - viewHeight - border;
                        if (scrollHeight < 1) scrollHeight = 1;

                        int var13 = (int)((float)(viewHeight * viewHeight) / (float)this.getContentHeight());

                        if (var13 < 32) var13 = 32;
                        if (var13 > viewHeight - border*2)
                            var13 = viewHeight - border*2;

                        this.scrollFactor /= (float)(viewHeight - var13) / (float)scrollHeight;
                    }
                    else
                    {
                        this.scrollFactor = 1.0F;
                    }

                    this.initialMouseClickY = mouseY;
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
            while (isHovering && Mouse.next())
            {
                int scroll = Mouse.getEventDWheel();
                if (scroll != 0)
                {
                    if      (scroll > 0) scroll = -1;
                    else if (scroll < 0) scroll =  1;

                    this.scrollDistance += (float)(scroll * this.slotHeight / 2);
                }
            }

            this.initialMouseClickY = -1.0F;
        }

        this.applyScrollLimits();

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer worldr = tess.getWorldRenderer();

        if (this.client.theWorld != null)
        {
            this.drawGradientRect(this.left, this.top, this.right, this.bottom, 0xC0101010, 0xD0101010);
        }
        else // Draw dark dirt background
        {
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            this.client.renderEngine.bindTexture(Gui.optionsBackground);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float scale = 32.0F;
            worldr.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            worldr.func_181662_b(this.left,  this.bottom, 0.0D).func_181673_a(this.left  / scale, (this.bottom + (int)this.scrollDistance) / scale).func_181669_b(0x20, 0x20, 0x20, 0xFF).func_181675_d();
            worldr.func_181662_b(this.right, this.bottom, 0.0D).func_181673_a(this.right / scale, (this.bottom + (int)this.scrollDistance) / scale).func_181669_b(0x20, 0x20, 0x20, 0xFF).func_181675_d();
            worldr.func_181662_b(this.right, this.top,    0.0D).func_181673_a(this.right / scale, (this.top    + (int)this.scrollDistance) / scale).func_181669_b(0x20, 0x20, 0x20, 0xFF).func_181675_d();
            worldr.func_181662_b(this.left,  this.top,    0.0D).func_181673_a(this.left  / scale, (this.top    + (int)this.scrollDistance) / scale).func_181669_b(0x20, 0x20, 0x20, 0xFF).func_181675_d();
            tess.draw();
        }

        int baseY = this.top + border - (int)this.scrollDistance;

        if (this.hasHeader) {
            this.drawHeader(entryRight, baseY, tess);
        }

        for (int slotIdx = 0; slotIdx < listLength; ++slotIdx)
        {
            int slotTop = baseY + slotIdx * this.slotHeight + this.headerHeight;
            int slotBuffer = this.slotHeight - border;

            if (slotTop <= this.bottom && slotTop + slotBuffer >= this.top)
            {
                if (this.highlightSelected && this.isSelected(slotIdx))
                {
                    int min = this.left;
                    int max = entryRight;
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableTexture2D();
                    worldr.func_181668_a(7, DefaultVertexFormats.field_181709_i);
                    worldr.func_181662_b(min,     slotTop + slotBuffer + 2, 0).func_181673_a(0, 1).func_181669_b(0x80, 0x80, 0x80, 0xFF).func_181675_d();
                    worldr.func_181662_b(max,     slotTop + slotBuffer + 2, 0).func_181673_a(1, 1).func_181669_b(0x80, 0x80, 0x80, 0xFF).func_181675_d();
                    worldr.func_181662_b(max,     slotTop              - 2, 0).func_181673_a(1, 0).func_181669_b(0x80, 0x80, 0x80, 0xFF).func_181675_d();
                    worldr.func_181662_b(min,     slotTop              - 2, 0).func_181673_a(0, 0).func_181669_b(0x80, 0x80, 0x80, 0xFF).func_181675_d();
                    worldr.func_181662_b(min + 1, slotTop + slotBuffer + 1, 0).func_181673_a(0, 1).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
                    worldr.func_181662_b(max - 1, slotTop + slotBuffer + 1, 0).func_181673_a(1, 1).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
                    worldr.func_181662_b(max - 1, slotTop              - 1, 0).func_181673_a(1, 0).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
                    worldr.func_181662_b(min + 1, slotTop              - 1, 0).func_181673_a(0, 0).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
                    tess.draw();
                    GlStateManager.enableTexture2D();
                }

                this.drawSlot(slotIdx, entryRight, slotTop, slotBuffer, tess);
            }
        }

        GlStateManager.disableDepth();
        if (this.client.theWorld == null)
        {
            this.overlayBackground(0,           this.top,        255, 255);
            this.overlayBackground(this.bottom, this.listHeight, 255, 255);
        }

        // Render the entire background over everything but our view
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableTexture2D();
        worldr.func_181668_a(7, DefaultVertexFormats.field_181709_i);
        worldr.func_181662_b(this.left,  this.top + border,    0).func_181673_a(0, 1).func_181669_b(0x00, 0x00, 0x00, 0x00).func_181675_d();
        worldr.func_181662_b(this.right, this.top + border,    0).func_181673_a(1, 1).func_181669_b(0x00, 0x00, 0x00, 0x00).func_181675_d();
        worldr.func_181662_b(this.right, this.top,             0).func_181673_a(1, 0).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
        worldr.func_181662_b(this.left,  this.top,             0).func_181673_a(0, 0).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
        tess.draw();
        worldr.func_181668_a(7, DefaultVertexFormats.field_181709_i);
        worldr.func_181662_b(this.left,  this.bottom,          0).func_181673_a(0, 1).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
        worldr.func_181662_b(this.right, this.bottom,          0).func_181673_a(1, 1).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
        worldr.func_181662_b(this.right, this.bottom - border, 0).func_181673_a(1, 0).func_181669_b(0x00, 0x00, 0x00, 0x00).func_181675_d();
        worldr.func_181662_b(this.left,  this.bottom - border, 0).func_181673_a(0, 0).func_181669_b(0x00, 0x00, 0x00, 0x00).func_181675_d();
        tess.draw();

        int extraHeight = this.getContentHeight() - viewHeight - border;
        if (extraHeight > 0)
        {
            int height = viewHeight * viewHeight / this.getContentHeight();

            if (height < 32) height = 32;

            if (height > viewHeight - border*2)
                height = viewHeight - border*2;

            int barTop = (int)this.scrollDistance * (viewHeight - height) / extraHeight + this.top;
            if (barTop < this.top)
            {
                barTop = this.top;
            }

            worldr.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            worldr.func_181662_b(scrollBarLeft,  this.bottom, 0.0D).func_181673_a(0.0D, 1.0D).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
            worldr.func_181662_b(scrollBarRight, this.bottom, 0.0D).func_181673_a(1.0D, 1.0D).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
            worldr.func_181662_b(scrollBarRight, this.top,    0.0D).func_181673_a(1.0D, 0.0D).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
            worldr.func_181662_b(scrollBarLeft,  this.top,    0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(0x00, 0x00, 0x00, 0xFF).func_181675_d();
            tess.draw();
            worldr.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            worldr.func_181662_b(scrollBarLeft,  barTop + height, 0.0D).func_181673_a(0.0D, 1.0D).func_181669_b(0x80, 0x80, 0x80, 0xFF).func_181675_d();
            worldr.func_181662_b(scrollBarRight, barTop + height, 0.0D).func_181673_a(1.0D, 1.0D).func_181669_b(0x80, 0x80, 0x80, 0xFF).func_181675_d();
            worldr.func_181662_b(scrollBarRight, barTop,          0.0D).func_181673_a(1.0D, 0.0D).func_181669_b(0x80, 0x80, 0x80, 0xFF).func_181675_d();
            worldr.func_181662_b(scrollBarLeft,  barTop,          0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(0x80, 0x80, 0x80, 0xFF).func_181675_d();
            tess.draw();
            worldr.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            worldr.func_181662_b(scrollBarLeft,      barTop + height - 1, 0.0D).func_181673_a(0.0D, 1.0D).func_181669_b(0xC0, 0xC0, 0xC0, 0xFF).func_181675_d();
            worldr.func_181662_b(scrollBarRight - 1, barTop + height - 1, 0.0D).func_181673_a(1.0D, 1.0D).func_181669_b(0xC0, 0xC0, 0xC0, 0xFF).func_181675_d();
            worldr.func_181662_b(scrollBarRight - 1, barTop,              0.0D).func_181673_a(1.0D, 0.0D).func_181669_b(0xC0, 0xC0, 0xC0, 0xFF).func_181675_d();
            worldr.func_181662_b(scrollBarLeft,      barTop,              0.0D).func_181673_a(0.0D, 0.0D).func_181669_b(0xC0, 0xC0, 0xC0, 0xFF).func_181675_d();
            tess.draw();
        }

        this.drawScreen(mouseX, mouseY);
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
    }

    private void overlayBackground(int top, int height, int alpha1, int alpha2)
    {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer worldr = tess.getWorldRenderer();
        this.client.renderEngine.bindTexture(Gui.optionsBackground);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float scale = 32.0F;
        double startUV = (screenWidth / scale) / screenWidth * (left-0);
        worldr.func_181668_a(7, DefaultVertexFormats.field_181709_i);
        worldr.func_181662_b(left,             height, 0.0D).func_181673_a(startUV,                    height / scale).func_181669_b(0x40, 0x40, 0x40, alpha2).func_181675_d();
        worldr.func_181662_b(left+listWidth+8, height, 0.0D).func_181673_a((left+listWidth+8) / scale, height / scale).func_181669_b(0x40, 0x40, 0x40, alpha2).func_181675_d();
        worldr.func_181662_b(left+listWidth+8, top,    0.0D).func_181673_a((left+listWidth+8) / scale, top / scale   ).func_181669_b(0x40, 0x40, 0x40, alpha1).func_181675_d();
        worldr.func_181662_b(left,             top,    0.0D).func_181673_a(startUV,                    top / scale   ).func_181669_b(0x40, 0x40, 0x40, alpha1).func_181675_d();
        tess.draw();
    }

    protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2)
    {
        float a1 = (float)(color1 >> 24 & 255) / 255.0F;
        float r1 = (float)(color1 >> 16 & 255) / 255.0F;
        float g1 = (float)(color1 >>  8 & 255) / 255.0F;
        float b1 = (float)(color1       & 255) / 255.0F;
        float a2 = (float)(color2 >> 24 & 255) / 255.0F;
        float r2 = (float)(color2 >> 16 & 255) / 255.0F;
        float g2 = (float)(color2 >>  8 & 255) / 255.0F;
        float b2 = (float)(color2       & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        worldrenderer.func_181662_b(right, top, 0.0D).func_181666_a(r1, g1, b1, a1).func_181675_d();
        worldrenderer.func_181662_b(left,  top, 0.0D).func_181666_a(r1, g1, b1, a1).func_181675_d();
        worldrenderer.func_181662_b(left,  bottom, 0.0D).func_181666_a(r2, g2, b2, a2).func_181675_d();
        worldrenderer.func_181662_b(right, bottom, 0.0D).func_181666_a(r2, g2, b2, a2).func_181675_d();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
