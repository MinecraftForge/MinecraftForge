/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;

/**
 * Use {@link ISkyRenderHandler} instead.
 *
 * todo: remove in 1.17
 */
@Deprecated
public interface SkyRenderHandler extends IRenderHandler {
	@Override
	default void render(int ticks, float partialTicks, ClientWorld world, Minecraft mc) {}

	void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc);

}