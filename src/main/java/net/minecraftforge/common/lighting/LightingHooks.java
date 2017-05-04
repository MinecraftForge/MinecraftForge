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

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class LightingHooks
{
    public static void relightSkylightColumn(final World world, final Chunk chunk, final int x, final int z, final int height1, final int height2)
    {
        final int yMin = Math.min(height1, height2);
        final int yMax = Math.max(height1, height2) - 1;

        final ExtendedBlockStorage[] sections = chunk.getBlockStorageArray();

        final int xBase = (chunk.xPosition << 4) + x;
        final int zBase = (chunk.zPosition << 4) + z;

        for (int y = yMax; y >= yMin; --y)
        {
            final ExtendedBlockStorage section = sections[y >> 4];

            if (section == Chunk.NULL_BLOCK_STORAGE)
            {
                for (final EnumFacing dir : EnumFacing.HORIZONTALS)
                {
                    world.checkLightFor(EnumSkyBlock.SKY, new BlockPos(xBase + dir.getFrontOffsetX(), y, zBase + dir.getFrontOffsetZ()));
                }
            }

            world.checkLightFor(EnumSkyBlock.SKY, new BlockPos(xBase, y, zBase));
        }
    }
}
