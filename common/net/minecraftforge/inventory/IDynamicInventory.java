package net.minecraftforge.inventory;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;


/**
 * An inventory that controls what items can be placed/taken to/from which slot.
 */
public interface IDynamicInventory extends IInventory {


	/**
	 * Determines the quantity of <code>itemStack</code> that can be placed on the inventory slot.
	 * <p/>
	 * Note: The actual process of placing/merging the item to the slot is done through
	 * <code>IInventory.setInventorySlotContents()</code>.
	 *
	 * @param itemStack the ItemStack to be checked.
	 * @param slot      the index of the inventory slot to which <code>itemStack</code> will placed.
	 * @return the quantity of <code>itemStack</code> that could be merged into the inventory slot.
	 * @see DefaultInventoryHandler#addItemToInventorySlot(net.minecraft.inventory.IInventory, int, net.minecraft.item.ItemStack)
	 */
	public int getSlotCapacityForItem(ItemStack itemStack, int slot); // canSlotAcceptItem


	/**
	 * Determines the quantity available of the item on the inventory slot.
	 * This will limit the max amount that can be taken from the inventory slot.
	 * <p/>
	 * By returning 0, you disallow picking up the item on the inventory slot.
	 *
	 * @param slot the index of the inventory slot to check.
	 * @return the quantity that can be taken from the inventory slot.
	 *         Should never return a negative number.
	 */
	public int getItemAvailabilityInSlot(int slot);


	/**
	 * Invoked when an ItemStack is (fully or partially) placed on the slot.
	 *
	 * @param itemStack the ItemStack merged into the slot.
	 * @param slot      the index of the inventory slot to which <code>itemStack</code> was placed.
	 */
	public void onItemPlaced(ItemStack itemStack, int slot);


	/**
	 * Invoked when an ItemStack is (fully or partially) taken from an inventory slot.
	 *
	 * @param itemStack the ItemStack taken.
	 * @param slot      the index of the inventory slot from which <code>itemStack</code> was taken.
	 */
	public void onItemTaken(ItemStack itemStack, int slot);

}
