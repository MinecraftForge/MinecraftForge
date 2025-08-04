/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.loading.sort;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

@GameTestNamespace("forge")
@Mod(ModLoadSortingTest.MODID)
public class ModLoadSortingTest extends BaseTestMod {
    static final String MODID = "load_sort_test";

    protected static final Logger LOGGER = LogUtils.getLogger();

    private static boolean beforeHasInit;
    private static boolean afterHasInit;

    public ModLoadSortingTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
        afterHasInit = ModLoadSortingAfter.hasInit;
        beforeHasInit = ModLoadSortingBefore.hasInit;
    }

    @GameTest
    public static void ran_after_parent(GameTestHelper helper) {
        helper.assertTrue(afterHasInit, "Mod constructor was fired before dependency finished");
        helper.succeed();
    }

    @GameTest
    public static void ran_before_child(GameTestHelper helper) {
        helper.assertFalse(beforeHasInit, "Mod constructor was fired before dependency finished");
        helper.succeed();
    }
}
