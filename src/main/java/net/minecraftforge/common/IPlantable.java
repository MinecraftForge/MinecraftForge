/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public interface IPlantable {
    default PlantType getPlantType(BlockGetter level, BlockPos pos) {
        if (this instanceof AttachedStemBlock) return PlantType.CROP;
        if (this instanceof CropBlock)     return PlantType.CROP;
        if (this == Blocks.MANGROVE_PROPAGULE) return PlantType.MOIST;
        if (this instanceof AzaleaBlock) return PlantType.MOIST;
        if (this instanceof SaplingBlock)  return PlantType.PLAINS;
        if (this instanceof FlowerBlock)   return PlantType.PLAINS;
        if (this == Blocks.PITCHER_CROP)   return PlantType.CROP;
        if (this == Blocks.DEAD_BUSH)      return PlantType.DESERT;
        if (this == Blocks.LILY_PAD)       return PlantType.WATER;
        if (this == Blocks.RED_MUSHROOM)   return PlantType.CAVE;
        if (this == Blocks.BROWN_MUSHROOM) return PlantType.CAVE;
        if (this == Blocks.CRIMSON_FUNGUS) return PlantType.FUNGUS;
        if (this == Blocks.WARPED_FUNGUS)  return PlantType.FUNGUS;
        if (this == Blocks.NETHER_WART)    return PlantType.NETHER;
        if (this == Blocks.TALL_GRASS)     return PlantType.PLAINS;
        return PlantType.PLAINS;
    }

    BlockState getPlant(BlockGetter level, BlockPos pos);
}
