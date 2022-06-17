/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IIngameOverlay
{
    void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height);
}
