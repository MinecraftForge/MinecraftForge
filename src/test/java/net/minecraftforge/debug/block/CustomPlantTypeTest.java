/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;

@Mod(CustomPlantTypeTest.MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class CustomPlantTypeTest
{
    static final String MODID = "custom_plant_type_test";
    private static final String CUSTOM_SOIL_BLOCK = "test_custom_block";
    private static final String CUSTOM_PLANT_BLOCK = "test_custom_plant";

    @ObjectHolder(registryName = "block", value = CUSTOM_SOIL_BLOCK)
    public static Block CUSTOM_SOIL;
    @ObjectHolder(registryName = "block", value = CUSTOM_PLANT_BLOCK)
    public static Block CUSTOM_PLANT;

    @SubscribeEvent
    public static void registerBlocks(RegisterEvent event)
    {
        event.register(ForgeRegistries.Keys.BLOCKS, helper ->
        {
            helper.register(CUSTOM_SOIL_BLOCK, new CustomBlock());
            helper.register(CUSTOM_PLANT_BLOCK, new CustomPlantBlock());
        });
    }

    @SubscribeEvent
    public static void registerItems(RegisterEvent event)
    {
        event.register(ForgeRegistries.Keys.ITEMS, helper ->
        {
            helper.register(CUSTOM_SOIL_BLOCK, new BlockItem(CUSTOM_SOIL, new Item.Properties()));
            helper.register(CUSTOM_PLANT_BLOCK, new BlockItem(CUSTOM_PLANT, new Item.Properties()));
        });
    }

    public static class CustomBlock extends Block
    {
        public CustomBlock()
        {
            super(Properties.of().mapColor(MapColor.STONE));
        }

        @Override
        public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable)
        {
            PlantType type = plantable.getPlantType(level, pos.relative(facing));
            if (type != null && type == CustomPlantBlock.pt)
            {
                return true;
            }
            return super.canSustainPlant(state, level, pos, facing, plantable);
        }
    }

    public static class CustomPlantBlock extends FlowerBlock implements IPlantable
    {
        public static PlantType pt = PlantType.get("custom_plant_type");

        public CustomPlantBlock()
        {
            super(MobEffects.WEAKNESS, 9, Properties.of().mapColor(MapColor.PLANT).noCollission().sound(SoundType.GRASS));
        }

        @Override
        public PlantType getPlantType(BlockGetter level, BlockPos pos)
        {
            return pt;
        }

        @Override
        public BlockState getPlant(BlockGetter level, BlockPos pos)
        {
            return defaultBlockState();
        }

        @Override
        public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos)
        {
            BlockState soil = world.getBlockState(pos.below());
            return soil.canSustainPlant(world, pos, Direction.UP, this);
        }

        @Override
        public boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos)
        {
            Block block = state.getBlock();
            return block == CUSTOM_SOIL;
        }
    }
}
