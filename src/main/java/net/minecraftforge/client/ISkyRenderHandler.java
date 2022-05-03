/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;

/**
 * Call {@link net.minecraft.client.world.DimensionRenderInfo#setSkyRenderHandler(ISkyRenderHandler)}, obtained from a {@link ClientWorld} with an implementation of this to override all sky rendering with your own.
 * This includes the sun, moon, stars, and sky-coloring.
 */
@FunctionalInterface
public interface ISkyRenderHandler {
    void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc);
}
