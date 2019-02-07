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
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockReed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

/**
 * Extension interfaces for default implementations of {@link IPlant} / {@link IHarvestablePlant} / {@link IGrowablePlant} / {@link IPlantable}
 *
 */
public final class PlantImpl
{

    /**
     * Implementation of IHarvestablePlant / IGrowablePlant for net.minecraft.block.BlockCrops
     */
    public static interface Crops extends IHarvestablePlant, IGrowablePlant
    {

        @Override
        default void harvest(IWorld world, Random rand, BlockPos pos, IBlockState state, EntityPlayer harvester, NonNullList<ItemStack> drops,
                boolean shouldReplant)
        {
            getThis().getDrops(state, drops, world.getWorld(), pos,
                    harvester == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvester.getHeldItemMainhand()));
            if (shouldReplant)
            {
                ItemStack seeds = getThis().getItem(world, pos, state);
                for (ItemStack s : drops)
                {
                    if (seeds.isItemEqual(s))
                    {
                        s.shrink(1);
                        break;
                    }
                }
                world.setBlockState(pos, state.with(BlockCrops.AGE, 0), 3);
            }
            else
                world.destroyBlock(pos, false);
        }

        @Override
        default boolean isMature(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return state.get(BlockCrops.AGE) == 7;
        }

        @Override
        default PlantType getPlantType(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return PlantTypes.CROP;
        }

