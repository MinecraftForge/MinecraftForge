/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface IPlantable
{

    BlockState getPlant(BlockGetter level, BlockPos pos);

    /**
     * Determines if the {@code IPlantable} can be planted by villagers.
     * @param level
     * @param pos
     */
    default boolean isVillagerPlantable(BlockGetter level, BlockPos pos)
    {
        return false;
    }
}
