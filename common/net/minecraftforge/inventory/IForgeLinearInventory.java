package net.minecraftforge.inventory;

import net.minecraft.item.ItemStack;

/**
 * An inventory that can be accessed as an array of slots, like a chest. This
 * doesn't make sense for all inventories, such as barrels.
 * 
 * This is similar to vanilla's IInventory but allows slots to be completely
 * independent of each other - for example, they can have different maximum
 * stack sizes.
 */
public interface IForgeLinearInventory extends IForgeInventory {
    /**
     * @return The number of slots in this inventory.
     */
    int getNumInventorySlots();

    /**
     * @param index
     *            The slot index. Valid slot indices are from 0 to
     *            getNumSlots()-1 inclusive.
     * @return The slot in this inventory with the given index.
     * @throws IndexOutOfBoundsException
     *             If the slot index is out of range.
     */
    ILinearInventorySlot getInventorySlot(int index) throws IndexOutOfBoundsException;
}
