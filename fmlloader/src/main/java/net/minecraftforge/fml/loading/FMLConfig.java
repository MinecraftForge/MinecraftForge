/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static net.minecraftforge.fml.loading.LogMarkers.CORE;

public class FMLConfig
{
    public enum ConfigValue {
        EARLY_WINDOW_CONTROL("earlyWindowControl", Boolean.TRUE, "Should we control the window. Disabling this disables new GL features and can be bad for mods that rely on them."),
        MAX_THREADS("maxThreads", -1, "Max threads for early initialization parallelism,  -1 is based on processor count", FMLConfig::maxThreads),
        VERSION_CHECK("versionCheck", Boolean.TRUE, "Enable forge global version checking"),
        DEFAULT_CONFIG_PATH("defaultConfigPath", "defaultconfigs", "Default config path for servers"),
        DISABLE_OPTIMIZED_DFU("disableOptimizedDFU", Boolean.TRUE, "Disables Optimized DFU client-side - already disabled on servers"),
        EARLY_WINDOW_PROVIDER("earlyWindowProvider", "fmlearlywindow", "Early window provider"),
        EARLY_WINDOW_WIDTH("earlyWindowWidth", 854, "Early window width"),
        EARLY_WINDOW_HEIGHT("earlyWindowHeight", 480, "Early window height"),
        EARLY_WINDOW_FBSCALE("earlyWindowFBScale", 1, "Early window framebuffer scale"),
        EARLY_WINDOW_MAXIMIZED("earlyWindowMaximized", Boolean.FALSE, "Early window starts maximized"),
        EARLY_WINDOW_SKIP_GL_VERSIONS("earlyWindowSkipGLVersions", List.of(), "Skip specific GL versions, may help with buggy graphics card drivers"),
        EARLY_WINDOW_SQUIR("earlyWindowSquir", Boolean.FALSE, "Squir?")
        ;

        private final String entry;
        private final Object defaultValue;
        private final String comment;
        private final Class<?> valueType;
        private final Function<Object, Object> entryFunction;

        ConfigValue(final String entry, final Object defaultValue, final String comment) {
            this(entry, defaultValue, comment, Function.identity());
        }
        ConfigValue(final String entry, final Object defaultValue, final String comment, Function<Object, Object> entryFunction) {
            this.entry = entry;
            this.defaultValue = defaultValue;
            this.comment = comment;
            this.valueType = defaultValue.getClass();
            this.entryFunction = entryFunction;
        }

        void buildConfigEntry(ConfigSpec spec, CommentedConfig commentedConfig) {
            if (this.defaultValue instanceof List<?> list) {
                spec.defineList(this.entry, list, e -> e instanceof String);
            } else {
                spec.define(this.entry, this.defaultValue);
            }
            commentedConfig.add(this.entry, this.defaultValue);
            commentedConfig.setComment(this.entry, this.comment);
        }
        @SuppressWarnings("unchecked")
        private <T> T getConfigValue(CommentedFileConfig config) {
            return (T) this.entryFunction.apply(config.get(this.entry));
        }

        public <T> void updateValue(final CommentedFileConfig configData, final T value) {
            configData.set(this.entry, value);
        }
    }

    private static Object maxThreads(final Object value) {
        int val = (Integer)value;
        if (val <= 0) return Runtime.getRuntime().availableProcessors();
        else return val;
    }

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FMLConfig INSTANCE = new FMLConfig();
    private static final ConfigSpec configSpec = new ConfigSpec();
    private static final CommentedConfig configComments = CommentedConfig.inMemory();
    static {
        for (ConfigValue cv: ConfigValue.values()) {
            cv.buildConfigEntry(configSpec, configComments);
        }
    }

    private CommentedFileConfig configData;

    private void loadFrom(final Path configFile)
    {
        configData = CommentedFileConfig.builder(configFile).sync()
                .onFileNotFound(FileNotFoundAction.copyData(Objects.requireNonNull(getClass().getResourceAsStream("/META-INF/defaultfmlconfig.toml"))))
                .writingMode(WritingMode.REPLACE)
                .build();
        try
        {
            configData.load();
        }
        catch (ParsingException e)
        {
            throw new RuntimeException("Failed to load FML config from " + configFile, e);
        }
        if (!configSpec.isCorrect(configData)) {
            LOGGER.warn(CORE, "Configuration file {} is not correct. Correcting", configFile);
            configSpec.correct(configData, (action, path, incorrectValue, correctedValue) ->
                    LOGGER.info(CORE, "Incorrect key {} was corrected from {} to {}", path, incorrectValue, correctedValue));
        }
        configData.putAllComments(configComments);
        configData.save();
    }

    public static void load()
    {
        final Path configFile = FMLPaths.FMLCONFIG.get();
        INSTANCE.loadFrom(configFile);
        if (LOGGER.isTraceEnabled(CORE))
        {
            LOGGER.trace(CORE, "Loaded FML config from {}", FMLPaths.FMLCONFIG.get());
            for (ConfigValue cv: ConfigValue.values()) {
                LOGGER.trace(CORE, "FMLConfig {} is {}", cv.entry, cv.getConfigValue(INSTANCE.configData));
            }
        }
        FMLPaths.getOrCreateGameRelativePath(Paths.get(FMLConfig.getConfigValue(ConfigValue.DEFAULT_CONFIG_PATH)));
    }

    public static String getConfigValue(ConfigValue v) {
        return v.getConfigValue(INSTANCE.configData);
    }

    public static boolean getBoolConfigValue(ConfigValue v) {
        return v.getConfigValue(INSTANCE.configData);
    }

    public static int getIntConfigValue(ConfigValue v) {
        return v.getConfigValue(INSTANCE.configData);
    }

    public static <A> List<A> getListConfigValue(ConfigValue v) {
        return v.getConfigValue(INSTANCE.configData);
    }
    public static <T> void updateConfig(ConfigValue v, T value) {
        v.updateValue(INSTANCE.configData, value);
        INSTANCE.configData.save();
    }

    public static String defaultConfigPath() {
        return getConfigValue(ConfigValue.DEFAULT_CONFIG_PATH);
    }
}
