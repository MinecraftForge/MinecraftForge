/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.item;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftsingularity.event.BuildCreativeModeTabContentsEvent;
import net.minecraftsingularity.eventbus.api.IEventBus;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;

@Mod(EnderMaskTest.MODID)
public class EnderMaskTest
{
    public static final String MODID = "ender_mask_test";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(singularityRegistries.ITEMS, MODID);

    public static RegistryObject<Item> ENDER_MASK = ITEMS.register("ender_mask", () ->
            new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, (new Item.Properties()))
            {
                @Override
                public boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity)
                {
                    return player.experienceLevel > 10;
                }
            }
    );

    public EnderMaskTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS)
            event.accept(ENDER_MASK);
    }
}
