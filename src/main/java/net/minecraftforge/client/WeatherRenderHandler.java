/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

/**
 * Call {@link net.minecraft.world.dimension.Dimension#setWeatherRenderer} with an implementation of this
 * to override all weather rendering with your own. This includes rain and snow.
 */
public interface WeatherRenderHandler extends IRenderHandler {
}
