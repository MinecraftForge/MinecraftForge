/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.data.loading;

import net.minecraft.Util;
import net.minecraft.client.ClientBootstrap;
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
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class DatagenModLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static ExistingFileHelper existingFileHelper;
    private static boolean runningDataGen;

    public static boolean isRunningDataGen() {
        return runningDataGen;
    }

    public static DatagenModLoader setup(OptionParser parser, boolean client) {
        return new DatagenModLoader(parser, client);
    }

    @SuppressWarnings("unused")
    private final OptionParser parser;
    private final OptionSpec<Void> client;
    private final OptionSpec<String> existing;
    private final OptionSpec<String> existingMod;
    private final OptionSpec<String> mod;
    private final OptionSpec<String> assetIndex;
    private final OptionSpec<File> gameDir;
    private final OptionSpec<File> assetsDir;
    private final OptionSpec<Void> flat;

    private DatagenModLoader(OptionParser parser, boolean client) {
        this.gameDir = parser.accepts("gameDir").withRequiredArg().ofType(java.io.File.class).defaultsTo(new java.io.File(".")).required(); //Need by modlauncher, so lets just eat it
        this.parser = parser;
        this.client = !client ? null : parser.accepts("client", "Include client generators");
        this.existing = parser.accepts("existing", "Existing resource packs that generated resources can reference").withRequiredArg();
        this.existingMod = parser.accepts("existing-mod", "Existing mods that generated resources can reference the resource packs of").withRequiredArg();
        this.mod = parser.accepts("mod", "A modid to dump").withRequiredArg().withValuesSeparatedBy(",");
        this.flat = parser.accepts("flat", "Do not append modid prefix to output directory when generating for multiple mods");
        this.assetIndex = parser.accepts("assetIndex").withRequiredArg();
        this.assetsDir = parser.accepts("assetsDir").withRequiredArg().ofType(java.io.File.class);
    }

    public boolean hasArgs(OptionSet options) {
        return options.specs().size() != 1 || !options.has(gameDir);
    }

    public boolean run(
        OptionSet options, Path output, Collection<Path> inputs,
        boolean genServer, boolean genClient, boolean genDev, boolean genReports, boolean validate
    ) {
        var existingPacks = options.valuesOf(this.existing).stream().map(Paths::get).toList();
        var existingMods = new HashSet<>(options.valuesOf(this.existingMod));
        var patterns = new HashSet<>(options.valuesOf(this.mod));
        var flat = patterns.isEmpty() || options.has(this.flat);
        var assetIndex = options.valueOf(this.assetIndex);
        var assetsDir = options.valueOf(this.assetsDir);

        if (patterns.contains("minecraft") && patterns.size() == 1)
            return true;

        if (!genClient && this.client != null)
            genClient = options.has(this.client);

        runningDataGen = true;
        Bootstrap.bootStrap();
        if (genClient)
            ClientBootstrap.bootstrap();
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

        return false;
    }
}
