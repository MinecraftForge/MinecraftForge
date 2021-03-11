package net.minecraftforge.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IIngameOverlay
{
    void render(ForgeIngameGui gui, MatrixStack mStack, float partialTicks, int width, int height);
}
