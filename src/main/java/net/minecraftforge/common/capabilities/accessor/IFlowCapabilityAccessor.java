package net.minecraftforge.common.capabilities.accessor;

/**
 * Accessor for capabilities that allow flow of content between two systems.
 * <p>
 * Examples: Energy, Fluid, and Items.
 * <p>
 * Created by Dark(DarkGuardsman, Robert) on 8/4/2019.
 */
public interface IFlowCapabilityAccessor<A> extends ICapabilityAccessor<A>
{
    /**
     * Allows setting the access to be simulated.
     * <p>
     * Example: Machine wants to predict how much to send would
     * return true to get a check value back.
     * <p>
     * Example 2: Machine wants to apply the changes would return
     * false to have the action go through.
     *
     * @return true to simulate, false to complete the action
     */
    boolean simulate();

    /**
     * Allows setting the access to input/output the
     * full amount provided. If this can't be provided
     * then it will reject the full amount.
     * <p>
     * Example: Machine wants to send fluid but doesn't care how much
     * is taken. This machine would return false.
     * <p>
     * Example 2: Machine is sending a packet of fluid. It wants to
     * pathfind to the first machine possible to take the packet. This
     * machine would return true and would also run a simulate check
     * to predict the path needed.
     *
     * @return true to require full input, false to take as much
     * as possible into the capability
     */
    default boolean requireFull()
    {
        return false;
    }

    /**
     * Allows bypassing any input/output limit on the capability.
     * <p>
     * Should only be used as needed. Most cases this should return false.
     * <p>
     * Example: Wires/Pipes/Belts should return false as they need to be
     * driven by the limits of the connection.
     * <p>
     * Example 2: Admin command to set or update the state would return true
     * as it needs to bypass the limit to quickly handle the action.
     *
     * @return true to bypass limits
     */
    default boolean bypassLimits()
    {
        return false;
    }
}
