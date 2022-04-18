/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

/**
 * Call {@link net.minecraft.client.renderer.DimensionSpecialEffects#setSkyRenderHandler(ISkyRenderHandler)}, obtained
 * from a {@link ClientLevel} with an implementation of this to override all sky rendering with your own.
 * This includes the sun, moon, stars, and sky-coloring.
 */
@FunctionalInterface
public interface ISkyRenderHandler {
    void render(int ticks, float partialTick, PoseStack poseStack, ClientLevel level, Minecraft minecraft);
}
