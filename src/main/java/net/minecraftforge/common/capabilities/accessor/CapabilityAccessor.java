package net.minecraftforge.common.capabilities.accessor;

import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Simplified sided accessor for generic retrieval of capabilities per side.
 * <p>
 * For those that only care about the side use the constants.
 *
 * For those that want to reference the accessor create a new instance
 * and cache it in the accessing object. For a TileEntity this should be
 * done to reduce memory churn of repeat access calls.
 */
public class CapabilityAccessor<A> implements ICapabilityAccessor<A>
{
    //Static versions to reduce churn usage of access that doesn't care about context beyond side
    public static final CapabilityAccessor SELF = new CapabilityAccessor(null, () -> null);
    public static final CapabilityAccessor NORTH = new CapabilityAccessor(Direction.NORTH, () -> null);
    public static final CapabilityAccessor SOUTH = new CapabilityAccessor(Direction.SOUTH, () -> null);
    public static final CapabilityAccessor EAST = new CapabilityAccessor(Direction.EAST, () -> null);
    public static final CapabilityAccessor WEST = new CapabilityAccessor(Direction.WEST, () -> null);
    public static final CapabilityAccessor UP = new CapabilityAccessor(Direction.UP, () -> null);
    public static final CapabilityAccessor DOWN = new CapabilityAccessor(Direction.DOWN, () -> null);

    //Array to provide looping
    public static final CapabilityAccessor[] SIDES = new CapabilityAccessor[]{
            DOWN,
            UP,
            NORTH,
            SOUTH,
            EAST,
            WEST
    };

    /** Side being accessed, can be null to indicate internal access */
    protected final Direction side;
    /** Object that is accessing the capability, can be null */
    protected final Supplier<A> accessor;

    public CapabilityAccessor(@Nullable Direction side, @Nullable Supplier<A> accessor)
    {
        this.side = side;
        this.accessor = accessor;
    }

    @Nullable
    @Override
    public Direction getAccessSide()
    {
        return side;
    }

    @Nullable
    @Override
    public A getAccessor()
    {
        return accessor.get();
    }

    /**
     * Helper to map from a direction to an accessor.
     * <p>
     * This works well for anything that may need to
     * dynamically get the accessor based on a side. As
     * well a simple solution for iterators.
     *
     * @param direction - direction to access
     * @return accessor instance for the side
     */
    public static CapabilityAccessor getSide(@Nonnull Direction direction)
    {
        if(direction == null) {
            return SELF;
        }
        return SIDES[direction.ordinal()];
    }
}
