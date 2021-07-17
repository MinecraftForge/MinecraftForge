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
     * @see RegionRenderCache
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
