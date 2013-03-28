package net.minecraftforge.inventory;

import net.minecraft.item.ItemStack;

/**
 * An inventory with custom insert and extract methods, similar to Buildcraft's
 * ISpecialInventory.
 * 
 * This is the most general interface. Any IInventory or ILinearInventory can be
 * wrapped as an ICustomInventory, but not vice versa.
 */
public interface IForgeCustomInventory extends IForgeInventory {

    /**
     * Attempts to insert some items into this inventory.
     * 
     * @param item
     *            The item to insert. The stack size is ignored.
     * @param amount
     *            The number of items to insert. Must be at least 1.
     * @param simulate
     *            If true, then the inventory will not be updated. The return
     *            value must be the same as if simulate was false.
     * @throws IllegalArgumentException
     *             If amount <= 0
     * @throws NullPointerException
     *             If item == null
     * @return The number of items that could not be inserted.
     */
    public int insertInventoryItems(ItemStack item, int amount, boolean simulate) throws IllegalArgumentException, NullPointerException;

    /**
     * Attempts to extract some items from this inventory.
     * 
     * @param filter
     *            A stack filter that defines which items can be extracted.
     * @param amount
     *            The maximum number of items to extract.
     * @param simulate
     *            If true, then the inventory will not be updated. The return
     *            value must be the same as if simulate was false.
     * @throws IllegalArgumentException
     *             If amount <= 0
     * @throws NullPointerException
     *             If filter == null
     * @return The stack extracted. If no items were extracted, this must be
     *         null. The stack size must be less than or equal to amount.
     */
    public ItemStack extractInventoryItems(IStackFilter filter, int amount, boolean simulate) throws IllegalArgumentException, NullPointerException;
}
