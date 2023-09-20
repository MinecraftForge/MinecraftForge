/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Test mod that demos disabling ambient occlusion on specific faces of "elements" models.
 */
@Mod(AmbientOcclusionElementsTest.MOD_ID)
public class AmbientOcclusionElementsTest
{
    private static final boolean ENABLED = false;

    public static final String MOD_ID = "ambient_occlusion_elements_test";
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Block> AO_BLOCK_SHADE = BLOCKS.register("ambient_occlusion_shade", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> AO_BLOCK_NO_SHADE = BLOCKS.register("ambient_occlusion_no_shade", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> NO_AO_BLOCK_SHADE = BLOCKS.register("no_ambient_occlusion_shade", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Block> NO_AO_BLOCK_NO_SHADE = BLOCKS.register("no_ambient_occlusion_no_shade", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Item> AO_BLOCK_SHADE_ITEM = ITEMS.register("ambient_occlusion_shade", () -> new BlockItem(AO_BLOCK_SHADE.get(), new Item.Properties()));
    public static final RegistryObject<Item> AO_BLOCK_NO_SHADE_ITEM = ITEMS.register("ambient_occlusion_no_shade", () -> new BlockItem(AO_BLOCK_NO_SHADE.get(), new Item.Properties()));
    public static final RegistryObject<Item> NO_AO_BLOCK_SHADE_ITEM = ITEMS.register("no_ambient_occlusion_shade", () -> new BlockItem(NO_AO_BLOCK_SHADE.get(), new Item.Properties()));
    public static final RegistryObject<Item> NO_AO_BLOCK_NO_SHADE_ITEM = ITEMS.register("no_ambient_occlusion_no_shade", () -> new BlockItem(NO_AO_BLOCK_NO_SHADE.get(), new Item.Properties()));

    public AmbientOcclusionElementsTest()
    {
        if (!ENABLED)
            return;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
        {
            event.accept(AO_BLOCK_SHADE_ITEM);
            event.accept(AO_BLOCK_NO_SHADE_ITEM);
            event.accept(NO_AO_BLOCK_SHADE_ITEM);
            event.accept(NO_AO_BLOCK_NO_SHADE_ITEM);
        }
    }
}
