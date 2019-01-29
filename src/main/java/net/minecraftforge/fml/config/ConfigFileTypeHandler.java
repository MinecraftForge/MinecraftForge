package net.minecraftforge.fml.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

import static net.minecraftforge.fml.config.ConfigTracker.CONFIG;

public class ConfigFileTypeHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    static ConfigFileTypeHandler TOML = new ConfigFileTypeHandler();

    public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
        return (c) -> {
            final Path configPath = configBasePath.resolve(c.getFileName());
            final CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().
                    autosave().
                    writingMode(WritingMode.REPLACE).
                    build();
            LOGGER.debug(CONFIG, "Built TOML config for {}", configPath.toString());
            try {
                FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(c, configData));
                LOGGER.debug(CONFIG, "Watching TOML config file {} for changes", configPath.toString());
            } catch (IOException e) {
                throw new RuntimeException("Couldn't watch config file", e);
            }
            configData.load();
            LOGGER.debug(CONFIG, "Loaded TOML config file {}", configPath.toString());
            return configData;
        };
    }

    private static class ConfigWatcher implements Runnable {
        private final ModConfig modConfig;
        private final CommentedFileConfig commentedFileConfig;

        ConfigWatcher(final ModConfig modConfig, final CommentedFileConfig commentedFileConfig) {
            this.modConfig = modConfig;
            this.commentedFileConfig = commentedFileConfig;
        }

        @Override
        public void run() {
            LOGGER.debug(CONFIG, "Config file {} changed, sending notifies", this.modConfig.getFileName());
            this.modConfig.fireEvent(new ModConfig.ConfigReloading(this.modConfig));
        }
    }
}
