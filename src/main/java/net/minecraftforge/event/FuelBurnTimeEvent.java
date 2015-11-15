package net.minecraftforge.event;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

/**
 * FuelBurnTimeEvent is fired whenever a furnace needs the burn time of a fuel. <br>
 * Normally, a registered {@link cpw.mods.fml.common.IFuelHandler} is preferred, but
 * this is useful in the rare situation where IFuelHandler is not effective.<br>
 * <br>
 * {@link Result#DEFAULT} allows the normal fuel handling code to proceed.
 * {@link Result#ALLOW} or {@link Result#DENY} uses the value of {@link #burnTime}
 * for the given {@link fuel}, bypassing both vanilla and IFuelHandler determinations.
 * <br>
 * {@link #fuel} contains the potential fuel.<br>
 * {@link #burnTime} the results if set by an event handler.<br>
 * <br>
 * This event are fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@HasResult
@Deprecated //Remove in 1.8
public class FuelBurnTimeEvent extends Event
{
    public final ItemStack fuel;
    public int burnTime;
    
    public FuelBurnTimeEvent(ItemStack fuel)
    {
        this.fuel = fuel;
    }
}