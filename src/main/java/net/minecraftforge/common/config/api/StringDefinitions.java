package net.minecraftforge.common.config.api;

import static net.minecraftforge.common.config.AbstractConfigSpec.split;

import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.config.values.StringValue;

public interface StringDefinitions
{
    /**
     * Define a new String configuration entry.
     *
     * @param path the path to the configuration entry
     * @param defaultSupplier the default value
     * @return a new {@link StringValue}
     */
    StringValue defineString(List<String>path, Supplier<String> defaultSupplier);

    /** @see #defineString(List, Supplier) */
    default StringValue defineString(String path, String value) {
        return defineString(split(path), () -> value);
    }
    /** @see #defineString(List, Supplier) */
    default StringValue defineString(List<String> path, String defaultValue) {
        return defineString(path, () -> defaultValue);
    }
    /** @see #defineString(List, Supplier) */
    default StringValue defineString(String path, Supplier<String> defaultSupplier) {
        return defineString(split(path), defaultSupplier);
    }
}
