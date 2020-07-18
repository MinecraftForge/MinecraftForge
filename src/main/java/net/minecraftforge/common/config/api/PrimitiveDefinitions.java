package net.minecraftforge.common.config.api;

import static net.minecraftforge.common.config.AbstractConfigSpec.split;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.tileentity.TileEntityMerger.ICallbackWrapper.Double;
import net.minecraftforge.common.config.values.BooleanValue;
import net.minecraftforge.common.config.values.ConfigValue;
import net.minecraftforge.common.config.values.DoubleValue;
import net.minecraftforge.common.config.values.IntValue;
import net.minecraftforge.common.config.values.LongValue;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Generic primitive definitions for mod configs.
 * We can't use 'define' because it conflicts with night-config's ConfigSpec
 */
public interface PrimitiveDefinitions
{
    /**
     * Define any object.
     *
     * @param path the path to the value
     * @param defaultSupplier the default value
     * @param validator the validator that makes sure the given value is valid
     * @param clazz the type of the value
     * @return a new {@link ConfigValue} containing the type of the value and it's current value
     */
    <T> ConfigValue<T> defineObject(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, Class<?> clazz);

    /**
     * Define a new boolean value
     *
     * @param path the path to the value
     * @param defaultSupplier the default value
     * @return a new {@link BooleanValue}
     */
    BooleanValue defineBoolean(List<String> path, Supplier<Boolean> defaultSupplier);

    /**
     * Define a new, simple, numerical configuration entry
     * If a method does not have the Predicate, {@link #isNumber()} will be used by default.
     *
     * @param path the path to the value
     * @param defaultValue the default value
     * @return a new {@link IntValue}
     */
    IntValue defineNumber(List<String> path, int defaultValue, Predicate<Object> validator);

    /**
     * Define a new, simple, numerical configuration entry
     * If a method does not have the Predicate, {@link #isNumber()} will be used by default.
     *
     * @param path the path to the value
     * @param defaultValue the default value
     * @return a new {@link IntValue}
     */
    DoubleValue defineNumber(List<String> path, double defaultValue, Predicate<Object> validator);

    /**
     * Define a new, simple, numerical configuration entry
     * If a method does not have the Predicate, {@link #isNumber()} will be used by default.
     *
     * @param path the path to the value
     * @param defaultValue the default value
     * @return a new {@link IntValue}
     */
    LongValue defineNumber(List<String> path, long defaultValue, Predicate<Object> validator);

    /** @see #defineObject(List, Supplier, Predicate, Class) */
    default <T> ConfigValue<T> defineObject(String path, T defaultValue) {
        return defineObject(split(path), defaultValue);
    }
    /** @see #defineObject(List, Supplier, Predicate, Class) */
    default <T> ConfigValue<T> defineObject(List<String> path, T defaultValue) {
        return defineObject(path, defaultValue, o -> o != null && defaultValue.getClass().isAssignableFrom(o.getClass()));
    }
    /** @see #defineObject(List, Supplier, Predicate, Class) */
    default <T> ConfigValue<T> defineObject(String path, T defaultValue, Predicate<Object> validator) {
        return defineObject(split(path), defaultValue, validator);
    }
    /** @see #defineObject(List, Supplier, Predicate, Class) */
    default <T> ConfigValue<T> defineObject(List<String> path, T defaultValue, Predicate<Object> validator) {
        Objects.requireNonNull(defaultValue, "Default value can not be null");
        return defineObject(path, () -> defaultValue, validator);
    }
    /** @see #defineObject(List, Supplier, Predicate, Class) */
    default <T> ConfigValue<T> defineObject(String path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
        return defineObject(split(path), defaultSupplier, validator);
    }
    /** @see #defineObject(List, Supplier, Predicate, Class) */
    default <T> ConfigValue<T> defineObject(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
        return defineObject(path, defaultSupplier, validator, Object.class);
    }

    // Boolean
    /** @see #defineBoolean(List, Supplier) */
    default BooleanValue defineBoolean(String path, boolean defaultValue) {
        return defineBoolean(split(path), defaultValue);
    }
    /** @see #defineBoolean(List, Supplier) */
    default BooleanValue defineBoolean(List<String> path, boolean defaultValue) {
        return defineBoolean(path, () -> defaultValue);
    }
    /** @see #defineBoolean(List, Supplier) */
    default BooleanValue defineBoolean(String path, Supplier<Boolean> defaultSupplier) {
        return defineBoolean(split(path), defaultSupplier);
    }

    // Int
    /** @see #defineNumber(List, int, Predicate) */
    default IntValue defineNumber(String path, int value) {
        return defineNumber(split(path), value, isNumber());
    }
    /** @see #defineNumber(List, int, Predicate) */
    default IntValue defineNumber(String path, int value, Predicate<Object> validator) {
        return defineNumber(split(path), value,validator);
    }
    // Double
    /** @see #defineNumber(List, double, Predicate) */
    default DoubleValue defineNumber(String path, double value) {
        return defineNumber(split(path), value, isNumber());
    }
    /** @see #defineNumber(List, double, Predicate) */
    default DoubleValue defineNumber(String path, double value, Predicate<Object> validator) {
        return defineNumber(split(path), value,validator);
    }
    // Long
    /** @see #defineNumber(List, long, Predicate) */
    default LongValue defineNumber(String path, long value) {
        return defineNumber(split(path), value, isNumber());
    }
    /** @see #defineNumber(List, long, Predicate) */
    default LongValue defineNumber(String path, long value, Predicate<Object> validator) {
        return defineNumber(split(path), value,validator);
    }

    static Predicate<Object> isNumber() {
        return o -> {
            return o instanceof Number || (o instanceof String ? NumberUtils.isCreatable((String) o) : (o != null && Number.class.isAssignableFrom(o.getClass())));
        };
    }
}
