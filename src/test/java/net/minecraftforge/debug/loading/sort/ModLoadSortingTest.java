/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.loading.sort;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.gametest.GameTest;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.test.BaseTestMod;

@GameTestNamespace("singularity")
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
