package net.minecraftforge.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.EnergyConsumerEvent.Load;
import net.minecraftforge.energy.EnergyConsumerEvent.Unload;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.TileFluidHandler;

/**
 * Implement this interface on TileEntities which should accept energy.
 * 
 * Typically, tile entities using this will have a small internal "battery",
 * which is refilled when {@link #offerEnergy(double)} is called, and which
 * the tile entity draws from. Tile entities are not required to use this.
 * 
 * Energy is always measured in furnace-ticks. One furnace-tick is the amount
 * of energy that a vanilla furnace uses in one tick.
 * One vanilla smelting operation takes 200 furnace-ticks.
 * One piece of coal is worth 1600 furnace-ticks.
 * 
 * Power is energy per time unit. Power is always measured in furnace-ticks per tick.
 * One furnace-tick per tick is, of course, the amount of power used by one furnace.
 * 
 * Tile entities that implement this must post a {@link Load} event
 * when {@link TileEntity#validate()} is called, and a {@link Unload}
 * when {@link TileEntity#invalidate()} or {@link TileEntity#onChunkUnload()} is called,
 * on the main Minecraft Forge event bus ({@link MinecraftForge#EVENT_BUS}), on the server.
 * 
 * Tile entities <i>may</i> also post those events on the client, but are not required to.
 * 
 * @author immibis
 */
public interface IEnergyConsumer {
    /**
     * Add energy to this tile entity.
     * This will be called periodically (typically every tick) by any
     * energy supplier connected to this tile entity. The exact details
     * are energy-system-specific.
     * 
     * @param amount The maximum amount of energy to use.
     * @return The amount of energy actually used.
     *     This could be less than the <var>amount</var> parameter - for example,
     *     if the tile entity uses an "internal battery" system, and its battery
     *     almost full.
     */
    public double offerEnergy(double amount);
    
    /**
     * Returns the nominal power draw of this tile entity.
     * This usually does not change over the lifetime of the tile entity.
     * 
     * If the energy system does not support delivering varying amounts of power,
     * it will try to deliver this much power at all times.
     * 
     * All energy systems may also use this value for other calculations, such as
     * maximum voltage ratings.
     */
    public double getNominalPowerDraw();
    
    /**
     * Returns the instantaneous power draw of this tile entity.
     * This is the amount of energy the tile entity would like to receive in the next tick,
     * for energy systems that do support delivering varying amounts of power.
     */
    public double getInstantaneousPowerDraw();
}
