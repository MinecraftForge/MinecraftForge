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
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;

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
    protected final int border;
    protected final int scrollBarWidth;
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
    public GuiScrollingList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight, int screenWidth, int screenHeight, int border, int scrollBarWidth)
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
        this.border = border;
        this.scrollBarWidth = scrollBarWidth;
    }
    public GuiScrollingList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight, int screenWidth, int screenHeight)
    {
    	this(client, width, height, top, bottom, left, entryHeight, screenWidth, screenHeight, 4, 6);
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

    protected int getBoxHeight()
    {
        return this.bottom - this.top;
    }

    protected int getViewWidth()
    {
        return listWidth - scrollBarWidth - scrollBarWidth;
    }

    protected int getViewHeight()
    {
        return this.getBoxHeight() - this.border;
    }

    protected int getContentHeight()
    {
        return this.getSize() * this.slotHeight + this.headerHeight;
    }

    protected int getOverflowHeight()
    {
        return this.getContentHeight() - this.getViewHeight();
    }

    protected int getScrollerHeight()
    {
        int boxHeight = this.getBoxHeight();
        int height = this.getViewHeight() * boxHeight / this.getContentHeight();
        return MathHelper.clamp_int(height,
                32, boxHeight);
    }

    protected abstract void drawBackground();

    /**
     * Draw anything special on the screen. GL_SCISSOR is enabled for anything that
     * is rendered outside of the view box. Do not mess with SCISSOR unless you support this.
     */
    protected abstract void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess);

    @Deprecated protected void func_27260_a(int entryRight, int relativeY, Tessellator tess) {}
    /**
     * Draw anything special on the screen. GL_SCISSOR is enabled for anything that
     * is rendered outside of the view box. Do not mess with SCISSOR unless you support this.
     */
    protected void drawHeader(int entryRight, int relativeY, Tessellator tess) { func_27260_a(entryRight, relativeY, tess); }

    @Deprecated protected void func_27255_a(int x, int y) {}
    protected void clickHeader(int x, int y) { func_27255_a(x, y); }

    @Deprecated protected void func_27257_b(int mouseX, int mouseY) {}
    /**
     * Draw anything special on the screen. GL_SCISSOR is enabled for anything that
     * is rendered outside of the view box. Do not mess with SCISSOR unless you support this.
     */
    protected void drawScreen(int mouseX, int mouseY) { func_27257_b(mouseX, mouseY); }

    public int func_27256_c(int x, int y)
    {
        int left = this.left + 1;
        int right = this.left + this.listWidth - 7;
        int relativeY = y - this.top - this.headerHeight + (int)this.scrollDistance - this.border;
        int entryIndex = relativeY / this.slotHeight;
        return x >= left && x <= right && entryIndex >= 0 && relativeY >= 0 && entryIndex < this.getSize() ? entryIndex : -1;
    }

    private void applyScrollLimits()
    {
        int overflow = this.getOverflowHeight();

        if (overflow < 0)
            this.scrollDistance = overflow / 2;
        else
            this.scrollDistance = MathHelper.clamp_float(scrollDistance, 0, overflow);
    }

    public boolean handleMouseInput()
    {
        if (this.mouseX < this.left || this.mouseX > this.left + this.listWidth ||
                this.mouseY < this.top || this.mouseY > this.bottom)
            return false;

        float scroll = Mouse.getEventDWheel();
        if (scroll != 0)
        {
            scroll = -scroll / 120 * Math.min(25, this.getViewHeight() / 4);

            this.scrollDistance += scroll;
        }

        return true;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.drawBackground();

        boolean isHovering = mouseX >= this.left && mouseX <= this.left + this.listWidth &&
                             mouseY >= this.top && mouseY <= this.bottom;
        int listLength     = this.getSize();
        int scrollBarRight = this.left + this.listWidth;
        int scrollBarLeft  = scrollBarRight - this.scrollBarWidth;
        int entryLeft      = this.left;
        int entryRight     = scrollBarLeft - 1;
        int boxHeight      = this.getBoxHeight();

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
                        int scrollHeight = getOverflowHeight();
                        if (scrollHeight < 1) scrollHeight = 1;

                        this.scrollFactor /= (float)(boxHeight - getScrollerHeight()) / scrollHeight;
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
            this.initialMouseClickY = -1.0F;
        }

        this.applyScrollLimits();

        Tessellator tess = Tessellator.getInstance();
        VertexBuffer worldr = tess.getBuffer();

        ScaledResolution res = new ScaledResolution(client);
        double scaleW = client.displayWidth / res.getScaledWidth_double();
        double scaleH = client.displayHeight / res.getScaledHeight_double();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)(left      * scaleW), (int)(client.displayHeight - (bottom * scaleH)),
                       (int)(listWidth * scaleW), (int)(boxHeight * scaleH));

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
            worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos(this.left,  this.bottom, 0.0D).tex(this.left  / scale, (this.bottom + (int)this.scrollDistance) / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            worldr.pos(this.right, this.bottom, 0.0D).tex(this.right / scale, (this.bottom + (int)this.scrollDistance) / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            worldr.pos(this.right, this.top,    0.0D).tex(this.right / scale, (this.top    + (int)this.scrollDistance) / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            worldr.pos(this.left,  this.top,    0.0D).tex(this.left  / scale, (this.top    + (int)this.scrollDistance) / scale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
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
                    worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                    worldr.pos(min,     slotTop + slotBuffer + 2, 0).tex(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
                    worldr.pos(max,     slotTop + slotBuffer + 2, 0).tex(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
                    worldr.pos(max,     slotTop              - 2, 0).tex(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
                    worldr.pos(min,     slotTop              - 2, 0).tex(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
                    worldr.pos(min + 1, slotTop + slotBuffer + 1, 0).tex(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
                    worldr.pos(max - 1, slotTop + slotBuffer + 1, 0).tex(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
                    worldr.pos(max - 1, slotTop              - 1, 0).tex(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
                    worldr.pos(min + 1, slotTop              - 1, 0).tex(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
                    tess.draw();
                    GlStateManager.enableTexture2D();
                }

                this.drawSlot(slotIdx, entryRight, slotTop, slotBuffer, tess);
            }
        }

        GlStateManager.disableDepth();

        int extraHeight = getOverflowHeight();
        if (extraHeight > 0)
        {
            int height = getScrollerHeight();

            int barTop = (int) (this.scrollDistance * (boxHeight - height) / extraHeight) + this.top;
            if (barTop < this.top)
                barTop = this.top;

            GlStateManager.disableTexture2D();
            worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos(scrollBarLeft,  this.bottom, 0.0D).tex(0.0D, 1.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.pos(scrollBarRight, this.bottom, 0.0D).tex(1.0D, 1.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.pos(scrollBarRight, this.top,    0.0D).tex(1.0D, 0.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.pos(scrollBarLeft,  this.top,    0.0D).tex(0.0D, 0.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            tess.draw();
            worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos(scrollBarLeft,  barTop + height, 0.0D).tex(0.0D, 1.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.pos(scrollBarRight, barTop + height, 0.0D).tex(1.0D, 1.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.pos(scrollBarRight, barTop,          0.0D).tex(1.0D, 0.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.pos(scrollBarLeft,  barTop,          0.0D).tex(0.0D, 0.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            tess.draw();
            worldr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldr.pos(scrollBarLeft,      barTop + height - 1, 0.0D).tex(0.0D, 1.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.pos(scrollBarRight - 1, barTop + height - 1, 0.0D).tex(1.0D, 1.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.pos(scrollBarRight - 1, barTop,              0.0D).tex(1.0D, 0.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.pos(scrollBarLeft,      barTop,              0.0D).tex(0.0D, 0.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            tess.draw();
        }

        this.drawScreen(mouseX, mouseY);
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
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
        VertexBuffer VertexBuffer = tessellator.getBuffer();
        VertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        VertexBuffer.pos(right, top, 0.0D).color(r1, g1, b1, a1).endVertex();
        VertexBuffer.pos(left,  top, 0.0D).color(r1, g1, b1, a1).endVertex();
        VertexBuffer.pos(left,  bottom, 0.0D).color(r2, g2, b2, a2).endVertex();
        VertexBuffer.pos(right, bottom, 0.0D).color(r2, g2, b2, a2).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
