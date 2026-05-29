/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.loading.sort;

import java.time.Duration;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.test.BaseTestMod;

@Mod(ModLoadSortingBefore.MODID)
public class ModLoadSortingBefore extends BaseTestMod {
    static final String MODID = "load_sort_before";
    protected static final Logger LOGGER = LogUtils.getLogger();
    static boolean hasInit = false;

    public ModLoadSortingBefore(FMLJavaModLoadingContext context) {
        super(context, false, false);
        try {
            Thread.sleep(Duration.ofSeconds(1));
        } catch (InterruptedException e) {
            sneak(e);
        }
        hasInit = true;
    }
}
