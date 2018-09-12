package net.minecraftforge.common.extensions;

import net.minecraft.item.ItemStack;

/*
 * Extension added to ItemStack that bounces to ItemSack sensitive Item methods. Typically this is just for convince.
 */
public interface IForgeItemStack
{
    // Helpers for accessing Item data
    default ItemStack getStack()
    {
        return (ItemStack)this;
    }

    /**
     * ItemStack sensitive version of getContainerItem. Returns a full ItemStack
     * instance of the result.
     *
     * @param itemStack The current ItemStack
     * @return The resulting ItemStack
     */
    default ItemStack getContainerItem()
    {
        return getStack().getItem().getContainerItem(getStack());
    }

    /**
     * ItemStack sensitive version of hasContainerItem
     *
     * @param stack The current item stack
     * @return True if this item has a 'container'
     */
    default boolean hasContainerItem()
    {
        return getStack().getItem().hasContainerItem(getStack());
    }

}
