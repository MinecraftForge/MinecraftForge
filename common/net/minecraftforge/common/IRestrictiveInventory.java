/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

/**
 * Allows the implementing inventory to veto on stacks beeing added to it. 
 * 
 * @author Xfel
 */
public interface IRestrictiveInventory extends IInventory {
	
	/**
	 * Queries whether an item may be added.
	 * 
	 * @param slot the slot the item would be added to
	 * @param item the item stack that would be added
	 * @return <code>true</code> if the item is allowed, <code>false</code> otherwise
	 */
	boolean isItemValid(int slot, ItemStack item);
	
}