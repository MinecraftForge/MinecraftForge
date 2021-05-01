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

package net.minecraftforge.common;

import net.minecraft.block.CropsBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IPlantable
{
    default PlantType getPlantType(IBlockReader world, BlockPos pos) {
        if (this instanceof CropsBlock) return PlantType.CROP;
        if (this instanceof SaplingBlock) return PlantType.PLAINS;
        if (this instanceof FlowerBlock) return PlantType.PLAINS;
        if (this == Blocks.DEAD_BUSH)      return PlantType.DESERT;
        if (this == Blocks.LILY_PAD)       return PlantType.WATER;
        if (this == Blocks.RED_MUSHROOM)   return PlantType.CAVE;
        if (this == Blocks.BROWN_MUSHROOM) return PlantType.CAVE;
        if (this == Blocks.NETHER_WART)    return PlantType.NETHER;
        if (this == Blocks.TALL_GRASS)      return PlantType.PLAINS;
        return net.minecraftforge.common.PlantType.PLAINS;
    }

    BlockState getPlant(IBlockReader world, BlockPos pos);
}
