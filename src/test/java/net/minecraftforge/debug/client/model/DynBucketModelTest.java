/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.model;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

@Mod(DynBucketModelTest.MODID)
public class DynBucketModelTest
{
    public static final boolean ENABLE = false; // TODO fix
    public static final String MODID = "dyn_bucket_model_test";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> DRIP_BUCKET = ITEMS.register("drip_bucket", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LAVA_OBSIDIAN = ITEMS.register("lava_obsidian", () -> new Item(new Item.Properties()));

    public DynBucketModelTest()
    {
        if (ENABLE)
        {
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            ITEMS.register(modEventBus);
            FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<CreativeModeTabEvent.BuildContents>) onBuildContents -> {
                if (onBuildContents.getTab() == CreativeModeTabs.INGREDIENTS) {
                    onBuildContents.register((flags, output, permissions) -> {
                        output.accept(DRIP_BUCKET.get());
                        output.accept(LAVA_OBSIDIAN.get());
                    });
                }
            });
        }
    }
}
