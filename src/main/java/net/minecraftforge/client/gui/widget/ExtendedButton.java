/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.client.gui.ScreenUtils;

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
public class ExtendedButton extends Button
{
    public ExtendedButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler)
    {
        this(xPos, yPos, width, height, displayString, handler, DEFAULT_NARRATION);
    }

    public ExtendedButton(int xPos, int yPos, int width, int height, Component displayString, OnPress handler, CreateNarration createNarration)
    {
        super(xPos, yPos, width, height, displayString, handler, createNarration);
    }

    public ExtendedButton(Button.Builder builder)
    {
        super(builder);
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        Minecraft mc = Minecraft.getInstance();
        int k = this.getYImage(this.isHoveredOrFocused());
        ScreenUtils.blitWithBorder(poseStack, WIDGETS_LOCATION, this.getX(), this.getY(), 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
        this.renderBg(poseStack, mc, mouseX, mouseY);

        final FormattedText buttonText = mc.font.ellipsize(this.getMessage(), this.width - 6); // Remove 6 pixels so that the text is always contained within the button's borders
        drawCenteredString(poseStack, mc.font, Language.getInstance().getVisualOrder(buttonText), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, getFGColor());
    }
}
