package net.minecraftforge.crafting;

import java.util.ArrayList;

import net.minecraft.item.crafting.IRecipe;

public interface IRecipeExtractable extends IRecipe{

    /**
     * Gets the copy of the raw input and output. The output items should be
     * assigned into the last element.
     * 
     * @return Raw recipe.
     * 
     */
    public Object[] getRawRecipe();

    /**
     * Gets a list of recipes which this instance can process. All elements of
     * recipe should be instances of ItemStack or ArrayList&lt;ItemStack&gt;. The
     * length of each recipe should be getRecipeSize() + 1, and the output item
     * should be assigned into the last element.
     * 
     * @return The list of possible recipes.
     */
    public ArrayList<Object[]> getPossibleRecipes();
}
