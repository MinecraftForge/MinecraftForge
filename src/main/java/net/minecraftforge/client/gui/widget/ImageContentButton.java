/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ImageContentButton extends Button
{
    protected final ResourceLocation resourceLocation;
    protected final int xTexStart;
    protected final int yTexStart;
    protected final int yDiffTex;
    protected final int textureWidth;
    protected final int textureHeight;

    public ImageContentButton(int x, int y, int width, int height, int xTexStart, int yTexStart, ResourceLocation textureLocation, Button.OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, height, textureLocation, 256, 256, onPress);
    }

    public ImageContentButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation textureLocation, Button.OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, textureLocation, 256, 256, onPress);
    }

    public ImageContentButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation textureLocation, int textureWidth, int textureHeight, Button.OnPress onPress) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, textureLocation, textureWidth, textureHeight, onPress, CommonComponents.EMPTY);
    }

    public ImageContentButton(int x, int y, int width, int height, int xTexStart, int yTextStart, int yDiffTex, ResourceLocation textureLocation, int textureWidth, int textureHeight, Button.OnPress onPress, Component message) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTextStart;
        this.yDiffTex = yDiffTex;
        this.resourceLocation = textureLocation;
    }

    @Override
    protected void renderButtonForeground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        this.renderTexture(guiGraphics, this.resourceLocation, this.getX(), this.getY(), this.xTexStart, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    public void setActive(final boolean isActive) {
        if (this.active == isActive)
            return;

        this.active = isActive;
        this.isHovered = false;
        this.setFocused(false);
    }
}
