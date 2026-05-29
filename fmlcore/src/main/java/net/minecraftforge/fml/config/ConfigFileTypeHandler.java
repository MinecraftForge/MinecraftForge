/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.fml.config;

import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.mojang.logging.LogUtils;
import net.minecraftsingularity.fml.loading.FMLConfig;
import net.minecraftsingularity.fml.loading.FMLPaths;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static net.minecraftsingularity.fml.config.ConfigTracker.CONFIG;

public class ConfigFileTypeHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path defaultConfigPath = FMLPaths.GAMEDIR.get().resolve(FMLConfig.getConfigValue(FMLConfig.ConfigValue.DEFAULT_CONFIG_PATH));

    private static final ConfigFileTypeHandler CLIENT = new ConfigFileTypeHandler(ModConfig.Type.CLIENT);
    private static final ConfigFileTypeHandler COMMON = new ConfigFileTypeHandler(ModConfig.Type.COMMON);
    private static final ConfigFileTypeHandler SERVER = new ConfigFileTypeHandler(ModConfig.Type.SERVER);

    private final @Nullable ModConfig.Type type;
    private @Nullable FileWatcher watcher;

    // exists for bin compat
    public ConfigFileTypeHandler() {
        this(null);
    }

    private ConfigFileTypeHandler(@Nullable ModConfig.Type type) {
        this.type = type;
    }

    /**
     * Gets the handler for the given {@link ModConfig.Type}.
     *
     * @param type The type to get the handler for
     * @return The handler
     */
    static ConfigFileTypeHandler get(ModConfig.Type type) {
        return switch (type) {
            case CLIENT -> CLIENT;
            case COMMON -> COMMON;
            case SERVER -> SERVER;
        };
    }

    /**
     * Gets the {@link FileWatcher} for this handler, creating it if it doesn't exist and/or has been stopped.
     *
     * @return The watcher
     *
     * @apiNote This is package-private so modders can't just call {@link FileWatcher#stop()} and fuck up everything.
     */
    FileWatcher getWatcher() {
        if (this.watcher == null) {
            LOGGER.debug(CONFIG, "Starting watcher for handler: {}", this);
            this.watcher = new FileWatcher();
        }

        return this.watcher;
    }

    /**
     * Stops the {@link FileWatcher} for this handler, and sets it to null afterward. Use this instead of
     * {@link FileWatcher#stop()}.
     */
    void stopWatcher() {
        if (this.watcher == null) return;

        LOGGER.debug(CONFIG, "Stopping watcher for hander: {}", this);
        this.watcher.stop();
        this.watcher = null;
    }

    public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
        return (c) -> {
            final Path configPath = configBasePath.resolve(c.getFileName());
            final CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().
                    preserveInsertionOrder().
                    autosave().
                    onFileNotFound((newfile, configFormat)-> setupConfigFile(c, newfile, configFormat)).
                    writingMode(WritingMode.REPLACE).
                    build();
            LOGGER.debug(CONFIG, "Built TOML config for {}", configPath);
            try
            {
                configData.load();
            }
            catch (ParsingException ex)
            {
                throw new ConfigLoadingException(c, ex);
            }
            LOGGER.debug(CONFIG, "Loaded TOML config file {}", configPath);
            this.getWatcher().addWatch(configPath, new ConfigWatcher(c, configData, Thread.currentThread().getContextClassLoader()));
            LOGGER.debug(CONFIG, "Watching TOML config file {} for changes", configPath);
            return configData;
        };
    }

    public void unload(Path configBasePath, ModConfig config) {
        Path configPath = configBasePath.resolve(config.getFileName());
        try {
            this.getWatcher().removeWatch(configPath);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to remove config {} from tracker!", configPath, e);
        }
    }

    private static boolean setupConfigFile(final ModConfig modConfig, final Path file, final ConfigFormat<?> conf) throws IOException {
        if (!Files.isDirectory(file.getParent())) {
            Files.createDirectories(file.getParent());
        }
        Path p = defaultConfigPath.resolve(modConfig.getFileName());
        if (Files.exists(p)) {
            LOGGER.info(CONFIG, "Loading default config file from path {}", p);
            Files.copy(p, file);
        } else {
            Files.createFile(file);
            conf.initEmptyFile(file);
        }
        return true;
    }

    public static void backUpConfig(final CommentedFileConfig commentedFileConfig)
    {
        backUpConfig(commentedFileConfig, 5); //TODO: Think of a way for mods to set their own preference (include a sanity check as well, no disk stuffing)
    }

    public static void backUpConfig(final CommentedFileConfig commentedFileConfig, final int maxBackups)
    {
        Path bakFileLocation = commentedFileConfig.getNioPath().getParent();
        String bakFileName = FilenameUtils.removeExtension(commentedFileConfig.getFile().getName());
        String bakFileExtension = FilenameUtils.getExtension(commentedFileConfig.getFile().getName()) + ".bak";
        Path bakFile = bakFileLocation.resolve(bakFileName + "-1" + "." + bakFileExtension);
        try
        {
            for(int i = maxBackups; i > 0; i--)
            {
                Path oldBak = bakFileLocation.resolve(bakFileName + "-" + i + "." + bakFileExtension);
                if(Files.exists(oldBak))
                {
                    if(i >= maxBackups)
                        Files.delete(oldBak);
                    else
                        Files.move(oldBak, bakFileLocation.resolve(bakFileName + "-" + (i + 1) + "." + bakFileExtension));
                }
            }
            Files.copy(commentedFileConfig.getNioPath(), bakFile);
        }
        catch (IOException exception)
        {
            LOGGER.warn(CONFIG, "Failed to back up config file {}", commentedFileConfig.getNioPath(), exception);
        }
    }

    @Override
    public String toString() {
        return "ConfigFileTypeHandler[" + (type != null ? type : "UNKNOWN") + "]";
    }

    private record ConfigWatcher(
            ModConfig modConfig,
            CommentedFileConfig commentedFileConfig,
            ClassLoader realClassLoader
    ) implements Runnable {
        @Override
        public void run() {
            // Force the regular classloader onto the special thread
            Thread.currentThread().setContextClassLoader(realClassLoader);
            if (!this.modConfig.getSpec().isCorrecting()) {
                try
                {
                    this.commentedFileConfig.load();
                    if(!this.modConfig.getSpec().isCorrect(commentedFileConfig))
                    {
                        LOGGER.warn(CONFIG, "Configuration file {} is not correct. Correcting", commentedFileConfig.getFile().getAbsolutePath());
                        ConfigFileTypeHandler.backUpConfig(commentedFileConfig);
                        this.modConfig.getSpec().correct(commentedFileConfig);
                        commentedFileConfig.save();
                    }
                }
                catch (ParsingException ex)
                {
                    throw new ConfigLoadingException(modConfig, ex);
                }
                LOGGER.debug(CONFIG, "Config file {} changed, sending notifies", this.modConfig.getFileName());
                this.modConfig.getSpec().afterReload();
                this.modConfig.fireEvent(IConfigEvent.reloading(this.modConfig));
            }
        }
    }

    private static class ConfigLoadingException extends RuntimeException
    {
        public ConfigLoadingException(ModConfig config, Exception cause)
        {
            super("Failed loading config file " + config.getFileName() + " of type " + config.getType() + " for modid " + config.getModId(), cause);
        }
    }
}
