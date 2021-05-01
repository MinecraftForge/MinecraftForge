/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
