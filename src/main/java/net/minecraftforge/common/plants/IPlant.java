/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.common.plants;

import net.minecraft.block.BlockAttachedStem;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IPlant
{

    /**
     * TODO: Update with other plants.
     * 
     * @param world The world
     * @param pos The current pos
     * @param state The current state
     * 
     * @return The type of plant that this plant is.
     */
    default PlantType getPlantType(IBlockReader world, BlockPos pos, IBlockState state)
    {
        if (this instanceof BlockCrops || this instanceof BlockStem || this instanceof BlockAttachedStem) return DefaultPlantTypes.CROP;
        if (this instanceof BlockSapling) return DefaultPlantTypes.SAPLING;
        if (this == Blocks.DEAD_BUSH || this == Blocks.CACTUS) return DefaultPlantTypes.DESERT;
        if (this == Blocks.RED_MUSHROOM || this == Blocks.BROWN_MUSHROOM) return DefaultPlantTypes.CAVE;
        if (this == Blocks.SUGAR_CANE) return DefaultPlantTypes.BEACH;
        if (this == Blocks.NETHER_WART) return DefaultPlantTypes.NETHER;
        if (this == Blocks.COCOA || this == Blocks.VINE) return DefaultPlantTypes.EPIPHYTE;
        if (this == Blocks.LILY_PAD || this instanceof ILiquidContainer) return DefaultPlantTypes.WATER;
        if (this == Blocks.CHORUS_PLANT || this == Blocks.CHORUS_FLOWER) return DefaultPlantTypes.ENDER;
        return DefaultPlantTypes.PLAINS;
    }

}
