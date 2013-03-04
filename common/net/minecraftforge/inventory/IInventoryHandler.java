package net.minecraftforge.inventory;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

import java.util.ArrayList;

/**
 * The handler interface for any inventory.
 * The implementations of this interface are what do the managing/manipulating inventories.
 * <p/>
 * The ICustomInventory interface is provided for IInventory implementations that behave on an unorthodox way,
 * so they can provide a custom implementation of IInventoryHandler.
 *
 * @see net.minecraftforge.inventory.ICustomInventory
 * @see DefaultInventoryHandler
 */
public interface IInventoryHandler {

	/**
	 * List all the ingredients that can be found on this inventory.
	 *
	 * @param inventory the inventory that holds the items.
	 * @param side      the side from which the inventory is accessed (only applies to ISidedInventory)
	 * @return the list that holds all the ingredients stored on this inventory.
	 */
	public ArrayList<ItemStack> listItemsInInventory(IInventory inventory, ForgeDirection side);

	/**
	 * Whether if an ItemStack can be placed on the IInventory.
	 *
	 * @param inventory the IInventory where to place the item.
	 * @param itemStack the ItemStack to check
	 * @param side      the ForgeDirection (side) from which the Inventory is accessed.
	 *                  Note: This is ignored if <code>inventory</code> it not a <code>ISidedInventory</code>
	 * @param fitAll    if true, this will check if there is enough space to fit the entire stack.
	 *                  otherwise, if there is space for at least one item.
	 * @return fitAll && space == itemStack.stackSize || space > 0
	 */
	public boolean canPlaceItemOnInventory(IInventory inventory, ItemStack itemStack, ForgeDirection side, boolean fitAll);

	/**
	 * Tries to add the ItemStack into the IInventory.
	 * <p/>
	 * Note: the itemStack.stackSize might be manipulated.
	 * If it's fully added, its stack size will become 0.
	 *
	 * @param inventory the IInventory where to place the item.
	 * @param itemStack the ItemStack to be added into the inventory.
	 * @param side      the ForgeDirection (side) from which the Inventory is accessed.
	 *                  Note: This is ignored if <code>inventory</code> it not a <code>ISidedInventory</code>
	 * @return the amount of itemStack added into the inventory.
	 */
	public int addItemToInventory(IInventory inventory, ItemStack itemStack, ForgeDirection side);

	/**
	 * Gets the available space on the inventory for the itemStack.
	 * Will respect ISidedInventory's functionality.
	 *
	 * @param inventory the IInventory to check for space
	 * @param itemStack the ItemStack to compare
	 * @param side      the ForgeDirection (side) from which the Inventory is accessed.
	 *                  Note: This is ignored if <code>inventory</code> it not a <code>ISidedInventory</code>
	 * @return the amount of itemStack that could fit on the inventory.
	 */
	public int getSpaceInInventoryForItem(IInventory inventory, ItemStack itemStack, ForgeDirection side);

	/**
	 * Takes the first available item stack on the inventory.
	 *
	 * @param inventory the inventory from where to take the item.
	 * @param side      the side from which the inventory is accessed.
	 *                  This will be ignored unless the inventory is ISidedInventory.
	 * @return the ItemStack of the item found, or null if no item is available on the inventory.
	 */
	public ItemStack takeItemFromInventory(IInventory inventory, ForgeDirection side);

	/**
	 * Takes from the inventory the first stack of the item passed.
	 *
	 * @param inventory the inventory from where to take the item.
	 * @param item      the item to be taken.
	 *                  Acts as a filter, so a <code>null</code> value would exclude nothing.
	 * @param side      the side from which the inventory is accessed.
	 *                  This will be ignored unless the inventory is ISidedInventory.
	 * @return the ItemStack taken.
	 */
	public ItemStack takeItemFromInventory(IInventory inventory, ItemStack item, ForgeDirection side);

	/**
	 * Takes from the inventory the amount specified of the item passed.
	 * <p/>
	 * If there is not enough to fulfill the request, will return as much as possible.
	 * Keep in mind that the max amount returned is limited by the item's max stack size.
	 *
	 * @param inventory the inventory from where to take the item.
	 * @param item      the item to be taken.
	 *                  Acts as a filter, so a <code>null</code> value would exclude nothing.
	 * @param quantity  the amount of items to take.
	 * @param side      the side from which the inventory is accessed.
	 *                  This will be ignored unless the inventory is ISidedInventory.
	 * @return the ItemStack taken.
	 */
	public ItemStack takeItemFromInventory(IInventory inventory, ItemStack item, int quantity, ForgeDirection side);

	/**
	 * Get the amount available of the specified item in the inventory.
	 *
	 * @param inventory the inventory to check for the item.
	 * @param itemStack the item to match on the inventory.
	 *                  Acts as a filter, so a <code>null</code> value would exclude nothing.
	 * @param side      the side from which the inventory is accessed.
	 *                  This will be ignored unless the inventory is ISidedInventory.
	 * @return the amount available of the specified item in the inventory.
	 */
	public int getItemCountInInventory(IInventory inventory, ItemStack itemStack, ForgeDirection side);

}
