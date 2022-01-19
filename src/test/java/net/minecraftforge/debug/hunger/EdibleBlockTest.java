/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.hunger;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.hunger.FoodValues;
import net.minecraftforge.common.hunger.IEdible;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

/**
 * Tests that implementing IEdible on a block works as expected.
 * Creates a block that when right-clicked rewards 10 hunger points and 20 saturation.
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
        if (ENABLED) event.getRegistry().register(new ForgeEdibleBlock(BlockBehaviour.Properties.of(Material.CAKE).strength(0.5F).sound(SoundType.WOOL)));
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
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
        {
            if (!player.canEat(this.canAlwaysEat()))
            {
                return InteractionResult.PASS;
            }
            else
            {
                if (!level.isClientSide)
                {
                    player.getFoodData().eat(this.asItem(), new ItemStack(this));
                    level.removeBlock(pos, false);
                }
                return InteractionResult.SUCCESS;
            }
        }

        @Override
        public FoodValues getFoodValues(@Nonnull ItemStack stack)
        {
            return new FoodValues(10, 1.0F);
        }

        @Override
        public boolean canAlwaysEat()
        {
            return true;
        }
    }
}
