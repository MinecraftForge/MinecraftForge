/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.data.loading;

import net.minecraft.server.Bootstrap;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ValidationPredicate;
import net.minecraftforge.data.event.GatherValidationPredicatesEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModWorkManager;
import net.minecraftforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

public class DatagenModLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static GatherDataEvent.DataGeneratorConfig dataGeneratorConfig;
    private static ExistingFileHelper existingFileHelper;
    private static boolean runningDataGen;

    public static boolean isRunningDataGen() {
        return runningDataGen;
    }

    @Deprecated(forRemoval = true, since = "1.19.2")
    public static void begin(final Set<String> mods, final Path path, final Collection<Path> inputs, Collection<Path> existingPacks, Set<String> existingMods, final boolean serverGenerators, final boolean clientGenerators, final boolean devToolGenerators, final boolean reportsGenerator, final boolean structureValidator, final boolean flat, final String assetIndex, final File assetsDir) {
        begin(mods, path, inputs, existingPacks, existingMods, serverGenerators, clientGenerators, devToolGenerators, reportsGenerator, ValidationPredicate.TRUE, flat, assetIndex, assetsDir);
    }

    @ApiStatus.Internal
    public static void begin(final Set<String> mods, final Path path, final Collection<Path> inputs, Collection<Path> existingPacks, Set<String> existingMods, final boolean serverGenerators, final boolean clientGenerators, final boolean devToolGenerators, final boolean reportsGenerator, final ValidationPredicate initialValidationPredicate, final boolean flat, final String assetIndex, final File assetsDir)
    {
        if (mods.contains("minecraft") && mods.size() == 1) return;
        LOGGER.info("Initializing Data Gatherer for mods {}", mods);
        runningDataGen = true;
        Bootstrap.bootStrap();
        dataGeneratorConfig = new GatherDataEvent.DataGeneratorConfig(mods, path, inputs, serverGenerators, clientGenerators, devToolGenerators, reportsGenerator, initialValidationPredicate, flat);
        ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ()->{});
        if (!mods.contains("forge")) {
            //If we aren't generating data for forge, automatically add forge as an existing so mods can access forge's data
            existingMods.add("forge");
        }
        ModLoader.get().runEventGenerator(mc -> new GatherValidationPredicatesEvent(mc, dataGeneratorConfig));
        existingFileHelper = new ExistingFileHelper(existingPacks, existingMods, dataGeneratorConfig.getValidationPredicate(), assetIndex, assetsDir);
        ModLoader.get().runEventGenerator(mc->new GatherDataEvent(mc, dataGeneratorConfig.makeGenerator(p->dataGeneratorConfig.isFlat() ? p : p.resolve(mc.getModId()), dataGeneratorConfig.getMods().contains(mc.getModId())), dataGeneratorConfig, existingFileHelper));
        dataGeneratorConfig.runAll();
    }
}
