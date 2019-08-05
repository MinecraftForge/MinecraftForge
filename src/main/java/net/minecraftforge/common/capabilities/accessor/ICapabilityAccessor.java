package net.minecraftforge.common.capabilities.accessor;


import net.minecraft.util.Direction;

import javax.annotation.Nullable;

/**
 * Wrapper to provide context into how the capability is being accessed. Use the data
 * provided to better supply capabilities customized to handle the data.
 * <p>
 * Example of usage of this accessor is to provided a sided capability or a non-sided
 * capability based on the {@link #getAccessSide()} direction.
 * <p>
 * Instances of {@link net.minecraftforge.common.capabilities.ICapabilityProvider} should
 * not cache this value. As it may be shared over several machines or recycled to help
 * with memory churn.
 * <p>
 * Created by Dark(DarkGuardsman, Robert) on 8/4/2019.
 */
public interface ICapabilityAccessor<A>
{
    /**
     * Side that will be accessed
     *
     * @return sided access, can be null to indicate internal access
     */
    @Nullable
    Direction getAccessSide();

    /**
     * System or object accessing the capability.
     * <p>
     * Do not use this to filter access to capabilities.
     * Instead use the accessor to provided feedback of actions,
     * debug of how the capability is being handled, or solutions
     * of edge cases such as infinite loops.
     * <p>
     * Example of usage: Pipe that can only have 1 input
     * and 1 output. In which instead of providing its own
     * capabilities it wrappers the target's capabilities
     * for return. In doing so it may return the sender's
     * capability due to also being the target. This can
     * cause an infinite loop if handled wrong. To avoid
     * this the target could check if accessor is itself.
     *
     * @return accessor, or null for generic access
     */
    @Nullable
    A getAccessor();
}
