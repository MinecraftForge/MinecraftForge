/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

/**
 * Call {@link net.minecraft.client.renderer.DimensionSpecialEffects#setCloudRenderHandler(ICloudRenderHandler)},
 * obtained from a {@link ClientLevel} with an implementation of this to override all cloud rendering with your own.
 * This is only responsible for rendering clouds.
 */
@FunctionalInterface
public interface ICloudRenderHandler {
    void render(int ticks, float partialTick, PoseStack poseStack, ClientLevel level, Minecraft minecraft, double camX, double camY, double camZ);
}
