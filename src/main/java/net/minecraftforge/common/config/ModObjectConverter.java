package net.minecraftforge.common.config;

import java.util.List;
import java.util.function.Supplier;

import com.electronwill.nightconfig.core.Config;

/**
 * This interface defines how a custom object is loaded and saved from config.
 */
public interface ModObjectConverter<T>
{
    /**
     * Load a custom object from a configuration.
     *
     *
     * @param subConfig
     * @param path the path to the entry
     * @param defaultSupplier the default value
     * @return a new instance of that object
     */
    T loadFromConfig(Config subConfig, List<String> path, Supplier<T> defaultSupplier);

    /**
     * Save a custom object to a configuration.
     * If there is any problem with the supplied object and default value can be retrieved via the supplier
     * @param config the {@link Config} to save to
     * @param path the path to the entry
     * @param object the object to save
     * @param defaultSupplier the default value
     */
    void saveToConfig(Config config, List<String> path, T object, Supplier<T> defaultSupplier);
}
