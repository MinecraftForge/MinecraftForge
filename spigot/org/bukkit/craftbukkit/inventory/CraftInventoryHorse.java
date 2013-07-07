package org.bukkit.craftbukkit.inventory;


public class CraftInventoryHorse extends CraftInventory {
    private final net.minecraft.inventory.IInventory resultInventory = null;

    public CraftInventoryHorse(net.minecraft.inventory.IInventory inventory) {
        super(inventory);
    }

    public net.minecraft.inventory.IInventory getResultInventory() {
        return resultInventory;
    }

    public net.minecraft.inventory.IInventory getIngredientsInventory() {
        return inventory;
    }

    @Override
    public int getSize() {
        //return getResultInventory().getSize() + getIngredientsInventory().getSize();
        return getIngredientsInventory().getSizeInventory();
    }
}
