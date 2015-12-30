/*
 * Forge Mod Loader
 * Copyright (c) 2012-2014 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors (this class):
 *     bspkrs - implementation
 */

package net.minecraftforge.fml.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

/**
 * This class provides a button that shows a string glyph at the beginning. The glyph can be scaled using the glyphScale parameter.
 *
 * @author bspkrs
 */
public class GuiUnicodeGlyphButton extends GuiButtonExt
{
    public String glyph;
    public float  glyphScale;

    public GuiUnicodeGlyphButton(int id, int xPos, int yPos, int width, int height, String displayString, String glyph, float glyphScale)
    {
        super(id, xPos, yPos, width, height, displayString);
        this.glyph = glyph;
        this.glyphScale = glyphScale;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.hovered);
            GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            this.mouseDragged(mc, mouseX, mouseY);
            int color = 14737632;

            if (packedFGColour != 0)
            {
                color = packedFGColour;
            }
            else if (!this.enabled)
            {
                color = 10526880;
            }
            else if (this.hovered)
            {
                color = 16777120;
            }

            String buttonText = this.displayString;
            int glyphWidth = (int) (mc.fontRendererObj.getStringWidth(glyph) * glyphScale);
            int strWidth = mc.fontRendererObj.getStringWidth(buttonText);
            int elipsisWidth = mc.fontRendererObj.getStringWidth("...");
            int totalWidth = strWidth + glyphWidth;

            if (totalWidth > width - 6 && totalWidth > elipsisWidth)
                buttonText = mc.fontRendererObj.trimStringToWidth(buttonText, width - 6 - elipsisWidth).trim() + "...";

            strWidth = mc.fontRendererObj.getStringWidth(buttonText);
            totalWidth = glyphWidth + strWidth;

            GlStateManager.pushMatrix();
            GlStateManager.scale(glyphScale, glyphScale, 1.0F);
            this.drawCenteredString(mc.fontRendererObj, glyph,
                    (int) (((this.xPosition + (this.width / 2) - (strWidth / 2)) / glyphScale) - (glyphWidth / (2 * glyphScale)) + 2),
                    (int) (((this.yPosition + ((this.height - 8) / glyphScale) / 2) - 1) / glyphScale), color);
            GlStateManager.popMatrix();

            this.drawCenteredString(mc.fontRendererObj, buttonText, (int) (this.xPosition + (this.width / 2) + (glyphWidth / glyphScale)),
                    this.yPosition + (this.height - 8) / 2, color);
        }
    }
}
