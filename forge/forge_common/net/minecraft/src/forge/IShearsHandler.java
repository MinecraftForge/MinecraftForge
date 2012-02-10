/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public interface IShearsHandler {
	/**
	 * Called when a shearable mob is right-clicked.
	 * @return true to apply the shears action and stop processing
	 */
	public boolean onUseShears(EntityPlayer player, EntityLiving mob);

	/**
	 * Called when a leaf block is destroyed. 
	 * @return true to drop the leaf block and stop processing
	 */
	public boolean onDestroyLeafBlock(EntityPlayer player,
			int x, int y, int z, int id, int meta);
}