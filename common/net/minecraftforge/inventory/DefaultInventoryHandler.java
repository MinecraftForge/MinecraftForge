package net.minecraftforge.inventory;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

import java.util.ArrayList;


/**
 * The default implementation of IInventoryHandler.
 * Designed to handler ISidedInventory, IDynamicInventory and the regular IInventory.
 */
public class DefaultInventoryHandler implements IInventoryHandler {

	@Override
	public ArrayList<ItemStack> listItemsInInventory(IInventory inventory, ForgeDirection side) {
		int iMin = 0, iMax;
		if( inventory instanceof ISidedInventory ) {
			iMin = ((ISidedInventory) inventory).getStartInventorySide( side );
			iMax = iMin + ((ISidedInventory) inventory).getSizeInventorySide( side );
		} else {
			iMax = inventory.getSizeInventory();
		}

		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for( int slotIndex = iMin; slotIndex < iMax; slotIndex++ ) {
			ItemStack stackInSlot = inventory.getStackInSlot( slotIndex );
			if( stackInSlot != null )
				list.add( stackInSlot );
		}

		return list;
	}

	@Override
	public boolean canPlaceItemOnInventory(IInventory inventory, ItemStack itemStack, ForgeDirection side, boolean fitAll) {
		int space = getSpaceInInventoryForItem( inventory, itemStack, side );
		return fitAll && space == itemStack.stackSize || space > 0;
	}

	@Override
	public int addItemToInventory(IInventory inventory, ItemStack itemStack, ForgeDirection side) {
		int iMin = 0, iMax;
		if( inventory instanceof ISidedInventory ) {
			iMin = ((ISidedInventory) inventory).getStartInventorySide( side );
			iMax = iMin + ((ISidedInventory) inventory).getSizeInventorySide( side );
		} else {
			iMax = inventory.getSizeInventory();
		}

		int added = 0;

		// Add to a slot that has some of that item.
		int slot = iMin;
		while( (slot = getPartialSlot( inventory, itemStack, slot, iMax )) >= 0 ) {
			if( itemStack.stackSize <= 0 )
				return added;
			added += addItemToInventorySlot( inventory, slot, itemStack );
		}

		// Add to an empty slot.
		slot = iMin;
		while( (slot = getEmptySlot( inventory, itemStack, slot, iMax )) >= 0 ) {
			if( itemStack.stackSize <= 0 )
				return added;
			added += addItemToInventorySlot( inventory, slot, itemStack );
		}

		return added;
	}

	public int addItemToInventorySlot(IInventory inventory, int slotIndex, ItemStack itemStack) {
		int space = getSpaceInSlotForItem( inventory, slotIndex, itemStack );
		if( space <= 0 )
			return 0;

		int amount = Math.min( space, itemStack.stackSize );
		ItemStack inventoryStack = inventory.getStackInSlot( slotIndex );
		ItemStack placedStack;

		if( inventoryStack == null ) {
			placedStack = itemStack.splitStack( amount );
			inventory.setInventorySlotContents( slotIndex, placedStack );
		} else {
			placedStack = itemStack.copy();
			placedStack.stackSize = amount;

			inventoryStack.stackSize += amount;
			itemStack.stackSize -= amount;
		}

		if( inventory instanceof IDynamicInventory )
			((IDynamicInventory) inventory).onItemPlaced( placedStack, slotIndex );

		inventory.onInventoryChanged();
		return amount;
	}

	@Override
	public int getSpaceInInventoryForItem(IInventory inventory, ItemStack itemStack, ForgeDirection side) {
		int iMin = 0, iMax;
		if( inventory instanceof ISidedInventory ) {
			iMin = ((ISidedInventory) inventory).getStartInventorySide( side );
			iMax = iMin + ((ISidedInventory) inventory).getSizeInventorySide( side );
		} else {
			iMax = inventory.getSizeInventory();
		}

		int space = 0;
		for( int slotIndex = iMin; slotIndex < iMax; slotIndex++ ) {
			space += getSpaceInSlotForItem( inventory, slotIndex, itemStack );
		}
		return space;
	}

	public int getSpaceInSlotForItem(IInventory inventory, int slotIndex, ItemStack itemStack) {
		if( slotIndex < 0 || slotIndex > inventory.getSizeInventory() )
			return 0;

		ItemStack stackInSlot = inventory.getStackInSlot( slotIndex );

		if( stackInSlot == null || InventoryUtils.areItemStacksSimilar( stackInSlot, itemStack ) ) {

			if( inventory instanceof IDynamicInventory )
				return ((IDynamicInventory) inventory).getSlotCapacityForItem( itemStack, slotIndex );

			if( stackInSlot == null )
				return inventory.getInventoryStackLimit();

			int maxSize = Math.min( stackInSlot.getMaxStackSize(), inventory.getInventoryStackLimit() );
			int space = maxSize - stackInSlot.stackSize;
			return space < 0 ? 0 : space;

		}
		return 0;
	}

	@Override
	public ItemStack takeItemFromInventory(IInventory inventory, ForgeDirection side) {
		return takeItemFromInventory( inventory, null, side );
	}

	@Override
	public ItemStack takeItemFromInventory(IInventory inventory, ItemStack item, ForgeDirection side) {
		int iMin = 0, iMax;
		if( inventory instanceof ISidedInventory ) {
			iMin = ((ISidedInventory) inventory).getStartInventorySide( side );
			iMax = iMin + ((ISidedInventory) inventory).getSizeInventorySide( side );
		} else {
			iMax = inventory.getSizeInventory();
		}

		for( int slotIndex = iMin; slotIndex < iMax; slotIndex++ ) {
			int count = getItemCountInSlot( inventory, slotIndex, item );
			if( count == 0 )
				continue;

			ItemStack takenStack = takeItemFromInventorySlot( inventory, slotIndex );
			if( takenStack != null )
				return takenStack;
		}
		return null;
	}

