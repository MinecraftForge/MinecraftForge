package org.bukkit.craftbukkit.inventory;

import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;


public class CraftInventoryBrewer extends CraftInventory implements BrewerInventory {
    public CraftInventoryBrewer(net.minecraft.inventory.IInventory inventory) {
        super(inventory);
    }

    public ItemStack getIngredient() {
        return getItem(3);
    }

    public void setIngredient(ItemStack ingredient) {
        setItem(3, ingredient);
    }

    @Override
    public BrewingStand getHolder() {
        return (BrewingStand) inventory.getOwner();
    }
}
