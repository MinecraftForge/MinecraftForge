/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;
import net.minecraftsingularity.registries.RegistryObject;

@Mod(CustomRarityTest.MOD_ID)
public class CustomRarityTest {

    private static final boolean ENABLED = true;
    static final String MOD_ID = "custom_rarity_test";

    private static final Rarity CUSTOM_RARITY = Rarity.create(MOD_ID + "_CUSTOM", style -> style.withItalic(true).withColor(ChatFormatting.DARK_AQUA));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(singularityRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> CUSTOM_ITEM = ITEMS.register(
            "test",
            () -> new Item(new Item.Properties())
            {
                @Override
                public Rarity getRarity(ItemStack p_41461_)
                {
                    return CUSTOM_RARITY;
                }
            }
    );

    public CustomRarityTest() {
        if (ENABLED)
        {
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }
}
