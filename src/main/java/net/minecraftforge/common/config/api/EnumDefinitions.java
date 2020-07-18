package net.minecraftforge.common.config.api;

import static net.minecraftforge.common.config.AbstractConfigSpec.split;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraftforge.common.config.values.EnumValue;

import com.electronwill.nightconfig.core.EnumGetMethod;

public interface EnumDefinitions
{
    /**
     * Define a new enum value.
     * For methods that do not contain {@link EnumGetMethod} as a parameter, it will default to {@link EnumGetMethod#NAME_IGNORECASE}
     *
     * @param path the path to the value
     * @param defaultSupplier the default value
     * @param converter how the config will convert from a string to the correct enum. See {@link EnumGetMethod}
     * @param validator the validator to make sure the incoming object is valid
     * @param clazz the type of the value
     * @return a new {@link EnumValue}
     */
    <V extends Enum<V>> EnumValue<V> defineEnumValue(List<String> path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz);

    /** @see #defineEnumValue(List, Supplier, EnumGetMethod, Predicate, Class) */
    default  <V extends Enum<V>> EnumValue<V> defineEnumValue(String path, V defaultValue) {
        return defineEnumValue(split(path), defaultValue);
    }
    /** @see #defineEnumValue(List, Supplier, EnumGetMethod, Predicate, Class) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(String path, V defaultValue, EnumGetMethod converter) {
        return defineEnumValue(split(path), defaultValue, converter);
    }
    /** @see #defineEnumValue(List, Supplier, EnumGetMethod, Predicate, Class) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(List<String> path, V defaultValue) {
        return defineEnumValue(path, defaultValue, defaultValue.getDeclaringClass().getEnumConstants());
    }
    /** @see #defineEnumValue(List, Supplier, EnumGetMethod, Predicate, Class) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(List<String> path, V defaultValue, EnumGetMethod converter) {
        return defineEnumValue(path, defaultValue, converter, defaultValue.getDeclaringClass().getEnumConstants());
    }
    /** @see #defineEnumValue(List, Supplier, EnumGetMethod, Predicate, Class) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(String path, V defaultValue, Predicate<Object> validator) {
        return defineEnumValue(split(path), defaultValue, validator);
    }
    /** @see #defineEnumValue(List, Supplier, EnumGetMethod, Predicate, Class) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(String path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
        return defineEnumValue(split(path), defaultValue, converter, validator);
    }
    /** @see #defineEnumValue(List, Supplier, EnumGetMethod, Predicate, Class) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(List<String> path, V defaultValue, Predicate<Object> validator) {
        return defineEnumValue(path, () -> defaultValue, validator, defaultValue.getDeclaringClass());
    }
    /** @see #defineEnumValue(List, Supplier, EnumGetMethod, Predicate, Class) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(List<String> path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
        return defineEnumValue(path, () -> defaultValue, converter, validator, defaultValue.getDeclaringClass());
    }
    /** @see #defineEnumValue(List, Supplier, EnumGetMethod, Predicate, Class) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(String path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
        return defineEnumValue(split(path), defaultSupplier, validator, clazz);
    }
    /** @see #defineEnumValue(List, Supplier, EnumGetMethod, Predicate, Class) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(String path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
        return defineEnumValue(split(path), defaultSupplier, converter, validator, clazz);
    }
    /** @see #defineEnumValue(List, Supplier, EnumGetMethod, Predicate, Class) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(List<String> path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
        return defineEnumValue(path, defaultSupplier, EnumGetMethod.NAME_IGNORECASE, validator, clazz);
    }


    // Restricted Enums
    /** @see #defineEnumValue(List, Enum, EnumGetMethod, Collection) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(String path, V defaultValue, V... acceptableValues) {
        return defineEnumValue(split(path), defaultValue, acceptableValues);
    }
    /** @see #defineEnumValue(List, Enum, EnumGetMethod, Collection) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(String path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
        return defineEnumValue(split(path), defaultValue, converter, acceptableValues);
    }
    /** @see #defineEnumValue(List, Enum, EnumGetMethod, Collection) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(List<String> path, V defaultValue, V... acceptableValues) {
        return defineEnumValue(path, defaultValue, Arrays.asList(acceptableValues));
    }
    /** @see #defineEnumValue(List, Enum, EnumGetMethod, Collection) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(List<String> path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
        return defineEnumValue(path, defaultValue, converter, Arrays.asList(acceptableValues));
    }
    /** @see #defineEnumValue(List, Enum, EnumGetMethod, Collection) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(String path, V defaultValue, Collection<V> acceptableValues) {
        return defineEnumValue(split(path), defaultValue, acceptableValues);
    }
    /** @see #defineEnumValue(List, Enum, EnumGetMethod, Collection) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(String path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
        return defineEnumValue(split(path), defaultValue, converter, acceptableValues);
    }
    /** @see #defineEnumValue(List, Enum, EnumGetMethod, Collection) */
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(List<String> path, V defaultValue, Collection<V> acceptableValues) {
        return defineEnumValue(path, defaultValue, EnumGetMethod.NAME_IGNORECASE, acceptableValues);
    }
    /**
     * Define a new enum value which can only be a part of the given collection
     *
     * @param path the path to the given value
     * @param defaultValue the default value
     * @param converter how the config will convert the config object to the correct type. See {@link EnumGetMethod}
     * @param acceptableValues a collection containing the acceptable values
     * @return a new {@link EnumValue}
     */
    @SuppressWarnings("unchecked")
    default <V extends Enum<V>> EnumValue<V> defineEnumValue(List<String> path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
        return defineEnumValue(path, defaultValue, converter, obj -> {
            if (obj instanceof Enum) {
                return acceptableValues.contains(obj);
            }
            if (obj == null) {
                return false;
            }
            try {
                return acceptableValues.contains(converter.get(obj, defaultValue.getClass()));
            } catch (IllegalArgumentException | ClassCastException e) {
                return false;
            }
        });
    }
}
