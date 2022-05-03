/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.world.ClientWorld;

/**
 * Call {@link net.minecraft.client.world.DimensionRenderInfo#setWeatherRenderHandler(IWeatherRenderHandler)}, obtained from a {@link ClientWorld} with an implementation of this to override all weather rendering with your own.
 * This includes rain and snow.
 */
@FunctionalInterface
public interface IWeatherRenderHandler {
    void render(int ticks, float partialTicks, ClientWorld world, Minecraft mc, LightTexture lightmapIn, double xIn, double yIn, double zIn);
}
