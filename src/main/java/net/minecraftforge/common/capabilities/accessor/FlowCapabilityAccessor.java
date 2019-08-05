package net.minecraftforge.common.capabilities.accessor;

import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Accessor for capabilities that allow flow of content between two systems.
 * <p>
 * Examples: Energy, Fluid, and Items
 * <p>
 * Created by Dark(DarkGuardsman, Robert) on 8/4/2019.
 */
public class FlowCapabilityAccessor<A> extends CapabilityAccessor<A> implements IFlowCapabilityAccessor<A>
{
    //Static cache of common values for normal access
    public static final FlowCapabilityAccessor[] FLOW_SIDES = new FlowCapabilityAccessor[]{
            new FlowCapabilityAccessor(Direction.DOWN, null).lock(),
            new FlowCapabilityAccessor(Direction.UP, null).lock(),
            new FlowCapabilityAccessor(Direction.NORTH, null).lock(),
            new FlowCapabilityAccessor(Direction.SOUTH, null).lock(),
            new FlowCapabilityAccessor(Direction.EAST, null).lock(),
            new FlowCapabilityAccessor(Direction.WEST, null).lock()
    };

    //Static cache of common values for simulated access
    public static final FlowCapabilityAccessor[] FLOW_SIDES_SIM = new FlowCapabilityAccessor[]{
            new FlowCapabilityAccessor(Direction.DOWN, null).setSimulate(true).lock(),
            new FlowCapabilityAccessor(Direction.UP, null).setSimulate(true).lock(),
            new FlowCapabilityAccessor(Direction.NORTH, null).setSimulate(true).lock(),
            new FlowCapabilityAccessor(Direction.SOUTH, null).setSimulate(true).lock(),
            new FlowCapabilityAccessor(Direction.EAST, null).setSimulate(true).lock(),
            new FlowCapabilityAccessor(Direction.WEST, null).setSimulate(true).lock()
    };

    public static final FlowCapabilityAccessor FLOW = new FlowCapabilityAccessor(null, null).lock();
    public static final FlowCapabilityAccessor FLOW_BYPASS = new FlowCapabilityAccessor(null, null).setBypassLimits(true).lock();
    public static final FlowCapabilityAccessor FLOW_SIM = new FlowCapabilityAccessor(null, null).setSimulate(true).lock();

    protected boolean simulate = false;
    protected boolean bypassLimits = false;
    protected boolean requireFull = false;
    protected boolean _lock = false;

    public FlowCapabilityAccessor(@Nullable Direction side, @Nullable Supplier<A> accessor)
    {
        super(side, accessor);
    }

    /**
     * Allows setting this accessor to require full
     *
     * @param value - true to require full, false to not
     */
    public FlowCapabilityAccessor setRequireFull(boolean value)
    {
        //Should not be called once locked
        if (_lock)
        {
            throw new UnsupportedOperationException();
        }
        this.requireFull = value;
        return this;
    }

    /**
     * Allows setting ths accessor to simulate actions
     *
     * @param value - true to simulate, false to complete action
     */
    public FlowCapabilityAccessor setSimulate(boolean value)
    {
        //Should not be called once locked
        if (_lock)
        {
            throw new UnsupportedOperationException();
        }
        this.simulate = value;
        return this;
    }

    /**
     * Allows setting this accessor to bypass limits
     *
     * @param value - true to bypass limits, false to complete action
     */
    public FlowCapabilityAccessor setBypassLimits(boolean value)
    {
        //Should not be called once locked
        if (_lock)
        {
            throw new UnsupportedOperationException();
        }
        this.bypassLimits = value;
        return this;
    }

    /**
     * Called to lock settings to prevent then from being
     * changed. Useful for creating immutable accessors.
     */
    public FlowCapabilityAccessor lock()
    {
        this._lock = true;
        return this;
    }

    @Override
    public boolean requireFull()
    {
        return requireFull;
    }

    @Override
    public boolean simulate()
    {
        return simulate;
    }

    @Override
    public boolean bypassLimits()
    {
        return bypassLimits;
    }

    /**
     * Helper to quickly get the static cache values per side and simulated status
     *
     * @param direction - side to access
     * @param simulate  - true to simulate
     * @return flow accessor cache value
     */
    public static FlowCapabilityAccessor getSide(@Nullable Direction direction, boolean simulate)
    {
        if (direction == null)
        {
            return simulate ? FLOW_SIM : FLOW;
        }
        return simulate ? FLOW_SIDES_SIM[direction.ordinal()] : FLOW_SIDES[direction.ordinal()];
    }
}
