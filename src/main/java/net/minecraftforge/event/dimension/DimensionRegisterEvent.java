package net.minecraftforge.event.dimension;

import net.minecraftforge.dimension.DynamicDimensionManager;
import net.minecraftforge.eventbus.api.Event;

/**
 * Register your custom dimension definitions when you receive this event.
 * 
 * <code>event.getDimensionManager().registerDimension(...)</code>
 * 
 * Every dimension registered within this event will be loaded automatically on server start, otherwise you need to load it manually using {@link DynamicDimensionManager#loadOrCreateDimension(net.minecraft.server.MinecraftServer, net.minecraft.util.ResourceLocation)}
 * 
 *
 */
public class DimensionRegisterEvent extends Event 
{
	
	private DynamicDimensionManager dimManager;
	
	public DimensionRegisterEvent(DynamicDimensionManager dimManager) 
	{
		this.dimManager = dimManager;
	}
	
	public DynamicDimensionManager getDimensionManager() 
	{
		return dimManager;
	}

}
