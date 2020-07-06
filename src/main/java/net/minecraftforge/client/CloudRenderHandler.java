/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;

/**
 * Call {@link net.minecraft.world.dimension.Dimension#setCloudRenderer} with an implementation of this
 * to override all cloud rendering with your own.
 */
public interface CloudRenderHandler extends IRenderHandler {
	@Override
	default void render(int ticks, float partialTicks, ClientWorld world, Minecraft mc) {}

	void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc);
}
