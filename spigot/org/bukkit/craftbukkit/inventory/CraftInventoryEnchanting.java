package org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;


public class CraftInventoryEnchanting extends CraftInventory implements EnchantingInventory {
    public CraftInventoryEnchanting(net.minecraft.inventory.SlotEnchantmentTable inventory) {
        super(inventory);
    }

    public void setItem(ItemStack item) {
        setItem(0,item);
    }

    public ItemStack getItem() {
        return getItem(0);
    }

    @Override
    public net.minecraft.inventory.SlotEnchantmentTable getInventory() {
        return (net.minecraft.inventory.SlotEnchantmentTable)inventory;
    }
}
