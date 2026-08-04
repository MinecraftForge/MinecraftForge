/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

// TODO 1.16: This should not be an enum. Change it to something that functions similarly to ToolType
public enum PlantType implements IExtensibleEnum
{
    Plains,
    Desert,
    Beach,
    Cave,
    Water,
    Nether,
    Crop;

    /**
     * Getting a custom {@link PlantType}, or an existing one if it has the same name as that one. Your plant should implement {@link IPlantable}
     * and return this custom type in {@link IPlantable#getPlantType(IBlockAccess, BlockPos)}.
     *
     * <p>If your new plant grows on blocks like any one of them above, never create a new {@link PlantType}.
     * This enumeration is only functioning in
     * {@link net.minecraft.block.Block#canSustainPlant(IBlockState, IWorldReader, BlockPos, EnumFacing, IPlantable)},
     * which you are supposed to override this function in your new block and create a new plant type to grow on that block.
     *
     * <p>You can create an instance of your plant type in your API and let your/others mods access it. It will be faster than calling this method.
     * @param name the name of the type of plant, you had better follow the style above
     * @return the acquired {@link PlantType}, a new one if not found.
     */
    public static PlantType create(String name)
    {
        throw new IllegalStateException("Enum not extended");
    }
}