        default BlockCrops getThis()
        {
            return (BlockCrops) this;
        }

    }

    /**
     * Implementation of IHarvestablePlant / IGrowablePlant for net.minecraft.block.BlockNetherWart
     */
    public static interface NetherWart extends IHarvestablePlant, IGrowablePlant
    {

        @Override
        default void harvest(IWorld world, Random rand, BlockPos pos, IBlockState state, EntityPlayer harvester, NonNullList<ItemStack> drops,
                boolean shouldReplant)
        {
            getThis().getDrops(state, drops, world.getWorld(), pos,
                    harvester == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvester.getHeldItemMainhand()));
            if (shouldReplant)
            {
                ItemStack seeds = getThis().getItem(world, pos, state);
                for (ItemStack s : drops)
                {
                    if (seeds.isItemEqual(s))
                    {
                        s.shrink(1);
                        break;
                    }
                }
                world.setBlockState(pos, state.with(BlockNetherWart.AGE, 0), 3);
            }
            else
                world.destroyBlock(pos, false);
        }

        @Override
        default boolean isMature(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return state.get(BlockNetherWart.AGE) == 3;
        }

        @Override
        default void grow(IWorld world, Random rand, BlockPos pos, IBlockState state, boolean natural)
        {
            world.setBlockState(pos, state.with(BlockNetherWart.AGE, Math.min(state.get(BlockNetherWart.AGE) + 1, 3)), 3);
        }

        @Override
        default boolean canGrow(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return !isMature(world, pos, state);
        }

        @Override
        default boolean canUseBonemeal(IBlockReader world, Random rand, BlockPos pos, IBlockState state)
        {
            return false;
        }

        @Override
        default PlantType getPlantType(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return PlantTypes.NETHER;
        }

        default BlockNetherWart getThis()
        {
            return (BlockNetherWart) this;
        }

    }

    /**
     * Implementation of IHarvestablePlant / IGrowablePlant for net.minecraft.block.BlockReed
     */
    public static interface Reeds extends IHarvestablePlant, IGrowablePlant
    {

        @Override
        default void harvest(IWorld world, Random rand, BlockPos pos, IBlockState state, EntityPlayer harvester, NonNullList<ItemStack> drops,
                boolean shouldReplant)
        {
            getThis().getDrops(state, drops, world.getWorld(), pos,
                    harvester == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvester.getHeldItemMainhand()));
            world.destroyBlock(pos, false);
        }

        @Override
        default boolean isMature(IBlockReader world,BlockPos pos, IBlockState state)
        {
            return world.getBlockState(pos.down()).getBlock() == getThis();
        }

        @Override
        default boolean canGrow(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return state.get(BlockReed.AGE) < 15
                    || (state.get(BlockReed.AGE) == 15 && world.getBlockState(pos.down(2)).getBlock() != getThis() && world.getBlockState(pos.up()).isAir(world, pos));
        }

        @Override
        default boolean canUseBonemeal(IBlockReader world, Random rand, BlockPos pos, IBlockState state)
        {
            return false;
        }

        @Override
        default void grow(IWorld world, Random rand, BlockPos pos, IBlockState state, boolean natural)
        {
            if (state.get(BlockReed.AGE) < 15)
                world.setBlockState(pos, state.with(BlockReed.AGE, state.get(BlockReed.AGE) + 1), 3);
            else
            {
                BlockPos up = pos.up();
                world.setBlockState(up, getThis().getDefaultState(), 3);
                IBlockState iblockstate = state.with(BlockReed.AGE, 0);
                world.setBlockState(pos, iblockstate, 4);
                iblockstate.neighborChanged(world.getWorld(), pos, getThis(), pos);
            }
        }

        @Override
        default PlantType getPlantType(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return PlantTypes.BEACH;
        }

        default BlockReed getThis()
        {
            return (BlockReed) this;
        }

    }

    /**
     * Implementation of IHarvestablePlant / IGrowablePlant for net.minecraft.block.BlockCactus
     */
    public static interface Cactus extends IHarvestablePlant, IGrowablePlant
    {

        @Override
        default void harvest(IWorld world, Random rand, BlockPos pos, IBlockState state, EntityPlayer harvester, NonNullList<ItemStack> drops,
                boolean shouldReplant)
        {
            getThis().getDrops(state, drops, world.getWorld(), pos,
                    harvester == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvester.getHeldItemMainhand()));
            world.destroyBlock(pos, false);
        }

        @Override
        default boolean isMature(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return world.getBlockState(pos.down()).getBlock() == getThis();
        }

        @Override
        default boolean canGrow(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return state.get(BlockCactus.AGE) < 15
                    || (state.get(BlockCactus.AGE) == 15 && world.getBlockState(pos.down(2)).getBlock() != getThis() && world.getBlockState(pos.up()).isAir(world, pos));
        }

        @Override
        default boolean canUseBonemeal(IBlockReader world, Random rand, BlockPos pos, IBlockState state)
        {
            return false;
        }

        @Override
        default void grow(IWorld world, Random rand, BlockPos pos, IBlockState state, boolean natural)
        {
            if (state.get(BlockCactus.AGE) < 15)
                world.setBlockState(pos, state.with(BlockCactus.AGE, state.get(BlockCactus.AGE) + 1), 3);
            else
            {
                BlockPos up = pos.up();
                world.setBlockState(up, getThis().getDefaultState(), 3);
                IBlockState iblockstate = state.with(BlockCactus.AGE, 0);
                world.setBlockState(pos, iblockstate, 4);
                iblockstate.neighborChanged(world.getWorld(), pos, getThis(), pos);
            }
        }

        @Override
        default PlantType getPlantType(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return PlantTypes.DESERT;
        }

        default BlockCactus getThis()
        {
            return (BlockCactus) this;
        }

    }

    /**
     * Implementation of IHarvestablePlant / IGrowablePlant for net.minecraft.block.BlockCocoa
     */
    public static interface Cocoa extends IHarvestablePlant, IGrowablePlant
    {

        @Override
        default void harvest(IWorld world, Random rand, BlockPos pos, IBlockState state, EntityPlayer harvester, NonNullList<ItemStack> drops,
                boolean shouldReplant)
        {
            getThis().getDrops(state, drops, world.getWorld(), pos,
                    harvester == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvester.getHeldItemMainhand()));
            if (shouldReplant)
            {
                ItemStack seeds = getThis().getItem(world, pos, state);
                for (ItemStack s : drops)
                {
                    if (seeds.isItemEqual(s))
                    {
                        s.shrink(1);
                        break;
                    }
                }
                world.setBlockState(pos, state.with(BlockCocoa.AGE, 0), 3);
            }
            else
                world.destroyBlock(pos, false);
        }

        @Override
        default boolean isMature(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return state.get(BlockCocoa.AGE) == 2;
        }

        @Override
        default PlantType getPlantType(IBlockReader world, BlockPos pos, IBlockState state)
        {
            return PlantTypes.EPIPHYTE;
        }

        default BlockCocoa getThis()
        {
            return (BlockCocoa) this;
        }

    }

    /**
     * Implementation of IPlantable for net.minecraft.item.ItemCocoa
     */
    public static interface ItemCocoa extends IPlantable
    {
        @Override
        default IBlockState getPlant(IBlockReader world, BlockPos pos, ItemStack stack)
        {
            return Blocks.COCOA.getDefaultState();
        }

        @Override
        default PlantType getPlantType(IBlockReader world, BlockPos pos, ItemStack stack)
        {
            return PlantTypes.EPIPHYTE;
        }
    }

}
