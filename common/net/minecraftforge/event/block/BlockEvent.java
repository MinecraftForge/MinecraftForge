package net.minecraftforge.event.block;

import net.minecraft.src.Block;
import net.minecraftforge.event.Event;

public class BlockEvent extends Event {

	public final Block block;

	public BlockEvent(Block block) {
		this.block = block;
	}

}
