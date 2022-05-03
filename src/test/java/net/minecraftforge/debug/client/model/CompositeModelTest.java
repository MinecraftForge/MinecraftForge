/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.model;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

@Mod(CompositeModelTest.MODID)
public class CompositeModelTest
{
    public static final String MODID = "composite_model_test";
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static RegistryObject<Block> composite_block = BLOCKS.register("composite_block", () ->
            new Block(Block.Properties.of(Material.WOOD).strength(10)) {
                @Override
                protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
                {
                    builder.add(BlockStateProperties.HORIZONTAL_FACING);
                }

                @Nullable
                @Override
                public BlockState getStateForPlacement(BlockItemUseContext context)
                {
                    return defaultBlockState().setValue(
                            BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection()
                    );
                }

                @Override
                public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
                    return VoxelShapes.or(
                            box(5.6, 5.6, 5.6, 10.4, 10.4, 10.4),
                            box(0, 0, 0, 4.8, 4.8, 4.8),
                            box(11.2, 0, 0, 16, 4.8, 4.8),
                            box(0, 0, 11.2, 4.8, 4.8, 16),
                            box(11.2, 0, 11.2, 16, 4.8, 16),
                            box(0, 11.2, 0, 4.8, 16, 4.8),
                            box(11.2, 11.2, 0, 16, 16, 4.8),
                            box(0, 11.2, 11.2, 4.8, 16, 16),
                            box(11.2, 11.2, 11.2, 16, 16, 16)
                    );
                }
            }
    );

    public static RegistryObject<Item> composite_item = ITEMS.register("composite_block", () ->
            new BlockItem(composite_block.get(), new Item.Properties().tab(ItemGroup.TAB_MISC)) {
                @Override
                public boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity)
                {
                    return armorType == EquipmentSlotType.HEAD;
                }
            }
    );

    public CompositeModelTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }
}
