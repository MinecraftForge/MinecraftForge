/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiConsumer;

/**
 * A test case used to ensure that {@link IForgeBlockState#onTreeGrow(LevelReader, BiConsumer, RandomSource, BlockPos, TreeConfiguration)}
 * works properly, using a custom grass block that should revert to its custom dirt form after a tree grows on
 * top of it instead of turning to dirt.
 */
@Mod(OnTreeGrowBlockTest.ID)
public class OnTreeGrowBlockTest
{
    public static final boolean ENABLED = true;

    static final String ID = "on_tree_grow_block_test";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.Keys.BLOCKS, ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, ID);

    public static final RegistryObject<Block> TEST_GRASS_BLOCK = BLOCKS.register("test_grass_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).destroyTime(1.5f))
    {
        @Override
        public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable)
        {
            return plantable instanceof SaplingBlock;
        }

        @Override
        public boolean onTreeGrow(BlockState state, LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config)
        {
            // Respect vanilla behavior for trees that want custom dirt blocks
            if (config.forceDirt)
            {
                return false;
            }
            else
            {
                placeFunction.accept(pos, TEST_DIRT.get().defaultBlockState());
                return true;
            }
        }
    });
    public static final RegistryObject<Block> TEST_DIRT = BLOCKS.register("test_dirt", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).destroyTime(1.5f))
    {
        @Override
        public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable)
        {
            return plantable instanceof SaplingBlock;
        }
    });
    public static final RegistryObject<Item> TEST_GRASS_BLOCK_ITEM = ITEMS.register("test_grass_block", () -> new BlockItem(TEST_GRASS_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> TEST_DIRT_ITEM = ITEMS.register("test_dirt", () -> new BlockItem(TEST_DIRT.get(), new Item.Properties()));

    public OnTreeGrowBlockTest()
    {
        if (ENABLED)
        {
            IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
            BLOCKS.register(modBus);
            ITEMS.register(modBus);
        }
    }
}
