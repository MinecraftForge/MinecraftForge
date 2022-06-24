/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.config;

import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static net.minecraftforge.fml.config.ConfigTracker.CONFIG;

public class ConfigFileTypeHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    static ConfigFileTypeHandler TOML = new ConfigFileTypeHandler();
    private static final Path defaultConfigPath = FMLPaths.GAMEDIR.get().resolve(FMLConfig.getConfigValue(FMLConfig.ConfigValue.DEFAULT_CONFIG_PATH));

    public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
        return (c) -> {
            final Path configPath = configBasePath.resolve(c.getFileName());
            final CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().
                    preserveInsertionOrder().
                    autosave().
                    onFileNotFound((newfile, configFormat)-> setupConfigFile(c, newfile, configFormat)).
                    writingMode(WritingMode.REPLACE).
                    build();
            LOGGER.debug(CONFIG, "Built TOML config for {}", configPath.toString());
            try
            {
                configData.load();
            }
            catch (ParsingException ex)
            {
                throw new ConfigLoadingException(c, ex);
            }
            LOGGER.debug(CONFIG, "Loaded TOML config file {}", configPath.toString());
            try {
                FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(c, configData, Thread.currentThread().getContextClassLoader()));
                LOGGER.debug(CONFIG, "Watching TOML config file {} for changes", configPath.toString());
            } catch (IOException e) {
                throw new RuntimeException("Couldn't watch config file", e);
            }
            return configData;
        };
    }

    public void unload(Path configBasePath, ModConfig config) {
        Path configPath = configBasePath.resolve(config.getFileName());
        try {
            FileWatcher.defaultInstance().removeWatch(configBasePath.resolve(config.getFileName()));
        } catch (RuntimeException e) {
            LOGGER.error("Failed to remove config {} from tracker!", configPath.toString(), e);
        }
    }

    private boolean setupConfigFile(final ModConfig modConfig, final Path file, final ConfigFormat<?> conf) throws IOException {
        Files.createDirectories(file.getParent());
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

    private static class ConfigWatcher implements Runnable {
        private final ModConfig modConfig;
        private final CommentedFileConfig commentedFileConfig;
        private final ClassLoader realClassLoader;

        ConfigWatcher(final ModConfig modConfig, final CommentedFileConfig commentedFileConfig, final ClassLoader classLoader) {
            this.modConfig = modConfig;
            this.commentedFileConfig = commentedFileConfig;
            this.realClassLoader = classLoader;
        }

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
