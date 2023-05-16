/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    static final Marker CONFIG = MarkerFactory.getMarker("CONFIG");
    public static final ConfigTracker INSTANCE = new ConfigTracker();
    private final ConcurrentHashMap<String, ModConfig> fileMap;
    private final EnumMap<ModConfig.Type, Set<ModConfig>> configSets;
    private final ConcurrentHashMap<String, Map<ModConfig.Type, ModConfig>> configsByMod;

    private ConfigTracker() {
        this.fileMap = new ConcurrentHashMap<>();
        this.configSets = new EnumMap<>(ModConfig.Type.class);
        this.configsByMod = new ConcurrentHashMap<>();
        this.configSets.put(ModConfig.Type.CLIENT, Collections.synchronizedSet(new LinkedHashSet<>()));
        this.configSets.put(ModConfig.Type.COMMON, Collections.synchronizedSet(new LinkedHashSet<>()));
//        this.configSets.put(ModConfig.Type.PLAYER, new ConcurrentSkipListSet<>());
        this.configSets.put(ModConfig.Type.SERVER, Collections.synchronizedSet(new LinkedHashSet<>()));
    }

    void trackConfig(final ModConfig config) {
        if (this.fileMap.containsKey(config.getFileName())) {
            LOGGER.error(CONFIG,"Detected config file conflict {} between {} and {}", config.getFileName(), this.fileMap.get(config.getFileName()).getModId(), config.getModId());
            throw new RuntimeException("Config conflict detected!");
        }
        this.fileMap.put(config.getFileName(), config);
        this.configSets.get(config.getType()).add(config);
        this.configsByMod.computeIfAbsent(config.getModId(), (k)->new EnumMap<>(ModConfig.Type.class)).put(config.getType(), config);
        LOGGER.debug(CONFIG, "Config file {} for {} tracking", config.getFileName(), config.getModId());
    }

    public void loadConfigs(ModConfig.Type type, Path configBasePath) {
        LOGGER.debug(CONFIG, "Loading configs type {}", type);
        this.configSets.get(type).forEach(config -> openConfig(config, configBasePath));
    }

    public void unloadConfigs(ModConfig.Type type, Path configBasePath) {
        LOGGER.debug(CONFIG, "Unloading configs type {}", type);
        this.configSets.get(type).forEach(config -> closeConfig(config, configBasePath));
    }

    private void openConfig(final ModConfig config, final Path configBasePath) {
        LOGGER.trace(CONFIG, "Loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
        final CommentedFileConfig configData = config.getHandler().reader(configBasePath).apply(config);
        config.setConfigData(configData);
        config.fireEvent(IConfigEvent.loading(config));
        config.save();
    }

    private void closeConfig(final ModConfig config, final Path configBasePath) {
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

    public void loadDefaultServerConfigs() {
        configSets.get(ModConfig.Type.SERVER).forEach(modConfig -> {
            final CommentedConfig commentedConfig = CommentedConfig.inMemory();
            modConfig.getSpec().correct(commentedConfig);
            modConfig.setConfigData(commentedConfig);
            modConfig.fireEvent(IConfigEvent.loading(modConfig));
        });
    }

    public String getConfigFileName(String modId, ModConfig.Type type) {
        return Optional.ofNullable(configsByMod.getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null)).
                map(ModConfig::getFullPath).map(Object::toString).orElse(null);
    }

    public Map<ModConfig.Type, Set<ModConfig>> configSets() {
        return configSets;
    }

    public ConcurrentHashMap<String, ModConfig> fileMap() {
        return fileMap;
    }
}
