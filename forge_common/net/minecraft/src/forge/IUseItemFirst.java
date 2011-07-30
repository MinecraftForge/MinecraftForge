/**
 * This software is provided under the terms of the Minecraft Forge Public 
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

/**
 * This interface is to be implemented by item classes. It will allow an item
 * to perform a use before the block is activated.
 *
 * @see Item
 */
public interface IUseItemFirst {
    
    /**
     * This is called when the item is used, before the block is activated.
     * Stop the computation when return true.
     */
	public boolean onItemUseFirst(ItemStack ist,
			EntityPlayer player, World world,
			int i, int j, int k, int l);
}
