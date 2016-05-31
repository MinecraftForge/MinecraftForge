package net.minecraftforge.fluids.capability;

/**
 * This is a hint about how the tank allows interactions from the outside.
 * It does not consider the contents or capacity of the tank.
 *
 * For example: a tank that can be filled and drained is always {@link #OPEN}, even if it is
 * currently full and can't accept more fluid at the moment.
 * A machine interacting with a filled {@link #OPEN} tank can reasonably expect to be able to drain
 * it and then fill it again.
 *
 * If your tank's logic is more complicated and no assumptions can be made, leave it {@link #OPEN}.
 * This is only a hint, real interactions will have to use {@link IFluidHandler} to find out what really happens.
 */
public enum TankInteractionType
{
	/** Open tanks can be filled or drained. */
	OPEN,
	/** Fill-only tanks can only be filled, not drained. */
	FILL_ONLY,
	/** Drain-only tanks can only be drained, not filled. */
	DRAIN_ONLY,
	/** Closed tanks can't be filled or drained. */
	CLOSED
}
