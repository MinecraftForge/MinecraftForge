/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;

/**
 * Call {@link net.minecraft.client.world.DimensionRenderInfo#setWeatherParticleRenderHandler(net.minecraftforge.client.IWeatherParticleRenderHandler)}, obtained from a {@link ClientWorld} with an implementation of this to override all weather particle rendering with your own.
 * This handles ground particles that can be seen when it's raining (splash/smoke particles).
 * This also includes playing rain sounds.
 */
@FunctionalInterface
public interface IWeatherParticleRenderHandler {
    void render(int ticks, ClientWorld world, Minecraft mc, ActiveRenderInfo activeRenderInfoIn);
}
