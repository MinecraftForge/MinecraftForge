/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import net.minecraftsingularity.fml.loading.FMLEnvironment;
import net.minecraftsingularity.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigTracker {
    private ConfigTracker() {}

    private static final Logger LOGGER = LogUtils.getLogger();
    static final Marker CONFIG = MarkerFactory.getMarker("CONFIG");
    private static final ConcurrentHashMap<String, ModConfig> FILE_MAP = new ConcurrentHashMap<>();
    private static final Map<ModConfig.Type, Set<ModConfig>> CONFIG_SETS = Map.of(
            ModConfig.Type.CLIENT, Collections.synchronizedSet(new LinkedHashSet<>()),
            ModConfig.Type.COMMON, Collections.synchronizedSet(new LinkedHashSet<>()),
            ModConfig.Type.SERVER, Collections.synchronizedSet(new LinkedHashSet<>())
    );
    private static final ConcurrentHashMap<String, Map<ModConfig.Type, ModConfig>> CONFIGS_BY_MOD = new ConcurrentHashMap<>();

    static void trackConfig(final ModConfig config) {
        if (FILE_MAP.containsKey(config.getFileName())) {
            LOGGER.error(CONFIG,"Detected config file conflict {} between {} and {}", config.getFileName(), FILE_MAP.get(config.getFileName()).getModId(), config.getModId());
            throw new RuntimeException("Config conflict detected!");
        }
        FILE_MAP.put(config.getFileName(), config);
        CONFIG_SETS.get(config.getType()).add(config);
        CONFIGS_BY_MOD.computeIfAbsent(config.getModId(), _ -> new EnumMap<>(ModConfig.Type.class)).put(config.getType(), config);
        LOGGER.debug(CONFIG, "Config file {} for {} tracking", config.getFileName(), config.getModId());
    }

    public static void loadConfigs(ModConfig.Type type, Path configBasePath) {
        LOGGER.debug(CONFIG, "Loading configs type {}", type);
        for (ModConfig config : CONFIG_SETS.get(type)) {
            openConfig(config, configBasePath);
        }
    }

    // TODO: [FML] This is only called for the server (outside of forceUnload)
    // rethink config implementation for eventual FML rewrite
    public static void unloadConfigs(ModConfig.Type type, Path configBasePath) {
        LOGGER.debug(CONFIG, "Unloading configs type {}", type);
        for (ModConfig config : CONFIG_SETS.get(type)) {
            closeConfig(config, configBasePath);
        }
        ConfigFileTypeHandler.get(type).stopWatcher();
    }

    // If there is a better way to do this, please tell me. Because this is FUCKED
    public static void forceUnload() {
        // This is how ModStateProvider handles loading configs. So we're doing the same but with unloadConfigs instead
        if (FMLEnvironment.dist.isClient())
            unloadConfigs(ModConfig.Type.CLIENT, FMLPaths.CONFIGDIR.get());

        unloadConfigs(ModConfig.Type.COMMON, FMLPaths.CONFIGDIR.get());

        // just in case server watcher is still alive somehow...
        ConfigFileTypeHandler.get(ModConfig.Type.SERVER).stopWatcher();
    }

    private static void openConfig(final ModConfig config, final Path configBasePath) {
        LOGGER.trace(CONFIG, "Loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
        final CommentedFileConfig configData = config.getHandler().reader(configBasePath).apply(config);
        config.setConfigData(configData);
        config.fireEvent(IConfigEvent.loading(config));
        config.save();
    }

    private static void closeConfig(final ModConfig config, final Path configBasePath) {
        if (config.getConfigData() != null) {
            LOGGER.trace(CONFIG, "Closing config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
            // stop the filewatcher before we save the file and close it, so reload doesn't fire
            config.getHandler().unload(configBasePath, config);
            var unloading = IConfigEvent.unloading(config);
            if (unloading != null)
                config.fireEvent(unloading);
            config.save();
            config.setConfigData(null);
        }
    }

    public static void loadDefaultServerConfigs() {
        CONFIG_SETS.get(ModConfig.Type.SERVER).forEach(modConfig -> {
            final CommentedConfig commentedConfig = CommentedConfig.inMemory();
            modConfig.getSpec().correct(commentedConfig);
            modConfig.setConfigData(commentedConfig);
            modConfig.fireEvent(IConfigEvent.loading(modConfig));
        });
    }

    public static String getConfigFileName(String modId, ModConfig.Type type) {
        return Optional.ofNullable(CONFIGS_BY_MOD.getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null)).
                map(ModConfig::getFullPath).map(Object::toString).orElse(null);
    }

    public static Map<ModConfig.Type, Set<ModConfig>> configSets() {
        return CONFIG_SETS;
    }

    public static ConcurrentHashMap<String, ModConfig> fileMap() {
        return FILE_MAP;
    }
}
