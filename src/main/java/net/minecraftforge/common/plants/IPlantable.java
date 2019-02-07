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

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public interface IPlantable
{

    /**
     * @param reader A block reader.
     * @param pos The position that this state may attempt to be placed at later.
     * @param stack The stack, which should contain an instance of this IPlantable.
     * 
     * @return The type of plant that will be planted by this IPlantable.
     */
    PlantType getPlantType(IBlockReader reader, BlockPos pos, ItemStack stack);

    /**
     * @param reader A block reader.
     * @param pos The position that this state may attempt to be placed at later.
     * @param stack The stack, which should contain an instance of this IPlantable.
     * 
     * @return The state that should be planted from this stack.
     */
    IBlockState getPlant(IBlockReader reader, BlockPos pos, ItemStack stack);

    /**
     * Used for custom logic when placing the plant state in the world. Should be called instead of setBlockState(getPlant()).
     * 
     * @param state The state to place, should be equivalent to {@link IPlantable#getPlant(ItemStack, IBlockReader, BlockPos)}.
     * @param world The world
     * @param pos The position to place at.
     * 
     * @return If this placement was successful.
     */
    default boolean placePlant(IBlockState state, World world, BlockPos pos)
    {
        return world.setBlockState(pos, state, 3);
    }
}
