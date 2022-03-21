/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class FMLConfig
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static FMLConfig INSTANCE = new FMLConfig();
    private static ConfigSpec configSpec = new ConfigSpec();
    static {
        configSpec.define("splashscreen", Boolean.TRUE);
        configSpec.define("maxThreads", -1);
        configSpec.define("versionCheck", Boolean.TRUE);
        configSpec.define("defaultConfigPath",  "defaultconfigs");
    }

    private CommentedFileConfig configData;

    private void loadFrom(final Path configFile)
    {
        configData = CommentedFileConfig.builder(configFile).sync()
                .onFileNotFound(FileNotFoundAction.copyData(Objects.requireNonNull(getClass().getResourceAsStream("/META-INF/defaultfmlconfig.toml"))))
                .autosave().autoreload()
                .writingMode(WritingMode.REPLACE)
                .build();
        try
        {
            configData.load();
        }
        catch (ParsingException e)
        {
            throw new RuntimeException("Failed to load FML config from " + configFile.toString(), e);
        }
        if (!configSpec.isCorrect(configData)) {
            LOGGER.warn(CORE, "Configuration file {} is not correct. Correcting", configFile);
            configSpec.correct(configData, (action, path, incorrectValue, correctedValue) ->
                    LOGGER.warn(CORE, "Incorrect key {} was corrected from {} to {}", path, incorrectValue, correctedValue));
        }
        configData.save();
    }

    public static void load()
    {
        final Path configFile = FMLPaths.FMLCONFIG.get();
        INSTANCE.loadFrom(configFile);
        LOGGER.trace(CORE, "Loaded FML config from {}", FMLPaths.FMLCONFIG.get());
        LOGGER.trace(CORE, "Splash screen is {}", FMLConfig::splashScreenEnabled);
        LOGGER.trace(CORE, "Max threads for mod loading computed at {}", FMLConfig::loadingThreadCount);
        LOGGER.trace(CORE, "Version check is {}", FMLConfig::runVersionCheck);
        LOGGER.trace(CORE, "Default config paths at {}", FMLConfig::defaultConfigPath);
        FMLPaths.getOrCreateGameRelativePath(Paths.get(FMLConfig.defaultConfigPath()), "default config directory");
    }

    public static boolean splashScreenEnabled() {
        return INSTANCE.configData.<Boolean>getOptional("splashscreen").orElse(Boolean.FALSE);
    }

    public static int loadingThreadCount() {
        int val = INSTANCE.configData.<Integer>getOptional("maxThreads").orElse(-1);
        if (val <= 0) return Runtime.getRuntime().availableProcessors();
        return val;
    }

    public static boolean runVersionCheck() {
        return INSTANCE.configData.<Boolean>getOptional("versionCheck").orElse(Boolean.TRUE);
    }

    public static String defaultConfigPath() {
        return INSTANCE.configData.<String>getOptional("defaultConfigPath").orElse("defaultconfigs");
    }
}
