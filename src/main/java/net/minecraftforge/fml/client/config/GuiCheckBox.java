/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

/**
 * This class provides a checkbox style control.
 */
public class GuiCheckBox extends Button
{
    private boolean isChecked;
    private int     boxWidth;

    public GuiCheckBox(int xPos, int yPos, String displayString, boolean isChecked)
    {
        super(xPos, yPos, Minecraft.getInstance().fontRenderer.getStringWidth(displayString) + 2 + 11, 11, displayString, b -> {});
        this.isChecked = isChecked;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partial)
    {
        super.renderButton(mouseX, mouseY, partial);
        if (this.isChecked)
            this.drawCenteredString(Minecraft.getInstance().fontRenderer, "x", this.x + this.boxWidth / 2 + 1, this.y + 1, 14737632);
    }

    @Override
    public void onPress()
    {
        this.isChecked = !this.isChecked;
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
