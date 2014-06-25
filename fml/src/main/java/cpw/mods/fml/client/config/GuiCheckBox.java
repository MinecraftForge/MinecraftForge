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

package cpw.mods.fml.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/**
 * This class provides a checkbox style control.
 * 
 * @author bspkrs
 */
public class GuiCheckBox extends GuiButton
{
    private boolean isChecked;
    private int     boxWidth;
    
    public GuiCheckBox(int id, int xPos, int yPos, String displayString, boolean isChecked)
    {
        super(id, xPos, yPos, displayString);
        this.isChecked = isChecked;
        this.boxWidth = 11;
        this.height = 11;
        this.width = this.boxWidth + 2 + Minecraft.getMinecraft().fontRenderer.getStringWidth(displayString);
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.boxWidth && mouseY < this.yPosition + this.height;
            GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46, this.boxWidth, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
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
            
            if (this.isChecked)
                this.drawCenteredString(mc.fontRenderer, "x", this.xPosition + this.boxWidth / 2 + 1, this.yPosition + 1, 14737632);
            
            this.drawString(mc.fontRenderer, displayString, xPosition + this.boxWidth + 2, yPosition + 2, color);
        }
    }
    
    @Override
    public boolean mousePressed(Minecraft p_146116_1_, int p_146116_2_, int p_146116_3_)
    {
        if (this.enabled && this.visible && p_146116_2_ >= this.xPosition && p_146116_3_ >= this.yPosition && p_146116_2_ < this.xPosition + this.width && p_146116_3_ < this.yPosition + this.height)
        {
            this.isChecked = !this.isChecked;
            return true;
        }
        
        return false;
    }
    
    public boolean isChecked()
    {
        return this.isChecked;
    }
    
    public void setIsChecked(boolean isChecked)
    {
        this.isChecked = isChecked;
    }
}