/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
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
