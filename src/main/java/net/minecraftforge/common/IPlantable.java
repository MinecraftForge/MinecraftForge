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
        if (this instanceof CropsBlock) return PlantType.Crop;
        if (this instanceof SaplingBlock) return PlantType.Plains;
        if (this instanceof FlowerBlock) return PlantType.Plains;
        if (this == Blocks.DEAD_BUSH)      return PlantType.Desert;
        if (this == Blocks.LILY_PAD)       return PlantType.Water;
        if (this == Blocks.RED_MUSHROOM)   return PlantType.Cave;
        if (this == Blocks.BROWN_MUSHROOM) return PlantType.Cave;
        if (this == Blocks.NETHER_WART)    return PlantType.Nether;
        if (this == Blocks.TALL_GRASS)      return PlantType.Plains;
        return net.minecraftforge.common.PlantType.Plains;
    }

    BlockState getPlant(IBlockReader world, BlockPos pos);
}
