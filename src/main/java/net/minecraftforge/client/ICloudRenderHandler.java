/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;

/**
 * Call {@link net.minecraft.client.world.DimensionRenderInfo#setCloudRenderHandler(ICloudRenderHandler)}, obtained from a {@link ClientWorld} with an implementation of this to override all cloud rendering with your own.
 * This is only responsible for rendering clouds.
 */
@FunctionalInterface
public interface ICloudRenderHandler {
    void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc, double viewEntityX, double viewEntityY, double viewEntityZ);
}
