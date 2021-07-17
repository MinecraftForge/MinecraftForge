/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.gui;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.Widget;
import net.minecraftforge.fmlclient.gui.GuiUtils;

public abstract class ScrollPanel extends AbstractContainerEventHandler implements Widget
{
    private final Minecraft client;
    protected final int width;
    protected final int height;
    protected final int top;
    protected final int bottom;
    protected final int right;
    protected final int left;
    private boolean scrolling;
    protected float scrollDistance;
    protected boolean captureMouse = true;
    protected final int border = 4;

    private final int barWidth = 6;
    private final int barLeft;

    public ScrollPanel(Minecraft client, int width, int height, int top, int left)
    {
        this.client = client;
        this.width = width;
        this.height = height;
        this.top = top;
        this.left = left;
        this.bottom = height + this.top;
        this.right = width + this.left;
        this.barLeft = this.left + this.width - barWidth;
    }

    protected abstract int getContentHeight();

    protected void drawBackground() {}

    /**
     * Draw anything special on the screen. GL_SCISSOR is enabled for anything that
     * is rendered outside of the view box. Do not mess with SCISSOR unless you support this.
     * @param mouseY
     * @param mouseX
     */
    protected abstract void drawPanel(PoseStack mStack, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY);

    protected boolean clickPanel(double mouseX, double mouseY, int button) { return false; }

    private int getMaxScroll()
    {
        return this.getContentHeight() - (this.height - this.border);
    }

    private void applyScrollLimits()
    {
        int max = getMaxScroll();

        if (max < 0)
        {
            max /= 2;
        }

        if (this.scrollDistance < 0.0F)
        {
            this.scrollDistance = 0.0F;
        }

        if (this.scrollDistance > max)
        {
            this.scrollDistance = max;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
    {
        if (scroll != 0)
        {
            this.scrollDistance += -scroll * getScrollAmount();
            applyScrollLimits();
            return true;
        }
        return false;
    }

    protected int getScrollAmount()
    {
        return 20;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return mouseX >= this.left && mouseX <= this.left + this.width &&
                mouseY >= this.top && mouseY <= this.bottom;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;

        this.scrolling = button == 0 && mouseX >= barLeft && mouseX < barLeft + barWidth;
        if (this.scrolling)
        {
            return true;
        }
        int mouseListY = ((int)mouseY) - this.top - this.getContentHeight() + (int)this.scrollDistance - border;
        if (mouseX >= left && mouseX <= right && mouseListY < 0)
        {
            return this.clickPanel(mouseX - left, mouseY - this.top + (int)this.scrollDistance - border, button);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        if (super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_))
            return true;
        boolean ret = this.scrolling;
        this.scrolling = false;
        return ret;
    }

    private int getBarHeight()
    {
        int barHeight = (height * height) / this.getContentHeight();

        if (barHeight < 32) barHeight = 32;

        if (barHeight > height - border*2)
            barHeight = height - border*2;

        return barHeight;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (this.scrolling)
        {
            int maxScroll = height - getBarHeight();
            double moved = deltaY / maxScroll;
            this.scrollDistance += getMaxScroll() * moved;
            applyScrollLimits();
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks)
    {
        this.drawBackground();

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder worldr = tess.getBuilder();

        double scale = client.getWindow().getGuiScale();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)(left  * scale), (int)(client.getWindow().getHeight() - (bottom * scale)),
                       (int)(width * scale), (int)(height * scale));

        if (this.client.level != null)
        {
            this.drawGradientRect(matrix, this.left, this.top, this.right, this.bottom, 0xC0101010, 0xD0101010);
        }
        else // Draw dark dirt background
        {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
            final float texScale = 32.0F;
            worldr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            worldr.vertex(this.left,  this.bottom, 0.0D).uv(this.left  / texScale, (this.bottom + (int)this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            worldr.vertex(this.right, this.bottom, 0.0D).uv(this.right / texScale, (this.bottom + (int)this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            worldr.vertex(this.right, this.top,    0.0D).uv(this.right / texScale, (this.top    + (int)this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            worldr.vertex(this.left,  this.top,    0.0D).uv(this.left  / texScale, (this.top    + (int)this.scrollDistance) / texScale).color(0x20, 0x20, 0x20, 0xFF).endVertex();
            tess.end();
        }

        int baseY = this.top + border - (int)this.scrollDistance;
        this.drawPanel(matrix, right, baseY, tess, mouseX, mouseY);

        RenderSystem.disableDepthTest();

        int extraHeight = (this.getContentHeight() + border) - height;
        if (extraHeight > 0)
        {
            int barHeight = getBarHeight();

            int barTop = (int)this.scrollDistance * (height - barHeight) / extraHeight + this.top;
            if (barTop < this.top)
            {
                barTop = this.top;
            }

            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.disableTexture();
            worldr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            worldr.vertex(barLeft,            this.bottom, 0.0D).uv(0.0F, 1.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.vertex(barLeft + barWidth, this.bottom, 0.0D).uv(1.0F, 1.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.vertex(barLeft + barWidth, this.top,    0.0D).uv(1.0F, 0.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            worldr.vertex(barLeft,            this.top,    0.0D).uv(0.0F, 0.0F).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            tess.end();
            worldr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            worldr.vertex(barLeft,            barTop + barHeight, 0.0D).uv(0.0F, 1.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.vertex(barLeft + barWidth, barTop + barHeight, 0.0D).uv(1.0F, 1.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.vertex(barLeft + barWidth, barTop,             0.0D).uv(1.0F, 0.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            worldr.vertex(barLeft,            barTop,             0.0D).uv(0.0F, 0.0F).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            tess.end();
            worldr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            worldr.vertex(barLeft,                barTop + barHeight - 1, 0.0D).uv(0.0F, 1.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.vertex(barLeft + barWidth - 1, barTop + barHeight - 1, 0.0D).uv(1.0F, 1.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.vertex(barLeft + barWidth - 1, barTop,                 0.0D).uv(1.0F, 0.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            worldr.vertex(barLeft,                barTop,                 0.0D).uv(0.0F, 0.0F).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            tess.end();
        }

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    protected void drawGradientRect(PoseStack mStack, int left, int top, int right, int bottom, int color1, int color2)
    {
        GuiUtils.drawGradientRect(mStack.last().pose(), 0, left, top, right, bottom, color1, color2);
    }

    @Override
    public List<? extends GuiEventListener> children()
    {
        return Collections.emptyList();
    }
}
