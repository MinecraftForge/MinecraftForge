package net.minecraftforge.inventory;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

import java.util.ArrayList;


/**
 * Provides a regular-use access to the inventory manipulation methods.
 */
public class InventoryHelper {

    /**
     * The IInventory handled by this InventoryHelper
     */
    protected IInventory inventory;

    /**
     * The IInventoryHandler used by this InventoryHelper
     */
    protected IInventoryHandler handler;

    /**
     * The ForgeDirection from which <code>inventory</code> is accessed.
     * This is ignored unless <code>inventory</code> is <code>ISidedInventory</code>.
     * <p/>
     * By default, this is set to <code>ForgeDirection.UNKNOWN</code>
     *
     * @see net.minecraftforge.common.ForgeDirection#UNKNOWN
     */
    protected ForgeDirection sideAccessed = ForgeDirection.UNKNOWN;

    protected InventoryHelper() {
        this.inventory = null;
        this.handler = null;
    }

    public InventoryHelper(IInventory inventory) {
        this.inventory = inventory;
        this.handler = InventoryUtils.getInventoryHandler(inventory);
    }


    /**
     * Configures this InventoryHelper to access the inventory from the specified side.
     * This call should always be done, even if the inventory is not ISidedInventory.
     *
     * @param sideAccessed the side from which to access the inventory.
     * @return this
     */
    public InventoryHelper setSide(ForgeDirection sideAccessed) {
        this.sideAccessed = sideAccessed;
        return this;
    }


    public ArrayList<ItemStack> listItemsInInventory() {
        return handler.listItemsInInventory(inventory, sideAccessed);
    }

    public boolean canPlaceItemOnInventory(ItemStack itemStack, boolean fitAll) {
        return handler.canPlaceItemOnInventory(inventory, itemStack, sideAccessed, fitAll);
    }

    public int addItemToInventory(ItemStack itemStack) {
        return handler.addItemToInventory(inventory, itemStack, sideAccessed);
    }

    public int getItemCount(ItemStack itemStack) {
        return handler.getItemCountInInventory(inventory, itemStack, sideAccessed);
    }

    public int getSpaceForItem(ItemStack itemStack) {
        return handler.getSpaceInInventoryForItem(inventory, itemStack, sideAccessed);
    }

    public ItemStack takeAnyItemFromInventory() { // whole stack
        return handler.takeItemFromInventory(inventory, null, sideAccessed);
    }

    // itemStack acts as a filter.
    public ItemStack takeItemFromInventory(ItemStack itemStack) {
        return handler.takeItemFromInventory(inventory, itemStack, sideAccessed);
    }

    // itemStack acts as a filter.
    public ItemStack takeItemFromInventory(ItemStack itemStack, int quantity) {
        return handler.takeItemFromInventory(inventory, itemStack, quantity, sideAccessed);
    }

}