	@Override
	public ItemStack takeItemFromInventory(IInventory inventory, ItemStack item, int quantity, ForgeDirection side) {
		int iMin = 0, iMax;
		if( inventory instanceof ISidedInventory ) {
			iMin = ((ISidedInventory) inventory).getStartInventorySide( side );
			iMax = iMin + ((ISidedInventory) inventory).getSizeInventorySide( side );
		} else {
			iMax = inventory.getSizeInventory();
		}

		int remaining = item == null ? quantity : Math.min( quantity, item.getMaxStackSize() );

		ItemStack takenItem = null;
		for( int slotIndex = iMin; slotIndex < iMax; slotIndex++ ) {
			if( remaining <= 0 )
				break;

			int count = getItemCountInSlot( inventory, slotIndex, item );
			if( count == 0 )
				continue;

			ItemStack tempStack = takeItemFromInventorySlot( inventory, slotIndex, remaining );
			if( tempStack == null )
				continue;

			remaining -= tempStack.stackSize;

			if( takenItem == null )
				takenItem = tempStack;
			else
				takenItem.stackSize += tempStack.stackSize;
		}

		return takenItem;
	}


	// takes the entire stack in the slot.
	public ItemStack takeItemFromInventorySlot(IInventory inventory, int slotIndex) {
		return takeItemFromInventorySlot( inventory, slotIndex, Integer.MAX_VALUE );
	}


	// will manipulate the inventoryStack's stack size
	// might return less than requested, unless there is plenty to fulfill the request.
	// prefer: takeItemFromInventory instead
	public ItemStack takeItemFromInventorySlot(IInventory inventory, int slotIndex, int quantity) {
		if( slotIndex < 0 || slotIndex > inventory.getSizeInventory() )
			return null;

		boolean dynamicInventory = inventory instanceof IDynamicInventory;
		if( dynamicInventory ) {
			int available = ((IDynamicInventory) inventory).getItemAvailabilityInSlot( slotIndex );
			if( available > 0 )
				quantity = Math.min( available, quantity );
			else
				return null;
		}

		ItemStack inventoryStack = inventory.getStackInSlot( slotIndex );
		if( inventoryStack == null )
			return null;

		ItemStack itemTaken;
		if( quantity >= inventoryStack.stackSize ) {
			itemTaken = inventoryStack.copy();
			inventory.setInventorySlotContents( slotIndex, null );
		} else {
			itemTaken = inventory.decrStackSize( slotIndex, quantity );
			if( inventoryStack.stackSize <= 0 )
				inventory.setInventorySlotContents( slotIndex, null );
		}

		if( dynamicInventory ) {
			((IDynamicInventory) inventory).onItemTaken( itemTaken, slotIndex );
		}

		inventory.onInventoryChanged();
		return itemTaken;
	}

	@Override
	public int getItemCountInInventory(IInventory inventory, ItemStack itemStack, ForgeDirection side) {
		int iMin = 0, iMax;
		if( inventory instanceof ISidedInventory ) {
			iMin = ((ISidedInventory) inventory).getStartInventorySide( side );
			iMax = iMin + ((ISidedInventory) inventory).getSizeInventorySide( side );
		} else {
			iMax = inventory.getSizeInventory();
		}

		int count = 0;
		for( int slotIndex = iMin; slotIndex < iMax; slotIndex++ ) {
			count += getItemCountInSlot( inventory, slotIndex, itemStack );
		}
		return count;
	}

	public int getItemCountInSlot(IInventory inventory, int slotIndex) {
		if( slotIndex < 0 || slotIndex > inventory.getSizeInventory() )
			return 0;

		if( inventory instanceof IDynamicInventory )
			return ((IDynamicInventory) inventory).getItemAvailabilityInSlot( slotIndex );

		ItemStack stackInSlot = inventory.getStackInSlot( slotIndex );
		if( stackInSlot == null )
			return 0;

		return stackInSlot.stackSize;
	}

	public int getItemCountInSlot(IInventory inventory, int slotIndex, ItemStack itemStack) {
		if( slotIndex < 0 || slotIndex > inventory.getSizeInventory() )
			return 0;

		int count = getItemCountInSlot( inventory, slotIndex );
		if( count > 0 ) {
			ItemStack stackInSlot = inventory.getStackInSlot( slotIndex );
			if( itemStack != null && !InventoryUtils.areItemStacksSimilar( itemStack, stackInSlot ) )
				count = 0;
		}

		return count;
	}


	protected int getPartialSlot(IInventory inventory, ItemStack itemStack, int minIndex, int maxIndex) {
		for( int index = minIndex; index < maxIndex; index++ ) {
			if( inventory.getStackInSlot( index ) == null )
				continue;

			if( getSpaceInSlotForItem( inventory, index, itemStack ) > 0 )
				return index;
		}
		return -1;
	}

	protected int getEmptySlot(IInventory inventory, ItemStack itemStack, int minIndex, int maxIndex) {
		for( int index = minIndex; index < maxIndex; index++ ) {
			if( inventory.getStackInSlot( index ) == null && getSpaceInSlotForItem( inventory, index, itemStack ) > 0 )
				return index;
		}
		return -1;
	}

}
