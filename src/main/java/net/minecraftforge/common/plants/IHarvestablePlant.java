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

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHarvestablePlant extends IPlant
{

    /**
     * 
     * @param world The world
     * @param rand A random
     * @param pos The current pos
     * @param state The current state
     * 
     * @return If this plant is mature, in that it can be harvested via {@link IPlant#harvest(EntityPlayer, World, Random, BlockPos, IBlockState)}
     */
    boolean isMature(World world, Random rand, BlockPos pos, IBlockState state);

    /**
     * This method is called to harvest a given plant.  To harvest a block is to change it, add the respective drops to the given list, and replant it if necessary.
     * Changing the block is up to the implementation.  Some blocks may be destroyed on harvest, others may revert to a previous state in the growth process.
     * Auto-replanting should attempt to drop one less seed item than usual, assuming that seed item was "used" up in the replanting process.
     * If this plant does not need to be replanted on harvest, then the final boolean can be ignored.
     * Finally, if the plant is not mature (as indicated by {@link IPlant#isMature(World, Random, BlockPos, IBlockState)}), this method should do nothing.
     * 
     * @param world The world
     * @param rand A random
     * @param pos The current pos
     * @param state The current state
     * @param harvester The harvesting player, if available.
     * @param drops A list of itemstacks to add harvested items to.
     * @param shouldReplant If this crop should try to automatically replant itself.  
     */
    void harvest(World world, Random rand, BlockPos pos, IBlockState state, @Nullable EntityPlayer harvester, NonNullList<ItemStack> drops, boolean shouldReplant);

}
