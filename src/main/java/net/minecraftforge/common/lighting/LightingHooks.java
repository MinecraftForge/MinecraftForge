/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.common.lighting;

import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class LightingHooks
{
    public static void initChunkLighting(final Chunk chunk, final World world)
    {
        final int xBase = chunk.x << 4;
        final int zBase = chunk.z << 4;

        final PooledMutableBlockPos pos = PooledMutableBlockPos.retain(xBase, 0, zBase);

        if (world.isAreaLoaded(pos.add(-16, 0, -16), pos.add(31, 255, 31), false))
        {
            final ExtendedBlockStorage[] extendedBlockStorage = chunk.getBlockStorageArray();

            for (int j = 0; j < extendedBlockStorage.length; ++j)
            {
                if (extendedBlockStorage[j] == Chunk.NULL_BLOCK_STORAGE)
                    continue;

                for (int x = 0; x < 16; ++x)
                {
                    for (int z = 0; z < 16; ++z)
                    {
                        for (int y = j << 4; y < (j + 1) << 4; ++y)
                        {
                            if (chunk.getBlockState(x, y, z).getLightValue(world, pos.setPos(xBase + x, y, zBase + z)) > 0)
                                world.checkLightFor(EnumSkyBlock.BLOCK, pos);
                        }
                    }
                }
            }

            if (world.provider.hasSkyLight())
                chunk.setSkylightUpdated();

            chunk.setLightInitialized(true);
        }

        pos.release();
    }

    public static void checkChunkLighting(final Chunk chunk, final World world)
    {
        if (!chunk.isLightInitialized())
            initChunkLighting(chunk, world);

        for (int x = -1; x <= 1; ++x)
        {
            for (int z = -1; z <= 1; ++z)
            {
                if (x != 0 || z != 0)
                {
                    Chunk nChunk = world.getChunkProvider().getLoadedChunk(chunk.x + x, chunk.z + z);

                    if (nChunk == null || !nChunk.isLightInitialized())
                        return;
                }
            }
        }

        chunk.setLightPopulated(true);
    }
}
