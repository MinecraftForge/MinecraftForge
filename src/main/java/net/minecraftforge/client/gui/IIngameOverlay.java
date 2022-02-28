/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IIngameOverlay
{
    void render(ForgeIngameGui gui, PoseStack mStack, float partialTicks, int width, int height);
}
