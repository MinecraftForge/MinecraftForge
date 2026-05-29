/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.client.model;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftsingularity.event.BuildCreativeModeTabContentsEvent;
import net.minecraftsingularity.eventbus.api.IEventBus;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;

@Mod(DynBucketModelTest.MODID)
public class DynBucketModelTest
{
    public static final boolean ENABLE = false; // TODO fix
    public static final String MODID = "dyn_bucket_model_test";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(singularityRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> DRIP_BUCKET = ITEMS.register("drip_bucket", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LAVA_OBSIDIAN = ITEMS.register("lava_obsidian", () -> new Item(new Item.Properties()));

    public DynBucketModelTest()
    {
        if (ENABLE)
        {
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            ITEMS.register(modEventBus);
            modEventBus.addListener(this::addCreative);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
        {
            event.accept(DRIP_BUCKET);
            event.accept(LAVA_OBSIDIAN);
        }
    }
}
