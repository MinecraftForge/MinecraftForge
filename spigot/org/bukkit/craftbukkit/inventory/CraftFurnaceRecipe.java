package org.bukkit.craftbukkit.inventory;


import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

public class CraftFurnaceRecipe extends FurnaceRecipe implements CraftRecipe {
    public CraftFurnaceRecipe(ItemStack result, ItemStack source) {
        super(result, source.getType(), source.getDurability());
    }

    public static CraftFurnaceRecipe fromBukkitRecipe(FurnaceRecipe recipe) {
        if (recipe instanceof CraftFurnaceRecipe) {
            return (CraftFurnaceRecipe) recipe;
        }
        return new CraftFurnaceRecipe(recipe.getResult(), recipe.getInput());
    }

    public void addToCraftingManager() {
        ItemStack result = this.getResult();
        ItemStack input = this.getInput();
        net.minecraft.item.crafting.FurnaceRecipes.smelting().addSmelting(input.getTypeId(), CraftItemStack.asNMSCopy(result), 0.1f);
    }
}
