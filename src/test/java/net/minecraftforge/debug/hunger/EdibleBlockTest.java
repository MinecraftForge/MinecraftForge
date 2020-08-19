/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.hunger;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.hunger.FoodValues;
import net.minecraftforge.common.hunger.IEdible;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

/**
 * Tests that implementing IEdible on a block works as expected.
 * Creates a block that when right-clicked rewards 10 hunger points and 10 saturation.
 */
@Mod(EdibleBlockTest.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EdibleBlockTest
{
    private static final boolean ENABLED = false;
    static final String MODID = "edible_block_test";
    private static final String BLOCKID = "forge_edible_block";

    @ObjectHolder(BLOCKID)
    private static Block EDIBLE_BLOCK;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        if (ENABLED) event.getRegistry().register(new ForgeEdibleBlock(AbstractBlock.Properties.of(Material.CAKE).strength(0.5F).sound(SoundType.WOOL)));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        if (ENABLED) event.getRegistry().register(new BlockItem(EDIBLE_BLOCK, new Item.Properties()).setRegistryName(MODID, BLOCKID));
    }

    public static class ForgeEdibleBlock extends Block implements IEdible
    {
        public ForgeEdibleBlock(Properties properties)
        {
            super(properties);
            setRegistryName(EdibleBlockTest.MODID, EdibleBlockTest.BLOCKID);
        }

        @Override
        public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
        {
            if (!player.canEat(this.canAlwaysEat()))
            {
                return ActionResultType.PASS;
            }
            else
            {
                if (!worldIn.isClientSide)
                {
                    player.getFoodData().eat(this.asItem(), new ItemStack(this));
                    worldIn.removeBlock(pos, false);
                }
                return ActionResultType.SUCCESS;
            }
        }

        @Override
        public FoodValues getFoodValues(@Nonnull ItemStack stack)
        {
            return new FoodValues(10, 10F);
        }

        @Override
        public boolean canAlwaysEat()
        {
            return true;
        }
    }
}
