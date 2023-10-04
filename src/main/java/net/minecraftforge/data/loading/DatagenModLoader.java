/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.data.loading;

import net.minecraft.Util;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModWorkManager;
import net.minecraftforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class DatagenModLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static ExistingFileHelper existingFileHelper;
    private static boolean runningDataGen;

    public static boolean isRunningDataGen() {
        return runningDataGen;
    }

    public static void begin(
        Set<String> patterns, Path output, Collection<Path> inputs, Collection<Path> existingPacks, Set<String> existingMods,
        boolean genServer, boolean genClient, boolean genDev, boolean genReports,
        boolean validate, boolean flat, String assetIndex, File assetsDir
    ) {
        if (patterns.contains("minecraft") && patterns.size() == 1)
            return;

        runningDataGen = true;
        Bootstrap.bootStrap();
        ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ()->{});
        var lookupProvider = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());

        var mods = new HashSet<String>();
        for (var pattern : patterns) {
            if (pattern.indexOf('.') == -1) // No wildcard!
                mods.add(pattern);

            var m = Pattern.compile('^' + pattern + '$');
            ModList.get().forEachModInOrder(mc -> {
                var id = mc.getModId();
                if (!"forge".equals(id) && !"minecraft".equals(id) && m.matcher(id).matches())
                    mods.add(id);
            });
        }
        LOGGER.info("Initializing Data Gatherer for mods {}", mods);

        var config = new GatherDataEvent.DataGeneratorConfig(mods, output, inputs, lookupProvider, genServer,
                genClient, genDev, genReports, validate, flat);

        if (!mods.contains("forge")) {
            // If we aren't generating data for forge, automatically add forge as an existing so mods can access forge's data
            existingMods.add("forge");
        }

        existingFileHelper = new ExistingFileHelper(existingPacks, existingMods, validate, assetIndex, assetsDir);
        ModLoader.get().runEventGenerator(mc -> new GatherDataEvent(
            mc,
            config.makeGenerator(
                p -> config.isFlat() ? p : p.resolve(mc.getModId()),
                config.getMods().contains(mc.getModId())
            ), config, existingFileHelper)
        );
        config.runAll();
    }
}
