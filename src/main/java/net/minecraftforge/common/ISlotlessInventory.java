package net.minecraftforge.common;

import net.minecraft.item.ItemStack;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/*

Example Implementation, this is not tested and is provided mearly as an example,

public class MySlotlessInv extends TileEntity implements Provider, ISlotlessInventory, ItemSet
{
    private final int maxStorage = 1000;
    private ItemStack storage = null;

    @Override
    public ISlotlessInventory getSlotlessInventory(ForgeDirection side)
    {
        return this;
    }

    @Override
    public ItemSet getStoredItems()
    {
        return this;
    }

    @Override
    public int getItemStackCount()
    {
        return storage == null ? 0 : 1;
    }

    @Override
    public ItemStack getItemStack(int index)
    {
        return index == 0 ? storage : null;
    }

    @Override
    public long getStoredItemQuantity(ItemStack itemBeingTested)
    {
        if (itemBeingTested != null && itemBeingTested.equals(storage))
            return itemBeingTested.stackSize;

        return 0;
    }

    @Override
    public long getAvailableStorageForItem(ItemStack itemBeingTested)
    {
        if (itemBeingTested == null)
            return maxStorage;

        if (itemBeingTested.equals(storage))
            return maxStorage - storage.stackSize;

        return 0;
    }

    @Override
    public long extractStoredItem(ItemStack itemBeingExtracted, long amountToExtract)
    {
        if (storage != null && storage.equals(itemBeingExtracted))
        {
            long output = Math.min(storage.stackSize, amountToExtract);
            storage.stackSize -= output;

            if (storage.stackSize <= 0) storage = null;

            return output;
        }

        return 0;
    }

    @Override
    public long insertStoredItem(ItemStack itemBeingInserted, long amountToInsert)
    {
        if (storage == null)
        {
            storage = itemBeingInserted.copy();
            storage.stackSize = (int) Math.min(maxStorage, amountToInsert);
            return storage.stackSize;
        }
        else if (storage.equals(itemBeingInserted))
        {
            int originalContents = storage.stackSize;
            storage.stackSize = (int) Math.min(maxStorage, originalContents + amountToInsert);
            return storage.stackSize - originalContents;
        }

        return 0;
    }

}

*/

/**
 * Internal inventory for a {@link Provider}, your TileEntity should implement
 * {@link Provider} so they can access your {@link ISlotlessInventory}
 */
public interface ISlotlessInventory
{

    /**
     * Provides access slotless inventories, useful for things that do not
     * follow stack size limitations or that have item type restrictions rather
     * then number of slot restrictions.
     */
    public interface Provider
    {

        /**
         * Acquire Slotless Inventory for a given side, d
         * 
         * @param side
         * @return null, or a {@link ISlotlessInventory} which will give access
         *         to the storage inside the provider.
         */
        ISlotlessInventory getSlotlessInventory(ForgeDirection side);

    }

    /**
     * Provides list of items in the Slotless inventory.
     */
    public interface ItemSet
    {

        /**
         * @return the number of ItemStacks in the Slotless inventory.
         */
        int getItemStackCount();

        /**
         * @param index
         *            of the ItemStack being requested.
         * @return The ItemStack at the requested index, this ItemStack should
         *         not be modified in anyway.
         */
        ItemStack getItemStack(int index);

    }

    /**
     * Request the types of {@link ItemStack} currently stored.
     * 
     * @return a collection of all available items that can be used with
     *         extraction.
     */
    ItemSet getStoredItems();

    /**
     * Request the stored quantity of the specific item, this item will normally
     * be a item returned via getStoredItems, however this is not required.
     * 
     * @param itemBeingTested
     * @return 0-Long.MAX_VALUE of the current quantity of the item available
     *         for extraction.
     */
    long getStoredItemQuantity(ItemStack itemBeingTested);

    /**
     * See how many of a specific item can be inserted into this inventory based
     * on its current state.
     * 
     * @param itemBeingTested
     * @return the maximum number of the provided item that can be inserted into
     *         this inventory.
     */
    long getAvailableStorageForItem(ItemStack itemBeingTested);

    /**
     * Attempt to Extract a stored item, this will normally be an item returned
     * via getStoredItems, however this is not required.
     * 
     * @param itemBeingExtracted
     * @param amountToExtract
     * @return the amount of the specified item that was extracted
     */
    long extractStoredItem(ItemStack itemBeingExtracted, long amountToExtract);

    /**
     * Attempt to store an amount of the specified item into the storage, this
     * can be any type of item, the storage will decide if it can accept the
     * item, and in what quantity.
     * 
     * @param itemBeingInserted
     * @param amountToExtract
     * @return the amount of the specified item that was inserted
     */
    long insertStoredItem(ItemStack itemBeingInserted, long amountToInsert);

}
