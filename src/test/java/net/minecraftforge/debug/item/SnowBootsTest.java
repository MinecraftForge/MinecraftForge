/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

@Mod(SnowBootsTest.MODID)
public class SnowBootsTest
{
    public static final String MODID = "snow_boots_test";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static RegistryObject<Item> snow_boots = ITEMS.register("snow_boots", () ->
        new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.FEET, (new Item.Properties()))
        {
            @Override
            public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
                return wearer.getHealth() < 10;
            }
        }
    );

    public SnowBootsTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        FMLJavaModLoadingContext.get().getModEventBus().addListener((Consumer<CreativeModeTabEvent.BuildContents>) onBuildContents -> {
            if (onBuildContents.getTab() == CreativeModeTabs.COMBAT) {
                onBuildContents.register((flags, output, permissions) -> {
                    output.accept(snow_boots.get());
                });
            }
        });
    }
}
