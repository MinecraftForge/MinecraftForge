package net.minecraftforge.common.config.api;

import static net.minecraftforge.common.config.AbstractConfigSpec.split;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraftforge.common.config.values.ListValue;

/**
 * The define methods used to define entries for lists.
 *
 * The old 'ForgeConfigSpec' had 'defineInList' methods, however all of them pointed to the
 * root define method and simple called 'Collection::contains'.
 * I chose not to include these because that behavior is already possible using existing methods.
 */
public interface ListDefinitions
{
    /**
     * Define a new list entry in the config.
     *
     * @param path the path to the configuration entry
     * @param defaultSupplier the default value if the list is null or empty.
     * @param elementValidator the predicate that will validate the list entries.
     *                         If not supplied this defaults to {@link java.util.Objects#nonNull(Object)}
     * @param elementConverter the function that will convert each element of the list to its appropriate type
     * @return a new {@link ListValue}
     */
    <T> ListValue<T> defineList(List<String> path, Supplier<List<T>> defaultSupplier, Predicate<Object> elementValidator, Function<Object, T> elementConverter);

    /** @see #defineList(List, Supplier, Predicate, Function) **/
    default  <T> ListValue<T> defineList(String path, List<T> defaultValue, Function<Object, T> elementConverter) {
        return defineList(split(path), () -> defaultValue, Objects::nonNull, elementConverter);
    }
    /** @see #defineList(List, Supplier, Predicate, Function) **/
    default  <T> ListValue<T> defineList(String path, List<T> defaultValue, Predicate<Object> elementValidator, Function<Object, T> elementConverter) {
        return defineList(split(path), () -> defaultValue, elementValidator, elementConverter);
    }
}
