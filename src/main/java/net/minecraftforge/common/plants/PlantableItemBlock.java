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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class PlantableItemBlock extends ItemBlock implements IPlantable
{

    public PlantableItemBlock(Block block, Builder builder)
    {
        super(block, builder);
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos, ItemStack stack)
    {
        return ((IPlant) getBlock()).getPlantType(world, pos, getPlant(world, pos, stack));
    }

    @Override
    public IBlockState getPlant(IBlockReader world, BlockPos pos, ItemStack stack)
    {
        return getBlock().getDefaultState();
    }

}
