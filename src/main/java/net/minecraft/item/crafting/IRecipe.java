package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IRecipe
{
    // JAVADOC METHOD $$ func_77569_a
    boolean matches(InventoryCrafting var1, World var2);

    // JAVADOC METHOD $$ func_77572_b
    ItemStack getCraftingResult(InventoryCrafting var1);

    // JAVADOC METHOD $$ func_77570_a
    int getRecipeSize();

    ItemStack getRecipeOutput();
}