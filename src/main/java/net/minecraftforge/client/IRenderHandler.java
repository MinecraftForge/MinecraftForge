/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
* Use one of {@link IWeatherRenderHandler}, {@link ICloudRenderHandler} or {@link ISkyRenderHandler} instead.
*
* todo: remove in 1.17
*/
@Deprecated
@FunctionalInterface
public interface IRenderHandler
{
    @OnlyIn(Dist.CLIENT)
    void render(int ticks, float partialTicks, ClientWorld world, Minecraft mc);
}