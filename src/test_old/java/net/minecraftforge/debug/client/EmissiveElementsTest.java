/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.client;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftsingularity.event.BuildCreativeModeTabContentsEvent;
import net.minecraftsingularity.eventbus.api.IEventBus;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;
import net.minecraftsingularity.registries.RegistryObject;

/**
 * Test mod that demos emissivity on "elements" models.
 */
@Mod(EmissiveElementsTest.MOD_ID)
public class EmissiveElementsTest
{
    public static final String MOD_ID = "emissive_elements_test";
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(singularityRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(singularityRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("emissive", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Item> TEST_BLOCK_ITEM = ITEMS.register("emissive", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties()));

    public EmissiveElementsTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(TEST_BLOCK_ITEM);
    }
}
