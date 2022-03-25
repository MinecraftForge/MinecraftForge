/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.GuiUtils;

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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (this.visible)
        {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getYImage(this.isHovered);
            GuiUtils.drawContinuousTexturedBox(poseStack, WIDGETS_LOCATION, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
            this.renderBg(poseStack, mc, mouseX, mouseY);

            Component buttonText = this.createNarrationMessage();
            int glyphWidth = (int) (mc.font.width(glyph) * glyphScale);
            int strWidth = mc.font.width(buttonText);
            int ellipsisWidth = mc.font.width("...");
            int totalWidth = strWidth + glyphWidth;

            if (totalWidth > width - 6 && totalWidth > ellipsisWidth)
                buttonText = new TextComponent(mc.font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString().trim() + "...") ;

            strWidth = mc.font.width(buttonText);
            totalWidth = glyphWidth + strWidth;

            poseStack.pushPose();
            poseStack.scale(glyphScale, glyphScale, 1.0F);
            this.drawCenteredString(poseStack, mc.font, new TextComponent(glyph),
                    (int) (((this.x + (this.width / 2) - (strWidth / 2)) / glyphScale) - (glyphWidth / (2 * glyphScale)) + 2),
                    (int) (((this.y + ((this.height - 8) / glyphScale) / 2) - 1) / glyphScale), getFGColor());
            poseStack.popPose();

            this.drawCenteredString(poseStack, mc.font, buttonText, (int) (this.x + (this.width / 2) + (glyphWidth / glyphScale)),
                    this.y + (this.height - 8) / 2, getFGColor());

        }
    }
}
