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

package net.minecraftforge.fmlclient.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fmlclient.gui.GuiUtils;

import net.minecraft.client.gui.components.Button.OnPress;

/**
 * This class provides a button that shows a string glyph at the beginning. The glyph can be scaled using the glyphScale parameter.
 *
 * @author bspkrs
 */
public class UnicodeGlyphButton extends ExtendedButton
{
    public String glyph;
    public float  glyphScale;

    public UnicodeGlyphButton(int xPos, int yPos, int width, int height, Component displayString, String glyph, float glyphScale, OnPress handler)
    {
        super(xPos, yPos, width, height, displayString, handler);
        this.glyph = glyph;
        this.glyphScale = glyphScale;
    }

    @Override
    public void render(PoseStack mStack, int mouseX, int mouseY, float partial)
    {
        if (this.visible)
        {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getYImage(this.isHovered);
            GuiUtils.drawContinuousTexturedBox(mStack, WIDGETS_LOCATION, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
            this.renderBg(mStack, mc, mouseX, mouseY);

            Component buttonText = this.createNarrationMessage();
            int glyphWidth = (int) (mc.font.width(glyph) * glyphScale);
            int strWidth = mc.font.width(buttonText);
            int ellipsisWidth = mc.font.width("...");
            int totalWidth = strWidth + glyphWidth;

            if (totalWidth > width - 6 && totalWidth > ellipsisWidth)
                buttonText = new TextComponent(mc.font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString().trim() + "...") ;

            strWidth = mc.font.width(buttonText);
            totalWidth = glyphWidth + strWidth;

            mStack.pushPose();
            mStack.scale(glyphScale, glyphScale, 1.0F);
            this.drawCenteredString(mStack, mc.font, new TextComponent(glyph),
                    (int) (((this.x + (this.width / 2) - (strWidth / 2)) / glyphScale) - (glyphWidth / (2 * glyphScale)) + 2),
                    (int) (((this.y + ((this.height - 8) / glyphScale) / 2) - 1) / glyphScale), getFGColor());
            mStack.popPose();

            this.drawCenteredString(mStack, mc.font, buttonText, (int) (this.x + (this.width / 2) + (glyphWidth / glyphScale)),
                    this.y + (this.height - 8) / 2, getFGColor());

        }
    }
}
