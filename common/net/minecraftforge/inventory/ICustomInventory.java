package net.minecraftforge.inventory;


import net.minecraft.inventory.IInventory;

/**
 * An IInventory that requires a custom handling of its contents.
 */
public interface ICustomInventory extends IInventory {

	/**
	 * Gets the IInventoryHandler that will do the inventory manipulations for this ICustomInventory.
	 */
	public IInventoryHandler getInventoryHandler();

}
