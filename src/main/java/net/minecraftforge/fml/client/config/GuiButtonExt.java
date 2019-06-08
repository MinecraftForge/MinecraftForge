/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;

import net.minecraft.client.gui.widget.button.Button.IPressable;

/**
 * This class provides a button that fixes several bugs present in the vanilla GuiButton drawing code.
 * The gist of it is that it allows buttons of any size without gaps in the graphics and with the
 * borders drawn properly. It also prevents button text from extending out of the sides of the button by
 * trimming the end of the string and adding an ellipsis.<br/><br/>
 *
 * The code that handles drawing the button is in GuiUtils.
 *
 * @author bspkrs
 */
public class GuiButtonExt extends Button
{
    public GuiButtonExt(int xPos, int yPos, int width, int height, String displayString, IPressable handler)
    {
        super(xPos, yPos, width, height, displayString, handler);
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void renderButton(int mouseX, int mouseY, float partial)
    {
        super.renderButton(mouseX, mouseY, partial);
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        int strWidth = font.getStringWidth(this.getMessage());
        int ellipsisWidth = font.getStringWidth("...");
        if (strWidth > width - 6 && strWidth > ellipsisWidth)
            setMessage(font.trimStringToWidth(getMessage(), width - 6 - ellipsisWidth).trim() + "...");
    }
}
