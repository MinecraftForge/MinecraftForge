package net.minecraftforge.common.stripping;

import net.minecraft.block.BlockState;

public interface IAxeStrippingMap {

	/**
	 * @param input
	 * 			The BlockState to be stripped.
     * @return True if the passed BlockState can be stripped by the axe.
     */
	boolean canStrip(BlockState input);
	
	/**
	 * @param input
	 * 			The BlockState being stripped.
     * @return The resulting stripped BlockState to replace the input.
     */
	BlockState getStrippedState(BlockState input);
}
