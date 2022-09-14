package net.minecraftforge.config.boot;

import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ConfigLoadingException;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Central configuration manager for forge boot operation.
 * Some systems within forge have inpact on the boot process, and need to be configured before the
 * mod configurations are loaded.
 * <p>
 * These configurations are not synced to the client, and are not available to mods.
 */
@ApiStatus.Internal
public final class ForgeBootConfigurationManager {
    private static final ForgeBootConfigurationManager INSTANCE = new ForgeBootConfigurationManager();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Marker CONFIG = MarkerFactory.getMarker("BOOT-CONFIG");

    public static ForgeBootConfigurationManager getInstance() {
        return INSTANCE;
    }

    private final ForgeConfigSpec specification;
    private final ForgeBootConfig configuration;

    private ForgeBootConfigurationManager() {
        final Pair<ForgeBootConfig, ForgeConfigSpec> buildConfiguration = new ForgeConfigSpec.Builder().configure(ForgeBootConfig::new);
        this.configuration = buildConfiguration.getLeft();
        this.specification = buildConfiguration.getRight();
    }

    public void init() {
        LOGGER.trace(CONFIG, "Loading boot configuration");
        final CommentedFileConfig configData = buildConfigurationData("forge-boot.toml", specification);
        specification.acceptConfig(configData);
        specification.save();
    }

    public ForgeBootConfig getConfiguration() {
        return configuration;
    }

    private static CommentedFileConfig buildConfigurationData(final String configName, final ForgeConfigSpec spec) {
        final Path configPath = FMLPaths.CONFIGDIR.get().resolve(configName);
        final CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().
                preserveInsertionOrder().
                autosave().
                onFileNotFound((newfile, configFormat)-> setupConfigFile(configName, newfile, configFormat)).
                writingMode(WritingMode.REPLACE).
                build();

        LOGGER.debug(CONFIG, "Built TOML config for {}", configPath);
        try
        {
            configData.load();
        }
        catch (ParsingException ex)
        {
            throw new ConfigLoadingException(configName, "boot", "forge", ex);
        }

        LOGGER.debug(CONFIG, "Loaded TOML config file {}", configPath);
        try {
            FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(spec, configName, configData, Thread.currentThread().getContextClassLoader()));
            LOGGER.debug(CONFIG, "Watching TOML config file {} for changes", configPath);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't watch config file", e);
        }
        return configData;
    }

    private static boolean setupConfigFile(final String configName, final Path file, final ConfigFormat<?> conf) throws IOException {
        Files.createDirectories(file.getParent());
        Path p = ConfigFileTypeHandler.DEFAULT_CONFIG_PATH.resolve(configName);
        if (Files.exists(p)) {
            LOGGER.info(CONFIG, "Loading default boot config file from path {}", p);
            Files.copy(p, file);
        } else {
            Files.createFile(file);
            conf.initEmptyFile(file);
        }
        return true;
    }

    private static class ConfigWatcher implements Runnable {
        private final ForgeConfigSpec spec;
        private final String configName;
        private final CommentedFileConfig commentedFileConfig;
        private final ClassLoader realClassLoader;

        ConfigWatcher(final ForgeConfigSpec spec, String configName, final CommentedFileConfig commentedFileConfig, final ClassLoader classLoader) {
            this.spec = spec;
            this.configName = configName;
            this.commentedFileConfig = commentedFileConfig;
            this.realClassLoader = classLoader;
        }

        @Override
        public void run() {
            // Force the regular classloader onto the special thread
            Thread.currentThread().setContextClassLoader(realClassLoader);
            if (!spec.isCorrecting()) {
                try
                {
                    this.commentedFileConfig.load();
                    if(!spec.isCorrect(commentedFileConfig))
                    {
                        LOGGER.warn(CONFIG, "Configuration file {} is not correct. Correcting", commentedFileConfig.getFile().getAbsolutePath());
                        ConfigFileTypeHandler.backUpConfig(commentedFileConfig);
                        spec.correct(commentedFileConfig);
                        commentedFileConfig.save();
                    }
                }
                catch (ParsingException ex)
                {
                    throw new ConfigLoadingException(configName, "BOOT", "Forge", ex);
                }
                LOGGER.debug(CONFIG, "Config file {} changed, sending notifies", configName);
                spec.afterReload();
            }
        }
    }

}
