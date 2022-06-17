/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.multiplayer.ClientLevel;

/**
 * Call {@link net.minecraft.client.renderer.DimensionSpecialEffects#setWeatherRenderHandler(IWeatherRenderHandler)},
 * obtained from a {@link ClientLevel} with an implementation of this to override all weather rendering with your own.
 * This includes rain and snow.
 */
@FunctionalInterface
public interface IWeatherRenderHandler {
    void render(int ticks, float partialTick, ClientLevel level, Minecraft minecraft, LightTexture lightTexture, double camX, double camY, double camZ);
}
