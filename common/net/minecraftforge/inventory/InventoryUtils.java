package net.minecraftforge.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class InventoryUtils {

    private InventoryUtils() {
    }

    /**
     * The default IInventoryHandler.
     */
    public static final IInventoryHandler defaultInventoryHandler = new DefaultInventoryHandler();

    /**
     * Determines if two ItemStack contain the same kind of items.
     * Compares itemIDs, damage value and the item's NBT. Disregards the stack size.
     *
     * @return true if both stacks match their itemIDs, damage value and NBT, or if both are null.
     */
    public static boolean areItemStacksSimilar(ItemStack itemStack1, ItemStack itemStack2) {
        if (itemStack1 == null || itemStack2 == null)
            return itemStack1 == itemStack2; // both are null.

        if (itemStack1.itemID == itemStack2.itemID) {
            if (itemStack2.getItemDamage() == itemStack1.getItemDamage())
                return ItemStack.areItemStackTagsEqual(itemStack1, itemStack2);
        }
        return false;
    }

    public static InventoryHelper getInventoryHelper(IInventory inventory) {
        return new InventoryHelper(inventory);
    }

    /**
     * Gets the IInventoryHandler suitable for the passed IInventory.
     * <p/>
     * If <code>inventory</code> is not an ICustomInventory, the default IInventoryHandler will be returned.
     *
     * @see net.minecraftforge.inventory.InventoryUtils#defaultInventoryHandler
     */
    public static IInventoryHandler getInventoryHandler(IInventory inventory) {
        if (inventory == null)
            throw new IllegalArgumentException("Inventory Utils: inventory null");

        if (inventory instanceof ICustomInventory)
            return ((ICustomInventory) inventory).getInventoryHandler();
        return defaultInventoryHandler;
    }

}
