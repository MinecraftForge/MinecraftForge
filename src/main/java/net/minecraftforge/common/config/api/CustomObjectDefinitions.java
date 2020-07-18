package net.minecraftforge.common.config.api;

import static net.minecraftforge.common.config.AbstractConfigSpec.split;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraftforge.common.config.ModObjectConverter;
import net.minecraftforge.common.config.values.ModValue;

/**
 * This defines how custom objects are saved to and from configs.
 */
public interface CustomObjectDefinitions
{
    /**
     * Define a custom mod object to be saved to the config.
     * By default, {@link Objects#nonNull(Object)} is used for the Predicate.
     *
     * @param path the path to the configuration entry
     * @param defaultSupplier the default value for this entry
     * @param validator the {@link Predicate} which will determine if the given configuration entry is valid
     * @param converter the {@link ModObjectConverter} which will transform the entry between TOML and the mod's object instance
     * @return a new {@link ModValue}
     */
    <T> ModValue<T> defineModObject(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, ModObjectConverter<T> converter);

    /** @see #defineModObject(List, Supplier, Predicate, ModObjectConverter) */
    default <T> ModValue<T> defineModObject(String path, T object, ModObjectConverter<T> converter) {
        return defineModObject(split(path), () -> object, Objects::nonNull, converter);
    }
    /** @see #defineModObject(List, Supplier, Predicate, ModObjectConverter) */
    default <T> ModValue<T> defineModObject(String path, T object, Predicate<Object> validator, ModObjectConverter<T> converter) {
        return defineModObject(split(path), () -> object, validator, converter);
    }
}
