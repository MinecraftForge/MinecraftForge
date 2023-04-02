/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.visible)
        {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            int k = !this.active ? 0 : (this.isHoveredOrFocused() ? 2 : 1);
            guiGraphics.blitWithBorder(WIDGETS_LOCATION, this.getX(), this.getY(), 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2);

            Component buttonText = this.createNarrationMessage();
            int glyphWidth = (int) (mc.font.width(glyph) * glyphScale);
            int strWidth = mc.font.width(buttonText);
            int ellipsisWidth = mc.font.width("...");
            int totalWidth = strWidth + glyphWidth;

            if (totalWidth > width - 6 && totalWidth > ellipsisWidth)
                buttonText = Component.literal(mc.font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString().trim() + "...") ;

            strWidth = mc.font.width(buttonText);
            totalWidth = glyphWidth + strWidth;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(glyphScale, glyphScale, 1.0F);
            guiGraphics.drawCenteredString(mc.font, Component.literal(glyph),
                    (int) (((this.getX() + (this.width / 2) - (strWidth / 2)) / glyphScale) - (glyphWidth / (2 * glyphScale)) + 2),
                    (int) (((this.getY() + ((this.height - 8) / glyphScale) / 2) - 1) / glyphScale), getFGColor());
            guiGraphics.pose().popPose();

            guiGraphics.drawCenteredString(mc.font, buttonText, (int) (this.getX() + (this.width / 2) + (glyphWidth / glyphScale)),
                    this.getY() + (this.height - 8) / 2, getFGColor());

        }
    }
}
