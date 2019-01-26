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

import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockReed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Extension interfaces for default implementations of {@link IHarvestablePlant}
 *
 */
public final class HarvestableImpl
{

    public static interface Crops extends IHarvestablePlant, IGrowablePlant
    {

    @Override
    default void harvest(World world, Random rand, BlockPos pos, IBlockState state, EntityPlayer harvester, NonNullList<ItemStack> drops, boolean shouldReplant)
    {
        getThis().getDrops(state, drops, world, pos, harvester == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvester.getHeldItemMainhand()));
        if(shouldReplant)
        {
            ItemStack seeds = getThis().getItem(world, pos, state);
            for(ItemStack s : drops)
            {
                if(seeds.isItemEqual(s)) 
                { 
                s.shrink(1); 
                break;
                }
            }
            world.setBlockState(pos, state.with(BlockCrops.AGE, 0));
        }
        else world.destroyBlock(pos, false);
    }

    @Override
    default boolean isMature(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return state.get(BlockCrops.AGE) == 7;
    }

    default BlockCrops getThis()
    {
        return (BlockCrops) this;
    }

    }
    
    public static interface NetherWart extends IHarvestablePlant, IGrowablePlant
    {

    @Override
    default void harvest(World world, Random rand, BlockPos pos, IBlockState state, EntityPlayer harvester, NonNullList<ItemStack> drops, boolean shouldReplant)
    {
        getThis().getDrops(state, drops, world, pos, harvester == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvester.getHeldItemMainhand()));
        if(shouldReplant)
        {
            ItemStack seeds = getThis().getItem(world, pos, state);
            for(ItemStack s : drops)
            {
                if(seeds.isItemEqual(s)) 
                { 
                s.shrink(1); 
                break;
                }
            }
            world.setBlockState(pos, state.with(BlockNetherWart.AGE, 0));
        }
        else world.destroyBlock(pos, false);
    }

    @Override
    default boolean isMature(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return state.get(BlockNetherWart.AGE) == 3;
    }

    @Override
    default void grow(World world, Random rand, BlockPos pos, IBlockState state, boolean natural)
    {
        world.setBlockState(pos, state.with(BlockNetherWart.AGE, Math.min(state.get(BlockNetherWart.AGE), 3)));
    }

    @Override
    default boolean canGrow(World world, BlockPos pos, IBlockState state)
    {
        return !isMature(world, world.rand, pos, state);
    }

    @Override
    default boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return false;
    }

    default BlockNetherWart getThis()
    {
        return (BlockNetherWart) this;
    }

    }

    public static interface Reeds extends IHarvestablePlant, IGrowablePlant
    {

    @Override
    default void harvest(World world, Random rand, BlockPos pos, IBlockState state, EntityPlayer harvester, NonNullList<ItemStack> drops, boolean shouldReplant)
    {
        getThis().getDrops(state, drops, world, pos, harvester == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvester.getHeldItemMainhand()));
        world.destroyBlock(pos, false);
    }

    @Override
    default boolean isMature(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return state.get(BlockReed.AGE) == 15;
    }
    
    @Override
    default boolean canGrow(World world, BlockPos pos, IBlockState state)
    {
        return state.get(BlockReed.AGE) < 15 || (state.get(BlockReed.AGE) == 15 && world.getBlockState(pos.down(2)).getBlock() != getThis() && world.isAirBlock(pos.up()));
    }
    
    @Override
    default boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return false;
    }
    
    @Override
    default void grow(World world, Random rand, BlockPos pos, IBlockState state, boolean natural)
    {
        if(state.get(BlockReed.AGE) < 15) world.setBlockState(pos, state.with(BlockReed.AGE, state.get(BlockReed.AGE) + 1));
        else
        {
            BlockPos up = pos.up();
            world.setBlockState(up, getThis().getDefaultState());
            IBlockState iblockstate = state.with(BlockCactus.AGE, 0);
            world.setBlockState(pos, iblockstate, 4);
            iblockstate.neighborChanged(world, pos, getThis(), pos);
        }
    }

    default BlockReed getThis()
    {
        return (BlockReed) this;
    }

    }
    
    public static interface Cactus extends IHarvestablePlant, IGrowablePlant
    {

    @Override
    default void harvest(World world, Random rand, BlockPos pos, IBlockState state, EntityPlayer harvester, NonNullList<ItemStack> drops, boolean shouldReplant)
    {
        getThis().getDrops(state, drops, world, pos, harvester == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvester.getHeldItemMainhand()));
        world.destroyBlock(pos, false);
    }

    @Override
    default boolean isMature(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return state.get(BlockCactus.AGE) == 15;
    }
    
    @Override
    default boolean canGrow(World world, BlockPos pos, IBlockState state)
    {
        return state.get(BlockCactus.AGE) < 15 || (state.get(BlockCactus.AGE) == 15 && world.getBlockState(pos.down(2)).getBlock() != getThis() && world.isAirBlock(pos.up()));
    }
    
    @Override
    default boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
    {
        return false;
    }
    
    @Override
    default void grow(World world, Random rand, BlockPos pos, IBlockState state, boolean natural)
    {
        if(state.get(BlockCactus.AGE) < 15) world.setBlockState(pos, state.with(BlockCactus.AGE, state.get(BlockCactus.AGE) + 1));
        else
        {
            BlockPos up = pos.up();
            world.setBlockState(up, getThis().getDefaultState());
            IBlockState iblockstate = state.with(BlockCactus.AGE, 0);
            world.setBlockState(pos, iblockstate, 4);
            iblockstate.neighborChanged(world, pos, getThis(), pos);
        }
    }

    default BlockCactus getThis()
    {
        return (BlockCactus) this;
    }

    }
    
}
