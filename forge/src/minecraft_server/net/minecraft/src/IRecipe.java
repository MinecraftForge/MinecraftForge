package net.minecraft.src;

public interface IRecipe
{
    boolean matches(InventoryCrafting var1);

    ItemStack getCraftingResult(InventoryCrafting var1);

    /**
     * Returns the size of the recipe area
     */
    int getRecipeSize();

    ItemStack getRecipeOutput();
}
