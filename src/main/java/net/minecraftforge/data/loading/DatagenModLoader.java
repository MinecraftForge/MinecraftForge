/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.data.loading;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModWorkManager;
import net.minecraftforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatagenModLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static GatherDataEvent.DataGeneratorConfig dataGeneratorConfig;
    private static ExistingFileHelper existingFileHelper;
    private static boolean runningDataGen;

    public static boolean isRunningDataGen() {
        return runningDataGen;
    }

    public static void begin(final Set<String> mods, final Path path, final Collection<Path> inputs, Collection<Path> existingPacks,
            Set<String> existingMods, final boolean serverGenerators, final boolean clientGenerators, final boolean devToolGenerators, final boolean reportsGenerator,
            final boolean structureValidator, final boolean flat, final String assetIndex, final File assetsDir) {
        if (mods.contains("minecraft") && mods.size() == 1)
            return;
        LOGGER.info("Initializing Data Gatherer for mods {}", mods);
        runningDataGen = true;
        Bootstrap.bootStrap();
        ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ()->{});
        CompletableFuture<HolderLookup.Provider> lookupProvider = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        dataGeneratorConfig = new GatherDataEvent.DataGeneratorConfig(mods, path, inputs, lookupProvider, serverGenerators,
                clientGenerators, devToolGenerators, reportsGenerator, structureValidator, flat);
        if (!mods.contains("forge")) {
            // If we aren't generating data for forge, automatically add forge as an existing so mods can access forge's data
            existingMods.add("forge");
        }
        existingFileHelper = new ExistingFileHelper(existingPacks, existingMods, structureValidator, assetIndex, assetsDir);
        ModLoader.get().runEventGenerator(mc -> new GatherDataEvent(mc, dataGeneratorConfig.makeGenerator(p -> dataGeneratorConfig.isFlat() ? p : p.resolve(mc.getModId()),
                dataGeneratorConfig.getMods().contains(mc.getModId())), dataGeneratorConfig, existingFileHelper));
        dataGeneratorConfig.runAll();
    }
}
