/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.World;

public interface ILeafDecayHandler {

	/**
	 * Called when an events triggers start of decay process for a leaf block
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void beginDecay(World world, int x, int y, int z);
	
}
