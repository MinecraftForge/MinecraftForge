package net.minecraftforge.client.event;

import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called upon the construction of BlockModelShapes after the registration 
 * of Vanilla blocks. This event should be used as an opportunity to register 
 * any custom state mappings for modded blocks.
 */
public class BlockModelRegisterEvent extends Event
{
	public final BlockModelShapes modelShapes;
	
	public BlockModelRegisterEvent(BlockModelShapes modelShapes)
	{
		this.modelShapes = modelShapes;
	}
}
