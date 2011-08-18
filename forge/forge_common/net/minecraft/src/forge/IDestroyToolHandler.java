/*
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */
package net.minecraft.src.forge;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumStatus;

public interface IDestroyToolHandler {
	/** Called when the user's currently equipped item is destroyed.
	 */
	public void onDestroyCurrentItem(EntityPlayer player);
}

