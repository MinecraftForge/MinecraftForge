/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IPlantable
{
    default EnumPlantType getPlantType(IBlockReader world, BlockPos pos) {
        if (this instanceof BlockCrops) return EnumPlantType.Crop;
        if (this instanceof BlockSapling) return EnumPlantType.Plains;
        if (this instanceof BlockFlower) return EnumPlantType.Plains;
        if (this == Blocks.DEAD_BUSH)      return EnumPlantType.Desert;
        if (this == Blocks.LILY_PAD)       return EnumPlantType.Water;
        if (this == Blocks.RED_MUSHROOM)   return EnumPlantType.Cave;
        if (this == Blocks.BROWN_MUSHROOM) return EnumPlantType.Cave;
        if (this == Blocks.NETHER_WART)    return EnumPlantType.Nether;
        if (this == Blocks.TALL_GRASS)      return EnumPlantType.Plains;
        return net.minecraftforge.common.EnumPlantType.Plains;
    }

    IBlockState getPlant(IBlockReader world, BlockPos pos);
}
