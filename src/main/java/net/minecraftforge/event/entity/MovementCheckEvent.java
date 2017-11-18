package net.minecraftforge.event.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * This event is fired when the server does a movement check. There are two
 * movement checks, one for players and one for vehicles. The type can be
 * determined using {@link #isVehicleCheck()}. If canceled, the check will be
 * skipped and no corrective action will be applied.
 */
@Cancelable
public class MovementCheckEvent extends EntityEvent 
{
	/**
	 * Whether or not the check is for a vehicle.
	 */
	private final boolean vehicle;

	public MovementCheckEvent(Entity entity, boolean vehicle) 
	{
		super(entity);
		this.vehicle = vehicle;
	}

	/**
	 * Allows differentiation between vehicle and player checks. 
	 * 
	 * @return Whether or not the check is for a vehicle. 
	 */
	public boolean isVehicleCheck() 
	{
		return this.vehicle;
	}
}