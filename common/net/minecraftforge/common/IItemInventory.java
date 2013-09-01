package net.minecraftforge.common;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/*
 *Used for items containing an Inventory 
 */
public interface IItemInventory {

    /**
     * Gets the inventory the item contains
     */
    IInventory getInventory(ItemStack itemStack);
}
