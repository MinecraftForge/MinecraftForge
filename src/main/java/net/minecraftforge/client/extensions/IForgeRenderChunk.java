/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.extensions;

import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IForgeRenderChunk
{
    /**
     * Creates a new RegionRenderCache instance.<br>
     * Extending classes can change the behavior of the cache, allowing to visually change
     * blocks (schematics etc).
     *
     * @see RenderChunkRegion
     * @param world The world to cache.
     * @param from The starting position of the chunk minus one on each axis.
     * @param to The ending position of the chunk plus one on each axis.
     * @param subtract Padding used internally by the RegionRenderCache constructor to make
     *                 the cache a 20x20x20 cube, for a total of 8000 states in the cache.
     * @return new RegionRenderCache instance
     */
    default RenderChunkRegion createRegionRenderCache(Level world, BlockPos from, BlockPos to, int subtract)
    {
        return RenderChunkRegion.createIfNotEmpty(world, from, to, subtract);
    }
}
