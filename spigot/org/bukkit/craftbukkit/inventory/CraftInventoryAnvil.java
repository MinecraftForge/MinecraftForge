package org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.AnvilInventory;

public class CraftInventoryAnvil extends CraftInventory implements AnvilInventory {
    private final net.minecraft.inventory.IInventory resultInventory;

    public CraftInventoryAnvil(net.minecraft.inventory.IInventory inventory, net.minecraft.inventory.IInventory resultInventory) {
        super(inventory);
        this.resultInventory = resultInventory;
    }

    public net.minecraft.inventory.IInventory getResultInventory() {
        return resultInventory;
    }

    public net.minecraft.inventory.IInventory getIngredientsInventory() {
        return inventory;
    }

    @Override
    public int getSize() {
        return getResultInventory().getSizeInventory() + getIngredientsInventory().getSizeInventory();
    }
}
