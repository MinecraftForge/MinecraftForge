package net.minecraftforge.common.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

/**
 * Enum that holds the convenience methods to create {@link Range}s.
 */
/* Due to some of the ranges only requiring a single argument, and others requiring two this could not be an interface or class. */
public enum RangeType
{
    /**
     * (a..b) { x | a < x < b } <br>
     * Create a Range that contains all values strictly greater than lower and less than upper
     */
    OPEN {
        @Override
        public <C extends Comparable<C>> Range<C> createRange(C... values)
        {
            Preconditions.checkArgument(values.length > 1,"Insufficient arguments for the " + name() + " range. Required two. Found " + values.length);
            return Range.open(values[0], values[1]);
        }
    },
    /**
     * [a..b] { x | a <= x <= b } <br>
     * Create a Range that contains all values greater than or equal to lower and less than or equal to upper
     */
    CLOSED {
        @Override
        public <C extends Comparable<C>> Range<C> createRange(C... values)
        {
            Preconditions.checkArgument(values.length > 1,"Insufficient arguments for the " + name() + " range. Required two. Found " + values.length);
            return Range.closed(values[0], values[1]);
        }
    },
    /**
     * (a..b] 	{ x | a < x <= b } <br>
     * Create Returns a range that contains all values strictly greater than lower and less than or equal to upper.
     */
    OPEN_CLOSED {
        @Override
        public <C extends Comparable<C>> Range<C> createRange(C... values)
        {
            Preconditions.checkArgument(values.length > 1,"Insufficient arguments for the " + name() + " range. Required two. Found " + values.length);
            return Range.openClosed(values[0], values[1]);
        }
    },
    /**
     * [a..b) 	{ x | a <= x < b } <br>
     * Create Returns a range that contains all values greater than or equal to lower and strictly less than upper.
     */
    CLOSED_OPEN {
        @Override
        public <C extends Comparable<C>> Range<C> createRange(C... values)
        {
            Preconditions.checkArgument(values.length > 1,"Insufficient arguments for the " + name() + " range. Required two. Found " + values.length);
            return Range.closedOpen(values[0], values[1]);
        }
    },
    /**
     * (a..+∞) 	{ x | x > a } <br>
     * Create Returns a range that contains all values strictly greater than the given value.
     */
    GREATERTHAN {
        @Override
        public <C extends Comparable<C>> Range<C> createRange(C... values)
        {
            Preconditions.checkArgument(values.length > 0,"Insufficient arguments for the " + name() + " range. Required one. Found " + values.length);
            return Range.greaterThan(values[0]);
        }
    },
    /**
     * [a..+∞) 	{ x | x >= a } <br>
     * Create Returns a range that contains all values greater than or equal to the given value.
     */
    ATLEAST {
        @Override
        public <C extends Comparable<C>> Range<C> createRange(C... values)
        {
            Preconditions.checkArgument(values.length > 0,"Insufficient arguments for the " + name() + " range. Required one. Found " + values.length);
            return Range.atLeast(values[0]);
        }
    },
    /**
     * (-∞..b] 	{ x | x <= b } <br>
     * Create Returns a range that contains all values strictly less than the given value.
     */
    ATMOST {
        @Override
        public <C extends Comparable<C>> Range<C> createRange(C... values)
        {
            Preconditions.checkArgument(values.length > 0,"Insufficient arguments for the " + name() + " range. Required one. Found " + values.length);
            return Range.atMost(values[0]);
        }
    };

    private RangeType() {}

    /**
     * Create a {@link Range} using the given value(s).
     *
     * See the javadocs for each Enum for how many values are required for each type of range
     *
     * @param values the values that represent the upper and lower bounds of the range
     * @return a new {@link Range}
     */
    public abstract <C extends Comparable<C>> Range<C> createRange(C... values);
}
