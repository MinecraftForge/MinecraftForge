package net.minecraftforge.common.config.api;

import static net.minecraftforge.common.config.AbstractConfigSpec.split;

import java.util.List;
import java.util.function.Supplier;

import net.minecraftforge.common.config.RangeType;
import net.minecraftforge.common.config.ValueRange;
import net.minecraftforge.common.config.values.ConfigValue;
import net.minecraftforge.common.config.values.DoubleValue;
import net.minecraftforge.common.config.values.IntValue;
import net.minecraftforge.common.config.values.LongValue;

import com.google.common.collect.Range;

/**
 * Where are {@link ValueRange} definitions are specified.
 *
 * These methods allow the creation of a simple numerical entry that can only be a part of the given range.
 */
public interface RangeDefinitions
{
    /**
     * Define a Comparable that should be within a given minimum and maximum
     * Comparable's can be any {@link Number}, or anything that inherits from {@link Comparable}
     *
     * @param path the path to the value
     * @param defaultSupplier the default value supplier
     * @param range the type of {@link com.google.common.collect.Range} to use. See {@link RangeType} for more information.
     * @param clazz the type of the value
     * @return a new {@link ConfigValue} containing the type of the value and it's current value
     */
    <V extends Comparable<? super V>> ConfigValue<V> defineInRange(List<String> path, Supplier<V> defaultSupplier, Range<V> range, Class<V> clazz);

    //Double
    /** @see #defineInRange */
    default DoubleValue defineInRange(String path, double defaultValue, Range<Double> range) {
        return defineInRange(split(path), defaultValue, range);
    }

    /** @see #defineInRange(List, Supplier, Range, Class)  */
    DoubleValue defineInRange(List<String> path, double defaultValue, Range<Double> range);

    //Ints
    /** @see #defineInRange(List, int, Range)  */
    default IntValue defineInRange(String path, int defaultValue, Range<Integer> range) {
        return defineInRange(split(path), defaultValue, range);
    }
    /** @see #defineInRange(List, Supplier, Range, Class)  */
    IntValue defineInRange(List<String> path, int defaultValue, Range<Integer> range);

    //Longs
    /** @see #defineInRange(List, long, Range)  */
    default LongValue defineInRange(String path, long defaultValue, Range<Long> range) {
        return defineInRange(split(path), defaultValue, range);
    }
    /** @see #defineInRange(List, Supplier, Range, Class)  */
    LongValue defineInRange(List<String> path, long defaultSupplier, Range<Long> range);
}
