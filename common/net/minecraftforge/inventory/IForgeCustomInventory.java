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
     *            May be over 64.
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
     *            The maximum number of items to extract. This may be greater
     *            than 64.
     * @param simulate
     *            If true, then the inventory will not be updated. The return
     *            value must be the same as if simulate was false.
     * @throws IllegalArgumentException
     *             If amount <= 0
     * @throws NullPointerException
     *             If filter == null
     * @return The stack extracted. If no items were extracted, this must be
     *         null. The stack size must be less than or equal to amount.
     *         If amount is over 64, this may be an oversized stack.
     */
    public ItemStack extractInventoryItems(IStackFilter filter, int amount, boolean simulate) throws IllegalArgumentException, NullPointerException;

    /**
     * Returns an object which can be used to enumerate all or part of the
     * contents of this inventory. Only items which can be immediately extracted
     * will be returned.
     * 
     * @param filter
     *            A stack filter that defines which items will be enumerated.
     * 
     * @throw NullPointerException If filter == null
     * 
     * @return An {@link Iterable} which lists the contents of the inventory
     *         after applying the filter. This only indicates the type and
     *         amount of items in the inventory, and does not necessarily
     *         indicate their arrangement.
     * 
     *         The {@link ItemStack#stackSize} field of ItemStacks returned by
     *         the Iterable may be outside normal bounds - anywhere from 1 to
     *         {@link Integer#MAX_VALUE} inclusive (eg. for GregTech quantum
     *         chests). There may be more than one ItemStack returned with the
     *         same type.
     * 
     *         The return value may not be null.
     * 
     *         The returned object should not be modified.
     */
    public Iterable<ItemStack> listExtractableContents(IStackFilter filter) throws NullPointerException;
}
