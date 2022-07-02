/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("banner_pattern_test")
public class BannerPatternTest
{

    private static final boolean ENABLED = true;

    public BannerPatternTest()
    {
        if (!ENABLED) return;

        BannerPattern.create("BANNER_PATTERN_TEST_TEST1", "banner_pattern_test:test1", "bpt:t1");
        var pattern = BannerPattern.create("BANNER_PATTERN_TEST_TEST2", "banner_pattern_test:test2", "bpt:t2", true);
        BannerPattern.create("BANNER_PATTERN_TEST_TEST3", "banner_pattern_test:test3", "bpt:t3");

        var register = DeferredRegister.create(ForgeRegistries.ITEMS, "banner_pattern_test");
        register.register("pattern_item_test", () -> new BannerPatternItem(pattern, new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
        register.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
