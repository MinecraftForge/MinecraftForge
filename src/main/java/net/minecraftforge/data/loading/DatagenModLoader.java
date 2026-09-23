/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.data.loading;

import net.minecraft.util.Util;
import net.minecraft.client.ClientBootstrap;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModWorkManager;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.data.event.GatherDataEvent.DataGeneratorConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public final class DatagenModLoader {
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
        boolean genServer, boolean genClient, boolean genDev, boolean genReports
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
        ModLoader.gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ()->{});
        var lookupProvider = CompletableFuture.supplyAsync(VanillaRegistries::createWorldLookup, Util.backgroundExecutor())
                .thenApplyAsync(VanillaRegistries::createReloadableLookup, Util.backgroundExecutor());

        var mods = new HashSet<String>();
        for (var pattern : patterns) {
            if (pattern.indexOf('.') == -1) // No wildcard!
                mods.add(pattern);

            var m = Pattern.compile('^' + pattern + '$');
            ModList.forEachModInOrder(mc -> {
                var id = mc.getModId();
                if (!"forge".equals(id) && !"minecraft".equals(id) && m.matcher(id).matches())
                    mods.add(id);
            });
        }
        LOGGER.info("Initializing Data Gatherer for mods {}", mods);

        var config = new GatherDataEvent.DataGeneratorConfig(mods, output, inputs, lookupProvider, genServer,
                genClient, genDev, genReports, flat);

        if (!mods.contains("forge")) {
            // If we aren't generating data for forge, automatically add forge as an existing so mods can access forge's data
            existingMods.add("forge");
        }

        existingFileHelper = new ExistingFileHelper(existingPacks, existingMods, assetIndex, assetsDir);

        if (ModLoader.isLoadingStateValid()) {
            var projectPaths = loadProjectPaths();

            for (var mod : ModList.getLoadedMods()) {
                var projectInfo = loadProjectInfo(mod);
                var modOutput = projectPaths.isEmpty() || projectInfo.isEmpty()
                    ? simpleOutput(output, config, mod)
                    : findOutput(projectPaths, projectInfo, output, config, mod);

                var gen = config.makeGenerator(modOutput, config.getMods().contains(mod.getModId()));
                var event = new GatherDataEvent(mod, gen, config, existingFileHelper);
                ModLoader.postEvent(mod, event);
            }

            config.runAll();
        } else {
            LOGGER.error("Cowardly refusing to send event generator to a broken mod state");
        }

        return false;
    }

    private static Map<String, String> loadProjectPaths() {
        var projectList = System.getProperty("forge.project.list");
        return projectList == null ? Map.of() : loadProps(Path.of(projectList));
    }

    private static Map<String, String> loadProjectInfo(ModContainer mod) {
        return loadProps(mod.getModInfo().getOwningFile().getFile().findResource(".project_info.properties"));
    }

    private static Map<String, String> loadProps(Path target) {
        if (!Files.exists(target))
            return Map.of();

        try (var input = Files.newInputStream(target)) {
            var reader = new InputStreamReader(input, StandardCharsets.UTF_8);
            var props = new Properties();
            props.load(reader);

            @SuppressWarnings({ "rawtypes", "unchecked" })
            var ret = (Map<String, String>)(Map)props;
            return ret;
        } catch (IOException e) {
            LOGGER.error("Failed to read project list file {}", target, e);
        }
        return Map.of();
    }

    private static Path simpleOutput(Path base, DataGeneratorConfig config, ModContainer mod) {
        return config.isFlat() ? base : base.resolve(mod.getModId());
    }

    private static Path findOutput(Map<String, String> projects, Map<String, String> info, Path original, DataGeneratorConfig config, ModContainer mod) {
        var name = info.get("name");
        var base = projects.get(name);
        var output = info.get("output");

        // We need to find both the base and the mod specific info
        if (base == null || output == null)
            return simpleOutput(original, config, mod);

        // If we can't find the output, then we're probably not in the workspace for this mod.
        var target = Path.of(base).resolve(output);
        if (!Files.exists(target))
            return simpleOutput(original, config, mod);

        if ("true".equals(info.get("flat")))
            return target;
        return target.resolve(mod.getModId());
    }
}
