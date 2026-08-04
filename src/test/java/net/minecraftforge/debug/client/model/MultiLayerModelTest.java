/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.model;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod(MultiLayerModelTest.MODID)
public class MultiLayerModelTest
{
    private static final boolean ENABLED = true;
    public static final String MODID = "forgedebugmultilayermodel";
    public static final String VERSION = "0.0";

    @ObjectHolder("test_layer_block_new")
    public static final Block TEST_BLOCK_NEW = null;

    public MultiLayerModelTest()
    {
        if (!ENABLED)
            return;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.register(Registration.class);
    }

    private static class MultiLayerBlock extends Block
    {
        public MultiLayerBlock()
        {
            super(Properties.create(Material.CLAY));
        }

        @Override
        public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer)
        {
            return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
        }
    }

    public static class Registration
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            event.getRegistry().registerAll(
                    new MultiLayerBlock().setRegistryName(new ResourceLocation(MODID, "test_layer_block_new"))
            );
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().registerAll(
                    new BlockItem(TEST_BLOCK_NEW, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(TEST_BLOCK_NEW.getRegistryName())
            );
        }
    }
}
