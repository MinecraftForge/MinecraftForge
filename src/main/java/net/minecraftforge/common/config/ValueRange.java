package net.minecraftforge.common.config;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.google.common.collect.Range;

public class ValueRange<V extends Comparable<? super V>> implements Predicate<Object>
{
    private final Range<V> range;
    private final Class<? extends V> clazz;

    public ValueRange(@Nonnull Class<V> clazz, @Nonnull Range<V> range)
    {
        this.clazz = clazz;
        this.range = range;
    }

    public Class<? extends V> getClazz() { return clazz; }
    public Range<V> getRange() { return range; }

    @Override
    public boolean test(Object t)
    {
        if (!clazz.isInstance(t)) return false;
        V c = clazz.cast(t);
        return range.contains(c);
    }

    /*
     * First, check if the range has a lower bound, and if so is the given value greater or equal to the lower endpoint
     * If it's not, return the minimum value
     * Second, check if the range has an upper bound and if so is the give value less than or equal to the higher endpoint
     * Last, now that we know the value is within the upper and lower bounds set by the range, the number is correct
     * so just return it
     *
     * Having the BoundType.OPEN means that specific bound does not exist (i.e. goes to infinity)
     * so any value will be valid as long as it's of the correct type
     */
    public Object correct(Object value, Object def)
    {
        if (!clazz.isInstance(value)) return def;
        V num = clazz.cast(value);
        if (range.hasLowerBound() && num.compareTo(range.lowerEndpoint()) < 0) {
            return range.lowerEndpoint();
        }
        if (range.hasUpperBound() && num.compareTo(range.upperEndpoint()) >= 1) {
            return range.upperEndpoint();
        }
        return value;
    }

    @Override
    public String toString()
    {
        return range.toString();
    }
}
