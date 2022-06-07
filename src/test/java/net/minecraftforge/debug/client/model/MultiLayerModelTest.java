/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.model;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;

@Mod(MultiLayerModelTest.MODID)
public class MultiLayerModelTest
{
    private static final boolean ENABLED = true;
    public static final String MODID = "forgedebugmultilayermodel";
    public static final String VERSION = "0.0";

    private static final String blockName = "test_layer_block";
    private static final ResourceLocation blockId = new ResourceLocation(MODID, blockName);
    @ObjectHolder(registryName = "block", value = blockName)
    public static final Block TEST_BLOCK = null;

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerBlocks(RegisterEvent event)
        {
            if (!ENABLED || !event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS))
                return;
            event.register(ForgeRegistries.Keys.BLOCKS, blockId, () -> new Block(Block.Properties.of(Material.WOOD).noOcclusion()));
        }

        @SubscribeEvent
        public static void registerItems(RegisterEvent event)
        {
            if (!ENABLED || !event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS))
                return;
            event.register(ForgeRegistries.Keys.ITEMS, blockId, () -> new BlockItem(TEST_BLOCK, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
        }

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event)
        {
            if (!ENABLED)
                return;
            ItemBlockRenderTypes.setRenderLayer(TEST_BLOCK, (layer) -> {
                return layer == RenderType.solid() || layer == RenderType.translucent();
            });
        }
    }
}
